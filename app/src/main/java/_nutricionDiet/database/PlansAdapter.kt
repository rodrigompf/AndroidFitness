package _nutricionDiet.database

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfitness.R

class DayPlanAdapter(private val items: MutableList<DayPlanItem>, private val showDeleteButton: Boolean) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_TEXT_PLAN = 0
        private const val VIEW_TYPE_PLAN = 1
    }

    fun getItems(): List<DayPlanItem> {
        return items
    }

    // Method to update the adapter's data
    fun updateItems(newItems: List<DayPlanItem>) {
        items.clear()  // Clear the existing data
        items.addAll(newItems)  // Add new data
        notifyDataSetChanged()  // Notify RecyclerView about the data change
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DayPlanItem.TextPlan -> VIEW_TYPE_TEXT_PLAN
            is DayPlanItem.Plan -> VIEW_TYPE_PLAN
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_TEXT_PLAN) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_text_plan, parent, false)
            TextPlanViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_plan, parent, false)
            PlanViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is DayPlanItem.TextPlan -> (holder as TextPlanViewHolder).bind(item)
            is DayPlanItem.Plan -> (holder as PlanViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item: DayPlanItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    // ViewHolder for Text Plan
    inner class TextPlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textPlanEditText: EditText = itemView.findViewById(R.id.textPlanEditText)
        private val deleteTextPlanButton: Button = itemView.findViewById(R.id.deleteTextPlanButton)

        private val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val item = items[adapterPosition] as DayPlanItem.TextPlan
                item.content = s?.toString() ?: ""
            }
        }

        fun bind(item: DayPlanItem.TextPlan) {
            textPlanEditText.setText(item.content)
            textPlanEditText.removeTextChangedListener(textWatcher)
            textPlanEditText.addTextChangedListener(textWatcher)

            // Set the delete button visibility based on the showDeleteButton flag
            deleteTextPlanButton.visibility = if (showDeleteButton) View.VISIBLE else View.GONE

            // Set up the delete button click listener
            deleteTextPlanButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    items.removeAt(position)  // Remove item from the list
                    notifyItemRemoved(position)  // Notify the adapter about the removal
                }
            }
        }
    }

    // ViewHolder for Plan (with title, description, calories)
    inner class PlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleEditText: EditText = itemView.findViewById(R.id.planTitleEditText)
        private val descriptionEditText: EditText =
            itemView.findViewById(R.id.planDescriptionEditText)
        private val caloriesEditText: EditText = itemView.findViewById(R.id.planCaloriesEditText)
        private val deletePlanButton: Button = itemView.findViewById(R.id.deletePlanButton)

        private val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val item = items[adapterPosition] as DayPlanItem.Plan
                item.title = s?.toString() ?: ""
            }
        }

        private val descriptionWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val item = items[adapterPosition] as DayPlanItem.Plan
                item.description = s?.toString() ?: ""
            }
        }

        private val caloriesWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val item = items[adapterPosition] as DayPlanItem.Plan
                item.calories = s?.toString()?.toIntOrNull() ?: 0
            }
        }

        fun bind(item: DayPlanItem.Plan) {
            titleEditText.setText(item.title)
            descriptionEditText.setText(item.description)
            caloriesEditText.setText(item.calories.toString())

            titleEditText.removeTextChangedListener(titleWatcher)
            descriptionEditText.removeTextChangedListener(descriptionWatcher)
            caloriesEditText.removeTextChangedListener(caloriesWatcher)

            titleEditText.addTextChangedListener(titleWatcher)
            descriptionEditText.addTextChangedListener(descriptionWatcher)
            caloriesEditText.addTextChangedListener(caloriesWatcher)

            // Set the delete button visibility based on the showDeleteButton flag
            deletePlanButton.visibility = if (showDeleteButton) View.VISIBLE else View.GONE

            // Set up the delete button click listener
            deletePlanButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    items.removeAt(position)  // Remove item from the list
                    notifyItemRemoved(position)  // Notify the adapter about the removal
                }
            }
        }
    }
}

