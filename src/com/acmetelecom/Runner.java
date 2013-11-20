package com.acmetelecom;

/**
 * Entry point for billing system, for testing purposes.
 */
public class Runner {

	/**
	 * Main entry point to program.
	 * @param args Command line arguments.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Running...");
		
		BillingSystem billingSystem = new BillingSystem();
		
		billingSystem.callInitiated("447711232343", "447766511332");
		sleepSeconds(2);
		billingSystem.callCompleted("447711232343", "447766511332");
		
		billingSystem.callInitiated("447711232343", "447766511111");
		sleepSeconds(2);
		billingSystem.callCompleted("447711232343", "447766511111");
		
		billingSystem.callInitiated("447711111111", "447766511332");
		sleepSeconds(2);
		billingSystem.callCompleted("447711111111", "447766511332");
		
		billingSystem.createCustomerBills();
	}
	
	private static void sleepSeconds(int n) throws InterruptedException {
		Thread.sleep(n*1000);
	}
}
