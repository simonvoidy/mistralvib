package com.emberline.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.emberline.launcher.ui.EmberlineApp
import com.emberline.launcher.ui.theme.EmberlineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EmberlineTheme {
                EmberlineApp()
            }
        }
    }
}
