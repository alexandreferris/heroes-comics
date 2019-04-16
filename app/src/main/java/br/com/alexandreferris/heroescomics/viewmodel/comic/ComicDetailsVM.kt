package br.com.alexandreferris.heroescomics.viewmodel.comic

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import br.com.alexandreferris.heroescomics.data.remote.MarvelAPIService
import br.com.alexandreferris.heroescomics.model.ComicData
import br.com.alexandreferris.heroescomics.model.ComicsResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

open class ComicDetailsVM @Inject constructor(private val networkApi: MarvelAPIService): ViewModel() {

    private var disposable: CompositeDisposable = CompositeDisposable()

    private val comic: MutableLiveData<ComicData> = MutableLiveData()
    private val comicLoadError = MutableLiveData<Boolean>()
    private val loading = MutableLiveData<Boolean>()

    fun getComic(): MutableLiveData<ComicData> {
        return comic
    }

    fun getError(): LiveData<Boolean> {
        return comicLoadError
    }

    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun fetchComic(comicId: Int) {
        loading.value = true
        disposable.add(
            networkApi.getComic(comicId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ComicsResponse>() {
                    override fun onError(e: Throwable) {
                        comicLoadError.value = true
                        loading.value = false
                    }

                    override fun onSuccess(comicsResponse: ComicsResponse) {
                        comicLoadError.value = false
                        loading.value = false

                        if (comicsResponse.data.results.size > NumberUtils.INTEGER_ZERO)
                            comic.value = comicsResponse.data.results[NumberUtils.INTEGER_ZERO]
                        else
                            comic.value = null
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

