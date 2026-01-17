package com.mosec.tpsuite.utils

import android.os.Build
import com.mosec.tpsuite.data.DosimetryData
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.sqrt

/**
 * Utility functions for oncology physics calculations.
 * Converted from the original Java CalculationMethods.
 */
object DosimetryUtils {

    /**
     * Calculates the equivalent square field size for a rectangular field.
     */
    fun calculateEquivalentFieldSize(x: Double, y: Double): Double {
        if (x <= 0 || y <= 0) return 0.0
        val eqfs = (2.015 * (x * y)) / ((1.015 * x) + y)
        return String.format(Locale.US,"%.1f", eqfs).toDouble()
    }

    /**
     * Calculates the reduced field size after blocking.
     */
    fun calculateReducedFieldSize(x: Double, y: Double, blockedArea: Double): Double {
        val reducedSize = sqrt((x * y) - blockedArea)
        return String.format(Locale.US,"%.1f", reducedSize).toDouble()
    }

    /**
     * Calculates Dmax dose.
     */
    fun calculateDmax(doseAtP: Double, factor: Double): Double {
        if (factor == 0.0) return 0.0
        return doseAtP / factor
    }

    /**
     * Finds the lower and higher values in a dataset for interpolation.
     */
    private fun getMinMax(value: Double, dataSet: DoubleArray): Pair<Double, Double> {
        val sorted = dataSet.sortedArray()
        var lower = sorted.first()
        var higher = sorted.last()

        for (num in sorted) {
            if (num < value) {
                lower = num
            } else if (num > value) {
                higher = num
                break
            }
        }
        return Pair(lower, higher)
    }

    /**
     * Performs linear interpolation.
     */
    private fun interpolate(x: Double, x1: Double, x2: Double, y1: Double, y2: Double): Double {
        if (x1 == x2) return y1
        val slope = (y2 - y1) / (x2 - x1)
        return y1 + slope * (x - x1)
    }

    /**
     * Looks up a value (TMR, PDD, SCP) from a data sheet with interpolation.
     */
    fun lookupDosimetryValue(fieldSize: Double, depth: String, dataSheet: Map<String, DoubleArray>): Double {
        try {
            val fsArray = dataSheet["FS"] ?: return 0.0

            // If depth exists directly
            if (dataSheet.containsKey(depth)) {
                val values = dataSheet[depth]!!
                return getValueAtFieldSize(fieldSize, fsArray, values)
            }

            // If depth needs interpolation (assumes integer depths)
            val d = depth.toDoubleOrNull()?.toInt() ?: return 0.0
            val d1 = d - 1
            val d2 = d + 1

            val v1 = lookupDosimetryValue(fieldSize, d1.toString(), dataSheet)
            val v2 = lookupDosimetryValue(fieldSize, d2.toString(), dataSheet)

            return (v1 + v2) / 2.0
        } catch (e: Exception) {
            e.printStackTrace()
            return 1.0
        }
    }

    private fun getValueAtFieldSize(fieldSize: Double, fsArray: DoubleArray, values: DoubleArray): Double {
        val exactIndex = fsArray.indexOfFirst { it == fieldSize }
        if (exactIndex != -1) return values[exactIndex]

        val (fs1, fs2) = getMinMax(fieldSize, fsArray)
        val idx1 = fsArray.indexOfFirst { it == fs1 }
        val idx2 = fsArray.indexOfFirst { it == fs2 }
        
        if (idx1 == -1 || idx2 == -1) return 0.0
        
        return interpolate(fieldSize, fs1, fs2, values[idx1], values[idx2])
    }

    /**
     * Specific lookup for SCP values.
     */
    fun getScpValue(fieldSize: Double): Double {
        val fsArray = DosimetryData.SCP_SHEET["FS"] ?: return 0.0
        val values = DosimetryData.SCP_SHEET["SCP_SHEET"] ?: return 0.0
        return getValueAtFieldSize(fieldSize, fsArray, values)
    }

    /**
     * Specific lookup for scatter values (Sc or Sp).
     */
    fun getScatterValue(fieldSize: Double, source: String): Double {
        val fsArray = DosimetryData.SCP_SHEET["FS"] ?: return 0.0
        val values = DosimetryData.SCP_SHEET[source] ?: return 0.0
        return getValueAtFieldSize(fieldSize, fsArray, values)
    }

    /**
     * Calculates the decay factor based on the current date.
     */

    fun getDecayFactor(): Double {
        val decayFactors = mapOf(
            5 to 1.0000, 6 to 0.9890, 7 to 0.9781, 8 to 0.9674, 9 to 0.9567,
            10 to 0.9462, 11 to 0.9358, 12 to 0.9255, 1 to 0.9153, 2 to 0.9052,
            3 to 0.8953, 4 to 0.8854
        )
        var day = 0
        var month = 0
        if(Build.VERSION.SDK_INT  >= 26){
            val now = LocalDate.now()
            month = now.monthValue
            day = now.dayOfMonth
        }else{
            val date = Date()
            val calendar = Calendar.getInstance()
            calendar.time = date
            month = calendar.get(Calendar.MONTH) + 1
            day = calendar.get(Calendar.DAY_OF_MONTH)
        }



        if (day < 7) {
            month = if (month <= 2) month + 10 else month - 2
        } else {
            // month remains the same or simple adjustment from original logic
            // original: stored_month = (stored_month - 1) % 12 + 1;
        }
        
        return decayFactors[month] ?: 1.0
    }

    /**
     * Final treatment time calculation.
     */
    fun calculateTreatmentTime(dMax: Double, totalScp: Double): Double {
        val shutterTime = 0.01
        val doseRate = 179.47
        val sadFactor = 1.01
        val decayFactor = getDecayFactor()
        
        if (totalScp == 0.0 || decayFactor == 0.0) return 0.0
        
        val time = (dMax / (doseRate * sadFactor * totalScp * decayFactor)) - shutterTime
        return String.format(Locale.US,"%.2f", time).toDouble()
    }
}
