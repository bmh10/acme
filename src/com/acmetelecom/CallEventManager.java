package com.acmetelecom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The CallEventManager is responsible for handling call events, grouping them together to make complete calls
 * and storing the call logs for all customers.
 */
public class CallEventManager implements ICallEventManager {

	// Hash map of completed calls, indexed by caller phone number.
	private HashMap<String, ArrayList<Call>> callLog = new HashMap<String, ArrayList<Call>>();
	
	// Hash map of calls in progress, indexed by caller phone number.
	// N.B. Assumes that single caller can be making several calls at same time, but not to same callee.
	private HashMap<String, ArrayList<CallStart>> callsInProgress = new HashMap<String, ArrayList<CallStart>>();
	
	/**
	 * Handles incoming call events. If finds a start and end event for a particular call then groups them together into
	 * a single Call object and stores it in call logs.
	 * @param event The incoming call event to handle.
	 */
	public void handleEvent(CallEvent event) {
		AssertionHelper.NotNull(event, "event");
		String caller = event.getCaller();
		
		if (event instanceof CallStart) {
			addCallStartEventToCallsInProgress((CallStart)event);
		}
		else if (event instanceof CallEnd) {
			ArrayList<CallStart> callStarts = callsInProgress.get(caller);
			
			// Search for CallStart event which matches this CallEnd event.
			for (CallStart startEvent : callStarts) {
			    if (startEvent.getCallee() == event.getCallee()) {
				    callStarts.remove(startEvent);
				    callsInProgress.put(caller, callStarts);
				    addCallToLog(new Call(startEvent, event));
			  }
			}
		}
	}
	
	/**
	 * Gets the call logs for a particular customer.
	 * @param caller The caller the get call logs for.
	 */
	public List<Call> getCallsForCustomer(String caller) {
		return callLog.get(caller);
	}
	
	/**
	 * Clears all call logs.
	 */
	public void clearCallLogs() {
		callLog.clear();
		callsInProgress.clear();
	}
	
	/**
	 * Adds the specified CallStart event to the calls in progress log.
	 * @param callStart The CallStart event to add.
	 */
	private void addCallStartEventToCallsInProgress(CallStart callStart) {
		String caller = callStart.getCaller();
    	ArrayList<CallStart> callStartEvents = callsInProgress.get(caller);
    	if (callStartEvents == null) {
    		callStartEvents = new ArrayList<CallStart>();
    	}
    	
    	callStartEvents.add(callStart);
    	callsInProgress.put(caller, callStartEvents);
    }
	
	/**
	 * Adds the specified Call to the call log.
	 * @param call The Call to add to the call log.
	 */
    private void addCallToLog(Call call) {
    	String caller = call.caller();
    	ArrayList<Call> calls = callLog.get(caller);
    	if (calls == null) {
    		calls = new ArrayList<Call>();
    	}
    	
    	calls.add(call);
        callLog.put(caller, calls);
    }
	
}
