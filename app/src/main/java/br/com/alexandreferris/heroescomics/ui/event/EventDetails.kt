package br.com.alexandreferris.heroescomics.ui.event

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import br.com.alexandreferris.heroescomics.App
import br.com.alexandreferris.heroescomics.R
import br.com.alexandreferris.heroescomics.model.EventData
import br.com.alexandreferris.heroescomics.model.GenericExtra
import br.com.alexandreferris.heroescomics.ui.GenericCharacters
import br.com.alexandreferris.heroescomics.ui.GenericCreators
import br.com.alexandreferris.heroescomics.utils.adapter.GenericDetailsExtraAdapter
import br.com.alexandreferris.heroescomics.utils.loadAdBanner
import br.com.alexandreferris.heroescomics.viewmodel.event.EventDetailsVM
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_generic_details.*
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

class EventDetails : AppCompatActivity() {

    @Inject
    lateinit var eventDetailsVM: EventDetailsVM

    private lateinit var genericDetailsExtraAdapter: GenericDetailsExtraAdapter

    private var eventId: Int = NumberUtils.INTEGER_ZERO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic_details)

        loadAdBanner(adView)

        (application as App).appComponent.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        eventId = intent.getIntExtra("EVENT_ID", NumberUtils.INTEGER_ZERO)

        observableViewModel()
        eventDetailsVM.fetchEvent(eventId)
    }

    private fun observableViewModel() {
        eventDetailsVM.getLoading().observe(this, Observer {
            loadingBar.visibility = if (it!!) View.VISIBLE else View.GONE
        })

        eventDetailsVM.getError().observe(this, Observer {
            textViewDetailsNoResult.visibility = View.VISIBLE
            recyclerViewExtras.visibility = View.GONE
        })

        eventDetailsVM.getEvent().observe(this, Observer {
            textViewDetailsNoResult.visibility = View.GONE
            recyclerViewExtras.visibility = View.VISIBLE
            displayEventDetails(it!!)
        })
    }

    private fun displayEventDetails(eventData: EventData) {
        supportActionBar?.title = eventData.title

        Glide.with(this)
            .load(eventData.thumbnail?.path + "/landscape_incredible." + eventData.thumbnail?.extension)
            .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
            .into(imageViewPosterImage)

        textViewName.text = eventData.title
        textViewDescription.text = eventData.description

        val eventExtras : ArrayList<GenericExtra> = ArrayList()
        if (eventData.creators.available > NumberUtils.INTEGER_ZERO)
            eventExtras.add(GenericExtra("Creators", eventData.creators.available))
        if (eventData.characters.available > NumberUtils.INTEGER_ZERO)
            eventExtras.add(GenericExtra("Characters", eventData.characters.available))

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewExtras.layoutManager = linearLayoutManager

        genericDetailsExtraAdapter = GenericDetailsExtraAdapter(this) { type ->

            var extraScreen = Intent()
            when (type) {
                "Characters" -> extraScreen = Intent(this, GenericCharacters::class.java)
                "Creators" -> extraScreen = Intent(this, GenericCreators::class.java)
            }

            extraScreen.putExtra("EXTRA_TYPE", "events")
            extraScreen.putExtra("EXTRA_NAME", eventData.title)
            extraScreen.putExtra("EXTRA_ID", eventId)
            startActivity(extraScreen)
        }
        genericDetailsExtraAdapter.setCharactersExtras(eventExtras)
        recyclerViewExtras.adapter = genericDetailsExtraAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}