package br.com.alexandreferris.heroescomics.model

import com.squareup.moshi.Json

data class ComicsResponse (
    val code: Int,
    val status: String,
    val copyright: String,
    val attributionText: String,
    val attributionHTML: String,
    @Json(name = "etag") val eTag: String,
    val data: ComicsResponseData
)

data class ComicsResponseData (
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int,
    val results: List<ComicData>
)

data class ComicData (
    val id: Int,
    val digitalId: Int,
    val title: String,
    val issueNumber: Int,
    val variantDescription: String,
    val description: String,
    val modified: String,
    val isbn: String,
    val upc: String,
    val diamondCode: String,
    val ean: String,
    val issn: String,
    val format: String,
    val pageCount: Int,
    val textObjects: List<ComicTextObject>,
    val resourceURI: String,
    val urls: List<ComicURL>,
    val series: ComicSeries,
    val dates: List<ComicDates>,
    val prices: List<ComicPrices>,
    val thumbnail: Thumbnail?,
    val images: List<ComicImages>,
    val creators: ComicCreators,
    val characters: ComicCharacters,
    val stories: ComicStories,
    val events: ComicEvents
)

data class ComicTextObject (
    val type: String,
    val language: String,
    val text: String
)

data class ComicURL(
    val type: String,
    val url: String
)

data class ComicSeries(
    val resourceURI: String,
    val name: String
)

data class ComicDates (
    val type: String,
    val date: String
)

data class ComicPrices (
    val type: String,
    val price: Double
)

data class ComicImages (
    val path: String,
    val extension: String
)

data class ComicCreators (
    val available: Int,
    val collectionURI: String,
    val items: List<ComicCreatorsItem>,
    val returned: Int
)

data class ComicCreatorsItem (
    val resourceURI: String,
    val name: String,
    val role: String
)

data class ComicCharacters (
    val available: Int,
    val collectionURI: String,
    val items: List<ComicCharactersItem>,
    val returned: Int
)

data class ComicCharactersItem (
    val resourceURI: String,
    val name: String
)

data class ComicStories (
    val available: Int,
    val collectionURI: String,
    val items: List<ComicStoriesItem>,
    val returned: Int
)

data class ComicStoriesItem (
    val resourceURI: String,
    val name: String,
    val type: String
)

data class ComicEvents (
    val available: Int,
    val collectionURI: String,
    val items: List<ComicEventsItem>,
    val returned: Int
)

data class ComicEventsItem (
    val resourceURI: String,
    val name: String
)