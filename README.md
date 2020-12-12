## RxDisposableWatcher — find leaked subscriptions in RxJava code 🐞
### The Problem
Consider the following RxJava code:
```kotlin
val subject = BehaviorSubject.create<State>()
// ...
subject.subscribe { /* ... */ } // Subscribed, but not disposed afterwards!
```
We subscribed to `BehaviorSubject` but never released a [Disposable](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/disposables/Disposable.html) resource later. **As a result it can break application logic or even cause a memory leak! 💩**

Use _RxDisposableWatcher_ plugin to find all undestroyed subscriptions & build the detailed HTML report:
<p align="center">
  <img src="https://github.com/andreyfomenkov/rx-disposable-watcher/blob/1.x/images/report.png" width="600">
</p>
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
[ ![Download](https://api.bintray.com/packages/andreyfomenkov/maven/rx-disposable-watcher/images/download.svg?version=2.0.0) ](https://bintray.com/andreyfomenkov/maven/rx-disposable-watcher/2.0.0/link)

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

### Display HTML report in desktop browser 🖥
Pull report file from Android device and display (replace with your paths):
```shell
adb pull /sdcard/report.html ~/report.html # Grab a report from SD card
open ~/report.html # for Mac
# or
google-chrome ~/report.html # for Linux
```
That's it!

### Displaying HTML report in one click (Like a boss) 😎
**I want a _magic button_ in Android Studio toolbar to show HTML report just in one click!**

The idea is pretty simple:
<p align="center">
  <img src="https://github.com/andreyfomenkov/rx-disposable-watcher/blob/1.x/images/magic.png" width="650">
</p>

Steps to implement (**read more details in my post on Medium**):
1. Download [report.sh](https://github.com/andreyfomenkov/rx-disposable-watcher/blob/1.x/images/magic.png) script from the repository and modify constants inside;
2. Create custom action in Android Studio using [External Tools](https://www.jetbrains.com/help/idea/settings-tools-external-tools.html) to run `report.sh`;
3. Register custom [BroadcastReceiver](https://developer.android.com/reference/android/content/BroadcastReceiver) in your app to handle special event, then build & save HTML report;
4. Add new button to Android Studio toolbar and bind it with custom action from step 2.

🔥 The post on Medium: [Find Leaked Subscriptions in RxJava code with RxDisposableWatcher](https://medium.com/p/8c2226dce01c/edit) 🔥

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
