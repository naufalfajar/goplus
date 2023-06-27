package id.naufalfajar.go.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.naufalfajar.go.R
import id.naufalfajar.go.model.Schedule

class ReminderAdapter(private val reminders: List<Schedule>) :
    RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewLocation: TextView = itemView.findViewById(R.id.tv_place_name)
        val textViewDateTime: TextView = itemView.findViewById(R.id.tv_datetime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return ReminderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.textViewLocation.text = reminder.location
        holder.textViewDateTime.text = reminder.dateTime
    }

    override fun getItemCount() = reminders.size
}