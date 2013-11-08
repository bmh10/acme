package com.acmetelecom;

import java.util.List;

public interface ICallEventManager {

	void handleEvent(CallEvent event);
	List<Call> getCallsForCustomer(String caller);
	void clearCallLogs();
}
