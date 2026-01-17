package com.mosec.tpsuite.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mosec.tpsuite.ui.theme.AccentTeal
import com.mosec.tpsuite.ui.theme.TPSuiteTheme
import androidx.compose.material3.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinacQAScreen(
    viewModel: LinacQaViewModel,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    rememberCoroutineScope()

    // Handle ViewModel error/success messages with snackbar
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
                title = { Text("LINAC QA Dosimetry") },
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
                    text = "Treatment Planning Suite - QA Tool",
                    style = MaterialTheme.typography.headlineSmall,
                    color = AccentTeal,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                QAInputCard(
                    title = "Ambient Conditions",
                    icon = Icons.Default.Thermostat
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LinacTextField(value = viewModel.t1, onValueChange = { viewModel.t1 = it }, label = "T1 (°C)", modifier = Modifier.weight(1f))
                        LinacTextField(value = viewModel.t2, onValueChange = { viewModel.t2 = it }, label = "T2 (°C)", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LinacTextField(value = viewModel.p1, onValueChange = { viewModel.p1 = it }, label = "P1 (kPa)", modifier = Modifier.weight(1f))
                        LinacTextField(value = viewModel.p2, onValueChange = { viewModel.p2 = it }, label = "P2 (kPa)", modifier = Modifier.weight(1f))
                    }
                }
            }

            item {
                QAInputCard(
                    title = "Electrometer Readings",
                    icon = Icons.Default.FlashOn
                ) {
                    ReadingPairRow(label1 = "Neg3-1", value1 = viewModel.neg31, onValue1 = { viewModel.neg31 = it },
                                     label2 = "Neg3-2", value2 = viewModel.neg32, onValue2 = { viewModel.neg32 = it })
                    Spacer(modifier = Modifier.height(8.dp))
                    ReadingPairRow(label1 = "Zero-1", value1 = viewModel.zero1, onValue1 = { viewModel.zero1 = it },
                                     label2 = "Zero-2", value2 = viewModel.zero2, onValue2 = { viewModel.zero2 = it })
                    Spacer(modifier = Modifier.height(8.dp))
                    ReadingPairRow(label1 = "Pos3-1", value1 = viewModel.pos31, onValue1 = { viewModel.pos31 = it },
                                     label2 = "Pos3-2", value2 = viewModel.pos32, onValue2 = { viewModel.pos32 = it })
                }
            }

            item {
                ResultCard(result = viewModel.calculationResult)
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
                    onClick = { viewModel.saveToHistory("CurrentUser") }, // Assume current user for now
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
fun ReadingPairRow(
    label1: String, value1: String, onValue1: (String) -> Unit,
    label2: String, value2: String, onValue2: (String) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        LinacTextField(value = value1, onValueChange = onValue1, label = label1, modifier = Modifier.weight(1f))
        LinacTextField(value = value2, onValueChange = onValue2, label = label2, modifier = Modifier.weight(1f))
    }
}

@Composable
fun QAInputCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = AccentTeal)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun LinacTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun ResultCard(result: LinacQaViewModel.Result?) {
    if (result == null) return

    val resultColor = when (result.colorType) {
        LinacQaViewModel.ResultColor.GREEN -> Color(0xFF2E7D32)
        LinacQaViewModel.ResultColor.YELLOW -> Color(0xFFFBC02D)
        LinacQaViewModel.ResultColor.RED -> Color(0xFFD32F2F)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = resultColor.copy(alpha = 0.1f)),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(resultColor))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Calculation Result",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = resultColor
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = resultColor.copy(alpha = 0.3f))


            ResultRow("c3", String.format(Locale.US,"%.4f", result.c3))
            ResultRow("cNeg", String.format(Locale.US,"%.4f", result.cNeg))
            ResultRow("c0", String.format(Locale.US,"%.4f", result.c0))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = String.format(Locale.US,"ERROR: %.2f%%", result.errorPercentage),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = resultColor
            )
        }
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun LinacQAScreenPreview() {
    // Note: In a real preview you'd want a mock DAO or a dummy ViewModel.
    // For this demonstration, we'll use a simplified version.
    TPSuiteTheme {
        // Mocking the behavior for preview would require more setup, 
        // but we can at least show the layout structure.
        Surface(modifier = Modifier.fillMaxSize()) {
            Text("Preview requires a real ViewModel or Mock.")
        }
    }
}
