package br.com.alexandreferris.heroescomics.di

import br.com.alexandreferris.heroescomics.ui.GenericCharacters
import br.com.alexandreferris.heroescomics.ui.GenericCreators
import br.com.alexandreferris.heroescomics.ui.character.CharacterDetails
import br.com.alexandreferris.heroescomics.ui.character.Characters
import br.com.alexandreferris.heroescomics.ui.comic.ComicDetails
import br.com.alexandreferris.heroescomics.ui.comic.Comics
import br.com.alexandreferris.heroescomics.ui.creator.CreatorDetails
import br.com.alexandreferris.heroescomics.ui.event.EventDetails
import br.com.alexandreferris.heroescomics.ui.event.Events
import br.com.alexandreferris.heroescomics.ui.serie.SerieDetails
import br.com.alexandreferris.heroescomics.ui.serie.Series
import br.com.alexandreferris.heroescomics.ui.storie.StorieDetails
import br.com.alexandreferris.heroescomics.ui.storie.Stories
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {
    fun inject(target: GenericCharacters)
    fun inject(target: GenericCreators)
    fun inject(target: CreatorDetails)
    fun inject(target: Characters)
    fun inject(target: CharacterDetails)
    fun inject(target: Comics)
    fun inject(target: ComicDetails)
    fun inject(target: Series)
    fun inject(target: SerieDetails)
    fun inject(target: Stories)
    fun inject(target: StorieDetails)
    fun inject(target: Events)
    fun inject(target: EventDetails)
}