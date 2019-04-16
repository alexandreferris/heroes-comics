package br.com.alexandreferris.heroescomics.utils.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.alexandreferris.heroescomics.R
import br.com.alexandreferris.heroescomics.model.SerieData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_recyclerview_grid.view.*

class SeriesAdapter(private val itemClickListener: (Int) -> Unit) : RecyclerView.Adapter<SeriesAdapter.ViewHolder>() {

    private lateinit var series: List<SerieData>

    fun setSeriesList(series: List<SerieData>) {
        this.series = series
    }

    // Listener adapter for setOnClickListener
    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(series[adapterPosition].id)
        }
        return this
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(serie: SerieData) {
            itemView.textViewName.text = serie.title

            Glide.with(itemView)
                .load(serie.thumbnail?.path + "/landscape_large." + serie.thumbnail?.extension)
                .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                .into(itemView.posterImageView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview_grid, parent, false)
        return ViewHolder(view).listen { pos ->
            itemClickListener.invoke(pos)
        }
    }

    override fun getItemCount() = series.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(series[position])
    }
}
