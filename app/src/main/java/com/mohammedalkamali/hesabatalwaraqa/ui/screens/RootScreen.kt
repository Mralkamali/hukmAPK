package com.mohammedalkamali.hesabatalwaraqa.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohammedalkamali.hesabatalwaraqa.R
import com.mohammedalkamali.hesabatalwaraqa.model.GameType
import com.mohammedalkamali.hesabatalwaraqa.ui.theme.LocalHukmColors
import kotlinx.coroutines.delay

@Composable
fun RootScreen() {
    var showSplash by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(1500)
        showSplash = false
    }
    Crossfade(targetState = showSplash, animationSpec = tween(400), label = "root") { splash ->
        if (splash) SplashScreen() else ContentRouter()
    }
}

@Composable
private fun SplashScreen() {
    val c = LocalHukmColors.current
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(c.bgDark, c.bg, c.bgDark))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Image(
                painter = painterResource(R.drawable.splash_logo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(110.dp)
                    .shadow(24.dp, RoundedCornerShape(24.dp), spotColor = c.goldText)
                    .clip(RoundedCornerShape(24.dp))
            )
            Text("حسابة الورقة", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = c.goldText)
        }
    }
}

@Composable
private fun ContentRouter() {
    var selectedGame by rememberSaveable { mutableStateOf<GameType?>(null) }
    Box(Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = selectedGame == null,
            enter = slideInHorizontally { -it } + fadeIn(),
            exit = slideOutHorizontally { -it } + fadeOut(),
        ) {
            HomeScreen(onSelect = { selectedGame = it })
        }
        AnimatedVisibility(
            visible = selectedGame != null,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut(),
        ) {
            val g = selectedGame
            if (g != null) ScoringScreen(game = g, onBack = { selectedGame = null })
        }
    }
}
