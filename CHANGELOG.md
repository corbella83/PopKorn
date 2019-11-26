Change Log
==========

Version 1.1.0 *(2019-UNKNOWN)*
-----------------------------
* New: Add PopKornCompat to use PopKorn from java code
* New: Supports `internal` modifier for Injectable classes
* New: Supports incremental annotation processing
* New: Add support for Obfuscation. Only needs one rule: `-keep class * implements cc.popkorn.mapping.Mapping`


Version 1.0.1 *(2019-11-20)*
-----------------------------
* Fix: Internal dependencies are now being resolved
* New: addInjectable(instance) can define the injectable class as any of the parents
    