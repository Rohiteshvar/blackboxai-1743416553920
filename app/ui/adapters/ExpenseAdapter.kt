import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanagerapp.data.ExpenseEntity
import com.example.expensemanagerapp.databinding.ItemExpenseBinding
import java.text.NumberFormat
import java.util.*

class ExpenseAdapter : ListAdapter<ExpenseEntity, ExpenseAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExpenseViewHolder(
        private val binding: ItemExpenseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: ExpenseEntity) {
            binding.apply {
                amountText.text = NumberFormat.getCurrencyInstance().format(expense.amount)
                categoryText.text = expense.category
                dateText.text = expense.date
                descriptionText.text = expense.description

                // Set category icon based on expense type
                val iconRes = when (expense.category.toLowerCase(Locale.ROOT)) {
                    "food" -> R.drawable.ic_food
                    "transport" -> R.drawable.ic_transport
                    "bills" -> R.drawable.ic_bills
                    "shopping" -> R.drawable.ic_shopping
                    "entertainment" -> R.drawable.ic_entertainment
                    else -> R.drawable.ic_other
                }
                categoryIcon.setImageResource(iconRes)
            }
        }
    }
}

private class ExpenseDiffCallback : DiffUtil.ItemCallback<ExpenseEntity>() {
    override fun areItemsTheSame(oldItem: ExpenseEntity, newItem: ExpenseEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ExpenseEntity, newItem: ExpenseEntity): Boolean {
        return oldItem == newItem
    }
}