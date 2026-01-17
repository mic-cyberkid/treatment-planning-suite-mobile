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
 * ViewModel for SAD Wedged Field dosimetry calculations.
 */
class SadWedgedFieldViewModel(private val dataLogDao: DataLogDao) : ViewModel() {

    // Input States
    var xField by mutableStateOf("")
    var yField by mutableStateOf("")
    var depth by mutableStateOf("")
    var prescribedDose by mutableStateOf("")
    var wedgeType by mutableStateOf(WedgeFactor.NONE)

    enum class WedgeFactor(val value: Double, val label: String) {
        NONE(1.0, "No Wedge"),
        W15(0.767, "15° Wedge"),
        W30(0.636, "30° Wedge"),
        W45(0.487, "45° Wedge"),
        W60(0.261, "60° Wedge")
    }

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
            val totalScp = scp * wedgeType.value
            
            val tmr = DosimetryUtils.lookupDosimetryValue(eqfs, depth, DosimetryData.TMR_SHEET)
            val dMax = DosimetryUtils.calculateDmax(dose, tmr)
            val time = DosimetryUtils.calculateTreatmentTime(dMax, totalScp)

            calculationResult = Result(eqfs, scp, tmr, dMax, time)
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Please enter valid numbers in all fields"
        }
    }

    fun clear() {
        xField = ""; yField = ""; depth = ""; prescribedDose = ""
        wedgeType = WedgeFactor.NONE
        calculationResult = null
        errorMessage = null
    }

    fun saveToHistory(username: String, patientId: String) {
        val result = calculationResult ?: return
        viewModelScope.launch {
            val values = JSONObject().apply {
                put("X", xField); put("Y", yField)
                put("Depth", depth); put("Dose", prescribedDose)
                put("Wedge", wedgeType.label)
                put("EQFS", result.eqfs)
                put("SCP", result.scp)
                put("TMR", result.tmr)
                put("Time", result.treatmentTime)
            }.toString()

            val log = DataLog(
                username = username,
                calculationType = "SAD Wedged Field",
                calculationValues = values,
                result = "${result.treatmentTime} MU"
            )
            dataLogDao.insertLog(log)
            errorMessage = "Saved to history successfully"
        }
    }
}
