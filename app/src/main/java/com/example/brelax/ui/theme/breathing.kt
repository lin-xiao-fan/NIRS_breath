package com.example.brelax.ui.theme

import android.animation.ValueAnimator
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.animation.doOnEnd
import com.example.brelax.R
import androidx.compose.animation.core.Animatable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.BreathScreenbox
import com.example.breathMainScreen
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BreathingActivity() : ComponentActivity() {
    val Tag = "呼吸測試"
    private var cycleCount by mutableStateOf(0)
    private var cycleDuration by mutableStateOf(0L)
    private var totalDuration by mutableStateOf(0L)
    private var breathingPhase by mutableStateOf("吸气")
    private var timerValue by mutableStateOf(0)
    private var selectedMethod = "4-7-8"
    private var totalTime = 1
    private var maxCycles = 0
    private var sunImage by mutableStateOf(R.drawable.badsun)

    data class BreathingMethod(val inhale: Int, val hold: Int, val exhale: Int)



    private val breathingMethods = mapOf(
        "4-7-8" to BreathingMethod(inhale = 4, hold = 7, exhale = 8),
        "3-3-3" to BreathingMethod(inhale = 3, hold = 3, exhale = 3),
        "4-2-6" to BreathingMethod(inhale = 4, hold = 2, exhale = 6),
        "Box" to BreathingMethod(inhale = 4, hold = 4, exhale = 4)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedMethod = intent.getStringExtra("BREATHING_METHOD") ?: "4-7-8"
        Log.d(Tag, "選擇模式 : $selectedMethod")
        totalTime = intent.getIntExtra("TIME_MINUTES", 1)

        val selectedBreathingMethod = breathingMethods[selectedMethod] ?: breathingMethods["4-7-8"]!!
        updateCycleDuration(selectedBreathingMethod)

        totalDuration = totalTime * 60 * 1000L
        if( selectedMethod != "box" ) {
            maxCycles = (totalDuration / cycleDuration).toInt()
        }
        else {
            maxCycles = (totalDuration / cycleDuration + ( 4 * 1000 ) ).toInt()
        }




        setContent {
            MaterialTheme {

                val navController = rememberNavController() // 獲取 NavController
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { breathMainScreen(navController) }
                    composable("breathing") { BreathScreenbox(navController) }
                    composable("breathingend"){ BreathScreenUI(navController) }
                }
                BreathingScreen(maxCycles,navController)
            }
        }
    }

    @Composable
    fun BreathingScreen(maxCycles: Int,navController: NavController) {
        val cloudLeftOffset = remember { Animatable(-300f) }
        val cloudRightOffset = remember { Animatable(300f) }
        val activity = (LocalContext.current as? Activity)
        val method = breathingMethods[selectedMethod] ?: return
        // 用來顯示當前呼吸狀態的文字
        val breathingStates = listOf("吸氣", "屏氣", "吐氣","準備")
        val currentState = remember { mutableStateOf(breathingStates[0]) } // 預設為 "吸氣"
        val shouldNavigate = remember { mutableStateOf(false) } // 用于控制导航



        Box(modifier = Modifier.fillMaxSize()
            .padding(0.dp)) {
            Image(
                painter = painterResource(id = R.drawable.rainy_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Image(
                painter = painterResource(id = sunImage),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
            )

            Image(
                painter = painterResource(id = R.drawable.cloudleft),
                contentDescription = null,
                modifier = Modifier
                    .size(550.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = cloudLeftOffset.value.dp,y = 60.dp )
            )

            Image(
                painter = painterResource(id = R.drawable.cloudright),
                contentDescription = null,
                modifier = Modifier
                    .size(550.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = cloudRightOffset.value.dp,y = 60.dp)
            )

            Text(
                text = currentState.value,
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 30.dp)
                    .align(Alignment.TopCenter)
            )

            Text(
                text = timerValue.toString(),
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 80.dp)
                    .align(Alignment.TopCenter)
            )
        }



        // 啟動呼吸循環動畫
        LaunchedEffect(Unit) {
            var i = 1
            currentState.value = breathingStates[3]// 更新狀態為 "準備"
            for (sec in 3 downTo 1) { // 準備時長 4 秒
                timerValue = sec // 直接更新 timerValue
                delay(1000) // 每秒更新一次
            }

            while ( i <= maxCycles ) { // 迴圈直到達到最大循環次數
                // 1. 吸氣：移動到終點位置
                currentState.value = breathingStates[0] // 更新狀態為 "吸氣"


                // 使用 coroutineScope 來確保這些動畫能同時進行
                coroutineScope {
                    launch {
                        for (sec in 1..method.inhale) { // 吸氣時長
                            timerValue = sec // 直接更新 timerValue
                            delay(1000) // 每秒更新一次
                        }
                    }


                    launch {
                        cloudLeftOffset.animateTo(
                            targetValue = -100f, // 左側雲朵最終位置
                            animationSpec = tween(durationMillis = method.inhale * 1000 , easing = LinearEasing)
                        )
                    }
                    launch {
                        cloudRightOffset.animateTo(
                            targetValue = 100f, // 右側雲朵最終位置
                            animationSpec = tween(durationMillis = method.inhale * 1000, easing = LinearEasing)
                        )
                    }
                }


                // 2.（憋氣）
                currentState.value = breathingStates[1] // 更新狀態為 "憋氣"
                for (sec in 1..method.hold) { // 憋氣時長 4 秒
                    timerValue = sec // 直接更新 timerValue
                    delay(1000) // 每秒更新一次
                }

                if( i == maxCycles / 2 ) {
                    sunImage = R.drawable.calmsun
                }
                else if ( i == maxCycles ){
                    sunImage = R.drawable.happysun
                }
                // 3. 吐氣：移動回來
                currentState.value = breathingStates[2] // 更新狀態為 "吐氣"

                coroutineScope {
                    launch {
                        for (sec in 1..method.exhale) { // 吐氣時長
                            timerValue = sec // 直接更新 timerValue
                            delay(1000) // 每秒更新一次
                        }
                    }

                    launch {
                        cloudLeftOffset.animateTo(
                            targetValue = -300f, // 回到起始位置
                            animationSpec = tween(durationMillis = method.exhale * 1000, easing = LinearEasing)
                        )
                    }
                    launch {
                        cloudRightOffset.animateTo(
                            targetValue = 300f, // 回到起始位置
                            animationSpec = tween(durationMillis = method.exhale * 1000, easing = LinearEasing)
                        )
                    }
                }

                // 可選：在下一次動畫之前等待一些時間
                //delay(500) // 在下一次動畫之前等待 500 毫秒

                if( selectedMethod == "Box" ){
                    currentState.value = breathingStates[1] // 更新狀態為 "憋氣"
                    for (sec in 1..method.hold) { // 吸氣時長 4 秒
                        timerValue = sec // 直接更新 timerValue
                        delay(1000) // 每秒更新一次
                    }
                }


                Log.d( Tag, "現在次數 : $i") // lod.d( 標籤 , 想要顯示的訊息

                i++ // 增加計數器



            }

            shouldNavigate.value = true
        }



        if (shouldNavigate.value) {
            LaunchedEffect(Unit) {
                shouldNavigate.value = false
                setResult(RESULT_OK)
                //delay(100)
                activity?.finish()
            }
        }
    }


    private fun updateCycleDuration(method: BreathingMethod) {
        cycleDuration = (method.inhale + method.hold + method.exhale) * 1000L
    }

    private fun startBreathingCycle(totalDuration: Long,
                                    maxCycles: Int,
                                    updateOffsets: (Float, Float) -> Unit // 用於更新雲的偏移量的 lambda
    ) {
        val method = breathingMethods[selectedMethod] ?: return
        var i :Int = 0
        while ( i < maxCycles ) {
            animateCloud(method.inhale, method.hold, method.exhale, updateOffsets)
            i++
        }
    }

    private fun animateCloud( inhale: Int,hold: Int,exhale: Int ,updateOffsets: (Float, Float) -> Unit ) {

    }





















    private fun animateClouds(duration: Long, isInhaling: Boolean, updateOffsets: (Float, Float) -> Unit) {
        val startLeft = if (isInhaling) 0f else -400f
        val endLeft = if (isInhaling) -400f else 0f
        val startRight = if (isInhaling) 0f else 400f
        val endRight = if (isInhaling) 400f else 0f

        val animatorLeft = ValueAnimator.ofFloat(startLeft, endLeft).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                updateOffsets(value, -value)
            }
        }





        val animatorRight = ValueAnimator.ofFloat(startRight, endRight).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                updateOffsets(-value, value)
            }
        }

        animatorLeft.start()
        animatorRight.start()
    }

    private fun countdown(seconds: Int, onFinish: () -> Unit) {
        timerValue = seconds
        val timerAnimator = ValueAnimator.ofInt(0, seconds).apply {
            this.duration = seconds * 1000L
            addUpdateListener { animation ->
                timerValue = (seconds - animation.animatedValue as Int)
            }
            doOnEnd { onFinish() }
        }
        timerAnimator.start()
    }
}
