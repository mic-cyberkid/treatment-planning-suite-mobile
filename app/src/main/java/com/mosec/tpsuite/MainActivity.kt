package com.mosec.tpsuite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mosec.tpsuite.navigation.NavGraph
import com.mosec.tpsuite.ui.theme.TPSuiteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TPSuiteTheme {
                NavGraph()
            }
        }
    }
}


