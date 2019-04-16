package br.com.alexandreferris.heroescomics.utils.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.alexandreferris.heroescomics.R
import br.com.alexandreferris.heroescomics.model.CreatorData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_recyclerview_grid.view.*

class CreatorsAdapter(private val context: Context, private val itemClickListener: (Int) -> Unit) : RecyclerView.Adapter<CreatorsAdapter.ViewHolder>() {

    private lateinit var creators: List<CreatorData>

    fun setCreatorsList(creators: List<CreatorData>) {
        this.creators = creators
    }

    // Listener adapter for setOnClickListener
    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(creators[adapterPosition].id)
        }
        return this
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(context: Context, creator: CreatorData) {
            itemView.textViewName.text = context.resources.getString(R.string.creator_name, creator.firstName, creator.lastName)

            Glide.with(itemView)
                .load(creator.thumbnail?.path + "/landscape_large." + creator.thumbnail?.extension)
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

    override fun getItemCount() = creators.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, creators[position])
    }
}