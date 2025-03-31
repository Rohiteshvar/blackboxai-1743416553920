import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ExpenseRepository(
        ExpenseDatabase.getDatabase(application).expenseDao()
    )

    val allExpenses: LiveData<List<ExpenseEntity>> = repository.allExpenses
    val totalExpenses: LiveData<Double> = repository.totalExpenses
    private val _currentCategory = MutableLiveData<String>()
    val expensesByCategory: LiveData<List<ExpenseEntity>> = 
        repository.getExpensesByCategory(_currentCategory.value ?: "")

    fun insert(expense: ExpenseEntity) = viewModelScope.launch {
        repository.insert(expense)
    }

    fun delete(expense: ExpenseEntity) = viewModelScope.launch {
        repository.delete(expense)
    }

    fun setCategoryFilter(category: String) {
        _currentCategory.value = category
    }
}