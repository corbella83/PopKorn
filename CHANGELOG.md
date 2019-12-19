Change Log
==========

Version 1.4.0 *(XXXXXX)*
-----------------------------


Version 1.3.0 *(2019-12-19)*
-----------------------------
* New: Can compile Injectable classes without default environment
* Fix: String, Bool (boolean), Int (int), etc.. were still giving compilation error when used in constructors/methods
* New: Can use `Empty` parameter at any constructor/method to 'fake overload'
* New: Constructor/method parameters of Injectable classes can now be nullable
* New: `InjectableProvider` can provide an Interface now
* New: Custom `Injector` can use custom pools
* Fix: Runtime injectable can now be an interface
* New: Can get the current environment in any injectable constructor/method by defining `Environment` parameter
* Fix: Multiple environments in a constructor/method result in an invalid compilation file 

Version 1.2.0 *(2019-12-10)*
-----------------------------
* New: Free `Injector` from it's internal modifier. Now can be used.
* New: Now can use parametrized methods when injecting through `InjectableProvider`  
* New: Add propagation strategy at `Injectable` and `InjectableProvider`
* New: Now an Injector can be purged to free memory
* Fix: String, Bool (boolean), Int (int), etc.. no longer give a compilation error when used in an `InjectableProvider` 

Version 1.1.0 *(2019-11-29)*
-----------------------------
* New: Add `PopKornCompat` to use PopKorn from java code
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
    