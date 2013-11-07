package com.acmetelecom;

import java.math.BigDecimal;

/**
 * Class responsible for money formatting operations.
 */
public class MoneyFormatter {
	
	/**
	 * Converts pence, in BigDecimal form, to pounds, in String form.
	 * @param pence The pence to convert.
	 * @return The converted pounds value as a String.
	 */
    public static String penceToPounds(BigDecimal pence) {
        BigDecimal pounds = pence.divide(new BigDecimal(100));
        return String.format("%.2f", pounds.doubleValue());
    }
}
