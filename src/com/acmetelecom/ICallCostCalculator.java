package com.acmetelecom;

import java.math.BigDecimal;

import com.acmetelecom.customer.Customer;

public interface ICallCostCalculator {
	
	BigDecimal calculateCallCost(Customer customer, Call call);
}
