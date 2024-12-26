package _homeScreen

import _homeScreen.DataBase.PartnerProfile
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidfitness.R

class PartnerAdapter(
    private val partnerList: List<PartnerProfile>,
    private val onCardClick: (PartnerProfile) -> Unit // Callback for card click
) : RecyclerView.Adapter<PartnerAdapter.PartnerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.partner_card_item, parent, false)
        return PartnerViewHolder(view, onCardClick, partnerList) // Pass partnerList to ViewHolder
    }

    override fun onBindViewHolder(holder: PartnerViewHolder, position: Int) {
        val partner = partnerList[position]
        holder.bind(partner)
    }

    override fun getItemCount(): Int = partnerList.size

    class PartnerViewHolder(
        itemView: View,
        private val onCardClick: (PartnerProfile) -> Unit,
        private val partnerList: List<PartnerProfile>
    ) : RecyclerView.ViewHolder(itemView) {

        private val partnerNome: TextView = itemView.findViewById(R.id.partnerNome)
        private val partnerIdade: TextView = itemView.findViewById(R.id.partnerIdade)
        private val partnerPicture: ImageView = itemView.findViewById(R.id.partnerPicture)

        fun bind(partner: PartnerProfile) {
            partnerNome.text = partner.nome
            partnerIdade.text = "Age: ${partner.idade}"

            // Load the partner picture using Glide
            Glide.with(itemView.context)
                .load(partner.picture)
                .placeholder(R.drawable.ic_launcher)
                .into(partnerPicture)

            // Handle card click
            itemView.setOnClickListener {
                onCardClick(partner)
            }
        }
    }

}
