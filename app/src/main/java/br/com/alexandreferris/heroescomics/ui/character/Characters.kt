package br.com.alexandreferris.heroescomics.ui.character

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import br.com.alexandreferris.heroescomics.App
import br.com.alexandreferris.heroescomics.R
import br.com.alexandreferris.heroescomics.model.CharacterData
import br.com.alexandreferris.heroescomics.utils.adapter.EndlessScrollListener
import br.com.alexandreferris.heroescomics.utils.adapter.CharactersAdapter
import br.com.alexandreferris.heroescomics.utils.loadAdBanner
import br.com.alexandreferris.heroescomics.viewmodel.character.CharactersVM
import kotlinx.android.synthetic.main.activity_generic_list.*
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

class Characters : AppCompatActivity(), View.OnClickListener {

    @Inject
    lateinit var charactersVM: CharactersVM

    private lateinit var charactersAdapter: CharactersAdapter
    private lateinit var scrollListener: EndlessScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic_list)

        loadAdBanner(adView)

        (application as App).appComponent.inject(this)

        buttonTryAgain.setOnClickListener(this)
        buttonLoadMore.setOnClickListener(this)
        observableViewModel()
    }

    private fun observableViewModel() {
        charactersVM.getLoading().observe(this, Observer {
            loadingBar.visibility = if (it!!) View.VISIBLE else View.GONE
        })

        charactersVM.getError().observe(this, Observer {
            hideShowLoadMoreAndNoSearchResult(it!!)
        })

        charactersVM.getCharacters().observe(this, Observer {
            hideShowLoadMoreAndNoSearchResult(false)
            hideShowRecyclerViewNoResult(it!!)
        })
    }

    private fun hideShowLoadMoreAndNoSearchResult(show: Boolean) {
        relativeLayoutLoadMoreError.visibility = View.GONE
        constraintLayoutNoResult.visibility = View.GONE

        if (show) {
            val booleanShowError = (charactersVM.currentPage == charactersVM.startingPage)

            if (!booleanShowError)
                Snackbar.make(constraintLayoutHome, R.string.error_loading_heroes, Snackbar.LENGTH_LONG).show()
            relativeLayoutLoadMoreError.visibility = if (!booleanShowError) View.VISIBLE else View.GONE
            constraintLayoutNoResult.visibility = if (booleanShowError) View.VISIBLE else View.GONE
        }
    }

    private fun hideShowRecyclerViewNoResult(list: List<CharacterData>) {
        val booleanShowError = (list.size == NumberUtils.INTEGER_ZERO && !StringUtils.isEmpty(charactersVM.searchQuery))

        recyclerView.visibility = if (!booleanShowError) View.VISIBLE else View.GONE
        textViewSearchNoResult.visibility = if (booleanShowError) View.VISIBLE else View.GONE

        if (!booleanShowError)
            displayCharacters(list)
    }

    private fun setupRecyclerView(characters: List<CharacterData>, visibleThreshold: Int) {
        val linearLayoutManager = GridLayoutManager(this, NumberUtils.INTEGER_TWO)
        recyclerView.layoutManager = linearLayoutManager

        charactersAdapter = CharactersAdapter { characterId ->
            val characterSelectedDetails = Intent(this, CharacterDetails::class.java)
            characterSelectedDetails.putExtra("CHARACTER_ID", characterId)

            startActivity(characterSelectedDetails)
        }
        charactersAdapter.setCharactersList(characters)
        recyclerView.adapter = charactersAdapter

        scrollListener = object : EndlessScrollListener(linearLayoutManager, visibleThreshold) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                charactersVM.currentPage = page
                charactersVM.fetchCharacters(page)
            }
        }

        recyclerView.addOnScrollListener(scrollListener)
    }

    private fun displayCharacters(listCharacters: List<CharacterData>) {
        recyclerView.visibility = View.VISIBLE
        if (charactersVM.currentPage == charactersVM.startingPage) {
            setupRecyclerView(listCharacters, (charactersVM.visibleThreshold - NumberUtils.INTEGER_ONE))
        } else {
            charactersAdapter.setCharactersList(listCharacters)
            charactersAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchItem: MenuItem = menu!!.findItem(R.id.action_search)

        val searchView: SearchView? = searchItem.actionView as SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                charactersVM.reset()
                charactersVM.searchQuery = query
                charactersVM.fetchCharacters()
                return false
            }

        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                resetCharacterList()
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun resetCharacterList() {
        charactersVM.reset()
        charactersVM.fetchCharacters()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonTryAgain, R.id.buttonLoadMore -> {
                relativeLayoutLoadMoreError.visibility = View.GONE
                charactersVM.fetchCharacters(charactersVM.currentPage)
            }
        }
    }
}