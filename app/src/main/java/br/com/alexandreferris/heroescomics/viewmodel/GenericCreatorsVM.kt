package br.com.alexandreferris.heroescomics.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import br.com.alexandreferris.heroescomics.data.remote.MarvelAPIService
import br.com.alexandreferris.heroescomics.model.CreatorData
import br.com.alexandreferris.heroescomics.model.CreatorsResponse
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.disposables.CompositeDisposable
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils

open class GenericCreatorsVM @Inject constructor(private val networkApi: MarvelAPIService): ViewModel() {

    private var disposable: CompositeDisposable = CompositeDisposable()

    private val listCreators: ArrayList<CreatorData> = ArrayList()
    private val creators: MutableLiveData<List<CreatorData>> = MutableLiveData()
    private val creatorsLoadError = MutableLiveData<Boolean>()
    private val loading = MutableLiveData<Boolean>()

    var extraType: String = StringUtils.EMPTY
    var extraId: Int = NumberUtils.INTEGER_ZERO

    private val limit: Int = 20
    val startingPage: Int = NumberUtils.INTEGER_ONE
    var currentPage: Int = NumberUtils.INTEGER_ONE
    var visibleThreshold: Int = NumberUtils.INTEGER_ZERO

    fun getCreators(): MutableLiveData<List<CreatorData>> {
        return creators
    }

    fun getError(): LiveData<Boolean> {
        return creatorsLoadError
    }

    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun fetchGenericCreators(offset: Int = NumberUtils.INTEGER_ZERO, paramLimit: Int = limit) {
        loading.value = true
        disposable.add(networkApi.getGenericCreators(
            extraType,
            extraId,
            (offset * paramLimit),
            paramLimit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<CreatorsResponse>() {
                override fun onError(e: Throwable) {
                    creatorsLoadError.value = true
                    loading.value = false
                }

                override fun onSuccess(creatorsResponse: CreatorsResponse) {
                    creatorsLoadError.value = false
                    loading.value = false

                    listCreators.addAll(creatorsResponse.data.results)
                    visibleThreshold = listCreators.size / currentPage

                    creators.value = listCreators
                }
            })
        )
    }

    fun reset() {
        listCreators.clear()
        currentPage = startingPage
        creators.value = listCreators
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}