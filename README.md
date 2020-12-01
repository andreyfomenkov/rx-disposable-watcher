## RxDisposableWatcher: monitoring alive subscriptions in RxJava projects
## The Purpose:
It's always possible to forget about releasing a resource, e.g.:
```kotlin
val subject = BehaviorSubject.create<State>()
// ...
subject.subscribe {} // We'd never used `Disposable` return result to stop receiving items
```
In this example we subscribed to RxJava subject but never destroyed the subscription afterwards.

**Sometimes it can break application logic or even cause a memory leak! 💩**

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
// The `result` contains info about subscriptions: stacktrace, type and number of entries
val result = RxDisposableWatcher.probe()
```
Add a step for generating a report:
```kotlin
val result = RxDisposableWatcher.probe()
val report = HtmlReportBuilder(result).build() // Code of the generated HMTL report
```
Write the report into Android external storage:
```kotlin
val report = ...
val file = File(context.getExternalFilesDir(null), "report.html") // Specify filename
val stream = FileOutputStream(file)
stream.use {
  it.write(report.toByteArray())
}
```

### Display HTML report
Let's pull a file from Android device and take a look:
```shell
adb pull /sdcard/report.html ~/report.html # Change path / filename on your own
open ~/report.html # for Mac
# or
google-chrome ~/report.html # for Linux
```

### Displaying HTML report (LIKE A BOSS 😎)
**I want to have a magic button in Android Studio. By clicking display HTML report on my desktop.**

It's always possible to do it using [External Tools](https://www.jetbrains.com/help/idea/settings-tools-external-tools.html).
