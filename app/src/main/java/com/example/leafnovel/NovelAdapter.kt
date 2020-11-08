import com.google.gson.annotations.SerializedName

class NovelResult:java.util.ArrayList<Novel>()
data class Novel(
    @SerializedName("breadcrumbUrl")
    val breadcrumbUrl: BreadcrumbUrl,
    @SerializedName("cacheUrl")
    val cacheUrl: String,
    @SerializedName("clicktrackUrl")
    val clicktrackUrl: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("contentNoFormatting")
    val contentNoFormatting: String,
    @SerializedName("formattedUrl")
    val formattedUrl: String,
    @SerializedName("richSnippet")
    val richSnippet: RichSnippet,
    @SerializedName("title")
    val title: String,
    @SerializedName("titleNoFormatting")
    val titleNoFormatting: String,
    @SerializedName("unescapedUrl")
    val unescapedUrl: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("visibleUrl")
    val visibleUrl: String
)

data class BreadcrumbUrl(
    val crumbs: List<String>,
    val host: String
)

data class RichSnippet(
    val cseImage: CseImage,
    val cseThumbnail: CseThumbnail,
    val metatags: Metatags
)

data class CseImage(
    val src: String
)

data class CseThumbnail(
    val height: String,
    val src: String,
    val width: String
)

data class Metatags(
    val appleMobileWebAppCapable: String,
    val appleTouchFullscreen: String,
    val ogDescription: String,
    val ogImage: String,
    val ogSiteName: String,
    val ogTitle: String,
    val ogUrl: String,
    val viewport: String
)