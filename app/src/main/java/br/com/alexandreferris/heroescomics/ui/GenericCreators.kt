package br.com.alexandreferris.heroescomics.ui


import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import br.com.alexandreferris.heroescomics.App
import br.com.alexandreferris.heroescomics.R
import br.com.alexandreferris.heroescomics.model.CreatorData
import br.com.alexandreferris.heroescomics.ui.creator.CreatorDetails
import br.com.alexandreferris.heroescomics.utils.adapter.EndlessScrollListener
import br.com.alexandreferris.heroescomics.utils.adapter.CreatorsAdapter
import br.com.alexandreferris.heroescomics.utils.loadAdBanner
import br.com.alexandreferris.heroescomics.viewmodel.GenericCreatorsVM
import kotlinx.android.synthetic.main.activity_generic_list.*
import org.apache.commons.lang3.math.NumberUtils
import javax.inject.Inject

class GenericCreators: AppCompatActivity(), View.OnClickListener {

    @Inject
    lateinit var genericCreatorsVM: GenericCreatorsVM

    private lateinit var creatorsAdapter: CreatorsAdapter
    private lateinit var scrollListener: EndlessScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic_list)

        loadAdBanner(adView)

        (application as App).appComponent.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra("EXTRA_NAME")

        genericCreatorsVM.extraType = intent.getStringExtra("EXTRA_TYPE")
        genericCreatorsVM.extraId = intent.getIntExtra("EXTRA_ID", NumberUtils.INTEGER_ZERO)
        genericCreatorsVM.fetchGenericCreators()

        buttonTryAgain.setOnClickListener(this)
        buttonLoadMore.setOnClickListener(this)
        observableViewModel()
    }

    private fun observableViewModel() {
        genericCreatorsVM.getLoading().observe(this, Observer {
            loadingBar.visibility = if (it!!) View.VISIBLE else View.GONE
        })

        genericCreatorsVM.getError().observe(this, Observer {
            hideShowLoadMoreAndNoSearchResult(it!!)
        })

        genericCreatorsVM.getCreators().observe(this, Observer {
            hideShowLoadMoreAndNoSearchResult(false)
            hideShowRecyclerViewNoResult(it!!)
        })
    }

    private fun hideShowLoadMoreAndNoSearchResult(show: Boolean) {
        relativeLayoutLoadMoreError.visibility = View.GONE
        constraintLayoutNoResult.visibility = View.GONE

        if (show) {
            val booleanShowError = (genericCreatorsVM.currentPage == genericCreatorsVM.startingPage)

            if (!booleanShowError)
                Snackbar.make(constraintLayoutHome, R.string.error_loading_heroes, Snackbar.LENGTH_LONG).show()
            relativeLayoutLoadMoreError.visibility = if (!booleanShowError) View.VISIBLE else View.GONE
            constraintLayoutNoResult.visibility = if (booleanShowError) View.VISIBLE else View.GONE
        }
    }

    private fun hideShowRecyclerViewNoResult(list: List<CreatorData>) {
        val booleanShowError = (list.size == NumberUtils.INTEGER_ZERO)

        recyclerView.visibility = if (!booleanShowError) View.VISIBLE else View.GONE

        if (!booleanShowError)
            displayCreators(list)
    }

    private fun setupRecyclerView(creators: List<CreatorData>, visibleThreshold: Int) {
        val gridLayoutManager = GridLayoutManager(this, NumberUtils.INTEGER_TWO)
        recyclerView.layoutManager = gridLayoutManager

        creatorsAdapter = CreatorsAdapter(this) { creatorId ->
            val creatorSelectedDetails = Intent(this, CreatorDetails::class.java)
            creatorSelectedDetails.putExtra("CREATOR_ID", creatorId)

            startActivity(creatorSelectedDetails)
        }
        creatorsAdapter.setCreatorsList(creators)
        recyclerView.adapter = creatorsAdapter

        scrollListener = object : EndlessScrollListener(gridLayoutManager, visibleThreshold) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                genericCreatorsVM.currentPage = page
                genericCreatorsVM.fetchGenericCreators(page)
            }
        }

        recyclerView.addOnScrollListener(scrollListener)
    }

    private fun displayCreators(listCreators: List<CreatorData>) {
        recyclerView.visibility = View.VISIBLE
        if (genericCreatorsVM.currentPage == genericCreatorsVM.startingPage) {
            setupRecyclerView(listCreators, (genericCreatorsVM.visibleThreshold - NumberUtils.INTEGER_ONE))
        } else {
            creatorsAdapter.setCreatorsList(listCreators)
            creatorsAdapter.notifyDataSetChanged()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonTryAgain, R.id.buttonLoadMore -> {
                relativeLayoutLoadMoreError.visibility = View.GONE
                genericCreatorsVM.fetchGenericCreators(genericCreatorsVM.currentPage)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}