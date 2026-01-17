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
 * ViewModel for SSD Blocked Field dosimetry calculations.
 */
class SsdBlockedFieldViewModel(private val dataLogDao: DataLogDao) : ViewModel() {

    var xField by mutableStateOf("")
    var yField by mutableStateOf("")
    var blockedArea by mutableStateOf("")
    var depth by mutableStateOf("")
    var prescribedDose by mutableStateOf("")
    var blockFactorType by mutableStateOf(SadBlockedFieldViewModel.BlockFactor.NONE)

    var calculationResult by mutableStateOf<Result?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)

    data class Result(
        val eqfs: Double,
        val reducedField: Double,
        val sc: Double,
        val sp: Double,
        val totalScp: Double,
        val pdd: Double,
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
            
            val sc = DosimetryUtils.getScatterValue(eqfs, "sc")
            val sp = DosimetryUtils.getScatterValue(reducedField, "sp")
            val scpBase = sc * sp
            val totalScp = scpBase * blockFactorType.value

            val pdd = DosimetryUtils.lookupDosimetryValue(reducedField, depth, DosimetryData.PDD_SHEET)
            val dMax = (dose / pdd) * 100.0
            val time = DosimetryUtils.calculateTreatmentTime(dMax, totalScp)

            calculationResult = Result(eqfs, reducedField, sc, sp, totalScp, pdd, dMax, time)
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Please enter valid numbers in all fields"
        }
    }

    fun clear() {
        xField = ""; yField = ""; blockedArea = ""; depth = ""; prescribedDose = ""
        blockFactorType = SadBlockedFieldViewModel.BlockFactor.NONE
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
                put("PDD", result.pdd)
                put("Time", result.treatmentTime)
            }.toString()

            val log = DataLog(
                username = username,
                calculationType = "SSD Blocked Field",
                calculationValues = values,
                result = "${result.treatmentTime} MU"
            )
            dataLogDao.insertLog(log)
            errorMessage = "Saved to history successfully"
        }
    }
}
