package br.com.alexandreferris.heroescomics.di

import br.com.alexandreferris.heroescomics.data.remote.MarvelAPIService
import br.com.alexandreferris.heroescomics.utils.constants.Credentials
import br.com.alexandreferris.heroescomics.utils.md5
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {
    companion object {
        private fun getOkHttpClient(): OkHttpClient {
            val client = OkHttpClient.Builder()
                .addInterceptor(CustomInterceptor())
                .build()

            return client
        }
    }

    @Provides
    @Singleton
    fun provideHttpClient() = getOkHttpClient()

    @Provides
    @Singleton
    fun provideRetrofit(): MarvelAPIService {

        return Retrofit.Builder()
            .baseUrl(Credentials.BASE_URL)
            .client(getOkHttpClient()) // OkHttpClient.Builder().build())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(MarvelAPIService::class.java)
    }
}

class CustomInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val timestamp = System.currentTimeMillis()
        val md5Hash = ("" + timestamp + "" + Credentials.API_KEY_PRIVATE + "" + Credentials.API_KEY_PUBLIC).md5()

        val url = chain.request().url().newBuilder()
            .addQueryParameter("ts", String.format("%s", timestamp))
            .addQueryParameter("apikey", Credentials.API_KEY_PUBLIC)
            .addQueryParameter("hash", md5Hash)
            .build()
        val request = chain.request().newBuilder()
            // .addHeader("Authorization", "Bearer token")
            .url(url)
            .build()
        return chain.proceed(request)
    }
}
