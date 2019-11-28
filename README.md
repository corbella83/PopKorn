PopKorn
==========


PopKorn is a simple, powerful and lightweight Dependency Injector 100% Kotlin. It doesn't need any boilerplate, just use it


Download
--------
Get it with Maven:
```xml
<dependency>
  <groupId>cc.popkorn</groupId>
  <artifactId>popkorn</artifactId>
  <version>1.1.0</version>
</dependency>
```

or Gradle:
```groovy
implementation 'cc.popkorn:popkorn:1.1.0'
kpt 'cc.popkorn:popkorn-compiler:1.1.0'
```

Working with Scopes and Environments
--------
Scopes are the way to define the life span of an instance. There are 3 types of scopes:
* Scope.BY_APP (default) -> Instance will be created only once, for hence will live forever. Normally for classes that have heavy construction or saves states (Retrofit, OkHttp, RoomDB, etc)
* Scope.BY_USE -> Instance will be created if no one is using it, meaning that will live as long as others are using it. Normally for classes that are just like helpers (datasources, repositories, usecases, etc..)
* Scope.BY_NEW -> Instance will be created every time is needed, so won't live at all. Normally for instances that doesn't make sense to reuse (presenters, screens, etc...)

Environments allow you to have multiple instances of the same object, but in a complete different configuration. For example, you can have 2 different and persistent Retrofit instances. See more examples at bottom
```kotlin
val r1 = inject<Retrofit>("pro") //This will inject a persistent instance of Retrofit attached to "pro"
val r2 = inject<Retrofit>("des") //This will inject a persistent instance of Retrofit attached to "des"
//r1 !== r2 as they have different environments.
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

or

val helloWorld:HelloWorld = inject()

or

val helloWorld = HelloWorld::class.inject()
```

If you are using java code, you can call it this way
```java
val helloWorld = PopKornCompat.inject(HelloWorld.class);
```

By default `HelloWorld` will be  Scope.BY_APP, but we can change it:
```kotlin
@Injectable(scope=Scope.BY_NEW)
class HelloWorld
```

Also if `HelloWorld` has injectable constructor dependencies, PopKorn will automatically resolve them
```kotlin
@Injectable
class HelloWorld(val helloBarcelona:HelloBarcelona, val helloParis:HelloParis)
```

And if we have different constructors for the class, we can define environments to distinguish them:
```kotlin
@Injectable
class HelloWorld {
    
    @ForEnviornmen("europe")
    constructor(val helloBarcelona:HelloBarcelona, val helloParis:HelloParis):this()
    
    @ForEnviornmen("usa")
    constructor(val helloNewYork:HelloNewYork, val helloLosAngeles:HelloLosAngeles):this()
}
```

and then can inject it like this:
```kotlin
val helloWorld = inject<HelloWorld>() // will inject a HelloWorld instance without parameters
val helloWorld = inject<HelloWorld>("europe") // will inject a HelloWorld instance with parameters HelloBarcelona and HelloParis
val helloWorld = inject<HelloWorld>("usa") // will inject a HelloWorld instance with parameters HelloNewYork and HelloLosAngeles
```


####Using Interfaces

Let's now define an interface:
```kotlin
interface Hello
```

and use it in our example
```kotlin
@Injectable
class HelloWorld : Hello
```

We can now inject by interface:
```kotlin
val helloWorld = inject<Hello>() //This will inject a HelloWorld instance 
```

And just like before, if you have different implementations of the same interface, you can distinguish them with environments
```kotlin
@Injectable
@ForEnvironment("planet")
class HelloPlanet : Hello
```

so, 
```kotlin
val hello = inject<Hello>("planet") // this will return an instance of HelloPlanet
val hello = inject<Hello>() // this will return an instance of HelloWorld
```


Injecting External Classes
--------
If you want to inject a class that is out of your code, just define a `Provider` and annotate it with `@InjectableProvider`:
```kotlin
@InjectableProvider
class MyOkHttpProvider : Provider<OkHttp> {
    override fun create(environment: String?) = OkHttpClient.Builder().build()
    override fun scope() =  Scope.BY_APP
}
```

and use it the same way:
```kotlin
val hello = inject<OkHttp>() //This will inject a persistent instance of OkHttp
```



Injecting Existing Instances
--------
There is also a way to use custom injection. You can take control of when an instance is injectable and when is not:
```kotlin
val someInstance = SomeType()

getPopKornController().addInjectable(someInstance)

val copy1 = inject<SomeType>() //Will inject someInstance

getPopKornController().removeInjectable(someInstance)

val copy2 = inject<SomeType>() //Will fail, because SomeType is not injectable anymore
```

In Android this is very useful when injecting the Context (An instance that is provided and can not be created)
```kotlin
class MyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        getPopKornController().addInjectable(this, Context::class)
    }

}
```

Obfuscation (R8, Proguard...)
--------
If you are using obfuscation, you will have to add this rule
```pro
-keep class * implements cc.popkorn.mapping.Mapping
```

More Examples
--------
You can find out more examples in popkorn-example project

```kotlin
interface Location

@Injectable
class RealLocation:Location{
    
    constructor():this(){
        //get LocationManager.GPS_PROVIDER
    }

    @ForEnvironemnt("network")
    constructor():this(){
        //get LocationManager.NETWORK_PROVIDER
    }
}

@Injectable(scope=Scope.BY_NEW)
@ForEnvironment("fake")
class FakeLocation:Location
```

and then
```kotlin
val r1 = inject<Location>() //This will inject a persistent instance of RealLocation to get GPS locations
val r2 = inject<Location>("network") //This will inject a persistent instance RealLocation to get Network locations
val r2 = inject<Location>("fake") //This will inject a volatile instance of FakeLocation
```

or use it in constructor of other injectable classes:
```kotlin
constructor(real:Location, @WithEnvironment("fake") fake:Location, @WithEnvironment("network") network:Location):this(){}
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
    