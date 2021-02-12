Change Log
==========

Version 2.2.0 *(2021-XX-XX)*
-----------------------------

* Fix: Compilation error when compiling on Windows OS

Version 2.1.0 *(2021-01-19)*
-----------------------------

* New: Add assisted dependencies
* New: Now you can create any `Injectable` class overriding any of its dependencies
* Maintenance: Update to Kotlin 1.4.21
* New: Add lazy initialization `by popkorn` and `by injecting`
* New: Add new scope: BY_HOLDER
* Fix: Compiler error logger wasn't showing the correct message
* Deprecated: `Alias` is no longer useful, use environments instead

Version 2.0.0 *(2020-09-05)*
-----------------------------

* New: Add multiplatform support (JVM, IOS, JS and Native)
* Maintenance: Update to Kotlin 1.4.0

Version 1.5.0 *(2020-06-03)*
-----------------------------

* Maintenance: Update library dependencies

Version 1.4.0 *(2020-03-18)*
-----------------------------

* New: Add support for abstract classes as if they were interfaces
* Fix: Add support to all prohibited packages(java.*, javax.*), not only java.lang.*
* New: Can get the injector at any injectable constructor/method

Version 1.3.0 *(2019-12-19)*
-----------------------------

* New: Can compile Injectable classes without default environment
* Fix: String, Bool (boolean), Int (int), etc... were still giving compilation error when used in constructors/methods
* New: Can use `Empty` parameter at any constructor/method to 'fake overload'
* New: Constructor/method parameters of Injectable classes can now be nullable
* New: `InjectableProvider` can provide an Interface now
* New: Custom `Injector` can use custom pools
* Fix: Runtime injectable can now be an interface
* New: Can get the current environment in any injectable constructor/method by defining `Environment` parameter
* Fix: Multiple environments in a constructor/method result in an invalid compilation file

Version 1.2.0 *(2019-12-10)*
-----------------------------

* New: Free `Injector` from its internal modifier. Now can be used.
* New: Now can use parametrized methods when injecting through `InjectableProvider`
* New: Add propagation strategy at `Injectable` and `InjectableProvider`
* New: Now an Injector can be purged to free memory
* Fix: String, Bool (boolean), Int (int), etc... no longer give a compilation error when used in an `InjectableProvider`

Version 1.1.0 *(2019-11-29)*
-----------------------------

* New: Add `PopKornCompat` to use PopKorn from java code
* New: Supports `internal` modifier for Injectable classes
* New: Supports incremental annotation processing
* New: Add support for Obfuscation. Only needs one rule: `-keep class * implements cc.popkorn.mapping.Mapping`
* Fix: Interfaces of Injectable classes can be inner classes now
* New: Downgrade JAVA support to java6 onward
* Fix: Classes can now be injected not only by its direct interfaces, but also indirect ones

Version 1.0.1 *(2019-11-20)*
-----------------------------

* Fix: Internal dependencies are now being resolved
* New: addInjectable(instance) can define the injectable class as any of the parents
