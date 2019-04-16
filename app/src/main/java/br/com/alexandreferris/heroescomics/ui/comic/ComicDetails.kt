package br.com.alexandreferris.heroescomics.ui.comic

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import br.com.alexandreferris.heroescomics.App
import br.com.alexandreferris.heroescomics.R
import br.com.alexandreferris.heroescomics.model.ComicData
import br.com.alexandreferris.heroescomics.model.GenericExtra
import br.com.alexandreferris.heroescomics.ui.GenericCharacters
import br.com.alexandreferris.heroescomics.ui.GenericCreators
import br.com.alexandreferris.heroescomics.utils.adapter.GenericDetailsExtraAdapter
import br.com.alexandreferris.heroescomics.utils.loadAdBanner
import br.com.alexandreferris.heroescomics.viewmodel.comic.ComicDetailsVM
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_generic_details.*
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

class ComicDetails : AppCompatActivity() {

    @Inject
    lateinit var comicDetailsVM: ComicDetailsVM

    private lateinit var genericDetailsExtraAdapter: GenericDetailsExtraAdapter

    private var comicId: Int = NumberUtils.INTEGER_ZERO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic_details)

        loadAdBanner(adView)

        (application as App).appComponent.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        comicId = intent.getIntExtra("COMIC_ID", NumberUtils.INTEGER_ZERO)

        observableViewModel()
        comicDetailsVM.fetchComic(comicId)
    }

    private fun observableViewModel() {
        comicDetailsVM.getLoading().observe(this, Observer {
            loadingBar.visibility = if (it!!) View.VISIBLE else View.GONE
        })

        comicDetailsVM.getError().observe(this, Observer {
            textViewDetailsNoResult.visibility = View.VISIBLE
            recyclerViewExtras.visibility = View.GONE
        })

        comicDetailsVM.getComic().observe(this, Observer {
            textViewDetailsNoResult.visibility = View.GONE
            recyclerViewExtras.visibility = View.VISIBLE
            displayComicDetails(it!!)
        })
    }

    private fun displayComicDetails(comicData: ComicData) {
        supportActionBar?.title = comicData.title

        Glide.with(this)
            .load(comicData.thumbnail?.path + "/landscape_incredible." + comicData.thumbnail?.extension)
            .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
            .into(imageViewPosterImage)

        textViewName.text = comicData.title
        textViewDescription.text = comicData.description

        val comicExtras : ArrayList<GenericExtra> = ArrayList()
        if (comicData.creators.available > NumberUtils.INTEGER_ZERO)
            comicExtras.add(GenericExtra("Creators", comicData.creators.available))
        if (comicData.characters.available > NumberUtils.INTEGER_ZERO)
            comicExtras.add(GenericExtra("Characters", comicData.characters.available))

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewExtras.layoutManager = linearLayoutManager

        genericDetailsExtraAdapter = GenericDetailsExtraAdapter(this) { type ->

            var extraScreen = Intent()
            when (type) {
                "Characters" -> extraScreen = Intent(this, GenericCharacters::class.java)
                "Creators" -> extraScreen = Intent(this, GenericCreators::class.java)
            }

            extraScreen.putExtra("EXTRA_TYPE", "comics")
            extraScreen.putExtra("EXTRA_NAME", comicData.title)
            extraScreen.putExtra("EXTRA_ID", comicId)
            startActivity(extraScreen)
        }
        genericDetailsExtraAdapter.setCharactersExtras(comicExtras)
        recyclerViewExtras.adapter = genericDetailsExtraAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
