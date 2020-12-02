## RxDisposableWatcher — monitoring alive subscriptions in RxJava projects
### The Problem:
Let's imagine the following situation with RxJava:
```kotlin
val subject = BehaviorSubject.create<State>()
// ...
subject.subscribe { /* Do something */ } // We didn't call dispose() to stop receiving items from `subject`
```
We subscribed to `BehaviorSubject` but never released a [Disposable](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/disposables/Disposable.html) resource afterwards. **As a result it can break application logic or even cause a memory leak! 💩**

With _RxDisposableWatcher_ it's possible to catch and analyze all undestroyed subscriptions in your application **_at the moment_**:

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
In order to save and then pull HTML report for Android application, add the necessary external storage permission into `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

### Make snapshot & generate HTML report
Now you're ready to go! Check whether you have alive Rx subscriptions at the moment:
```kotlin
val result = RxDisposableWatcher.probe() // Exhaustive info: stacktrace, number of calls, etc.
val report = HtmlReportBuilder(result).build() // Generate HTML report
```
Save the report to Android external storage:
```kotlin
val report = ...
val file = File(context.getExternalFilesDir(null), "report.html") // Specify filename
val stream = FileOutputStream(file)
stream.use { it.write(report.toByteArray()) }
```

### Display HTML report
Let's pull a file from Android device and take a look:
```shell
adb pull /sdcard/report.html ~/report.html # Grab a report from Android device
# Then display in browser
open ~/report.html # for Mac
# or
google-chrome ~/report.html # for Linux
```

### Displaying HTML report (LIKE A BOSS 😎)
**I want to have a magic button in Android Studio. By clicking display HTML report on my desktop.**

It's always possible to do it using [External Tools](https://www.jetbrains.com/help/idea/settings-tools-external-tools.html).
