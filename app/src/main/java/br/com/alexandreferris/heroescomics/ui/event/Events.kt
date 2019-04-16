package br.com.alexandreferris.heroescomics.ui.event

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
import br.com.alexandreferris.heroescomics.model.EventData
import br.com.alexandreferris.heroescomics.utils.adapter.EndlessScrollListener
import br.com.alexandreferris.heroescomics.utils.adapter.EventsAdapter
import br.com.alexandreferris.heroescomics.utils.loadAdBanner
import br.com.alexandreferris.heroescomics.viewmodel.event.EventsVM
import kotlinx.android.synthetic.main.activity_generic_list.*
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

class Events : AppCompatActivity(), View.OnClickListener {

    @Inject
    lateinit var eventsVM: EventsVM

    private lateinit var eventsAdapter: EventsAdapter
    private lateinit var scrollListener: EndlessScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic_list)

        loadAdBanner(adView)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.events_title, intent.getStringExtra("EXTRA_NAME"))

        (application as App).appComponent.inject(this)

        eventsVM.extraType = intent.getStringExtra("EXTRA_TYPE")
        eventsVM.extraId = intent.getIntExtra("EXTRA_ID", NumberUtils.INTEGER_ZERO)
        eventsVM.fetchEvents()

        buttonTryAgain.setOnClickListener(this)
        buttonLoadMore.setOnClickListener(this)
        observableViewModel()
    }

    private fun observableViewModel() {
        eventsVM.getLoading().observe(this, Observer {
            loadingBar.visibility = if (it!!) View.VISIBLE else View.GONE
        })

        eventsVM.getError().observe(this, Observer {
            hideShowLoadMoreAndNoSearchResult(it!!)
        })

        eventsVM.getEvents().observe(this, Observer {
            hideShowLoadMoreAndNoSearchResult(false)
            hideShowRecyclerViewNoResult(it!!)
        })
    }

    private fun hideShowLoadMoreAndNoSearchResult(show: Boolean) {
        relativeLayoutLoadMoreError.visibility = View.GONE
        constraintLayoutNoResult.visibility = View.GONE

        if (show) {
            val booleanShowError = (eventsVM.currentPage == eventsVM.startingPage)

            if (!booleanShowError)
                Snackbar.make(constraintLayoutHome, R.string.error_loading_heroes, Snackbar.LENGTH_LONG).show()
            relativeLayoutLoadMoreError.visibility = if (!booleanShowError) View.VISIBLE else View.GONE
            constraintLayoutNoResult.visibility = if (booleanShowError) View.VISIBLE else View.GONE
        }
    }

    private fun hideShowRecyclerViewNoResult(list: List<EventData>) {
        val booleanShowError = (list.size == NumberUtils.INTEGER_ZERO)

        recyclerView.visibility = if (!booleanShowError) View.VISIBLE else View.GONE

        if (!booleanShowError)
            displayEvents(list)
    }

    private fun setupRecyclerView(events: List<EventData>, visibleThreshold: Int) {
        val linearLayoutManager = GridLayoutManager(this, NumberUtils.INTEGER_TWO)
        recyclerView.layoutManager = linearLayoutManager

        eventsAdapter = EventsAdapter { eventId ->
            val eventSelectedDetails = Intent(this, EventDetails::class.java)
            eventSelectedDetails.putExtra("EVENT_ID", eventId)

            startActivity(eventSelectedDetails)
        }
        eventsAdapter.setEventsList(events)
        recyclerView.adapter = eventsAdapter

        if (events.size >= eventsVM.limit) {
            scrollListener = object : EndlessScrollListener(linearLayoutManager, visibleThreshold) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    eventsVM.currentPage = page
                    eventsVM.fetchEvents(page)
                }
            }

            recyclerView.addOnScrollListener(scrollListener)
        }
    }

    private fun displayEvents(listEvents: List<EventData>) {
        recyclerView.visibility = View.VISIBLE
        if (eventsVM.currentPage == eventsVM.startingPage) {
            setupRecyclerView(listEvents, (eventsVM.visibleThreshold - NumberUtils.INTEGER_ONE))
        } else {
            eventsAdapter.setEventsList(listEvents)
            eventsAdapter.notifyDataSetChanged()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonTryAgain, R.id.buttonLoadMore -> {
                relativeLayoutLoadMoreError.visibility = View.GONE
                eventsVM.fetchEvents(eventsVM.currentPage)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
