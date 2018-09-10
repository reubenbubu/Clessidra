package com.ccbill.clessidra.tests.services;

import java.util.Date;

import org.junit.Ignore;
import org.springframework.stereotype.Service;

import com.ccbill.clessidra.annotations.RateLimited;
import com.ccbill.clessidra.enums.MethodGrouping;

@Ignore
@Service
@RateLimited(methodGrouping = MethodGrouping.GROUPED, groupName = "invocationRateLimiterOnClass", limiterBean = "invocationRateLimiter")
public class InvocationRateServiceClassAnnotated {

	public void testInvocationRateGrouped(int i) {
	}

	public void testInvocationRateGrouped(String s) {

	}

	public void testInvocationRateGrouped(Date d) {

	}

}
