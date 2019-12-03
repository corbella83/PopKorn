Change Log
==========

Version 1.2.0 *(UNKNOWN)*
-----------------------------
* New: Free `Injector` from it's internal modifier. Now can be used.

Version 1.1.0 *(2019-11-29)*
-----------------------------
* New: Add PopKornCompat to use PopKorn from java code
* New: Supports `internal` modifier for Injectable classes
* New: Supports incremental annotation processing
* New: Add support for Obfuscation. Only needs one rule: `-keep class * implements cc.popkorn.mapping.Mapping`
* Fix: Interfaces of Injectable classes can be inner classes now
* New: Downgrade JAVA support to java6 onwards
* Fix: Classes can now be injected not only by its direct interfaces, but also indirect ones

Version 1.0.1 *(2019-11-20)*
-----------------------------
* Fix: Internal dependencies are now being resolved
* New: addInjectable(instance) can define the injectable class as any of the parents
    