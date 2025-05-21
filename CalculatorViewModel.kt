package com.example.calculator

import androidx.annotation.OptIn
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

class CalculatorViewModel : ViewModel() {
    private val _equationText = MutableLiveData("")
    val equationText: LiveData<String> = _equationText

    private val _resultText = MutableLiveData("0")
    val resultText: LiveData<String> = _resultText

    @OptIn(UnstableApi::class)
    fun onButtonClick(btn: String) {
        Log.d("Clicked Button", btn)

        _equationText.value?.let {
            when (btn) {
                "AC" -> {
                    _equationText.value = ""
                    _resultText.value = "0"
                    return
                }
                "C" -> {
                    if (it.isNotEmpty()) {
                        _equationText.value = it.substring(0, it.length - 1)
                        return
                    }
                }
                "=" -> {
                    _equationText.value = _resultText.value
                    return
                }
            }

            _equationText.value = it + btn

            try {
                _resultText.value = calculateResult(_equationText.value.toString())
            } catch (e: Exception) {
                _resultText.value = "Error"
            }
        }
    }

    fun calculateResult(equation: String): String {
        val context: Context = Context.enter()
        context.optimizationLevel = -1
        val scriptable: Scriptable = context.initStandardObjects()
        val rawResult = context.evaluateString(scriptable, equation, "Javascript", 1, null).toString()

        val result = rawResult.toDoubleOrNull()
        return if (result != null) {
            if (result % 1 == 0.0) {
                result.toInt().toString()
            } else {
                String.format("%.6f", result).trimEnd('0').trimEnd('.')
            }
        } else {
            "Error"
        }
    }
}
