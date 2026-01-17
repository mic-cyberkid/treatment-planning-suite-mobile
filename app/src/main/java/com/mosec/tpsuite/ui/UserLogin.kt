package com.mosec.tpsuite.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserLoginScreen(
    onLogin: (String, String) -> Unit,
    onBackToHome: () -> Unit,
    infoMessage: String = ""
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
     
    Box(
        modifier = Modifier
            .padding(20.dp)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFE6F0FA), Color(0xFFF5F7FA))
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .widthIn(max = 520.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Text(
                "Treatment Planning Suite",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF003087)
            )

            Text(
                "User Login",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )

            if (infoMessage.isNotEmpty()) {
                Text(infoMessage, color = Color(0xFFF56565))
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.width(300.dp),
                    label = { Text("Username") }
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.width(300.dp),
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            Button(
                onClick = { onLogin(username, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            ) {
                Text("Login", fontSize = 18.sp)
            }

            TextButton(onClick = onBackToHome) {
                Text("Back to Home")
            }
        }
    }
}
