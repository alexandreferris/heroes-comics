package br.com.alexandreferris.heroescomics.model

import com.squareup.moshi.Json

data class SeriesResponse (
    val code: Int,
    val status: String,
    val copyright: String,
    val attributionText: String,
    val attributionHTML: String,
    @Json(name = "etag") val eTag: String,
    val data: SeriesResponseData
)

data class SeriesResponseData (
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int,
    val results: List<SerieData>
)

data class SerieData (
    val id: Int,
    val title: String,
    val description: String,
    val resourceURI: String,
    val urls: List<SerieURL>,
    val startYear: Int,
    val endYear: Int,
    val type: String,
    val modified: String,
    val thumbnail: Thumbnail?,
    val creators: SerieCreators,
    val characters: SerieCharacters,
    val stories: SerieStories,
    val comics: SerieComics,
    val events: SerieEvents
)

data class SerieURL (
    val type: String,
    val url: String
)

data class SerieCreators (
    val available: Int,
    val collectionURI: String,
    val items: List<SerieCreatorsItem>,
    val returned: Int
)

data class SerieCreatorsItem (
    val resourceURI: String,
    val name: String,
    val role: String
)

data class SerieCharacters (
    val available: Int,
    val collectionURI: String,
    val items: List<SerieCharactersItem>,
    val returned: Int
)

data class SerieCharactersItem (
    val resourceURI: String,
    val name: String
)

data class SerieStories (
    val available: Int,
    val collectionURI: String,
    val items: List<SerieStoriesItem>,
    val returned: Int
)

data class SerieStoriesItem (
    val resourceURI: String,
    val name: String,
    val type: String
)

data class SerieComics (
    val available: Int,
    val collectionURI: String,
    val items: List<SerieComicsItem>,
    val returned: Int
)

data class SerieComicsItem (
    val resourceURI: String,
    val name: String
)

data class SerieEvents (
    val available: Int,
    val collectionURI: String,
    val items: List<SerieEventsItem>,
    val returned: Int
)

data class SerieEventsItem (
    val resourceURI: String,
    val name: String
)