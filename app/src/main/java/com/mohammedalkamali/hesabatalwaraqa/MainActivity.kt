package com.mohammedalkamali.hesabatalwaraqa

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mohammedalkamali.hesabatalwaraqa.ui.screens.RootScreen
import com.mohammedalkamali.hesabatalwaraqa.ui.theme.HukmTheme
import com.mohammedalkamali.hesabatalwaraqa.util.AppPrefs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppPrefs.init(applicationContext)
        enableEdgeToEdge()
        // keep the screen awake during a game — matches UIApplication.isIdleTimerDisabled = true
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            HukmTheme(appTheme = AppPrefs.appTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize().systemBarsPadding(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        RootScreen()
                    }
                }
            }
        }
    }
}
