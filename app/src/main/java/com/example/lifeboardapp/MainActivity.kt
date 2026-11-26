
package com.example.lifeboardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.lifeboardapp.data.presentation.navigation.LifeBoardNavigation
import com.example.lifeboardapp.ui.theme.LifeBoardTheme
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LifeBoardTheme {
                LifeBoardNavigation()
            }
        }
    }
}
