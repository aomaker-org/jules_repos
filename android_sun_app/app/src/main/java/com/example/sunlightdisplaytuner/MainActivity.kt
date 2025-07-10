package com.example.sunlightdisplaytuner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.sunlightdisplaytuner.ui.main.MainScreen
import com.example.sunlightdisplaytuner.ui.main.MainViewModel
import com.example.sunlightdisplaytuner.ui.theme.SunlightDisplayTunerTheme

class MainActivity : ComponentActivity() {

    // If you need to pass 'this' (Activity context) or 'application' to ViewModel constructor,
    // you might need a ViewModelProvider.Factory.
    // For AndroidViewModel, this is often handled automatically.
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SunlightDisplayTunerTheme {
                // A surface container using the 'background' color from the theme
                // Surface is not strictly necessary here if MainScreen handles its own background
                // in Scaffold.
                MainScreen(mainViewModel = mainViewModel)
            }
        }
    }
}

// Preview can be kept in MainScreen.kt or here, but it's often better with the Composable itself.
// Removing Greeting and GreetingPreview as MainScreen now serves as the primary UI.
}
