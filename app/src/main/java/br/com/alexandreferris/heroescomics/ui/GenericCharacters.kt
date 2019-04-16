package br.com.alexandreferris.heroescomics.ui

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import br.com.alexandreferris.heroescomics.App
import br.com.alexandreferris.heroescomics.R
import br.com.alexandreferris.heroescomics.model.CharacterData
import br.com.alexandreferris.heroescomics.ui.character.CharacterDetails
import br.com.alexandreferris.heroescomics.utils.adapter.EndlessScrollListener
import br.com.alexandreferris.heroescomics.utils.adapter.CharactersAdapter
import br.com.alexandreferris.heroescomics.utils.loadAdBanner
import br.com.alexandreferris.heroescomics.viewmodel.GenericCharactersVM
import kotlinx.android.synthetic.main.activity_generic_list.*
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

class GenericCharacters: AppCompatActivity(), View.OnClickListener {

    @Inject
    lateinit var genericCharactersVM: GenericCharactersVM

    private lateinit var charactersAdapter: CharactersAdapter
    private lateinit var scrollListener: EndlessScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic_list)

        loadAdBanner(adView)

        (application as App).appComponent.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra("EXTRA_NAME")

        genericCharactersVM.extraType = intent.getStringExtra("EXTRA_TYPE")
        genericCharactersVM.extraId = intent.getIntExtra("EXTRA_ID", NumberUtils.INTEGER_ZERO)
        genericCharactersVM.fetchGenericCharacters()

        buttonTryAgain.setOnClickListener(this)
        buttonLoadMore.setOnClickListener(this)
        observableViewModel()
    }

    private fun observableViewModel() {
        genericCharactersVM.getLoading().observe(this, Observer {
            loadingBar.visibility = if (it!!) View.VISIBLE else View.GONE
        })

        genericCharactersVM.getError().observe(this, Observer {
            hideShowLoadMoreAndNoSearchResult(it!!)
        })

        genericCharactersVM.getCharacters().observe(this, Observer {
            hideShowLoadMoreAndNoSearchResult(false)
            hideShowRecyclerViewNoResult(it!!)
        })
    }

    private fun hideShowLoadMoreAndNoSearchResult(show: Boolean) {
        relativeLayoutLoadMoreError.visibility = View.GONE
        constraintLayoutNoResult.visibility = View.GONE

        if (show) {
            val booleanShowError = (genericCharactersVM.currentPage == genericCharactersVM.startingPage)

            if (!booleanShowError)
                Snackbar.make(constraintLayoutHome, R.string.error_loading_heroes, Snackbar.LENGTH_LONG).show()
            relativeLayoutLoadMoreError.visibility = if (!booleanShowError) View.VISIBLE else View.GONE
            constraintLayoutNoResult.visibility = if (booleanShowError) View.VISIBLE else View.GONE
        }
    }

    private fun hideShowRecyclerViewNoResult(list: List<CharacterData>) {
        val booleanShowError = (list.size == NumberUtils.INTEGER_ZERO)

        recyclerView.visibility = if (!booleanShowError) View.VISIBLE else View.GONE

        if (!booleanShowError)
            displayCharacters(list)
    }

    private fun setupRecyclerView(characters: List<CharacterData>, visibleThreshold: Int) {
        val gridLayoutManager = GridLayoutManager(this, NumberUtils.INTEGER_TWO)
        recyclerView.layoutManager = gridLayoutManager

        charactersAdapter = CharactersAdapter { characterId ->
            val characterSelectedDetails = Intent(this, CharacterDetails::class.java)
            characterSelectedDetails.putExtra("CHARACTER_ID", characterId)

            startActivity(characterSelectedDetails)
        }
        charactersAdapter.setCharactersList(characters)
        recyclerView.adapter = charactersAdapter

        scrollListener = object : EndlessScrollListener(gridLayoutManager, visibleThreshold) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                genericCharactersVM.currentPage = page
                genericCharactersVM.fetchGenericCharacters(page)
            }
        }

        recyclerView.addOnScrollListener(scrollListener)
    }

    private fun displayCharacters(listCharacters: List<CharacterData>) {
        recyclerView.visibility = View.VISIBLE
        if (genericCharactersVM.currentPage == genericCharactersVM.startingPage) {
            setupRecyclerView(listCharacters, (genericCharactersVM.visibleThreshold - NumberUtils.INTEGER_ONE))
        } else {
            charactersAdapter.setCharactersList(listCharacters)
            charactersAdapter.notifyDataSetChanged()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonTryAgain, R.id.buttonLoadMore -> {
                relativeLayoutLoadMoreError.visibility = View.GONE
                genericCharactersVM.fetchGenericCharacters(genericCharactersVM.currentPage)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}