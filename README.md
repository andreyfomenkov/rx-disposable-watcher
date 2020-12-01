## RxDisposableWatcher: monitoring leaked subscriptions in RxJava projects
A library for monitoring leaked [Disposable](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/disposables/Disposable.html) subscriptions in RxJava projects and building comprehensive HTML reports.

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
In order to save and then pull HTML report for Android application, add the necessary permission into `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

### Make snapshot & generate HTML report
Check whether you have alive Rx subscriptions at the moment:
```kotlin
// The `list` contains info about subscriptions: stacktrace, type and number of entries
val list = RxDisposableWatcher.probe()
```
