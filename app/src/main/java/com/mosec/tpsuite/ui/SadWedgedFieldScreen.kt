package com.mosec.tpsuite.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mosec.tpsuite.ui.theme.AccentTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SadWedgedFieldScreen(
    viewModel: SadWedgedFieldViewModel,
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
                title = { Text("SAD Wedged Field") },
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
                    text = "Source Axis Distance - Wedged",
                    style = MaterialTheme.typography.titleMedium,
                    color = AccentTeal,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                QAInputCard(
                    title = "Field Parameters",
                    icon = Icons.Default.ChangeHistory
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LinacTextField(value = viewModel.xField, onValueChange = { viewModel.xField = it }, label = "X (cm)", modifier = Modifier.weight(1f))
                        LinacTextField(value = viewModel.yField, onValueChange = { viewModel.yField = it }, label = "Y (cm)", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinacTextField(value = viewModel.depth, onValueChange = { viewModel.depth = it }, label = "Depth (cm)", modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    LinacTextField(value = viewModel.prescribedDose, onValueChange = { viewModel.prescribedDose = it }, label = "Dose (cGy)", modifier = Modifier.fillMaxWidth())
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Physical Wedge Type", style = MaterialTheme.typography.bodySmall, color = AccentTeal)
                    
                    Column(Modifier.selectableGroup()) {
                        SadWedgedFieldViewModel.WedgeFactor.values().forEach { wedge ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .selectable(
                                        selected = (wedge == viewModel.wedgeType),
                                        onClick = { viewModel.wedgeType = wedge },
                                        role = Role.RadioButton
                                    )
                                    .padding(horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (wedge == viewModel.wedgeType),
                                    onClick = null
                                )
                                Text(
                                    text = wedge.label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                SadOpenResultCard(result = viewModel.calculationResult?.let {
                    // Mapping to the common SadOpenFieldViewModel.Result for the display card
                    // In a production app you'd share this Result class or card logic
                    SadOpenFieldViewModel.Result(it.eqfs, it.scp, it.tmr, it.dMax, it.treatmentTime)
                })
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
