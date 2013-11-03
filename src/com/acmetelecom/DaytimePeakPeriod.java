package com.acmetelecom;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.acmetelecom.customer.Tariff;

// TODO: add interface for this.
class DaytimePeakPeriod {

	private int PeakStart = 7;
	private int PeakEnd = 19;
	
	// Defines periods in a day.
	enum DayPeriod {
		PrePeak,
		Peak,
		PostPeak
	};
	
//    public boolean offPeak(DateTime time) {
////        Calendar calendar = Calendar.getInstance();
////        calendar.setTime(time);
////        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int hour = time.getHourOfDay();
//        return hour < 7 || hour >= 19;
//    }
    
	public int getPeriodDurationSeconds(DayPeriod period) {
 		switch (period) {
 		case PrePeak:  return PeakStart * 60 * 60;
 		case Peak:     return (PeakEnd - PeakStart) * 60 * 60;
 		case PostPeak: return (24 - PeakEnd) * 60 * 60;
 		default:       return 0;
 		}
 	}
 	
 	public int getSecondInDayAtEndOfPeriod(DayPeriod period) {
 		int a = getPeriodDurationSeconds(DayPeriod.PrePeak);
 		int b = getPeriodDurationSeconds(DayPeriod.Peak);
 		int c = getPeriodDurationSeconds(DayPeriod.PrePeak);
 		
 		switch (period) {
 		case PrePeak:  return a;
 		case Peak:     return a + b;
 		case PostPeak: return a + b + c;
 		default:       return 0;
 		}
 	}
 	
 	public BigDecimal getPeriodRate(DayPeriod period, Tariff tariff) {
 		switch (period) {
 		case PrePeak:  return tariff.offPeakRate();
 		case Peak:     return tariff.peakRate();
 		case PostPeak: return tariff.offPeakRate();
 		default:       return new BigDecimal(0);
 		}
 	}
 	
	public DayPeriod getPeriodOfDay(DateTime time) {
		int hour = time.getHourOfDay();
		if (hour < PeakStart) return DayPeriod.PrePeak;
		if (hour < PeakEnd) return DayPeriod.Peak;
		return DayPeriod.PostPeak;
		
	}
}
