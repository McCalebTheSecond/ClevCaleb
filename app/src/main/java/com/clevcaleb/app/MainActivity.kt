package com.clevcaleb.app

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.clevcaleb.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var firstOperand: Double? = null
    private var pendingOp: Char? = null
    private var resetOnNextDigit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val digits = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9
        )
        digits.forEach { button ->
            button.setOnClickListener { onDigit((it as Button).text.toString()) }
        }

        binding.btnDot.setOnClickListener { onDot() }
        binding.btnClear.setOnClickListener { clearAll() }
        binding.btnBack.setOnClickListener { onBackspace() }

        binding.btnAdd.setOnClickListener { onOperator('+') }
        binding.btnSub.setOnClickListener { onOperator('-') }
        binding.btnMul.setOnClickListener { onOperator('\u00D7') }
        binding.btnDiv.setOnClickListener { onOperator('\u00F7') }
        binding.btnEquals.setOnClickListener { onEquals() }

        clearAll()
    }

    private fun currentValue(): Double =
        binding.display.text.toString().toDoubleOrNull() ?: 0.0

    private fun onDigit(digit: String) {
        val current = binding.display.text.toString()
        if (resetOnNextDigit || current == "0") {
            binding.display.text = digit
            resetOnNextDigit = false
        } else {
            binding.display.text = current + digit
        }
    }

    private fun onDot() {
        if (resetOnNextDigit) {
            binding.display.text = "0"
            resetOnNextDigit = false
        }
        if (!binding.display.text.contains('.')) {
            binding.display.text = "${binding.display.text}."
        }
    }

    private fun onOperator(op: Char) {
        if (pendingOp != null && !resetOnNextDigit) {
            onEquals()
        }
        firstOperand = currentValue()
        pendingOp = op
        resetOnNextDigit = true
    }

    private fun onEquals() {
        val op = pendingOp ?: return
        val first = firstOperand ?: return
        val result = try {
            Calculator.evaluate(first, op, currentValue())
        } catch (e: IllegalArgumentException) {
            binding.display.text = "Error"
            clearState()
            return
        }
        binding.display.text = format(result)
        clearState()
        resetOnNextDigit = true
    }

    private fun onBackspace() {
        val current = binding.display.text.toString()
        binding.display.text = if (current.length <= 1) "0" else current.dropLast(1)
    }

    private fun clearAll() {
        binding.display.text = "0"
        clearState()
    }

    private fun clearState() {
        firstOperand = null
        pendingOp = null
        resetOnNextDigit = false
    }

    private fun format(value: Double): String =
        if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()
}
