## RxDisposableWatcher — find leaked subscriptions in RxJava code 🐞
### The Problem
Consider the following RxJava code:
```kotlin
val subject = BehaviorSubject.create<State>()
// ...
subject.subscribe { /* ... */ } // Subscribed, but not disposed afterwards!
```
We subscribed to `BehaviorSubject` but never released a [Disposable](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/disposables/Disposable.html) resource later. **As a result it can break application logic or even cause a memory leak! 💩**

Use _RxDisposableWatcher_ plugin to find all undestroyed subscriptions _at the moment_ & build the detailed HTML report:
![](https://github.com/andreyfomenkov/rx-disposable-watcher/blob/1.x/images/report.png)
<p align="center">
  <span>Everything we need: </span>
  <b>stack trace, number of calls & Observable types.</b>
</p>

## Getting started
### Download
Include library dependency into your Gradle project:
```groovy
implementation 'ru.fomenkov:rx-disposable-watcher:x.y.z'
```
Please replace `x.y.z` with the latest version numbers:
- for [RxJava 2.x](https://github.com/ReactiveX/RxJava/tree/2.x) projects:
[ ![Download](https://api.bintray.com/packages/andreyfomenkov/maven/rx-disposable-watcher/images/download.svg?version=1.0.0) ](https://bintray.com/andreyfomenkov/maven/rx-disposable-watcher/1.0.0/link)
- for [RxJava 3.x](https://github.com/ReactiveX/RxJava/tree/3.x) projects:
[ ![Download](https://api.bintray.com/packages/andreyfomenkov/maven/rx-disposable-watcher/images/download.svg?version=1.0.0) ](https://bintray.com/andreyfomenkov/maven/rx-disposable-watcher/1.0.0/link)

### Initialization
```kotlin
RxDisposableWatcher.init()
```
For Android application add storage permission into `AndroidManifest.xml` to save & pull generated HTML report:
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

### Make snapshot & generate HTML report 📋
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

### Display HTML report on desktop 🖥
Pull report file from Android device and display in a browser:
```shell
adb pull /sdcard/report.html ~/report.html # Grab a report from SD card
open ~/report.html # for Mac
# or
google-chrome ~/report.html # for Linux
```
See [report.sh](https://github.com/andreyfomenkov/rx-disposable-watcher/blob/1.x/report.sh) in the repository.
That's it!

### Displaying HTML report on desktop (Like a boss 😎)
**I want a MAGIC BUTTON in Android Studio toolbar to show HTML report in one click!**

It's possible to do run shell script using [External Tools](https://www.jetbrains.com/help/idea/settings-tools-external-tools.html).
