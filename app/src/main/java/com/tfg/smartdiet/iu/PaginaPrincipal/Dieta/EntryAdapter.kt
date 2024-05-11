import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tfg.smartdiet.R

class EntryAdapter(private val entries: MutableList<HashMap<String, Any>>, private val listener: OnItemLongClickListener) : RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {

    inner class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {
        val textViewNombre: TextView = itemView.findViewById(R.id.textViewNombre)
        val textViewCalorias: TextView = itemView.findViewById(R.id.textViewCalorias)
        val textViewProteinas: TextView = itemView.findViewById(R.id.textViewProteinas)
        val textViewGrasas: TextView = itemView.findViewById(R.id.textViewGrasas)
        val textViewCarbohidratos: TextView = itemView.findViewById(R.id.textViewCarbohidratos)
        val textViewTime: TextView = itemView.findViewById(R.id.textViewTime)

        init {
            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemLongClick(position)
            }
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_entry, parent, false)
        return EntryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val currentItem = entries[position]
        holder.textViewNombre.text = currentItem["nombre"].toString()
        holder.textViewCalorias.text = currentItem["calorias"].toString()
        holder.textViewProteinas.text = currentItem["proteinas"].toString()
        holder.textViewGrasas.text = currentItem["grasas"].toString()
        holder.textViewCarbohidratos.text = currentItem["carbohidratos"].toString()
        holder.textViewTime.text = currentItem["hora"].toString()
    }

    override fun getItemCount() = entries.size

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }
}
