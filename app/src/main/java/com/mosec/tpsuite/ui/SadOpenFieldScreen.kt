package com.mosec.tpsuite.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mosec.tpsuite.ui.theme.AccentTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SadOpenFieldScreen(
    viewModel: SadOpenFieldViewModel,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.errorMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("SAD Open Field") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Source Axis Distance - No Block",
                    style = MaterialTheme.typography.titleMedium,
                    color = AccentTeal,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                QAInputCard(
                    title = "Field Parameters",
                    icon = Icons.Default.GridOn
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LinacTextField(value = viewModel.xField, onValueChange = { viewModel.xField = it }, label = "X (cm)", modifier = Modifier.weight(1f))
                        LinacTextField(value = viewModel.yField, onValueChange = { viewModel.yField = it }, label = "Y (cm)", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinacTextField(value = viewModel.depth, onValueChange = { viewModel.depth = it }, label = "Depth (cm)", modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    LinacTextField(value = viewModel.prescribedDose, onValueChange = { viewModel.prescribedDose = it }, label = "Dose (cGy)", modifier = Modifier.fillMaxWidth())
                }
            }

            item {
                SadOpenResultCard(result = viewModel.calculationResult)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.calculate() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentTeal)
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Calculate")
                    }
                    OutlinedButton(
                        onClick = { viewModel.clear() },
                        modifier = Modifier.weight(0.7f)
                    ) {
                        Text("Clear")
                    }
                }
            }

            item {
                Button(
                    onClick = { viewModel.saveToHistory("CurrentUser", "None") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = viewModel.calculationResult != null,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save to History")
                }
            }
        }
    }
}

@Composable
fun SadOpenResultCard(result: SadOpenFieldViewModel.Result?) {
    if (result == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AccentTeal.copy(alpha = 0.1f)),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AccentTeal))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Dosimetry Results",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AccentTeal
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = AccentTeal.copy(alpha = 0.3f))
            
            ResultRow("Equivalent FS (cm)", String.format("%.1f", result.eqfs))
            ResultRow("Sc,p", String.format("%.4f", result.scp))
            ResultRow("TMR", String.format("%.4f", result.tmr))
            ResultRow("Dmax (cGy)", String.format("%.1f", result.dMax))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "${result.treatmentTime} MU",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = AccentTeal
            )
        }
    }
}
