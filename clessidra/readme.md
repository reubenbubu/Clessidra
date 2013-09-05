# Clessidra


## Maven Artifact

com.ccbill:clessidra

## Description


A library used to control the inflow of requests to selected methods by use of a set of strategies.

By annotating a method or a class with @RateLimited method calls will be intercepted, selected strategies will be
called in a chain and they will decide if the method call should be allowed or blocked.
