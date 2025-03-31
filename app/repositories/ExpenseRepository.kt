import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val allExpenses: LiveData<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    val totalExpenses: LiveData<Double> = expenseDao.getTotalExpenses()

    suspend fun insert(expense: ExpenseEntity) {
        expenseDao.insert(expense)
    }

    suspend fun delete(expense: ExpenseEntity) {
        expenseDao.delete(expense)
    }

    fun getExpensesByCategory(category: String): LiveData<List<ExpenseEntity>> {
        return expenseDao.getExpensesByCategory(category)
    }
}