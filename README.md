[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cc.popkorn/popkorn/badge.svg)](https://search.maven.org/artifact/cc.popkorn/popkorn)
[![Kotlin](https://img.shields.io/badge/kotlin-1.8.21-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Slack channel](https://img.shields.io/badge/chat-slack-red.svg?logo=slack)](https://kotlinlang.slack.com/messages/popkorn/)

PopKorn - Kotlin Multiplatform DI
==========


PopKorn is a simple, powerful and lightweight Kotlin Multiplatform Dependency Injector. It doesn't need any modules or components, just use it without writing a single extra file! It supports AND, IOS, JVM, JS and NATIVE.


Download
--------
Get it with Gradle:

```groovy
implementation 'cc.popkorn:popkorn:2.3.1'
kapt 'cc.popkorn:popkorn-compiler:2.3.1'
```

The Kotlin Gradle Plugin 1.4.0 will automatically resolve platform dependent implementations (jvm, js, iosX64...). But if you are using Kotlin Gradle Plugin below 1.4.0 you will have to specify the platform yourself. In the case of Android/JVM is the following:

```groovy
implementation 'cc.popkorn:popkorn-jvm:2.3.1'
kapt 'cc.popkorn:popkorn-compiler:2.3.1'
```

Working with Scopes and Environments
--------
Scopes are the way to define the life span of an instance. There are 4 types of scopes:

* Scope.BY_APP (default) -> Instance will be created only once, for hence will live forever. Normally for classes that have heavy construction or saves states (Retrofit, OkHttp, RoomDB, etc)
* Scope.BY_USE -> Instance will be created if no one is using it, meaning will live as long as others are using it. Normally for classes that are just like helpers (dataSources, repositories, useCases, etc...)
* Scope.BY_HOLDER -> Instance will be created if used with a different holder, will live as long as its holder (container). Normally for instances that needs to be shared by a common parent (presenters, viewModels, etc...)
* Scope.BY_NEW -> Instance will be created every time it's needed, so won't live at all. Normally for instances that doesn't make sense to reuse (presenters, viewModels, screens, etc...)

Environments allow you to have multiple instances of the same object, but in a complete different configuration. For example, you can have 2 different and persistent Retrofit instances. See more examples at bottom.

```kotlin
val r1 = inject<Retrofit>("pro") // This will inject a persistent instance of Retrofit attached to "pro"
val r2 = inject<Retrofit>("des") // This will inject a persistent instance of Retrofit attached to "des"
// r1 !== r2 as they have different environments.
```

Injecting Project Classes
--------
Just add `@Injectable` to any class...

```kotlin
@Injectable
class HelloWorld
```

...and inject it anywhere you want:

```kotlin
val helloWorld = inject<HelloWorld>()

// Or by lazy
val helloWorld by popkorn<HelloWorld>()
```

By default `HelloWorld` will be Scope.BY_APP, but we can change it:

```kotlin
@Injectable(scope = Scope.BY_NEW)
class HelloWorld
```

Also, if `HelloWorld` has injectable constructor dependencies, PopKorn will automatically resolve them

```kotlin
@Injectable
class HelloWorld(val helloBarcelona: HelloBarcelona, val helloParis: HelloParis)
```

and if we have different constructors for the class, we can define environments to distinguish them:

```kotlin
@Injectable
class HelloWorld {

    @ForEnvironments("europe")
    constructor(val helloBarcelona: HelloBarcelona, val helloParis: HelloParis) : this()

    @ForEnvironments("usa")
    constructor(val helloNewYork: HelloNewYork, val helloLosAngeles: HelloLosAngeles) : this()
}
```

and then can inject it like this:

```kotlin
val helloWorld = inject<HelloWorld>() // Will inject a HelloWorld instance without parameters
val helloWorld = inject<HelloWorld>("europe") // Will inject a HelloWorld instance with parameters HelloBarcelona and HelloParis
val helloWorld = inject<HelloWorld>("usa") // Will inject a HelloWorld instance with parameters HelloNewYork and HelloLosAngeles
```

### Using Interfaces

Let's now define an interface:

```kotlin
interface Hello
```

and use it in our example

```kotlin
@Injectable
class HelloWorld : Hello
```

We can now inject by an interface:

```kotlin
val helloWorld = inject<Hello>() // This will inject a HelloWorld instance 
```

And just like before, if you have different implementations of the same interface, you can distinguish them with environments

```kotlin
@Injectable
@ForEnvironments("planet")
class HelloPlanet : Hello
```

so,

```kotlin
val hello = inject<Hello>("planet") // This will return an instance of HelloPlanet
val hello = inject<Hello>() // This will return an instance of HelloWorld
```

### Using runtime arguments (or assisted dependencies)

For injectable classes with BY_NEW scope, you can have assisted arguments

```kotlin
@Injectable(BY_NEW)
class HelloViewModel(@Assisted val id: Long, val param2: HelloBarcelona, val param2: HelloNewYork) : Hello
```

that you provide in runtime as:

```kotlin
val id = 4
val hello = inject<Hello> {
    assist(id)
}
```

Injecting External Classes
--------
If you want to inject a class out of your code, just define a class and annotate it with `@InjectableProvider`. Notice that you can use as many injectable objects as you need defining them as parameters of your method.

```kotlin
@InjectableProvider(scope = Scope.BY_APP)
class MyRetrofitProvider {

    fun createRetrofit(client: OkHttp): Retrofit {
        return Retrofit.Builder()
            .baseUrl("my.url")
            .client(client)
            .build()
    }
}
```

and use it the same way:

```kotlin
val hello = inject<Retrofit>() // This will inject a persistent instance of Retrofit
```

Injecting Runtime Instances
--------
There is also a way to use custom injection. You can take control of when an instance is injectable and when is not:

```kotlin
val someInstance = SomeType()

popKorn().addInjectable(someInstance)

val copy1 = inject<SomeType>() // Will inject someInstance

popKorn().removeInjectable(someInstance)

val copy2 = inject<SomeType>() // Will fail, because SomeType is not injectable anymore
```

In Android this is very useful when injecting the Context (An instance that is provided and cannot be created)

```kotlin
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        popKorn().addInjectable(this, Context::class)
    }
}
```

Testing
--------
PopKorn also offers the ability to create injectable classes at any time (ignoring its scope), overriding any dependency you like

```kotlin
class Hello(val param: HelloBarcelona, val param: HelloParis)

val hello = popKorn().create<Hello> {
    override(HelloTestBarcelona())
}
```

This will create a hello instance using HelloTestBarcelona instead of the default HelloBarcelona

Using Android / JVM
--------
PopKorn provides full support to Android platforms. You don't need to initialize anything. Just use it as described above.

To use it from pure java classes, use PopKornCompat:

```java
HelloWorld helloWorld = PopKornCompat.inject(HelloWorld.class);
```

To prevent you to exclude lots of classes from obfuscation, PopKorn saves some mappings that needs to be merged when generating the APK. If you are using multiple modules, Android will take only the last one by default (
or throw a compilation error depending on the Gradle version), unless the following option it's set in the `build.gradle`:

```groovy
android {
    packagingOptions {
        merge 'META-INF/popkorn.provider.mappings'
        merge 'META-INF/popkorn.resolver.mappings'
    }
}
```

This is the error that the above fixes:

```text
Execution failed for task ':app:mergeDebugJavaResource'.
> A failure occurred while executing com.android.build.gradle.internal.tasks.Workers$ActionFacade
   > More than one file was found with OS independent path 'META-INF/popkorn.provider.mappings'
```

Using IOS
--------
PopKorn provides full support to Objective C / Swift platforms. You will need to do the following:

A) In your multiplatform project, write a File (Bridge.kt) on your IOS module and add this 2 functions:

```kotlin
fun init(creator: (ObjCClass) -> Mapping) = cc.popkorn.setup(creator)

fun getInjector() = InjectorObjC(popKorn())
```

B) From your IOS project you will need to initialize PopKorn at the beginning of your app (AppDelegate):

```swift
BridgeKt.doInit { (clazz) -> PopkornMapping in
            return clazz.alloc() as! PopkornMapping
        }
```

C) To be used anywhere like this in ObjectiveC / Swift code

