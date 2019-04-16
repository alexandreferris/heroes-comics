package br.com.alexandreferris.heroescomics.ui.comic

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import br.com.alexandreferris.heroescomics.App
import br.com.alexandreferris.heroescomics.R
import br.com.alexandreferris.heroescomics.model.ComicData
import br.com.alexandreferris.heroescomics.utils.adapter.ComicsAdapter
import br.com.alexandreferris.heroescomics.utils.adapter.EndlessScrollListener
import br.com.alexandreferris.heroescomics.utils.loadAdBanner
import br.com.alexandreferris.heroescomics.viewmodel.comic.ComicsVM
import kotlinx.android.synthetic.main.activity_generic_list.*
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

class Comics : AppCompatActivity(), View.OnClickListener {

    @Inject
    lateinit var comicsVM: ComicsVM

    private lateinit var comicsAdapter: ComicsAdapter
    private lateinit var scrollListener: EndlessScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic_list)

        loadAdBanner(adView)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.comics_title, intent.getStringExtra("EXTRA_NAME"))

        (application as App).appComponent.inject(this)

        comicsVM.extraType = intent.getStringExtra("EXTRA_TYPE")
        comicsVM.extraId = intent.getIntExtra("EXTRA_ID", NumberUtils.INTEGER_ZERO)
        comicsVM.fetchComics()

        buttonTryAgain.setOnClickListener(this)
        buttonLoadMore.setOnClickListener(this)
        observableViewModel()
    }

    private fun observableViewModel() {
        comicsVM.getLoading().observe(this, Observer {
            loadingBar.visibility = if (it!!) View.VISIBLE else View.GONE
        })

        comicsVM.getError().observe(this, Observer {
            hideShowLoadMoreAndNoSearchResult(it!!)
        })

        comicsVM.getComics().observe(this, Observer {
            hideShowLoadMoreAndNoSearchResult(false)
            hideShowRecyclerViewNoResult(it!!)
        })
    }

    private fun hideShowLoadMoreAndNoSearchResult(show: Boolean) {
        relativeLayoutLoadMoreError.visibility = View.GONE
        constraintLayoutNoResult.visibility = View.GONE

        if (show) {
            val booleanShowError = (comicsVM.currentPage == comicsVM.startingPage)

            if (!booleanShowError)
                Snackbar.make(constraintLayoutHome, R.string.error_loading_heroes, Snackbar.LENGTH_LONG).show()
            relativeLayoutLoadMoreError.visibility = if (!booleanShowError) View.VISIBLE else View.GONE
            constraintLayoutNoResult.visibility = if (booleanShowError) View.VISIBLE else View.GONE
        }
    }

    private fun hideShowRecyclerViewNoResult(list: List<ComicData>) {
        val booleanShowError = (list.size == NumberUtils.INTEGER_ZERO)

        recyclerView.visibility = if (!booleanShowError) View.VISIBLE else View.GONE

        if (!booleanShowError)
            displayComics(list)
    }

    private fun setupRecyclerView(comics: List<ComicData>, visibleThreshold: Int) {
        val linearLayoutManager = GridLayoutManager(this, NumberUtils.INTEGER_TWO)
        recyclerView.layoutManager = linearLayoutManager

        comicsAdapter = ComicsAdapter { comicId ->
            val comicSelectedDetails = Intent(this, ComicDetails::class.java)
            comicSelectedDetails.putExtra("COMIC_ID", comicId)

            startActivity(comicSelectedDetails)
        }
        comicsAdapter.setComicsList(comics)
        recyclerView.adapter = comicsAdapter

        if (comics.size >= comicsVM.limit) {
            scrollListener = object : EndlessScrollListener(linearLayoutManager, visibleThreshold) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    comicsVM.currentPage = page
                    comicsVM.fetchComics(page)
                }
            }

            recyclerView.addOnScrollListener(scrollListener)
        }
    }

    private fun displayComics(listComics: List<ComicData>) {
        recyclerView.visibility = View.VISIBLE
        if (comicsVM.currentPage == comicsVM.startingPage) {
            setupRecyclerView(listComics, (comicsVM.visibleThreshold - NumberUtils.INTEGER_ONE))
        } else {
            comicsAdapter.setComicsList(listComics)
            comicsAdapter.notifyDataSetChanged()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonTryAgain, R.id.buttonLoadMore -> {
                relativeLayoutLoadMoreError.visibility = View.GONE
                comicsVM.fetchComics(comicsVM.currentPage)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}