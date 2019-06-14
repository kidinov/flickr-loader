# Flick Image Feed

**[Apk File](../master/assets/app-debug.apk)**

### Description
App follows standard Model View Presenter architectural pattern. Some kind of mix DI and service locator is used to provide dependencies.
Due to restriction on 3rd party libraries usage `GridView` is used for list rendering. For HTTP calls `HttpURLConnection` is used.
In order to perform background operations `ExecutorService` utilized. In order to report back to UI thread, I post messages via `Handler` with `main thread looper.
In the app implemented 2 levels of caches: 
* Memory cache. Implemented via `LruCache`, which can be occupied up to 25% of the whole memory of the process.
* Disk cache. Also uses a least-recently-used strategy. It keeps files in the cache folder of the app.

In case when the image is in the first level cache, it is immediately returned. If it is not there, then I check the second level. If it is there, I insert it in the first level and return the image.
If the image is not in the cache, network request is executed. After the image is downloaded, both caches receive this image.
In order to avoid "blinking" of the images provided by threads which were spammed, I keep a reference to every running thread and cancel it if for the same view (convertView) new picture was requested
Same is done for the queue of messages I post on UI thread in order to post image.

### Shortcuts
* No Bitmap Pool usage, so sometimes GC can cause a delay. Although memory performance monitor shows that it is ok even with the current implementation.
* Rather a naive image downloading. Firstly it downloaded into byte array and then already converting it into the bitmap. For some reason `BitmapFactory::decodeStream` sometimes was producing corrupted bitmaps when a thread was interrupted.
* I share same thread pool for downloading images and making HTTP call. In order to make it more efficient, the priority queue should be used and HTTP call should have higher priority. 
* Disk cache could be improved if a measure of "recently used" will not be last modified date of the file, but some in-memory data structure with reference on file names. Because last modified apparently is precise up to 1 second on android.
* Backend model and the app model is the same. Separation often is useful. Also, I use `Serialisable` in order to save state via configuration changes. Parcelable is faster. 
* UI is very simple and doesn't follow any serious standards
* I used `jUnit`, `Mockito` and assertion library for testing. I hope this is fine. 

![](../master/assets/demo.gif)
