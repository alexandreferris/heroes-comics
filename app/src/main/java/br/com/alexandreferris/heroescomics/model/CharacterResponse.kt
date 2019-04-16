package br.com.alexandreferris.heroescomics.model

import com.squareup.moshi.Json

data class CharactersResponse (
    val code: Int,
    val status: String,
    val copyright: String,
    val attributionText: String,
    val attributionHTML: String,
    @Json(name = "etag") val eTag: String,
    val data: CharactersResponseData
)

data class CharactersResponseData (
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int,
    val results: List<CharacterData>
)

data class CharacterData (
    val id: Int,
    val name: String,
    val description: String,
    val modified: String,
    val thumbnail: Thumbnail?,
    val resourceURI: String,
    val comics: Comics,
    val series: Series,
    val stories: Stories,
    val events: Events,
    val urls: List<CharacterURL>
)

data class Thumbnail (
    val path: String,
    val extension: String
)

data class Comics (
    val available: Int,
    val collectionURI: String,
    val items: List<ComicsItem>,
    val returned: Int
)

data class ComicsItem (
    val resourceURI: String,
    val name :String
)

data class Series (
    val available: Int,
    val collectionURI: String,
    val items: List<SeriesItem>,
    val returned: Int
)

data class SeriesItem (
    val resourceURI: String,
    val name: String
)

data class Stories (
    val available: Int,
    val collectionURI: String,
    val items: List<StoriesItem>,
    val returned: Int
)

data class StoriesItem (
    val resourceURI: String,
    val name: String,
    val type: String
)

data class Events (
    val available: Int,
    val collectionURI: String,
    val items: List<EventsItem>,
    val returned: Int
)

data class EventsItem (
    val resourceURI: String,
    val name: String
)

data class CharacterURL (
    val type: String,
    val url :String
)