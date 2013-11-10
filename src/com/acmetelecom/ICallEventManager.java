package com.acmetelecom;

import java.util.List;

/**
 * The CallEventManager interface which all call event managers should implement.
 */
public interface ICallEventManager {

	/**
	 * Handles incoming call events.
	 * @param event The incoming call event to handle.
	 * @exception IllegalArgumentException If any of arguments are null.
	 */
	void handleEvent(CallEvent event);
	
	/**
	 * Gets the call logs for a particular customer.
	 * @param caller The caller the get call logs for.
	 * @exception IllegalArgumentException If any of arguments are null.
	 */
	List<Call> getCallsForCustomer(String caller);
	
	/**
	 * Clears all call logs.
	 */
	void clearCallLogs();
}
