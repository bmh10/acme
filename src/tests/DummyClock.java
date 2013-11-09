package tests;

import org.joda.time.DateTime;

import com.acmetelecom.IClock;

public class DummyClock implements IClock {

	private DateTime time;
	
	public DummyClock() {
		this.time = DateTime.now();
	}
	
	public void setTime(DateTime time) {
		this.time = time;
	}
	
	@Override
	public DateTime now() {
		return time;
	}
}
