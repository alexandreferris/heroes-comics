package br.com.alexandreferris.heroescomics.ui.storie

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import br.com.alexandreferris.heroescomics.App
import br.com.alexandreferris.heroescomics.R
import br.com.alexandreferris.heroescomics.model.GenericExtra
import br.com.alexandreferris.heroescomics.model.StorieData
import br.com.alexandreferris.heroescomics.ui.GenericCharacters
import br.com.alexandreferris.heroescomics.ui.GenericCreators
import br.com.alexandreferris.heroescomics.utils.adapter.GenericDetailsExtraAdapter
import br.com.alexandreferris.heroescomics.utils.loadAdBanner
import br.com.alexandreferris.heroescomics.viewmodel.storie.StorieDetailsVM
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_generic_details.*
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

class StorieDetails : AppCompatActivity() {

    @Inject
    lateinit var storieDetailsVM: StorieDetailsVM

    private lateinit var genericDetailsExtraAdapter: GenericDetailsExtraAdapter

    private var storieId: Int = NumberUtils.INTEGER_ZERO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic_details)

        loadAdBanner(adView)

        (application as App).appComponent.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        storieId = intent.getIntExtra("STORIE_ID", NumberUtils.INTEGER_ZERO)

        observableViewModel()
        storieDetailsVM.fetchStorie(storieId)
    }

    private fun observableViewModel() {
        storieDetailsVM.getLoading().observe(this, Observer {
            loadingBar.visibility = if (it!!) View.VISIBLE else View.GONE
        })

        storieDetailsVM.getError().observe(this, Observer {
            textViewDetailsNoResult.visibility = View.VISIBLE
            recyclerViewExtras.visibility = View.GONE
        })

        storieDetailsVM.getStorie().observe(this, Observer {
            textViewDetailsNoResult.visibility = View.GONE
            recyclerViewExtras.visibility = View.VISIBLE
            displayStorieDetails(it!!)
        })
    }

    private fun displayStorieDetails(storieData: StorieData) {
        supportActionBar?.title = storieData.title

        Glide.with(this)
            .load(storieData.thumbnail?.path + "/landscape_incredible." + storieData.thumbnail?.extension)
            .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
            .into(imageViewPosterImage)

        textViewName.text = storieData.title
        textViewDescription.text = storieData.description

        val storiesExtras : ArrayList<GenericExtra> = ArrayList()
        if (storieData.creators.available > NumberUtils.INTEGER_ZERO)
            storiesExtras.add(GenericExtra("Creators", storieData.creators.available))
        if (storieData.characters.available > NumberUtils.INTEGER_ZERO)
            storiesExtras.add(GenericExtra("Characters", storieData.characters.available))

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewExtras.layoutManager = linearLayoutManager

        genericDetailsExtraAdapter = GenericDetailsExtraAdapter(this) { type ->

            var extraScreen = Intent()
            when (type) {
                "Characters" -> extraScreen = Intent(this, GenericCharacters::class.java)
                "Creators" -> extraScreen = Intent(this, GenericCreators::class.java)
            }

            extraScreen.putExtra("EXTRA_TYPE", "stories")
            extraScreen.putExtra("EXTRA_NAME", storieData.title)
            extraScreen.putExtra("EXTRA_ID", storieId)
            startActivity(extraScreen)
        }
        genericDetailsExtraAdapter.setCharactersExtras(storiesExtras)
        recyclerViewExtras.adapter = genericDetailsExtraAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}