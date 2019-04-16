package br.com.alexandreferris.heroescomics.data.remote

import br.com.alexandreferris.heroescomics.model.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarvelAPIService {

    @GET("{extraType}/{extraId}/characters")
    fun getGenericCharacters(
        @Path("extraType") extraType: String,
        @Path("extraId") extraId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int): Single<CharactersResponse>

    @GET("{extraType}/{extraId}/creators")
    fun getGenericCreators(
        @Path("extraType") extraType: String,
        @Path("extraId") extraId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int): Single<CreatorsResponse>

    @GET("creators/{creatorId}")
    fun getCreator(
        @Path("creatorId") creatorId: Int): Single<CreatorsResponse>

    @GET("characters?orderBy=modified")
    fun getCharacters(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("nameStartsWith") nameSearch: String?): Single<CharactersResponse>

    @GET("characters/{characterId}")
    fun getCharacter(
        @Path("characterId") characterId: Int): Single<CharactersResponse>

    @GET("{extraType}/{characterId}/comics")
    fun getComicsFromExtra(
        @Path("extraType") extraType: String,
        @Path("characterId") characterId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int): Single<ComicsResponse>

    @GET("comics/{comicId}")
    fun getComic(
        @Path("comicId") comicId: Int): Single<ComicsResponse>

    @GET("{extraType}/{characterId}/series")
    fun getSeriesFromExtra(
        @Path("extraType") extraType: String,
        @Path("characterId") characterId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int): Single<SeriesResponse>

    @GET("series/{serieId}")
    fun getSerie(
        @Path("serieId") serieId: Int): Single<SeriesResponse>

    @GET("{extraType}/{characterId}/stories")
    fun getStoriesFromExtra(
        @Path("extraType") extraType: String,
        @Path("characterId") characterId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int): Single<StoriesResponse>

    @GET("stories/{storieId}")
    fun getStorie(
        @Path("storieId") storieId: Int): Single<StoriesResponse>

    @GET("{extraType}/{characterId}/events")
    fun getEventsFromExtra(
        @Path("extraType") extraType: String,
        @Path("characterId") characterId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int): Single<EventsResponse>

    @GET("events/{eventId}")
    fun getEvent(
        @Path("eventId") eventId: Int): Single<EventsResponse>
}