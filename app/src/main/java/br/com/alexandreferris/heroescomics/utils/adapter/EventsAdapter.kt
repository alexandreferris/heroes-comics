package br.com.alexandreferris.heroescomics.utils.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.alexandreferris.heroescomics.R
import br.com.alexandreferris.heroescomics.model.EventData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_recyclerview_grid.view.*

class EventsAdapter(private val itemClickListener: (Int) -> Unit) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    private lateinit var events: List<EventData>

    fun setEventsList(events: List<EventData>) {
        this.events = events
    }

    // Listener adapter for setOnClickListener
    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(events[adapterPosition].id)
        }
        return this
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(event: EventData) {
            itemView.textViewName.text = event.title

            Glide.with(itemView)
                .load(event.thumbnail?.path + "/landscape_large." + event.thumbnail?.extension)
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

    override fun getItemCount() = events.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(events[position])
    }
}
