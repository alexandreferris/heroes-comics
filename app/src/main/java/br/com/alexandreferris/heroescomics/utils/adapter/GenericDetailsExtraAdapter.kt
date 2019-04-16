package br.com.alexandreferris.heroescomics.utils.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.alexandreferris.heroescomics.R
import br.com.alexandreferris.heroescomics.model.GenericExtra
import kotlinx.android.synthetic.main.item_extra.view.*

class GenericDetailsExtraAdapter(private val context: Context, private val itemClickListener: (String) -> Unit) : RecyclerView.Adapter<GenericDetailsExtraAdapter.ViewHolder>() {

    private lateinit var genericExtras: List<GenericExtra>

    fun setCharactersExtras(characters: List<GenericExtra>) {
        this.genericExtras = characters
    }

    // Listener adapter for setOnClickListener
    fun <T : RecyclerView.ViewHolder> T.listen(event: (type: String) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(genericExtras[adapterPosition].type)
        }
        return this
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(context: Context, genericExtra: GenericExtra) {
            itemView.textViewExtraNameCount.text = context.resources.getString(R.string.extra_count, genericExtra.type, genericExtra.count)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_extra, parent, false)
        return ViewHolder(view).listen { pos ->
            itemClickListener.invoke(pos)
        }
    }

    override fun getItemCount() = genericExtras.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, genericExtras[position])
    }
}