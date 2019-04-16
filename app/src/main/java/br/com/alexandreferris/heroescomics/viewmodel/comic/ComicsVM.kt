package br.com.alexandreferris.heroescomics.viewmodel.comic

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import br.com.alexandreferris.heroescomics.data.remote.MarvelAPIService
import br.com.alexandreferris.heroescomics.model.ComicData
import br.com.alexandreferris.heroescomics.model.ComicsResponse
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.disposables.CompositeDisposable
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils

open class ComicsVM @Inject constructor(private val networkApi: MarvelAPIService): ViewModel() {

    var extraType: String = StringUtils.EMPTY
    var extraId: Int = NumberUtils.INTEGER_ZERO

    private var disposable: CompositeDisposable = CompositeDisposable()

    private val listComics: ArrayList<ComicData> = ArrayList()
    private val comics: MutableLiveData<List<ComicData>> = MutableLiveData()
    private val comicsLoadError = MutableLiveData<Boolean>()
    private val loading = MutableLiveData<Boolean>()

    val limit: Int = 20
    val startingPage: Int = NumberUtils.INTEGER_ONE
    var currentPage: Int = NumberUtils.INTEGER_ONE
    var visibleThreshold: Int = NumberUtils.INTEGER_ZERO

    fun getComics(): MutableLiveData<List<ComicData>> {
        return comics
    }

    fun getError(): LiveData<Boolean> {
        return comicsLoadError
    }

    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun fetchComics(offset: Int = NumberUtils.INTEGER_ZERO, paramLimit: Int = limit) {
        loading.value = true

        disposable.add(networkApi.getComicsFromExtra(extraType, extraId, offset, paramLimit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<ComicsResponse>() {
                override fun onError(e: Throwable) {
                    comicsLoadError.value = true
                    loading.value = false
                }

                override fun onSuccess(comicsResponse: ComicsResponse) {
                    comicsLoadError.value = false
                    loading.value = false

                    listComics.addAll(comicsResponse.data.results)
                    visibleThreshold = listComics.size / currentPage

                    comics.value = listComics
                }
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}