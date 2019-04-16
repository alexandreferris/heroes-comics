package br.com.alexandreferris.heroescomics.viewmodel.event

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import br.com.alexandreferris.heroescomics.data.remote.MarvelAPIService
import br.com.alexandreferris.heroescomics.model.EventData
import br.com.alexandreferris.heroescomics.model.EventsResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

open class EventDetailsVM @Inject constructor(private val networkApi: MarvelAPIService): ViewModel() {

    private var disposable: CompositeDisposable = CompositeDisposable()

    private val event: MutableLiveData<EventData> = MutableLiveData()
    private val eventLoadError = MutableLiveData<Boolean>()
    private val loading = MutableLiveData<Boolean>()

    fun getEvent(): MutableLiveData<EventData> {
        return event
    }

    fun getError(): LiveData<Boolean> {
        return eventLoadError
    }

    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun fetchEvent(eventId: Int) {
        loading.value = true
        disposable.add(
            networkApi.getEvent(eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<EventsResponse>() {
                    override fun onError(e: Throwable) {
                        eventLoadError.value = true
                        loading.value = false
                    }

                    override fun onSuccess(eventsResponse: EventsResponse) {
                        eventLoadError.value = false
                        loading.value = false

                        if (eventsResponse.data.results.size > NumberUtils.INTEGER_ZERO)
                            event.value = eventsResponse.data.results[NumberUtils.INTEGER_ZERO]
                        else
                            event.value = null
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

