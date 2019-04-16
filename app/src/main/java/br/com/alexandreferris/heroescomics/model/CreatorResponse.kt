package br.com.alexandreferris.heroescomics.model

import com.squareup.moshi.Json

data class CreatorsResponse (
    val code: Int,
    val status: String,
    val copyright: String,
    val attributionText: String,
    val attributionHTML: String,
    @Json(name = "etag") val eTag: String,
    val data: CreatorsResponseData
)

data class CreatorsResponseData (
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int,
    val results: List<CreatorData>
)

data class CreatorData (
    val id: Int,
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val suffix: String,
    val modified: String,
    val thumbnail: Thumbnail?,
    val resourceURI: String,
    val comics: CreatorComics,
    val series: CreatorSeries,
    val stories: CreatorStories,
    val events: CreatorEvents,
    val urls: List<CreatorURL>
)

data class CreatorComics (
    val available: Int,
    val collectionURI: String,
    val items: List<CreatorComicData>,
    val returned: Int
)

data class CreatorComicData (
    val resourceURI: String,
    val name: String
)

data class CreatorSeries (
    val available: Int,
    val collectionURI: String,
    val items: List<CreatorSerieData>,
    val returned: Int
)

data class CreatorSerieData (
    val resourceURI: String,
    val name: String
)

data class CreatorStories (
    val available: Int,
    val collectionURI: String,
    val items: List<CreatorStorieData>,
    val returned: Int
)

data class CreatorStorieData (
    val resourceURI: String,
    val name: String
)

data class CreatorEvents (
    val available: Int,
    val collectionURI: String,
    val items: List<CreatorEventData>,
    val returned: Int
)

data class CreatorEventData (
    val resourceURI: String,
    val name: String
)

data class CreatorURL (
    val type: String,
    val url: String
)