package id.naufalfajar.go.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.naufalfajar.go.databinding.ItemPlaceBinding
import id.naufalfajar.go.model.Place
import id.naufalfajar.go.view.GoPlusFragmentDirections
import id.naufalfajar.go.view.HomeFragmentDirections

class GoPlaceAdapter(
    private val placeList: ArrayList<Place>
): RecyclerView.Adapter<GoPlaceAdapter.PlaceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(placeList[position])
    }

    override fun getItemCount(): Int = placeList.size

    inner class PlaceViewHolder(private val binding: ItemPlaceBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(item: Place) {
            binding.apply {
                tvPlaceName.text = item.name
                Glide.with(itemView.context)
                    .load(item.image)
                    .into(ivTempat)
                cvTempat.setOnClickListener {
                    it.findNavController().navigate(
                        GoPlusFragmentDirections.actionGoPlusFragmentToDetailPlaceFragment(
                        item.id!!
                    ))
                }
            }
        }
    }
}