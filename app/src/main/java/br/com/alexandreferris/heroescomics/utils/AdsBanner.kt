package br.com.alexandreferris.heroescomics.utils

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

fun loadAdBanner(adView: AdView) {
    val adRequest = AdRequest.Builder().build()
    adView.loadAd(adRequest)
}