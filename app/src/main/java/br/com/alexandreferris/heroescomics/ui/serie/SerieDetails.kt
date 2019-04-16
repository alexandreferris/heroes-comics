package br.com.alexandreferris.heroescomics.ui.serie

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import br.com.alexandreferris.heroescomics.App
import br.com.alexandreferris.heroescomics.R
import br.com.alexandreferris.heroescomics.model.GenericExtra
import br.com.alexandreferris.heroescomics.model.SerieData
import br.com.alexandreferris.heroescomics.ui.GenericCharacters
import br.com.alexandreferris.heroescomics.ui.GenericCreators
import br.com.alexandreferris.heroescomics.utils.adapter.GenericDetailsExtraAdapter
import br.com.alexandreferris.heroescomics.utils.loadAdBanner
import br.com.alexandreferris.heroescomics.viewmodel.serie.SerieDetailsVM
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_generic_details.*
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

class SerieDetails : AppCompatActivity() {

    @Inject
    lateinit var serieDetailsVM: SerieDetailsVM

    private lateinit var genericDetailsExtraAdapter: GenericDetailsExtraAdapter

    private var serieId: Int = NumberUtils.INTEGER_ZERO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic_details)

        loadAdBanner(adView)

        (application as App).appComponent.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        serieId = intent.getIntExtra("SERIE_ID", NumberUtils.INTEGER_ZERO)

        observableViewModel()
        serieDetailsVM.fetchSerie(serieId)
    }

    private fun observableViewModel() {
        serieDetailsVM.getLoading().observe(this, Observer {
            loadingBar.visibility = if (it!!) View.VISIBLE else View.GONE
        })

        serieDetailsVM.getError().observe(this, Observer {
            textViewDetailsNoResult.visibility = View.VISIBLE
            recyclerViewExtras.visibility = View.GONE
        })

        serieDetailsVM.getSerie().observe(this, Observer {
            textViewDetailsNoResult.visibility = View.GONE
            recyclerViewExtras.visibility = View.VISIBLE
            displaySerieDetails(it!!)
        })
    }

    private fun displaySerieDetails(serieData: SerieData) {
        supportActionBar?.title = serieData.title

        Glide.with(this)
            .load(serieData.thumbnail?.path + "/landscape_incredible." + serieData.thumbnail?.extension)
            .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
            .into(imageViewPosterImage)

        textViewName.text = serieData.title
        textViewDescription.text = serieData.description

        val serieExtras : ArrayList<GenericExtra> = ArrayList()
        if (serieData.creators.available > NumberUtils.INTEGER_ZERO)
            serieExtras.add(GenericExtra("Creators", serieData.creators.available))
        if (serieData.characters.available > NumberUtils.INTEGER_ZERO)
            serieExtras.add(GenericExtra("Characters", serieData.characters.available))

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewExtras.layoutManager = linearLayoutManager

        genericDetailsExtraAdapter = GenericDetailsExtraAdapter(this) { type ->

            var extraScreen = Intent()
            when (type) {
                "Characters" -> extraScreen = Intent(this, GenericCharacters::class.java)
                "Creators" -> extraScreen = Intent(this, GenericCreators::class.java)
            }

            extraScreen.putExtra("EXTRA_TYPE", "series")
            extraScreen.putExtra("EXTRA_NAME", serieData.title)
            extraScreen.putExtra("EXTRA_ID", serieId)
            startActivity(extraScreen)
        }
        genericDetailsExtraAdapter.setCharactersExtras(serieExtras)
        recyclerViewExtras.adapter = genericDetailsExtraAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}