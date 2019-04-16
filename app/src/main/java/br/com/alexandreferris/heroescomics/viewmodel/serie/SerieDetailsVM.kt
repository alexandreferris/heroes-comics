package br.com.alexandreferris.heroescomics.viewmodel.serie

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import br.com.alexandreferris.heroescomics.data.remote.MarvelAPIService
import br.com.alexandreferris.heroescomics.model.SerieData
import br.com.alexandreferris.heroescomics.model.SeriesResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

open class SerieDetailsVM @Inject constructor(private val networkApi: MarvelAPIService): ViewModel() {

    private var disposable: CompositeDisposable = CompositeDisposable()

    private val serie: MutableLiveData<SerieData> = MutableLiveData()
    private val serieLoadError = MutableLiveData<Boolean>()
    private val loading = MutableLiveData<Boolean>()

    fun getSerie(): MutableLiveData<SerieData> {
        return serie
    }

    fun getError(): LiveData<Boolean> {
        return serieLoadError
    }

    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun fetchSerie(serieId: Int) {
        loading.value = true
        disposable.add(
            networkApi.getSerie(serieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<SeriesResponse>() {
                    override fun onError(e: Throwable) {
                        serieLoadError.value = true
                        loading.value = false
                    }

                    override fun onSuccess(seriesResponse: SeriesResponse) {
                        serieLoadError.value = false
                        loading.value = false

                        if (seriesResponse.data.results.size > NumberUtils.INTEGER_ZERO)
                            serie.value = seriesResponse.data.results[NumberUtils.INTEGER_ZERO]
                        else
                            serie.value = null
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

