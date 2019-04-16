package br.com.alexandreferris.heroescomics.model

import com.squareup.moshi.Json

data class EventsResponse (
    val code: Int,
    val status: String,
    val copyright: String,
    val attributionText: String,
    val attributionHTML: String,
    @Json(name = "etag") val eTag: String,
    val data: EventsResponseData
)

data class EventsResponseData (
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int,
    val results: List<EventData>
)

data class EventData (
    val id: Int,
    val title: String,
    val description: String,
    val resourceURI: String,
    val urls: List<EventURL>,
    val modified: String,
    val start: String,
    val end: String,
    val thumbnail: Thumbnail?,

    val creators: EventCreators,
    val characters: EventCharacters,
    val comics: EventComics,
    val series: EventSeries,
    val stories: EventStories,
    val next: EventNext,
    val previous: EventPrevious
)

data class EventURL (
    val type: String,
    val url: String
)

data class EventCreators (
    val available: Int,
    val collectionURI: String,
    val items: List<EventCreatorsItem>,
    val returned: Int
)

data class EventCreatorsItem (
    val resourceURI: String,
    val name: String,
    val role: String
)

data class EventCharacters (
    val available: Int,
    val collectionURI: String,
    val items: List<EventCharactersItem>,
    val returned: Int
)

data class EventCharactersItem (
    val resourceURI: String,
    val name: String
)

data class EventSeries (
    val available: Int,
    val collectionURI: String,
    val items: List<EventSerieItem>,
    val returned: Int
)

data class EventSerieItem (
    val resourceURI: String,
    val name: String
)

data class EventComics (
    val available: Int,
    val collectionURI: String,
    val items: List<EventComicsItem>,
    val returned: Int
)

data class EventComicsItem (
    val resourceURI: String,
    val name: String
)

data class EventStories (
    val available: Int,
    val collectionURI: String,
    val items: List<EventStoriesItem>,
    val returned: Int
)

data class EventStoriesItem (
    val resourceURI: String,
    val name: String
)

data class EventNext (
    val resourceURI: String,
    val name: String
)

data class EventPrevious (
    val resourceURI: String,
    val name: String
)