import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.expensemanagerapp.R
import com.example.expensemanagerapp.databinding.FragmentAddExpenseBinding
import com.example.expensemanagerapp.viewmodels.ExpenseViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseFragment : Fragment() {
    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategorySpinner()
        setupDatePicker()
        setupSaveButton()
    }

    private fun setupCategorySpinner() {
        val categories = resources.getStringArray(R.array.expense_categories)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter
    }

    private fun setupDatePicker() {
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        binding.dateEditText.setText(currentDate)
        binding.dateEditText.setOnClickListener {
            // TODO: Implement date picker dialog
        }
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            if (validateInput()) {
                saveExpense()
                findNavController().navigate(R.id.action_add_to_list)
            }
        }
    }

    private fun validateInput(): Boolean {
        return when {
            binding.amountEditText.text.isNullOrEmpty() -> {
                showError("Please enter amount")
                false
            }
            binding.amountEditText.text.toString().toDoubleOrNull() == null -> {
                showError("Please enter valid amount")
                false
            }
            else -> true
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun saveExpense() {
        val expense = ExpenseEntity(
            amount = binding.amountEditText.text.toString().toDouble(),
            category = binding.categorySpinner.selectedItem.toString(),
            date = binding.dateEditText.text.toString(),
            description = binding.descriptionEditText.text.toString()
        )
        viewModel.insert(expense)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}