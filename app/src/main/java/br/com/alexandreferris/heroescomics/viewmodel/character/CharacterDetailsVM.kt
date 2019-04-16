package br.com.alexandreferris.heroescomics.viewmodel.character

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import br.com.alexandreferris.heroescomics.data.remote.MarvelAPIService
import br.com.alexandreferris.heroescomics.model.CharacterData
import br.com.alexandreferris.heroescomics.model.CharactersResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

open class CharacterDetailsVM @Inject constructor(private val networkApi: MarvelAPIService): ViewModel() {

    private var disposable: CompositeDisposable = CompositeDisposable()

    private val character: MutableLiveData<CharacterData> = MutableLiveData()
    private val characterLoadError = MutableLiveData<Boolean>()
    private val loading = MutableLiveData<Boolean>()

    fun getCharacter(): MutableLiveData<CharacterData> {
        return character
    }

    fun getError(): LiveData<Boolean> {
        return characterLoadError
    }

    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun fetchCharacter(characterId: Int) {
        loading.value = true
        disposable.add(networkApi.getCharacter(characterId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<CharactersResponse>() {
                override fun onError(e: Throwable) {
                    characterLoadError.value = true
                    loading.value = false
                }

                override fun onSuccess(charactersResponse: CharactersResponse) {
                    characterLoadError.value = false
                    loading.value = false

                    if (charactersResponse.data.results.size > NumberUtils.INTEGER_ZERO)
                        character.value = charactersResponse.data.results[NumberUtils.INTEGER_ZERO]
                    else
                        character.value = null
                }
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}