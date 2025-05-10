package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import kotlin.math.*
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private lateinit var display: TextView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private var currentValue = 0.0
    private var lastOperation = ""
    private var newNumber = true
    private var memoryValue = 0.0
    private val calculationHistory = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.display)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        sharedPreferences = getSharedPreferences("CalculatorPrefs", MODE_PRIVATE)
        
        setupHistoryRecyclerView()
        setupNumberButtons()
        setupOperationButtons()
        setupMemoryButtons()
        setupSpecialButtons()
    }

    private fun setupHistoryRecyclerView() {
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        // TODO: Implement HistoryAdapter
    }

    private fun setupNumberButtons() {
        val numberButtons = arrayOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnDecimal
        )

        numberButtons.forEach { id ->
            findViewById<MaterialButton>(id).setOnClickListener {
                onNumberClick((it as MaterialButton).text.toString())
            }
        }
    }

    private fun setupOperationButtons() {
        val operationButtons = arrayOf(
            R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply,
            R.id.btnDivide, R.id.btnPercent, R.id.btnSqrt,
            R.id.btnSin, R.id.btnCos, R.id.btnTan,
            R.id.btnLog, R.id.btnLn, R.id.btnPow,
            R.id.btnFact
        )

        operationButtons.forEach { id ->
            findViewById<MaterialButton>(id).setOnClickListener {
                onOperationClick((it as MaterialButton).text.toString())
            }
        }
    }

    private fun setupMemoryButtons() {
        findViewById<MaterialButton>(R.id.btnMC).setOnClickListener { onMemoryClear() }
        findViewById<MaterialButton>(R.id.btnMR).setOnClickListener { onMemoryRecall() }
        findViewById<MaterialButton>(R.id.btnMPlus).setOnClickListener { onMemoryAdd() }
        findViewById<MaterialButton>(R.id.btnMMinus).setOnClickListener { onMemorySubtract() }
    }

    private fun setupSpecialButtons() {
        findViewById<MaterialButton>(R.id.btnClear).setOnClickListener { onClearClick() }
        findViewById<MaterialButton>(R.id.btnEquals).setOnClickListener { onEqualsClick() }
        findViewById<MaterialButton>(R.id.btnUnitConv).setOnClickListener { showUnitConverter() }
        findViewById<MaterialButton>(R.id.btnCGPA).setOnClickListener { showCGPACalculator() }
        findViewById<MaterialButton>(R.id.btnHistory).setOnClickListener { toggleHistory() }
    }

    private fun onNumberClick(number: String) {
        if (newNumber) {
            display.text = number
            newNumber = false
        } else {
            if (number == "." && display.text.contains(".")) return
            display.append(number)
        }
    }

    private fun onOperationClick(operation: String) {
        val currentNumber = display.text.toString().toDoubleOrNull() ?: return
        
        try {
            when (operation) {
                "+" -> currentValue += currentNumber
                "-" -> currentValue -= currentNumber
                "×" -> currentValue *= currentNumber
                "÷" -> if (currentNumber != 0.0) currentValue /= currentNumber else throw ArithmeticException("Division by zero")
                "%" -> currentValue = (currentValue * currentNumber) / 100
                "√" -> currentValue = sqrt(currentNumber)
                "sin" -> currentValue = sin(currentNumber * PI / 180)
                "cos" -> currentValue = cos(currentNumber * PI / 180)
                "tan" -> currentValue = tan(currentNumber * PI / 180)
                "log" -> if (currentNumber > 0) currentValue = log10(currentNumber) else throw ArithmeticException("Invalid input for logarithm")
                "ln" -> if (currentNumber > 0) currentValue = ln(currentNumber) else throw ArithmeticException("Invalid input for natural logarithm")
                "x^y" -> currentValue = currentValue.pow(currentNumber)
                "x!" -> if (currentNumber >= 0 && currentNumber <= 170) currentValue = factorial(currentNumber.toInt()).toDouble() else throw ArithmeticException("Factorial too large")
            }
            
            addToHistory("$currentNumber $operation = $currentValue")
            display.text = formatResult(currentValue)
            lastOperation = operation
            newNumber = true
        } catch (e: ArithmeticException) {
            display.text = "Error: ${e.message}"
            newNumber = true
        }
    }

    private fun onMemoryClear() {
        memoryValue = 0.0
        Toast.makeText(this, "Memory cleared", Toast.LENGTH_SHORT).show()
    }

    private fun onMemoryRecall() {
        display.text = formatResult(memoryValue)
        newNumber = true
    }

    private fun onMemoryAdd() {
        val currentNumber = display.text.toString().toDoubleOrNull() ?: return
        memoryValue += currentNumber
        Toast.makeText(this, "Added to memory", Toast.LENGTH_SHORT).show()
    }

    private fun onMemorySubtract() {
        val currentNumber = display.text.toString().toDoubleOrNull() ?: return
        memoryValue -= currentNumber
        Toast.makeText(this, "Subtracted from memory", Toast.LENGTH_SHORT).show()
    }

    private fun showUnitConverter() {
        val dialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_unit_converter, null)
        
        dialog.setView(dialogView)
            .setTitle("Unit Converter")
            .setPositiveButton("Convert") { _, _ ->
                // TODO: Implement unit conversion logic
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCGPACalculator() {
        val dialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_cgpa_calculator, null)
        
        dialog.setView(dialogView)
            .setTitle("CGPA Calculator")
            .setPositiveButton("Calculate") { _, _ ->
                // TODO: Implement CGPA calculation logic
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun toggleHistory() {
        historyRecyclerView.visibility = if (historyRecyclerView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun addToHistory(calculation: String) {
        calculationHistory.add(0, calculation)
        // TODO: Update RecyclerView adapter
    }

    private fun formatResult(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            String.format("%.8f", value).trimEnd('0').trimEnd('.')
        }
    }

    private fun factorial(n: Int): Long {
        return if (n <= 1) 1 else n * factorial(n - 1)
    }

    private fun onClearClick() {
        currentValue = 0.0
        lastOperation = ""
        newNumber = true
        display.text = "0"
    }

    private fun onEqualsClick() {
        if (lastOperation.isNotEmpty()) {
            val currentNumber = display.text.toString().toDoubleOrNull() ?: return
            onOperationClick(lastOperation)
        }
    }
} 