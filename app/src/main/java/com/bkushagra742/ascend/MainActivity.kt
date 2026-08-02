package com.bkushagra742.ascend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.bkushagra742.ascend.core.navigation.AscendNavHost
import com.bkushagra742.ascend.core.theme.AscendColors
import com.bkushagra742.ascend.core.theme.AscendTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AscendApp()
        }
    }
}

@Composable
private fun AscendApp() {
    AscendTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = AscendColors.ObsidianBlack,
        ) {
            val navController = rememberNavController()
            AscendNavHost(navController = navController)
        }
    }
}
