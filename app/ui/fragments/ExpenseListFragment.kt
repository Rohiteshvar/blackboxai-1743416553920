import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensemanagerapp.R
import com.example.expensemanagerapp.databinding.FragmentExpenseListBinding
import com.example.expensemanagerapp.viewmodels.ExpenseViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExpenseListFragment : Fragment() {
    private var _binding: FragmentExpenseListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeToDelete()
        observeExpenses()
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter()
        binding.recyclerView.apply {
            adapter = expenseAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeToDelete() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: android.support.v7.widget.RecyclerView,
                viewHolder: android.support.v7.widget.RecyclerView.ViewHolder,
                target: android.support.v7.widget.RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: android.support.v7.widget.RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val expense = expenseAdapter.currentList[position]
                viewModel.delete(expense)
                showUndoSnackbar(expense)
            }
        }).attachToRecyclerView(binding.recyclerView)
    }

    private fun observeExpenses() {
        lifecycleScope.launch {
            viewModel.allExpenses.collectLatest { expenses ->
                expenseAdapter.submitList(expenses)
                binding.emptyView.visibility = if (expenses.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showUndoSnackbar(expense: ExpenseEntity) {
        Snackbar.make(binding.root, R.string.expense_deleted, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) {
                viewModel.insert(expense)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}