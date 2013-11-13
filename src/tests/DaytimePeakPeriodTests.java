package tests;

import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.acmetelecom.DaytimePeakPeriod;
import com.acmetelecom.FileLogger;
import com.acmetelecom.IPeakPeriod.DayPeriod;
import com.acmetelecom.customer.Tariff;

/**
 * Tests behaviour of DaytimePeakPeriod in an isolated context.
 */
public class DaytimePeakPeriodTests {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Instance across which tests are to be applied.
	private DaytimePeakPeriod daytimePeakPeriod;
	
	/**
	 * Setup which is run before each unit test.
	 */
	@Before
	public void setup() {
		daytimePeakPeriod = new DaytimePeakPeriod(); 
		FileLogger.setActive(false);
	}
	
	/**
	 * Tests that peak start time is before the peak end time.
	 */
	@Test
	public void peakStartIsBeforePeakEnd() {
		assertTrue(daytimePeakPeriod.getPeakStart() < daytimePeakPeriod.getPeakEnd());
	}
	
	/**
	 * Tests that passing null while getting period duration throws an IllegalArgumentException.
	 */
	@Test
	public void passingNullWhileGettingPeriodDurationThrowsIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		daytimePeakPeriod.getPeriodDurationSeconds(null);
	}
	
	/**
	 * Tests that getting period durations returns expected period durations.
	 */
	@Test
	public void gettingPeriodDurationsInSecondsReturnsCorrectDurations() {
		int expectedPrePeakDuration = daytimePeakPeriod.getPeakStart() * 60 * 60;
		int expectedPeakDuration = (daytimePeakPeriod.getPeakEnd() - daytimePeakPeriod.getPeakStart()) * 60 * 60;
		int expectedPostPeakDuration = (24 - daytimePeakPeriod.getPeakEnd()) * 60 * 60;
		
		assertTrue(daytimePeakPeriod.getPeriodDurationSeconds(DayPeriod.PrePeak) == expectedPrePeakDuration);
		assertTrue(daytimePeakPeriod.getPeriodDurationSeconds(DayPeriod.Peak) == expectedPeakDuration);
		assertTrue(daytimePeakPeriod.getPeriodDurationSeconds(DayPeriod.PostPeak) == expectedPostPeakDuration);
	}
	
	/**
	 * Tests that passing null while getting second in day at end of period throws an IllegalArgumentException.
	 */
	@Test
	public void passingNullWhileGettingSecondInDayAtEndOfPeriodThrowsIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		daytimePeakPeriod.getSecondInDayAtEndOfPeriod(null);
	}
	
	/**
	 * Tests that getting second in day at end of periods returns expected times in seconds.
	 */
	@Test
	public void gettingSecondInDayAtEndOfPeriodsReturnsCorrectTimes() {
		int prePeakEndTime = daytimePeakPeriod.getPeakStart() * 60 * 60;
		int peakEndTime = prePeakEndTime + (daytimePeakPeriod.getPeakEnd() - daytimePeakPeriod.getPeakStart()) * 60 * 60;
		int postPeakEndTime = 24 * 60 * 60;
		
		assertTrue(daytimePeakPeriod.getSecondInDayAtEndOfPeriod(DayPeriod.PrePeak) == prePeakEndTime);
		assertTrue(daytimePeakPeriod.getSecondInDayAtEndOfPeriod(DayPeriod.Peak) == peakEndTime);
		assertTrue(daytimePeakPeriod.getSecondInDayAtEndOfPeriod(DayPeriod.PostPeak) == postPeakEndTime);
	}
	
	/**
	 * Tests that passing null parameters while getting period rate throws an IllegalArgumentException.
	 */
	@Test
	public void passingNullWhileGettingPeriodRateThrowsIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		daytimePeakPeriod.getPeriodRate(null, null);
	}
	
	/**
	 * Tests that getting period rates returns correct rates.
	 */
	@Test
	public void gettingPeriodRateReturnsCorrectRates() {
		Tariff[] tariffs = Tariff.values();
		
		for (Tariff tariff : tariffs) {
			assertTrue(daytimePeakPeriod.getPeriodRate(DayPeriod.PrePeak, tariff) == tariff.offPeakRate());
			assertTrue(daytimePeakPeriod.getPeriodRate(DayPeriod.Peak, tariff) == tariff.peakRate());
			assertTrue(daytimePeakPeriod.getPeriodRate(DayPeriod.PostPeak, tariff) == tariff.offPeakRate());
		}
	}
	
	/**
	 * Tests that passing null parameters while getting period of day throws an IllegalArgumentException.
	 */
	@Test
	public void passingNullWhileGettingPeriodOfDayThrowsIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		daytimePeakPeriod.getPeriodOfDay(null);
	}
	
	/**
	 * Tests that getting period of day returns correct period of day for different times.
	 */
	@Test
	public void gettingPeriodOfDayReturnsCorrectPeriodForDifferentTimes() {
		int peakStart = daytimePeakPeriod.getPeakStart();
		int peakEnd = daytimePeakPeriod.getPeakEnd();
		
		if (peakStart != 0) {
			assertTrue(daytimePeakPeriod.getPeriodOfDay(new DateTime(2013, 11, 13, 0, 0)) == DayPeriod.PrePeak);
			assertTrue(daytimePeakPeriod.getPeriodOfDay(new DateTime(2013, 11, 13, peakStart - 1, 59)) == DayPeriod.PrePeak);
		}
		assertTrue(daytimePeakPeriod.getPeriodOfDay(new DateTime(2013, 11, 13, peakStart, 0)) == DayPeriod.Peak);
		assertTrue(daytimePeakPeriod.getPeriodOfDay(new DateTime(2013, 11, 13, peakEnd - 1, 59)) == DayPeriod.Peak);
		assertTrue(daytimePeakPeriod.getPeriodOfDay(new DateTime(2013, 11, 13, peakEnd, 0)) == DayPeriod.PostPeak);
		assertTrue(daytimePeakPeriod.getPeriodOfDay(new DateTime(2013, 11, 13, 23, 59)) == DayPeriod.PostPeak);
	}
}