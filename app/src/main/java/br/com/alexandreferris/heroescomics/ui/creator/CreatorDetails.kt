package br.com.alexandreferris.heroescomics.ui.creator

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import br.com.alexandreferris.heroescomics.App
import br.com.alexandreferris.heroescomics.R
import br.com.alexandreferris.heroescomics.model.CreatorData
import br.com.alexandreferris.heroescomics.model.GenericExtra
import br.com.alexandreferris.heroescomics.ui.comic.Comics
import br.com.alexandreferris.heroescomics.ui.event.Events
import br.com.alexandreferris.heroescomics.ui.serie.Series
import br.com.alexandreferris.heroescomics.ui.storie.Stories
import br.com.alexandreferris.heroescomics.utils.adapter.GenericDetailsExtraAdapter
import br.com.alexandreferris.heroescomics.utils.loadAdBanner
import br.com.alexandreferris.heroescomics.viewmodel.creator.CreatorDetailsVM
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_generic_details.*
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

class CreatorDetails : AppCompatActivity() {

    @Inject
    lateinit var creatorDetailsVM: CreatorDetailsVM

    private lateinit var genericDetailsExtraAdapter: GenericDetailsExtraAdapter

    private var creatorId: Int = NumberUtils.INTEGER_ZERO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic_details)

        loadAdBanner(adView)

        (application as App).appComponent.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        creatorId = intent.getIntExtra("CREATOR_ID", NumberUtils.INTEGER_ZERO)

        observableViewModel()
        creatorDetailsVM.fetchCreator(creatorId)
    }

    private fun observableViewModel() {
        creatorDetailsVM.getLoading().observe(this, Observer {
            loadingBar.visibility = if (it!!) View.VISIBLE else View.GONE
        })

        creatorDetailsVM.getError().observe(this, Observer {
            textViewDetailsNoResult.visibility = View.VISIBLE
            recyclerViewExtras.visibility = View.GONE
        })

        creatorDetailsVM.getCreator().observe(this, Observer {
            textViewDetailsNoResult.visibility = View.GONE
            recyclerViewExtras.visibility = View.VISIBLE
            displayCreatorDetails(it!!)
        })
    }

    private fun displayCreatorDetails(creatorData: CreatorData) {
        val creatorName = resources.getString(R.string.creator_name, creatorData.firstName, creatorData.lastName)
        supportActionBar?.title = creatorName

        Glide.with(this)
            .load(creatorData.thumbnail?.path + "/landscape_incredible." + creatorData.thumbnail?.extension)
            .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
            .into(imageViewPosterImage)

        textViewName.text = creatorName

        val creatorExtras: ArrayList<GenericExtra> = ArrayList()
        if (creatorData.comics.available > NumberUtils.INTEGER_ZERO)
            creatorExtras.add(GenericExtra("Comics", creatorData.comics.available))
        if (creatorData.series.available > NumberUtils.INTEGER_ZERO)
            creatorExtras.add(GenericExtra("Series", creatorData.series.available))
        if (creatorData.stories.available > NumberUtils.INTEGER_ZERO)
            creatorExtras.add(GenericExtra("Stories", creatorData.stories.available))
        if (creatorData.events.available > NumberUtils.INTEGER_ZERO)
            creatorExtras.add(GenericExtra("Events", creatorData.events.available))

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

            extraScreen.putExtra("EXTRA_TYPE", "creators")
            extraScreen.putExtra("EXTRA_NAME", creatorName)
            extraScreen.putExtra("EXTRA_ID", creatorId)
            startActivity(extraScreen)
        }
        genericDetailsExtraAdapter.setCharactersExtras(creatorExtras)
        recyclerViewExtras.adapter = genericDetailsExtraAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
