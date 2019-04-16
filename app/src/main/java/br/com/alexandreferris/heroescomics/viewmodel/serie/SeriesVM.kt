package br.com.alexandreferris.heroescomics.viewmodel.serie

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import br.com.alexandreferris.heroescomics.data.remote.MarvelAPIService
import br.com.alexandreferris.heroescomics.model.SerieData
import br.com.alexandreferris.heroescomics.model.SeriesResponse
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.disposables.CompositeDisposable
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils

open class SeriesVM @Inject constructor(private val networkApi: MarvelAPIService): ViewModel() {

    var extraType: String = StringUtils.EMPTY
    var extraId: Int = NumberUtils.INTEGER_ZERO

    private var disposable: CompositeDisposable = CompositeDisposable()

    private val listSeries: ArrayList<SerieData> = ArrayList()
    private val series: MutableLiveData<List<SerieData>> = MutableLiveData()
    private val seriesLoadError = MutableLiveData<Boolean>()
    private val loading = MutableLiveData<Boolean>()

    val limit: Int = 20
    val startingPage: Int = NumberUtils.INTEGER_ONE
    var currentPage: Int = NumberUtils.INTEGER_ONE
    var visibleThreshold: Int = NumberUtils.INTEGER_ZERO

    fun getSeries(): MutableLiveData<List<SerieData>> {
        return series
    }

    fun getError(): LiveData<Boolean> {
        return seriesLoadError
    }

    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun fetchSeries(offset: Int = NumberUtils.INTEGER_ZERO, paramLimit: Int = limit) {
        loading.value = true
        disposable.add(networkApi.getSeriesFromExtra(extraType, extraId, offset, paramLimit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<SeriesResponse>() {
                override fun onError(e: Throwable) {
                    seriesLoadError.value = true
                    loading.value = false
                }

                override fun onSuccess(seriesResponse: SeriesResponse) {
                    seriesLoadError.value = false
                    loading.value = false

                    listSeries.addAll(seriesResponse.data.results)
                    visibleThreshold = listSeries.size / currentPage

                    series.value = listSeries
                }
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}