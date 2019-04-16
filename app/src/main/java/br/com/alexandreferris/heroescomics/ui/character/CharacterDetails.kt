package br.com.alexandreferris.heroescomics.ui.character

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import br.com.alexandreferris.heroescomics.App
import br.com.alexandreferris.heroescomics.R
import br.com.alexandreferris.heroescomics.model.CharacterData
import br.com.alexandreferris.heroescomics.model.GenericExtra
import br.com.alexandreferris.heroescomics.ui.comic.Comics
import br.com.alexandreferris.heroescomics.ui.event.Events
import br.com.alexandreferris.heroescomics.ui.serie.Series
import br.com.alexandreferris.heroescomics.ui.storie.Stories
import br.com.alexandreferris.heroescomics.utils.adapter.GenericDetailsExtraAdapter
import br.com.alexandreferris.heroescomics.utils.loadAdBanner
import br.com.alexandreferris.heroescomics.viewmodel.character.CharacterDetailsVM
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_character_details.*
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

class CharacterDetails : AppCompatActivity() {

    @Inject
    lateinit var characterDetailsVM: CharacterDetailsVM

    private lateinit var genericDetailsExtraAdapter: GenericDetailsExtraAdapter

    private var characterId: Int = NumberUtils.INTEGER_ZERO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_details)

        loadAdBanner(adView)

        (application as App).appComponent.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        characterId = intent.getIntExtra("CHARACTER_ID", NumberUtils.INTEGER_ZERO)

        observableViewModel()
        characterDetailsVM.fetchCharacter(characterId)
    }

    private fun observableViewModel() {
        characterDetailsVM.getLoading().observe(this, Observer {
            loadingBar.visibility = if (it!!) View.VISIBLE else View.GONE
        })

        characterDetailsVM.getError().observe(this, Observer {
            textViewHeroDetailsNoResult.visibility = View.VISIBLE
            recyclerViewExtras.visibility = View.GONE
        })

        characterDetailsVM.getCharacter().observe(this, Observer {
            textViewHeroDetailsNoResult.visibility = View.GONE
            recyclerViewExtras.visibility = View.VISIBLE
            displayCharacterDetails(it!!)
        })
    }

    private fun displayCharacterDetails(characterData: CharacterData) {
        supportActionBar?.title = characterData.name

        Glide.with(this)
            .load(characterData.thumbnail?.path + "/landscape_incredible." + characterData.thumbnail?.extension)
            .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
            .into(imageViewCharacterPosterImage)

        textViewCaracterName.text = characterData.name
        textViewCharacterDescription.text = characterData.description

        val characterExtras: ArrayList<GenericExtra> = ArrayList()
        if (characterData.comics.available > NumberUtils.INTEGER_ZERO)
            characterExtras.add(GenericExtra("Comics", characterData.comics.available))
        if (characterData.series.available > NumberUtils.INTEGER_ZERO)
            characterExtras.add(GenericExtra("Series", characterData.series.available))
        if (characterData.stories.available > NumberUtils.INTEGER_ZERO)
            characterExtras.add(GenericExtra("Stories", characterData.stories.available))
        if (characterData.events.available > NumberUtils.INTEGER_ZERO)
            characterExtras.add(GenericExtra("Events", characterData.events.available))

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewExtras.layoutManager = linearLayoutManager

        genericDetailsExtraAdapter = GenericDetailsExtraAdapter(this) { type ->

            var extraScreen = Intent()
            when (type) {
                "Comics" -> extraScreen = Intent(this, Comics::class.java)
                "Series" -> extraScreen = Intent(this, Series::class.java)
                "Stories" -> extraScreen = Intent(this, Stories::class.java)
                "Events" -> extraScreen = Intent(this, Events::class.java)
            }
            extraScreen.putExtra("EXTRA_TYPE", "characters")
            extraScreen.putExtra("EXTRA_NAME", characterData.name)
            extraScreen.putExtra("EXTRA_ID", characterId)
            startActivity(extraScreen)
        }
        genericDetailsExtraAdapter.setCharactersExtras(characterExtras)
        recyclerViewExtras.adapter = genericDetailsExtraAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
