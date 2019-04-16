package br.com.alexandreferris.heroescomics.viewmodel.storie

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import br.com.alexandreferris.heroescomics.data.remote.MarvelAPIService
import br.com.alexandreferris.heroescomics.model.StorieData
import br.com.alexandreferris.heroescomics.model.StoriesResponse
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.disposables.CompositeDisposable
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils

open class StoriesVM @Inject constructor(private val networkApi: MarvelAPIService): ViewModel() {

    var extraType: String = StringUtils.EMPTY
    var extraId: Int = NumberUtils.INTEGER_ZERO

    private var disposable: CompositeDisposable = CompositeDisposable()

    private val listStories: ArrayList<StorieData> = ArrayList()
    private val stories: MutableLiveData<List<StorieData>> = MutableLiveData()
    private val storiesLoadError = MutableLiveData<Boolean>()
    private val loading = MutableLiveData<Boolean>()

    val limit: Int = 20
    val startingPage: Int = NumberUtils.INTEGER_ONE
    var currentPage: Int = NumberUtils.INTEGER_ONE
    var visibleThreshold: Int = NumberUtils.INTEGER_ZERO

    fun getStories(): MutableLiveData<List<StorieData>> {
        return stories
    }

    fun getError(): LiveData<Boolean> {
        return storiesLoadError
    }

    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun fetchStories(offset: Int = NumberUtils.INTEGER_ZERO, paramLimit: Int = limit) {
        loading.value = true
        disposable.add(networkApi.getStoriesFromExtra(extraType, extraId, offset, paramLimit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<StoriesResponse>() {
                override fun onError(e: Throwable) {
                    storiesLoadError.value = true
                    loading.value = false
                }

                override fun onSuccess(storiesResponse: StoriesResponse) {
                    storiesLoadError.value = false
                    loading.value = false

                    listStories.addAll(storiesResponse.data.results)
                    visibleThreshold = listStories.size / currentPage

                    stories.value = listStories
                }
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}