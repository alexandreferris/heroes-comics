package br.com.alexandreferris.heroescomics.viewmodel.event

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import br.com.alexandreferris.heroescomics.data.remote.MarvelAPIService
import br.com.alexandreferris.heroescomics.model.EventData
import br.com.alexandreferris.heroescomics.model.EventsResponse
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.disposables.CompositeDisposable
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils

open class EventsVM @Inject constructor(private val networkApi: MarvelAPIService): ViewModel() {

    var extraType: String = StringUtils.EMPTY
    var extraId: Int = NumberUtils.INTEGER_ZERO

    private var disposable: CompositeDisposable = CompositeDisposable()

    private val listEvents: ArrayList<EventData> = ArrayList()
    private val events: MutableLiveData<List<EventData>> = MutableLiveData()
    private val eventsLoadError = MutableLiveData<Boolean>()
    private val loading = MutableLiveData<Boolean>()

    val limit: Int = 20
    val startingPage: Int = NumberUtils.INTEGER_ONE
    var currentPage: Int = NumberUtils.INTEGER_ONE
    var visibleThreshold: Int = NumberUtils.INTEGER_ZERO

    fun getEvents(): MutableLiveData<List<EventData>> {
        return events
    }

    fun getError(): LiveData<Boolean> {
        return eventsLoadError
    }

    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun fetchEvents(offset: Int = NumberUtils.INTEGER_ZERO, paramLimit: Int = limit) {
        loading.value = true
        disposable.add(networkApi.getEventsFromExtra(extraType, extraId, offset, paramLimit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<EventsResponse>() {
                override fun onError(e: Throwable) {
                    eventsLoadError.value = true
                    loading.value = false
                }

                override fun onSuccess(storiesResponse: EventsResponse) {
                    eventsLoadError.value = false
                    loading.value = false

                    listEvents.addAll(storiesResponse.data.results)
                    visibleThreshold = listEvents.size / currentPage

                    events.value = listEvents
                }
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}