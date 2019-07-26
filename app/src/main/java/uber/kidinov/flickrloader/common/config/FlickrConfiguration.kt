package uber.kidinov.flickrloader.common.config

const val SCROLLING_THRESHOLD = 15

const val PER_PAGE = "42"

interface Configuration {
    val baseUrl: String
}

class FlickrConfiguration : Configuration {
    private val API_KEY = "3e7cc266ae2b0e0d78e279ce8e361736"

    override val baseUrl =
        "https://api.flickr.com/services/rest/?method=flickr.photos.search&" +
                "api_key=$API_KEY&" +
                "format=json&nojsoncallback=1&" +
                "safe_search=1&" +
                "per_page=$PER_PAGE&" +
                "media=photos"
}

class GiphyConfigration : Configuration {
    private val API_KEY = "32rweBKqUGkDEfVaVpb6F2U5KFFPSf8e"
    override val baseUrl: String
        get() = "https://api.giphy.com/v1/gifs/search?" +
                "api_key=$API_KEY&" +
                "limit=$PER_PAGE&"
}