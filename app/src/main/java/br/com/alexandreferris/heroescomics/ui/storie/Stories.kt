package br.com.alexandreferris.heroescomics.ui.storie

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
import br.com.alexandreferris.heroescomics.model.StorieData
import br.com.alexandreferris.heroescomics.utils.adapter.EndlessScrollListener
import br.com.alexandreferris.heroescomics.utils.adapter.StoriesAdapter
import br.com.alexandreferris.heroescomics.utils.loadAdBanner
import br.com.alexandreferris.heroescomics.viewmodel.storie.StoriesVM
import kotlinx.android.synthetic.main.activity_generic_list.*
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

class Stories : AppCompatActivity(), View.OnClickListener {

    @Inject
    lateinit var storiesVM: StoriesVM

    private lateinit var storiesAdapter: StoriesAdapter
    private lateinit var scrollListener: EndlessScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic_list)

        loadAdBanner(adView)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.stories_title, intent.getStringExtra("EXTRA_NAME"))

        (application as App).appComponent.inject(this)

        storiesVM.extraType = intent.getStringExtra("EXTRA_TYPE")
        storiesVM.extraId = intent.getIntExtra("EXTRA_ID", NumberUtils.INTEGER_ZERO)
        storiesVM.fetchStories()

        buttonTryAgain.setOnClickListener(this)
        buttonLoadMore.setOnClickListener(this)
        observableViewModel()
    }

    private fun observableViewModel() {
        storiesVM.getLoading().observe(this, Observer {
            loadingBar.visibility = if (it!!) View.VISIBLE else View.GONE
        })

        storiesVM.getError().observe(this, Observer {
            hideShowLoadMoreAndNoSearchResult(it!!)
        })

        storiesVM.getStories().observe(this, Observer {
            hideShowLoadMoreAndNoSearchResult(false)
            hideShowRecyclerViewNoResult(it!!)
        })
    }

    private fun hideShowLoadMoreAndNoSearchResult(show: Boolean) {
        relativeLayoutLoadMoreError.visibility = View.GONE
        constraintLayoutNoResult.visibility = View.GONE

        if (show) {
            val booleanShowError = (storiesVM.currentPage == storiesVM.startingPage)

            if (!booleanShowError)
                Snackbar.make(constraintLayoutHome, R.string.error_loading_heroes, Snackbar.LENGTH_LONG).show()
            relativeLayoutLoadMoreError.visibility = if (!booleanShowError) View.VISIBLE else View.GONE
            constraintLayoutNoResult.visibility = if (booleanShowError) View.VISIBLE else View.GONE
        }
    }

    private fun hideShowRecyclerViewNoResult(list: List<StorieData>) {
        val booleanShowError = (list.size == NumberUtils.INTEGER_ZERO)

        recyclerView.visibility = if (!booleanShowError) View.VISIBLE else View.GONE

        if (!booleanShowError)
            displayStories(list)
    }

    private fun setupRecyclerView(stories: List<StorieData>, visibleThreshold: Int) {
        val linearLayoutManager = GridLayoutManager(this, NumberUtils.INTEGER_TWO)
        recyclerView.layoutManager = linearLayoutManager

        storiesAdapter = StoriesAdapter { storieId ->
            val storieSelectedDetails = Intent(this, StorieDetails::class.java)
            storieSelectedDetails.putExtra("STORIE_ID", storieId)

            startActivity(storieSelectedDetails)
        }
        storiesAdapter.setStoriesList(stories)
        recyclerView.adapter = storiesAdapter

        if (stories.size >= storiesVM.limit) {
            scrollListener = object : EndlessScrollListener(linearLayoutManager, visibleThreshold) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    storiesVM.currentPage = page
                    storiesVM.fetchStories(page)
                }
            }

            recyclerView.addOnScrollListener(scrollListener)
        }
    }

    private fun displayStories(listStories: List<StorieData>) {
        recyclerView.visibility = View.VISIBLE
        if (storiesVM.currentPage == storiesVM.startingPage) {
            setupRecyclerView(listStories, (storiesVM.visibleThreshold - NumberUtils.INTEGER_ONE))
        } else {
            storiesAdapter.setStoriesList(listStories)
            storiesAdapter.notifyDataSetChanged()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonTryAgain, R.id.buttonLoadMore -> {
                relativeLayoutLoadMoreError.visibility = View.GONE
                storiesVM.fetchStories(storiesVM.currentPage)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}