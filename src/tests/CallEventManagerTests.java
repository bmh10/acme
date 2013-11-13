package tests;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.acmetelecom.Call;
import com.acmetelecom.CallEnd;
import com.acmetelecom.CallEventManager;
import com.acmetelecom.CallStart;
import com.acmetelecom.FileLogger;

/**
 * Tests behaviour of CallEventManager in an isolated context.
 */
public class CallEventManagerTests {
	final String dummyCallerNumber = "440000000000";
	final String dummyCalleeNumber = "440000000001";
	final String dummyCustomerName = "DummyName";
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Instance across which tests are to be applied.
	private CallEventManager callEventManager;
	
	/**
	 * Setup which is run before each unit test.
	 */
	@Before
	public void setup() {
		callEventManager = new CallEventManager();
		FileLogger.setActive(false);
	}
	
	/**
	 * Tests that handling event with null parameter throws an IllegalArgumentException.
	 */
	@Test
	public void attemptingToHandleEventWithNullParameterThrowsIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		callEventManager.handleEvent(null);
	}
	
	/**
	 * Tests that getting calls for a customer with null parameter throws an IllegalArgumentException.
	 */
	@Test
	public void attemptingToGetCallsForCustomerWithNullParameterThrowsIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		callEventManager.getCallsForCustomer(null);
	}
	
	/**
	 * Tests that if a call start and end event is received for the same call then the call is stored in
	 * the customer's log.
	 */
	@Test
	public void ifStartAndEndEventsReceivedForSameCallStoresCallInCallLogForCustomer() {
		callEventManager.handleEvent(new CallStart(dummyCallerNumber, dummyCalleeNumber, DateTime.now()));
		callEventManager.handleEvent(new CallEnd(dummyCallerNumber, dummyCalleeNumber, DateTime.now().plusMinutes(5)));
		
		List<Call> calls = callEventManager.getCallsForCustomer(dummyCallerNumber);
		assertTrue(calls.size() == 1);
		Call call = calls.get(0);
		assertTrue(call.caller().equals(dummyCallerNumber));
		assertTrue(call.callee().equals(dummyCalleeNumber));
	}
	
	/**
	 * Tests that only completed calls are stored in a customers log (i.e. cannot bill calls which are currently underway).
	 */
	@Test
	public void onlyStoredCompletedCallsInCallLogForCustomer() {
		callEventManager.handleEvent(new CallStart(dummyCallerNumber, dummyCalleeNumber+"1", DateTime.now()));
		callEventManager.handleEvent(new CallStart(dummyCallerNumber, dummyCalleeNumber+"2", DateTime.now().plusMinutes(5)));
		callEventManager.handleEvent(new CallEnd(dummyCallerNumber, dummyCalleeNumber+"1", DateTime.now().plusMinutes(5)));
		
		List<Call> calls = callEventManager.getCallsForCustomer(dummyCallerNumber);
		assertTrue(calls.size() == 1);
		Call call = calls.get(0);
		assertTrue(call.caller().equals(dummyCallerNumber));
		assertTrue(call.callee().equals(dummyCalleeNumber+"1"));
	}
	
	/**
	 * Tests that a customer can make multiple calls at the same time to different people and all logs will be saved.
	 */
	@Test
	public void customerCanMakeMultipleCallsAtSameTimeToDifferentCalleesAndAllLogsAreSaved() {
		callEventManager.handleEvent(new CallStart(dummyCallerNumber, dummyCalleeNumber+"1", DateTime.now()));
		callEventManager.handleEvent(new CallStart(dummyCallerNumber, dummyCalleeNumber+"2", DateTime.now()));
		callEventManager.handleEvent(new CallStart(dummyCallerNumber, dummyCalleeNumber+"3", DateTime.now()));
		callEventManager.handleEvent(new CallEnd(dummyCallerNumber, dummyCalleeNumber+"1", DateTime.now().plusMinutes(5)));
		callEventManager.handleEvent(new CallEnd(dummyCallerNumber, dummyCalleeNumber+"2", DateTime.now().plusMinutes(5)));
		callEventManager.handleEvent(new CallEnd(dummyCallerNumber, dummyCalleeNumber+"3", DateTime.now().plusMinutes(5)));
		
		List<Call> calls = callEventManager.getCallsForCustomer(dummyCallerNumber);
		assertTrue(calls.size() == 3);
		assertTrue(calls.get(0).callee().equals(dummyCalleeNumber+"1"));
		assertTrue(calls.get(1).callee().equals(dummyCalleeNumber+"2"));
		assertTrue(calls.get(2).callee().equals(dummyCalleeNumber+"3"));
	}
	
	/**
	 * Tests that if a customer starts a call to same callee twice without hanging up in between then an IllegalStateException
	 * is thrown.
	 */
	@Test
	public void ifCustomerStartsCallToSameCalleeTwiceWithoutHangingUpInbetweenIllegalStateExceptionThrown() {
		exception.expect(IllegalStateException.class);
		callEventManager.handleEvent(new CallStart(dummyCallerNumber, dummyCalleeNumber+"1", DateTime.now()));
		callEventManager.handleEvent(new CallStart(dummyCallerNumber, dummyCalleeNumber+"1", DateTime.now()));		
	}
	
	/**
	 * Tests that if a customer starts a call to same callee twice without hanging up in between then an IllegalStateException
	 * is thrown.
	 */
	@Test
	public void ifCustomerCallsOwnNumberIllegalStateExceptionThrown() {
		exception.expect(IllegalStateException.class);
		callEventManager.handleEvent(new CallStart(dummyCallerNumber, dummyCalleeNumber+"1", DateTime.now()));
		callEventManager.handleEvent(new CallStart(dummyCallerNumber, dummyCalleeNumber+"1", DateTime.now()));		
	}
	
	/**
	 * Tests that clearing the call logs clears out the call logs.
	 */
	@Test
	public void clearingCallLogsClearsCallLogs() {
		callEventManager.handleEvent(new CallStart(dummyCallerNumber, dummyCalleeNumber, DateTime.now()));
		callEventManager.handleEvent(new CallEnd(dummyCallerNumber, dummyCalleeNumber, DateTime.now().plusMinutes(5)));
		
		assertTrue(callEventManager.getCallsForCustomer(dummyCallerNumber).size() == 1);
		callEventManager.clearCallLogs();
		assertTrue(callEventManager.getCallsForCustomer(dummyCallerNumber).size() == 0);
	}
}
