# Clessidra Release Notes
=======================

## 1.0.2 - Work in Progress

- [TODO] Improve UUID Acquisition, possible performance gain.


## 1.0.1

- Fixed a PropertyOverrideProvider bug, last chained strategy was not getting the correct provider set.
- Added method call queueing

## 1.0.0

- Enhanced property override provider. Added support for wildcards in property override configuration strategy group key and method group.
- Fixed a bug in BaseRateLimiter where exceptions thrown from a rate limited method were causing the post invocation cleanup not to be called.
- Pulled out the call to the next chained strategy from the base limiters to AbstractLimiterStrategy in callNextChainedLimiterStrategy()
- Changed maven artifact group to com.ccbill

## 0.0.1

- Initial version 