package com.example.newcalculator

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var editTextInput: EditText
    private lateinit var textViewResult: TextView
    private lateinit var gridLayoutKeyboard: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(findViewById(R.id.toolbar))

        editTextInput = findViewById(R.id.editTextInput)
        textViewResult = findViewById(R.id.textViewResult)
        gridLayoutKeyboard = findViewById(R.id.gridLayoutKeyboard)

        //определим поведение ClickListener для каждой кнопки
        val buttonClickListener = View.OnClickListener { view ->
            val button = view as Button
            val input = editTextInput.text.toString()
            when (button.id) {
                // "=" вычисляет введенное выражение
                R.id.buttonEquals -> calculateResult(input)
                // "C" Очищает поля ввода и вывода
                R.id.buttonClear -> {
                    editTextInput.setText("")
                    textViewResult.text = ""
                }
                //остальные кнопки передают свой текст в поле ввода
                else -> editTextInput.append(button.text)
            }
        }

        //повесим ClickListener на кнопки
        for (i in 0 until gridLayoutKeyboard.childCount) {
            val child = gridLayoutKeyboard.getChildAt(i)
            if (child is Button) {
                child.setOnClickListener(buttonClickListener)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_exit -> {
                finishAffinity()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun calculateResult(input: String) {
        try {
            val result = calc(input)
            textViewResult.text = result.toString()
        } catch (e: Exception) {
            Snackbar.make(gridLayoutKeyboard, "Ошибка ввода", Snackbar.LENGTH_SHORT).show()
        }
    }

    // Вычислим выражение
    private fun calc(expression: String): Double? {
        val regex = """(-?\d+)([\+\-\*\/])(-?\d+)""".toRegex()
        val matchResult = regex.matchEntire(expression)
        var res: Double? = 0.0

        if (matchResult != null) {
            val num1 = matchResult.groupValues[1].toDouble()
            val operator = matchResult.groupValues[2][0]
            val num2 = matchResult.groupValues[3].toDouble()

            when (operator) {
                '+' -> res = num1 + num2
                '-' -> res = num1 - num2
                '*' -> res = num1 * num2
                '/' -> res = if (num2 != 0.0) num1 / num2 else null
            }
        } else {
            Snackbar.make(gridLayoutKeyboard, "Некорректный ввод", Snackbar.LENGTH_SHORT).show()
            res = null
        }

        return res
    }
}