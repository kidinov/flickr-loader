package uber.kidinov.flickrloader.common.config

private const val API_KEY = "3e7cc266ae2b0e0d78e279ce8e361736"
private const val PER_PAGE = "21"

object Configuration {
    const val BASE_URL = "https://api.flickr.com/services/rest/?method=flickr.photos.search&" +
            "api_key=$API_KEY&" +
            "format=json&nojsoncallback=1&" +
            "safe_search=1" +
            "per_page=$PER_PAGE"

    const val PICTURE_URL_TEMPLATE = "https://farm%d.static.flickr.com/%s/%s_%s.jpg"

    const val DISK_CACHE_FILES_AMOUNT = 300
}