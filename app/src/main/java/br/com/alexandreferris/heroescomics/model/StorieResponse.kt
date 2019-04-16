package br.com.alexandreferris.heroescomics.model

import com.squareup.moshi.Json

data class StoriesResponse (
    val code: Int,
    val status: String,
    val copyright: String,
    val attributionText: String,
    val attributionHTML: String,
    @Json(name = "etag") val eTag: String,
    val data: StoriesResponseData
)

data class StoriesResponseData (
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int,
    val results: List<StorieData>
)

data class StorieData (
    val id: Int,
    val title: String,
    val description: String,
    val resourceURI: String,
    val type: String,
    val modified: String,
    val thumbnail: Thumbnail?,
    val creators: StorieCreators,
    val characters: StorieCharacters,
    val series: StorieSeries,
    val comics: StorieComics,
    val events: StorieEvents,
    val originalIssue: StorieOriginalIssue
)

data class StorieCreators (
    val available: Int,
    val collectionURI: String,
    val items: List<StorieCreatorsItem>,
    val returned: Int
)

data class StorieCreatorsItem (
    val resourceURI: String,
    val name: String,
    val role: String
)

data class StorieCharacters (
    val available: Int,
    val collectionURI: String,
    val items: List<StorieCharactersItem>,
    val returned: Int
)

data class StorieCharactersItem (
    val resourceURI: String,
    val name: String
)

data class StorieSeries (
    val available: Int,
    val collectionURI: String,
    val items: List<StorieSerieItem>,
    val returned: Int
)

data class StorieSerieItem (
    val resourceURI: String,
    val name: String
)

data class StorieComics (
    val available: Int,
    val collectionURI: String,
    val items: List<StorieComicsItem>,
    val returned: Int
)

data class StorieComicsItem (
    val resourceURI: String,
    val name: String
)

data class StorieEvents (
    val available: Int,
    val collectionURI: String,
    val items: List<StorieEventsItem>,
    val returned: Int
)

data class StorieEventsItem (
    val resourceURI: String,
    val name: String
)

data class StorieOriginalIssue (
    val resourceURI: String,
    val name: String
)