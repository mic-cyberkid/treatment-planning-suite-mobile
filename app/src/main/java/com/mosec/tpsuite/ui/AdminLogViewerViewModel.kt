package com.mosec.tpsuite.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mosec.tpsuite.data.DataLog
import com.mosec.tpsuite.data.DataLogDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/**
 * ViewModel for viewing and exporting all dosimetry calculation logs.
 */
class AdminLogViewerViewModel(private val dataLogDao: DataLogDao) : ViewModel() {

    val allLogs: StateFlow<List<DataLog>> = dataLogDao.getAllLogs() // Assuming getLogs returns all in future refactor or current state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun exportLogsToCsv(context: Context) {
        viewModelScope.launch {
            val logs = allLogs.value
            if (logs.isEmpty()) return@launch

            val csvHeader = "ID,Timestamp,User,Type,Values,Result\n"
            val csvData = logs.joinToString(separator = "\n") { log ->
                "${log.id},${log.date},${log.username},${log.calculationType},\"${log.calculationValues.replace("\"", "'")}\",${log.result}"
            }

            val file = File(context.cacheDir, "DosimetryLogsExport.csv")
            FileOutputStream(file).use { 
                it.write((csvHeader + csvData).toByteArray())
            }

            shareFile(context, file)
        }
    }

    private fun shareFile(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Export Dosimetry Logs"))
    }
}
