package com.acmetelecom;

/**
 * The BillPrinter interface which all types of bill printer should implement.
 */
public interface IBillPrinter {

	/**
	 * Prints the bill heading.
	 * @param name The customer name to include in the heading.
	 * @param phoneNumber The customer's phone number.
	 * @param pricePlan The customer's price plan/tariff.
	 */
    void printHeading(String name, String phoneNumber, String pricePlan);

    /**
     * Prints the specified call information.
     * @param time The start time of the call.
     * @param callee The receiver's phone number.
     * @param duration The call duration.
     * @param cost The call cost.
     */
    void printItem(String time, String callee, String duration, String cost);

    /**
     * Prints the total cost.
     * @param total The bill total cost.
     */
    void printTotal(String total);
}
