package _homeScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import _homeScreen.DataBase.PartnerProfile
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.androidfitness.R

class PartnerAdapter(private val partnerList: List<PartnerProfile>) : RecyclerView.Adapter<PartnerAdapter.PartnerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.partner_card_item, parent, false)
        return PartnerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PartnerViewHolder, position: Int) {
        val partner = partnerList[position]
        holder.bind(partner)
    }

    override fun getItemCount(): Int {
        return partnerList.size
    }

    class PartnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val partnerNome: TextView = itemView.findViewById(R.id.partnerNome)
        private val partnerIdade: TextView = itemView.findViewById(R.id.partnerIdade)
        private val partnerDescricao: TextView = itemView.findViewById(R.id.partnerDescricao)
        private val partnerPicture: ImageView = itemView.findViewById(R.id.partnerPicture)

        fun bind(partner: PartnerProfile) {
            // Bind data to UI elements
            partnerNome.text = partner.nome
            partnerIdade.text = partner.idade.toString()
            partnerDescricao.text = partner.descricao

            // Load image using Glide
            Glide.with(itemView.context)
                .load(partner.picture)
                .placeholder(R.drawable.ic_launcher)
                .into(partnerPicture)
        }
    }
}

