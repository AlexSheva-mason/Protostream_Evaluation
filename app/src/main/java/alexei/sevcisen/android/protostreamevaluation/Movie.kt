package alexei.sevcisen.android.protostreamevaluation

data class Movie(
    val id: String,
    val title: String,
    val description: String,
    val duration: String,
    val releaseDate: String,
    val images: List<ImageUrl>
)

data class ImageUrl(
    val url: String,
    val type: String
)
