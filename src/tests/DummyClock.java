package tests;

import org.joda.time.DateTime;

import com.acmetelecom.IClock;

/**
 * A dummy clock implementation which, for testing purposes, allows us to set the
 * time to whatever we want.
 */
class DummyClock implements IClock {

	private DateTime time;

	/**
	 * Constructor.
	 */
	public DummyClock() {
		this.time = DateTime.now();
	}
	
	/**
	 * Sets the current clock time to the specified time.
	 * @param time The time to set the clock to.
	 */
	public void setTime(DateTime time) {
		this.time = time;
	}
	
	/**
	 * Gets the clocks current time.
	 * @return The current clock time as a DateTime object.
	 */
	@Override
	public DateTime now() {
		return time;
	}
}
