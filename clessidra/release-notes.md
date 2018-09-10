# Clessidra Release Notes
=======================

## 1.0.1

- Fixed a bug with PropertyOverrideProvider

## 1.0.1

- Fixed a bug with PropertyOverrideProvider

## 1.0.0

- Enhanced property override provider. Added support for wildcards in property override configuration strategy group key and method group.
- Fixed a bug in BaseRateLimiter where exceptions thrown from a rate limited method were causing the post invocation cleanup not to be called.
- Pulled out the call to the next chained strategy from the base limiters to AbstractLimiterStrategy in callNextChainedLimiterStrategy()
- Changed maven artifact group to com.ccbill

## 0.0.1

- Initial version 
