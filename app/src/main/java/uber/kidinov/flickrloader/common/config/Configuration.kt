package uber.kidinov.flickrloader.common.config

private const val API_KEY = "3e7cc266ae2b0e0d78e279ce8e361736"
private const val PER_PAGE = "21"

class Configuration {
    val baseUrl = "https://api.flickr.com/services/rest/?method=flickr.photos.search&" +
            "api_key=$API_KEY&" +
            "format=json&nojsoncallback=1&" +
            "safe_search=1" +
            "per_page=$PER_PAGE"

    val pictureUrlTemplate = "http://farm%d.static.flickr.com/%s/%s_%s.jpg"
}