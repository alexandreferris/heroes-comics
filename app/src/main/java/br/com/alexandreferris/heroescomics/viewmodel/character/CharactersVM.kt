package br.com.alexandreferris.heroescomics.viewmodel.character

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import br.com.alexandreferris.heroescomics.data.remote.MarvelAPIService
import br.com.alexandreferris.heroescomics.model.CharacterData
import br.com.alexandreferris.heroescomics.model.CharactersResponse
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.disposables.CompositeDisposable
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils

open class CharactersVM @Inject constructor(private val networkApi: MarvelAPIService): ViewModel() {

    private var disposable: CompositeDisposable = CompositeDisposable()

    private val listCharacters: ArrayList<CharacterData> = ArrayList()
    private val characters: MutableLiveData<List<CharacterData>> = MutableLiveData()
    private val charactersLoadError = MutableLiveData<Boolean>()
    private val loading = MutableLiveData<Boolean>()

    private val limit: Int = 20
    val startingPage: Int = NumberUtils.INTEGER_ONE
    var currentPage: Int = NumberUtils.INTEGER_ONE
    var visibleThreshold: Int = NumberUtils.INTEGER_ZERO

    var searchQuery: String = StringUtils.EMPTY

    fun getCharacters(): MutableLiveData<List<CharacterData>> {
        return characters
    }

    fun getError(): LiveData<Boolean> {
        return charactersLoadError
    }

    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun fetchCharacters(offset: Int = NumberUtils.INTEGER_ZERO, paramLimit: Int = limit) {
        loading.value = true
        disposable.add(networkApi.getCharacters(
            (offset * paramLimit),
            paramLimit,
            if (StringUtils.isEmpty(searchQuery)) null else searchQuery)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<CharactersResponse>() {
                override fun onError(e: Throwable) {
                    charactersLoadError.value = true
                    loading.value = false
                }

                override fun onSuccess(charactersResponse: CharactersResponse) {
                    charactersLoadError.value = false
                    loading.value = false

                    listCharacters.addAll(charactersResponse.data.results)
                    visibleThreshold = listCharacters.size / currentPage

                    characters.value = listCharacters
                }
            })
        )
    }

    fun reset() {
        listCharacters.clear()
        searchQuery = StringUtils.EMPTY
        currentPage = startingPage
        characters.value = listCharacters
    }

    init {
        fetchCharacters()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}