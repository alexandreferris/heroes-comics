package br.com.alexandreferris.heroescomics.ui.serie

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
import br.com.alexandreferris.heroescomics.model.SerieData
import br.com.alexandreferris.heroescomics.utils.adapter.SeriesAdapter
import br.com.alexandreferris.heroescomics.utils.adapter.EndlessScrollListener
import br.com.alexandreferris.heroescomics.utils.loadAdBanner
import br.com.alexandreferris.heroescomics.viewmodel.serie.SeriesVM
import kotlinx.android.synthetic.main.activity_generic_list.*
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

class Series : AppCompatActivity(), View.OnClickListener {

    @Inject
    lateinit var seriesVM: SeriesVM

    private lateinit var seriesAdapter: SeriesAdapter
    private lateinit var scrollListener: EndlessScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic_list)

        loadAdBanner(adView)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.series_title, intent.getStringExtra("EXTRA_NAME"))

        (application as App).appComponent.inject(this)

        seriesVM.extraType = intent.getStringExtra("EXTRA_TYPE")
        seriesVM.extraId = intent.getIntExtra("EXTRA_ID", NumberUtils.INTEGER_ZERO)
        seriesVM.fetchSeries()

        buttonTryAgain.setOnClickListener(this)
        buttonLoadMore.setOnClickListener(this)
        observableViewModel()
    }

    private fun observableViewModel() {
        seriesVM.getLoading().observe(this, Observer {
            loadingBar.visibility = if (it!!) View.VISIBLE else View.GONE
        })

        seriesVM.getError().observe(this, Observer {
            hideShowLoadMoreAndNoSearchResult(it!!)
        })

        seriesVM.getSeries().observe(this, Observer {
            hideShowLoadMoreAndNoSearchResult(false)
            hideShowRecyclerViewNoResult(it!!)
        })
    }

    private fun hideShowLoadMoreAndNoSearchResult(show: Boolean) {
        relativeLayoutLoadMoreError.visibility = View.GONE
        constraintLayoutNoResult.visibility = View.GONE

        if (show) {
            val booleanShowError = (seriesVM.currentPage == seriesVM.startingPage)

            if (!booleanShowError)
                Snackbar.make(constraintLayoutHome, R.string.error_loading_heroes, Snackbar.LENGTH_LONG).show()
            relativeLayoutLoadMoreError.visibility = if (!booleanShowError) View.VISIBLE else View.GONE
            constraintLayoutNoResult.visibility = if (booleanShowError) View.VISIBLE else View.GONE
        }
    }

    private fun hideShowRecyclerViewNoResult(list: List<SerieData>) {
        val booleanShowError = (list.size == NumberUtils.INTEGER_ZERO)

        recyclerView.visibility = if (!booleanShowError) View.VISIBLE else View.GONE

        if (!booleanShowError)
            displaySeries(list)
    }

    private fun setupRecyclerView(series: List<SerieData>, visibleThreshold: Int) {
        val linearLayoutManager = GridLayoutManager(this, NumberUtils.INTEGER_TWO)
        recyclerView.layoutManager = linearLayoutManager

        seriesAdapter = SeriesAdapter { serieId ->
            val serieSelectedDetails = Intent(this, SerieDetails::class.java)
            serieSelectedDetails.putExtra("SERIE_ID", serieId)

            startActivity(serieSelectedDetails)
        }
        seriesAdapter.setSeriesList(series)
        recyclerView.adapter = seriesAdapter

        if (series.size >= seriesVM.limit) {
            scrollListener = object : EndlessScrollListener(linearLayoutManager, visibleThreshold) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    seriesVM.currentPage = page
                    seriesVM.fetchSeries(page)
                }
            }

            recyclerView.addOnScrollListener(scrollListener)
        }
    }

    private fun displaySeries(listSeries: List<SerieData>) {
        recyclerView.visibility = View.VISIBLE
        if (seriesVM.currentPage == seriesVM.startingPage) {
            setupRecyclerView(listSeries, (seriesVM.visibleThreshold - NumberUtils.INTEGER_ONE))
        } else {
            seriesAdapter.setSeriesList(listSeries)
            seriesAdapter.notifyDataSetChanged()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonTryAgain, R.id.buttonLoadMore -> {
                relativeLayoutLoadMoreError.visibility = View.GONE
                seriesVM.fetchSeries(seriesVM.currentPage)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}