package br.com.alexandreferris.heroescomics.viewmodel.creator

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import br.com.alexandreferris.heroescomics.data.remote.MarvelAPIService
import br.com.alexandreferris.heroescomics.model.CreatorData
import br.com.alexandreferris.heroescomics.model.CreatorsResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

open class CreatorDetailsVM @Inject constructor(private val networkApi: MarvelAPIService): ViewModel() {

    private var disposable: CompositeDisposable = CompositeDisposable()

    private val creator: MutableLiveData<CreatorData> = MutableLiveData()
    private val creatorLoadError = MutableLiveData<Boolean>()
    private val loading = MutableLiveData<Boolean>()

    fun getCreator(): MutableLiveData<CreatorData> {
        return creator
    }

    fun getError(): LiveData<Boolean> {
        return creatorLoadError
    }

    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun fetchCreator(creatorId: Int) {
        loading.value = true
        disposable.add(networkApi.getCreator(creatorId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<CreatorsResponse>() {
                override fun onError(e: Throwable) {
                    creatorLoadError.value = true
                    loading.value = false
                }

                override fun onSuccess(creatorsResponse: CreatorsResponse) {
                    creatorLoadError.value = false
                    loading.value = false

                    if (creatorsResponse.data.results.size > NumberUtils.INTEGER_ZERO)
                        creator.value = creatorsResponse.data.results[NumberUtils.INTEGER_ZERO]
                    else
                        creator.value = null
                }
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}