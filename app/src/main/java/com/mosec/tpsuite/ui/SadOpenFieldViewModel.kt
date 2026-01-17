package com.mosec.tpsuite.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mosec.tpsuite.data.DataLog
import com.mosec.tpsuite.data.DataLogDao
import com.mosec.tpsuite.data.DosimetryData
import com.mosec.tpsuite.utils.DosimetryUtils
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * ViewModel for SAD Open Field dosimetry calculations.
 */
class SadOpenFieldViewModel(private val dataLogDao: DataLogDao) : ViewModel() {

    // Input States
    var xField by mutableStateOf("")
    var yField by mutableStateOf("")
    var depth by mutableStateOf("")
    var prescribedDose by mutableStateOf("")

    // Calculation Results
    var calculationResult by mutableStateOf<Result?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)

    data class Result(
        val eqfs: Double,
        val scp: Double,
        val tmr: Double,
        val dMax: Double,
        val treatmentTime: Double
    )

    fun calculate() {
        try {
            val x = xField.toDouble()
            val y = yField.toDouble()
            val d = depth.toDouble()
            val dose = prescribedDose.toDouble()

            if (x <= 0 || y <= 0 || d <= 0 || dose <= 0) {
                errorMessage = "All values must be greater than zero"
                return
            }

            val eqfs = DosimetryUtils.calculateEquivalentFieldSize(x, y)
            val scp = DosimetryUtils.getScpValue(eqfs)
            val tmr = DosimetryUtils.lookupDosimetryValue(eqfs, depth, DosimetryData.TMR_SHEET)
            val dMax = DosimetryUtils.calculateDmax(dose, tmr)
            val time = DosimetryUtils.calculateTreatmentTime(dMax, scp)

            calculationResult = Result(eqfs, scp, tmr, dMax, time)
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Please enter valid numbers in all fields"
        }
    }

    fun clear() {
        xField = ""; yField = ""; depth = ""; prescribedDose = ""
        calculationResult = null
        errorMessage = null
    }

    fun saveToHistory(username: String, patientId: String) {
        val result = calculationResult ?: return
        viewModelScope.launch {
            val values = JSONObject().apply {
                put("X", xField); put("Y", yField)
                put("Depth", depth); put("Dose", prescribedDose)
                put("EQFS", result.eqfs)
                put("SCP", result.scp)
                put("TMR", result.tmr)
                put("DMax", result.dMax)
            }.toString()

            val log = DataLog(
                username = username,
                calculationType = "SAD Open Field",
                calculationValues = values,
                result = "${result.treatmentTime} MU"
            )
            dataLogDao.insertLog(log)
            errorMessage = "Saved to history successfully"
        }
    }
}
