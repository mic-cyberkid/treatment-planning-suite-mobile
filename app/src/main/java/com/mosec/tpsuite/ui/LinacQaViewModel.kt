package com.mosec.tpsuite.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mosec.tpsuite.data.DataLog
import com.mosec.tpsuite.data.DataLogDao
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.math.abs

class LinacQaViewModel(private val dataLogDao: DataLogDao) : ViewModel() {

    // Input States
    var t1 by mutableStateOf("")
    var t2 by mutableStateOf("")
    var p1 by mutableStateOf("")
    var p2 by mutableStateOf("")
    var neg31 by mutableStateOf("")
    var neg32 by mutableStateOf("")
    var zero1 by mutableStateOf("")
    var zero2 by mutableStateOf("")
    var pos31 by mutableStateOf("")
    var pos32 by mutableStateOf("")
    var negC by mutableStateOf("")
    var c0 by mutableStateOf("")
    var c3 by mutableStateOf("")

    // Calculation Results
    var calculationResult by mutableStateOf<Result?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)

    data class Result(
        val c0: Double,
        val c3: Double,
        val cNeg: Double,
        val errorPercentage: Double,
        val colorType: ResultColor
    )

    enum class ResultColor { GREEN, YELLOW, RED }

    fun calculate() {
        try {
            val PR = 101.32;
            val TR = 20.0;
            val CR = 12.32;
            val vT1 = t1.toDouble()
            val vT2 = t2.toDouble()
            val vP1 = p1.toDouble()
            val vP2 = p2.toDouble()
            val vNeg31 = neg31.toDouble()
            val vNeg32 = neg32.toDouble()
            val vZero1 = zero1.toDouble()
            val vZero2 = zero2.toDouble()
            val vPos31 = pos31.toDouble()
            val vPos32 = pos32.toDouble()

            val avgT = (vT1 + vT2) / 2
            val avgP = (vP1 + vP2) / 2
            val avgNeg3 = (vNeg31 + vNeg32) / 2
            val avgZero = (vZero1 + vZero2) / 2
            val avgPos3 = (vPos31 + vPos32) / 2

            val calc1 = (273.2 + avgT) / (273.2 + TR)
            val calc2 = (PR / avgP)
            val calc3 = calc1 * calc2

            val c3 = (calc3 * avgPos3)
            val c0 = (calc3 * avgZero)
            val co = String.format("%.2f", c0).toDouble()
            val cNeg = calc3 * avgNeg3
            val error = ((CR - co ) / CR )
            val percError = error * 100

            val color = when {
                abs(error) < 2.0 -> ResultColor.GREEN
                abs(error) <= 5.0 -> ResultColor.YELLOW
                else -> ResultColor.RED
            }

            calculationResult = Result(c0, c3, cNeg,percError,   color)
            errorMessage = null
        } catch (_: Exception) {
            errorMessage = "Please enter valid numbers in all required fields"
        }
    }


    fun clear() {
        t1 = ""; t2 = ""; p1 = ""; p2 = ""
        neg31 = ""; neg32 = ""; zero1 = ""; zero2 = ""; pos31 = ""; pos32 = ""
        negC = ""; c0 = ""; c3 = ""
        calculationResult = null
        errorMessage = null
    }

    fun saveToHistory(username: String) {
        val result = calculationResult ?: return
        viewModelScope.launch {
            val values = JSONObject().apply {
                put("T1", t1); put("T2", t2)
                put("P1", p1); put("P2", p2)
                put("neg31", neg31); put("neg32", neg32)
                put("zero1", zero1); put("zero2", zero2)
                put("pos31", pos31); put("pos32", pos32)
                put("negC", negC); put("c0", c0); put("c3", c3)
            }.toString()

            val log = DataLog(
                username = username,
                calculationValues = values,
                result = String.format("%.2f%%", result.errorPercentage)
            )
            dataLogDao.insertLog(log)
            errorMessage = "Saved to history successfully"
        }
    }
}
