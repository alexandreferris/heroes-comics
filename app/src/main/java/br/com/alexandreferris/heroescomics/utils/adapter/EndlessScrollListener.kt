package br.com.alexandreferris.heroescomics.utils.adapter

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import org.apache.commons.lang3.math.NumberUtils

abstract class EndlessScrollListener: RecyclerView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private var visibleThreshold: Int = NumberUtils.INTEGER_ZERO
    // The current offset index of data you have loaded
    private var currentPage = NumberUtils.INTEGER_ONE
    // The total number of items in the dataset after the last load
    private var previousTotalItemCount = NumberUtils.INTEGER_ZERO
    // True if we are still waiting for the last set of data to load.
    var loading = true
    // Sets the starting page index
    private val startingPageIndex = NumberUtils.INTEGER_ONE

    private var mLayoutManager: RecyclerView.LayoutManager

    constructor(layoutManager: LinearLayoutManager, visibleThreshold: Int) {
        this.mLayoutManager = layoutManager
        this.visibleThreshold = visibleThreshold
    }

    constructor(layoutManager: GridLayoutManager, visibleThreshold: Int) {
        this.mLayoutManager = layoutManager
        visibleThreshold.times(layoutManager.spanCount)
        this.visibleThreshold = visibleThreshold
    }

    constructor(layoutManager: StaggeredGridLayoutManager, visibleThreshold: Int) {
        this.mLayoutManager = layoutManager
        visibleThreshold.times(layoutManager.spanCount)
        this.visibleThreshold = visibleThreshold
    }

    fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = NumberUtils.INTEGER_ZERO
        for (i in lastVisibleItemPositions.indices) {
            if (i == NumberUtils.INTEGER_ZERO) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        var lastVisibleItemPosition = NumberUtils.INTEGER_ZERO
        val totalItemCount = mLayoutManager.itemCount

        if (mLayoutManager is StaggeredGridLayoutManager) {
            val lastVisibleItemPositions = (mLayoutManager as StaggeredGridLayoutManager).findLastCompletelyVisibleItemPositions(null)
            // get maximum element within the list
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
        } else if (mLayoutManager is GridLayoutManager) {
            lastVisibleItemPosition = (mLayoutManager as GridLayoutManager).findLastCompletelyVisibleItemPosition()
        } else if (mLayoutManager is LinearLayoutManager) {
            lastVisibleItemPosition = (mLayoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
        }

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex
            this.previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                this.loading = true
            }
        }
        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        if (!loading && lastVisibleItemPosition >= (visibleThreshold * this.currentPage)) {
            currentPage++
            onLoadMore(currentPage, totalItemCount, recyclerView)
            loading = true
        }
    }

    // Call this method whenever performing new searches
    fun resetState() {
        this.currentPage = this.startingPageIndex
        this.previousTotalItemCount = 0
        this.loading = true
    }

    // Defines the process for actually loading more data based on page
    abstract fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?)
}