```swift
let injector = BridgeKt.getInjector()

let helloWorld = injector.inject(clazz: HelloWorld.self) as! HelloWorld
```

You can also use runtime injections

```swift
let someInstance = SomeType()

injector.addInjectable(instance: someInstance, clazz: SomeType.self)

let copy1 = injector.inject(clazz: SomeType.self) as! SomeType // Will inject someInstance

injector.removeInjectable(clazz: SomeType.self)

let copy2 = injector.inject(clazz: SomeType.self) as! SomeType // Will fail, because SomeType is not injectable anymore
```

Using JS / Native
--------
PopKorn provides basic support to JS / Native platforms. In your multiplatform project, write a File on your JS / Native module and add this function:

```kotlin
fun init() {
    val resolvers: Set<Mapping> = hashSetOf(/* LOCATE ALL RESOLVER CLASSES OF TYPE MAPPING THAT POPKORN AUTOGENERATED */)
    val providers: Set<Mapping> = hashSetOf(/* LOCATE ALL PROVIDER CLASSES OF TYPE MAPPING THAT POPKORN AUTOGENERATED */)
    cc.popkorn.setup(resolvers, providers)
}
```

then call it somewhere to initialize PopKorn. For now, injections for JS / Native can only be done from your multiplatform project. Injections from JS / Native code is not yet available.

More Examples
--------
You can find out more examples in popkorn-example project

```kotlin
interface Location

@Injectable
class RealLocation : Location {

    constructor() : this() {
        // Get LocationManager.GPS_PROVIDER
    }

    @ForEnvironments("network")
    constructor() : this() {
        // Get LocationManager.NETWORK_PROVIDER
    }
}

@Injectable(scope = Scope.BY_NEW)
@ForEnvironments("fake")
class FakeLocation : Location
```

and then

```kotlin
val r1 = inject<Location>() // This will inject a persistent instance of RealLocation to get GPS locations
val r2 = inject<Location>("network") // This will inject a persistent instance RealLocation to get Network locations
val r2 = inject<Location>("fake") // This will inject a volatile instance of FakeLocation
```

or use it in any constructor of other injectable classes:

```kotlin
constructor(real:Location, @WithEnvironment("fake") fake:Location, @WithEnvironment("network") network:Location) {}
```

License
-------

    Copyright 2019 Pau Corbella

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
