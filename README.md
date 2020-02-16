# file-import
A Java utility for importing data files.

# Notice on using
This library depends on `org.springframework:spring-core`.

The dependency is of scope "provided", so you have to provide spring-core at runtime, and you can provide  spring-core of any version as long as it is compatible with this library.

`java.util.logging` is used for logging in order to reduce external library dependencies. You may setup `java.util.logging` or provide an adapter to another logging library to enable logging inside this library.

# How to build
There are 4 profiles in pom.xml corresponding to various version of `spring-core`:
* 3.2.2
* 3.2.2
* 5.0.9 (corresponding to Spring Boot 2.0.5)
* 5.2.0 (corresponding to Spring Boot 2.2.0)

You can choose any one of the above profile to build this package.
