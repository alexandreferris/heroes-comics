package br.com.alexandreferris.heroescomics.viewmodel.storie

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import br.com.alexandreferris.heroescomics.data.remote.MarvelAPIService
import br.com.alexandreferris.heroescomics.model.StorieData
import br.com.alexandreferris.heroescomics.model.StoriesResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

open class StorieDetailsVM @Inject constructor(private val networkApi: MarvelAPIService): ViewModel() {

    private var disposable: CompositeDisposable = CompositeDisposable()

    private val storie: MutableLiveData<StorieData> = MutableLiveData()
    private val storieLoadError = MutableLiveData<Boolean>()
    private val loading = MutableLiveData<Boolean>()

    fun getStorie(): MutableLiveData<StorieData> {
        return storie
    }

    fun getError(): LiveData<Boolean> {
        return storieLoadError
    }

    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun fetchStorie(storieId: Int) {
        loading.value = true
        disposable.add(
            networkApi.getStorie(storieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<StoriesResponse>() {
                    override fun onError(e: Throwable) {
                        storieLoadError.value = true
                        loading.value = false
                    }

                    override fun onSuccess(storiesResponse: StoriesResponse) {
                        storieLoadError.value = false
                        loading.value = false

                        if (storiesResponse.data.results.size > NumberUtils.INTEGER_ZERO)
                            storie.value = storiesResponse.data.results[NumberUtils.INTEGER_ZERO]
                        else
                            storie.value = null
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

