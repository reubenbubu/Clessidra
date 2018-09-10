# Clessidra


## Maven Artifact

<a href="http://central.maven.org/maven2/com/ccbill/clessidra/" target="_blank">com.ccbill:clessidra</a>

## Description

A library used to control the inflow of requests to selected methods by use of a set of strategies.

By annotating a method or a class with @RateLimited method calls will be intercepted, selected strategies will be
called in a chain and they will decide if the method call should be allowed or blocked.

## Built-in strategies

This library provides the following limiter strategies:

- DefaultInvocationRateLimiterStrategy - Limits method call rate in a given time period.
- DefaultConcurrencyLimiterStrategy - Limits on the amount of concurrently running instances of a method or methods.
- DefaultCostBasedLimiterStrategy - Applies a user defined method cost to a method invocation and limits method calls based on a cost limit in a given time period.


[![Codacy Badge](https://api.codacy.com/project/badge/Grade/26dd92370364445fa566f07a6f1a1e0d)](https://www.codacy.com/app/reubenbubu/Clessidra?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=reubenbubu/Clessidra&amp;utm_campaign=Badge_Grade)