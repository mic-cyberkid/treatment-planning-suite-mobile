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
 * ViewModel for SAD Blocked Field dosimetry calculations.
 */
class SadBlockedFieldViewModel(private val dataLogDao: DataLogDao) : ViewModel() {

    // Input States
    var xField by mutableStateOf("")
    var yField by mutableStateOf("")
    var blockedArea by mutableStateOf("")
    var depth by mutableStateOf("")
    var prescribedDose by mutableStateOf("")
    var blockFactorType by mutableStateOf(BlockFactor.NONE)

    enum class BlockFactor(val value: Double, val label: String) {
        NONE(1.0, "No Tray"),
        TRAY(0.957, "Tray Factor"),
        BELLY(0.978, "Belly Board")
    }

    // Calculation Results
    var calculationResult by mutableStateOf<Result?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)

    data class Result(
        val eqfs: Double,
        val reducedField: Double,
        val sc: Double,
        val sp: Double,
        val totalScp: Double,
        val tmr: Double,
        val dMax: Double,
        val treatmentTime: Double
    )

    fun calculate() {
        try {
            val x = xField.toDouble()
            val y = yField.toDouble()
            val area = blockedArea.toDoubleOrNull() ?: 0.0
            val d = depth.toDouble()
            val dose = prescribedDose.toDouble()

            if (x <= 0 || y <= 0 || d <= 0 || dose <= 0) {
                errorMessage = "X, Y, Depth, and Dose must be greater than zero"
                return
            }

            val eqfs = DosimetryUtils.calculateEquivalentFieldSize(x, y)
            val reducedField = DosimetryUtils.calculateReducedFieldSize(x, y, area)
            
            // For blocked fields: Sc is from collimator (EQFS), Sp is from patient (Reduced Field)
            val sc = DosimetryUtils.getScatterValue(eqfs, "sc")
            val sp = DosimetryUtils.getScatterValue(reducedField, "sp")
            val scpBase = sc * sp
            val totalScp = scpBase * blockFactorType.value

            val tmr = DosimetryUtils.lookupDosimetryValue(reducedField, depth, DosimetryData.TMR_SHEET)
            val dMax = DosimetryUtils.calculateDmax(dose, tmr)
            val time = DosimetryUtils.calculateTreatmentTime(dMax, totalScp)

            calculationResult = Result(eqfs, reducedField, sc, sp, totalScp, tmr, dMax, time)
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Please enter valid numbers in all fields"
        }
    }

    fun clear() {
        xField = ""; yField = ""; blockedArea = ""; depth = ""; prescribedDose = ""
        blockFactorType = BlockFactor.NONE
        calculationResult = null
        errorMessage = null
    }

    fun saveToHistory(username: String, patientId: String) {
        val result = calculationResult ?: return
        viewModelScope.launch {
            val values = JSONObject().apply {
                put("X", xField); put("Y", yField)
                put("BlockedArea", blockedArea)
                put("BlockFactor", blockFactorType.label)
                put("EQFS", result.eqfs)
                put("ReducedField", result.reducedField)
                put("TotalSCP", result.totalScp)
                put("TMR", result.tmr)
                put("Time", result.treatmentTime)
            }.toString()

            val log = DataLog(
                username = username,
                calculationType = "SAD Blocked Field",
                calculationValues = values,
                result = "${result.treatmentTime} MU"
            )
            dataLogDao.insertLog(log)
            errorMessage = "Saved to history successfully"
        }
    }
}
