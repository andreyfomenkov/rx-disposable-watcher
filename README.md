## RxDisposableWatcher — find leaked subscriptions in RxJava code 🐞
### The Problem
Consider the following RxJava code:
```kotlin
class Thermometer {
  fun observeTemperature(): Observable<Int>
}
// ...
val thermometer = Thermometer.getInstance()
// ...
thermometer
  .observeTemperature()
  .subscribe { /* ... */ } // Subscribed, but not disposed afterwards!
```
We subscribed to `Thermometer` instance but never released a [Disposable](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/disposables/Disposable.html) resource later. **As a result it can blow up an application logic or even cause a memory leak! 💩**

🔥 Read my post on Medium: [Detect Leaked Subscriptions in RxJava code with RxDisposableWatcher](https://andrey-fomenkov.medium.com/find-leaked-subscriptions-in-rxjava-code-using-rxdisposablewatcher-8c2226dce01c) 🔥

Use RxDisposableWatcher plugin to find all undestroyed subscriptions & build the detailed HTML report:
<p align="center">
  <img src="https://github.com/andreyfomenkov/rx-disposable-watcher/blob/1.x/images/report.png" width="600">
</p>
<p align="center">
  <span>Everything we need: </span>
  <b>stack trace, number of calls & Observable types.</b>
</p>

## Getting started
### Setup
Gradle:
```groovy
repositories {
    jcenter()
}

implementation 'ru.fomenkov:rx-disposable-watcher:x.y.z'
```
Maven:
```xml
<dependency>
  <groupId>ru.fomenkov</groupId>
  <artifactId>rx-disposable-watcher</artifactId>
  <version>x.y.z</version>
  <type>pom</type>
</dependency>
```
Please replace `x.y.z` with the latest version numbers:
- for [RxJava 2](https://github.com/ReactiveX/RxJava/tree/2.x) projects:
[ ![Download](https://api.bintray.com/packages/andreyfomenkov/maven/rx-disposable-watcher/images/download.svg?version=1.0.0) ](https://bintray.com/andreyfomenkov/maven/rx-disposable-watcher/1.0.0/link)
- for [RxJava 3](https://github.com/ReactiveX/RxJava/tree/3.x) projects:
[ ![Download](https://api.bintray.com/packages/andreyfomenkov/maven/rx-disposable-watcher/images/download.svg) ](https://bintray.com/andreyfomenkov/maven/rx-disposable-watcher/_latestVersion)

### Initialization
```kotlin
RxDisposableWatcher.init()
```
For Android application add storage permission into `AndroidManifest.xml` to save & pull generated HTML report:
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
⚠️ **Note:** in case you're facing with `IllegalStateException: Plugins can't be changed anymore`, then another application component tries to use [RxJavaPlugins](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/plugins/RxJavaPlugins.html) utility class with exclusive access. Disable this component when working with the plugin.

### Make snapshot & generate HTML report
Now you're ready to go! Check whether you have alive Rx subscriptions at the moment:
```kotlin
val result = RxDisposableWatcher.probe() // Collect info: stacktrace, number of calls, type
val report = HtmlReportBuilder(result).build() // Generate HTML report
```
For Android save the report to SD card:
```kotlin
val report = ...
val file = File(context.getExternalFilesDir(null), "report.html") // Specify filename
val stream = FileOutputStream(file)
stream.use { it.write(report.toByteArray()) }
```

### Display HTML report in a desktop browser
Pull report file from Android device and display (replace with your paths):
```shell
adb pull /sdcard/report.html ~/report.html # Grab a report from SD card
open ~/report.html # for Mac
# or
google-chrome ~/report.html # for Linux
```
That's it!

### Displaying HTML report in one click (Like a boss) 😎
**I want a _magic button_ in Android Studio toolbar to show HTML report in one click!**

The idea is pretty simple:
<p align="center">
  <img src="https://github.com/andreyfomenkov/rx-disposable-watcher/blob/1.x/images/magic.png" width="650">
</p>

As a lazy developer I prefer the described approach, because it dramatically saves my time!

How to add this button? 👉 Read the dedicated section in my post: [Get a report in one click from Android Studio](https://andrey-fomenkov.medium.com/find-leaked-subscriptions-in-rxjava-code-using-rxdisposablewatcher-8c2226dce01c#ae2a)

### Licence
```
Copyright 2020 Andrey Fomenkov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
