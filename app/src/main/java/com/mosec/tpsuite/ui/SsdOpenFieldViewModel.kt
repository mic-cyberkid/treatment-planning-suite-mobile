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
 * ViewModel for SSD Open Field dosimetry calculations.
 */
class SsdOpenFieldViewModel(private val dataLogDao: DataLogDao) : ViewModel() {

    var xField by mutableStateOf("")
    var yField by mutableStateOf("")
    var depth by mutableStateOf("")
    var prescribedDose by mutableStateOf("")

    var calculationResult by mutableStateOf<Result?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)

    data class Result(
        val eqfs: Double,
        val scp: Double,
        val pdd: Double,
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
            val pdd = DosimetryUtils.lookupDosimetryValue(eqfs, depth, DosimetryData.PDD_SHEET)
            
            // Dmax = (Dose / PDD) * 100 (since PDD is in percentage)
            val dMax = (dose / pdd) * 100.0
            val time = DosimetryUtils.calculateTreatmentTime(dMax, scp)

            calculationResult = Result(eqfs, scp, pdd, dMax, time)
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
                put("PDD", result.pdd)
                put("Time", result.treatmentTime)
            }.toString()

            val log = DataLog(
                username = username,
                calculationType = "SSD Open Field",
                calculationValues = values,
                result = "${result.treatmentTime} MU"
            )
            dataLogDao.insertLog(log)
            errorMessage = "Saved to history successfully"
        }
    }
}
