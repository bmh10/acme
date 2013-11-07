package com.acmetelecom;

/**
 * The BillGenerator interface which all bill generators should implement.
 */
public interface IBillGenerator {
	
	/**
	 * Generates the specified bill and prints it out.
	 * @param bill The Bill to generate.
	 */
	void generateBill(Bill bill);
}
