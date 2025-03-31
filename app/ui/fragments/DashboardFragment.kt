import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.expensemanagerapp.databinding.FragmentDashboardBinding
import com.example.expensemanagerapp.viewmodels.ExpenseViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.NumberFormat
import java.util.*

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChart()
        observeExpenses()
    }

    private fun setupChart() {
        with(binding.pieChart) {
            setUsePercentValues(true)
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            holeRadius = 45f
            transparentCircleRadius = 50f
            setDrawEntryLabels(false)
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            animateY(1400, Easing.EaseInOutQuad)
            legend.isEnabled = false
        }
    }

    private fun observeExpenses() {
        viewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->
            if (expenses.isNotEmpty()) {
                updateChart(expenses)
                updateSummary(expenses)
            }
        }
    }

    private fun updateChart(expenses: List<ExpenseEntity>) {
        val categoryMap = expenses.groupBy { it.category }
        val entries = categoryMap.map { (category, items) ->
            PieEntry(items.sumOf { it.amount }.toFloat(), category)
        }

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                Color.parseColor("#FF6384"),
                Color.parseColor("#36A2EB"),
                Color.parseColor("#FFCE56"),
                Color.parseColor("#4BC0C0"),
                Color.parseColor("#9966FF")
            )
            valueTextColor = Color.WHITE
            valueTextSize = 12f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return NumberFormat.getCurrencyInstance().format(value.toDouble())
                }
            }
        }

        binding.pieChart.data = PieData(dataSet)
        binding.pieChart.invalidate()
    }

    private fun updateSummary(expenses: List<ExpenseEntity>) {
        val total = expenses.sumOf { it.amount }
        val average = if (expenses.isNotEmpty()) total / expenses.size else 0.0

        binding.totalText.text = NumberFormat.getCurrencyInstance().format(total)
        binding.averageText.text = NumberFormat.getCurrencyInstance().format(average)
        binding.countText.text = expenses.size.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}