package com.example

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brelax.R
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.brelax.ui.theme.BreathingActivity
import com.example.brelax.ui.theme.BreathingMethodInfo
import com.example.brelax.ui.theme.BreathingMethodInfo2
import com.example.brelax.ui.theme.BreathingModeSelector
import com.example.brelax.ui.theme.Breathmethod1
import com.example.brelax.ui.theme.PreviousButton
import com.example.brelax.ui.theme.StartButton

@OptIn(ExperimentalFoundationApi::class)
@Preview( showSystemUi = true)
@Composable
fun BreathScreenbox(navController: NavController= rememberNavController()) {
    // State variables for selected mode and breathing method
    val selectedMode = remember { mutableStateOf("深度放鬆") }
    val selectedBreathingMethod = remember { mutableStateOf("4-7-8") }
    val items = (1..10).toList() // Simulated data
    val lazyListState = rememberLazyListState() // State for LazyRow scrolling
    val centralIndex = remember { mutableIntStateOf(1) } // Default selected index
    val context = LocalContext.current // Get context

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        result ->
        if (result.resultCode == Activity.RESULT_OK) {
            navController.navigate("breathingend")
        }
    }
    // Box to overlay background image and other content
    Box(
        modifier = Modifier.fillMaxSize() // Fill the entire screen
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.happybackground), // Background image resource
            contentDescription = null, // No content description needed
            modifier = Modifier.fillMaxSize(), // Background image fills the entire screen
            contentScale = ContentScale.Crop // Ensure the image fills and crops
        )

        // Column for vertical layout with vertical scroll
        Column(
            modifier = Modifier
                .fillMaxSize() // Fill the entire screen
                .padding(0.dp)
                .verticalScroll(rememberScrollState()), // Enable vertical scroll
            horizontalAlignment = Alignment.CenterHorizontally, // Center horizontally
            verticalArrangement = Arrangement.Top // Arrange items from top to bottom
        ) {
            PreviousButton(
                modifier = Modifier.align(Alignment.Start),
                onClick = { navController.popBackStack() }
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Image display
            Image(
                painter = painterResource(id = R.drawable.sun_choose), // Image resource
                contentDescription = null, // No content description needed
                modifier = Modifier.size(200.dp) // Set image size
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Breathing mode selector
            BreathingModeSelector(selectedMode.value) { mode ->
                selectedMode.value = mode
                when ( selectedMode.value ) {
                    "深度放鬆" -> {
                        selectedBreathingMethod.value = "4-7-8"
                    }
                    "快速放鬆"-> {
                        selectedBreathingMethod.value = "3-3-3"
                    }
                    "專注放鬆"-> {
                        selectedBreathingMethod.value = "Box"
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Breathing method selector
            Breathmethod1(selectedMode.value, selectedBreathingMethod.value) { method ->
                selectedBreathingMethod.value = method
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show selected breathing method details
            when (selectedBreathingMethod.value) {
                "4-7-8" -> {
                    BreathingMethodInfo(
                        title = "4-7-8 呼吸法",
                        shortD = "適合在感到焦慮、無法入睡時使用",
                        detailed = "4-7-8呼吸法是一種能有效減少焦慮並改善睡眠的技巧。" +
                                "這種方法有助於放慢呼吸速度，" +
                                "促進身體進入深度放鬆的狀態。" +
                                "當你難以入睡或面臨極大壓力與焦慮時，這項技巧特別有幫助。",
                        inhale = "吸氣 4 秒",
                        exhale = "吐氣 7 秒",
                        hold = "屏氣 8 秒"
                    )
                }
                "4-2-6" -> {
                    BreathingMethodInfo(
                        title = "4-2-6 呼吸法",
                        shortD = "適合在壓力大的情況下，幫助回到平靜狀態",
                        detailed = "是幫助從交感神經系統（逃跑或戰鬥反應）轉移到副交感神經系統（休息和消化狀態）。" +
                                "這種方法可以促進橫膈膜呼吸，活化迷走神經，" +
                                "從而降低心跳速度、促進消化，幫助身體回到平靜狀態，減少壓力和焦慮。",
                        inhale = "吸氣 4 秒",
                        exhale = "吐氣 6 秒",
                        hold = "屏氣 2 秒"
                    )
                }
                "3-3-3" -> {
                    BreathingMethodInfo(
                        title = "3-3-3 呼吸法",
                        shortD = "適合日常短暫休息時應用",
                        detailed = "3-3-3呼吸法是一種簡單而有效的呼吸技巧，幫助個人快速放鬆、減少壓力和焦慮感，" +
                                "有助於平靜神經系統的快速呼吸法，可以在日常生活中隨時隨地應用。" +
                                "當您感到焦慮或壓力並需要快速恢復平靜時，這是一個很好的工具。",
                        inhale = "吸氣 3 秒",
                        exhale = "吐氣 3 秒",
                        hold = "屏氣 3 秒"
                    )
                }
                "Box" -> {
                    BreathingMethodInfo2(
                        title = "Box 呼吸法",
                        shortD = "專注與減壓，特別適合在工作或需要提升專注力時使用",
                        detailed = "Box breathing（方形呼吸），" +
                                "又稱為四方呼吸，是一種簡單的呼吸技巧，主要用於幫助減輕壓力、提升專注力和促進放鬆。" +
                                "它可以幫助調節自主神經系統，減少焦慮並改善心理狀態。",
                        inhale = "吸氣 4 秒",
                        exhale = "吐氣 4 秒",
                        hold = "屏氣 4 秒"
                    )
                }



            // 其他呼吸法信息略去
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Control breathing minutes display
            Text(text = "呼吸分鐘", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(25.dp))

            // Horizontal scrolling LazyRow
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Animate scroll to the central index
                LaunchedEffect(centralIndex.intValue) {
                    val targetIndex = centralIndex.intValue - 1
                    lazyListState.animateScrollToItem((targetIndex - 3).coerceAtLeast(1))
                }

                // LazyRow for horizontal scrolling
                LazyRow(
                    state = lazyListState,
                    flingBehavior = rememberSnapFlingBehavior(lazyListState), // Enable Snap behavior
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 1.dp), // Padding for centered numbers
                    horizontalArrangement = Arrangement.spacedBy(15.dp) // Spacing between numbers
                ) {
                    items(items.size) { index ->
                        val item = items[index]
                        val isSelected = centralIndex.intValue == index + 1  // Check if current item is selected

                        // Display numbers with a circular background for selected number
                        Box(
                            contentAlignment = Alignment.Center, // Center content
                            modifier = Modifier
                                .size(40.dp) // Size for circular background
                                .background(
                                    color = if (isSelected) Color.White else Color.Transparent, // Background color based on selection
                                    shape = CircleShape // Circular background
                                )
                                .clickable {
                                    centralIndex.intValue = index + 1 // Set selected item on click
                                }
                        ) {
                            Text(
                                text = item.toString(),
                                fontSize = if (isSelected) 24.sp else 20.sp, // Font size for selected item
                                color = if (isSelected) Color(0xFF0E0F0F) else Color.Gray, // Color based on selection
                                modifier = Modifier
                                    .padding(8.dp) // Padding for text
                                    .fillMaxWidth(), // Fill available width for horizontal centering
                                textAlign = TextAlign.Center // Center text alignment
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Start breathing button
            StartButton(
                text = "開始呼吸",
                onClick = {
                // Build Intent to pass selected breathing method and time
                val intent = Intent(context, BreathingActivity::class.java).apply {
                    putExtra("BREATHING_METHOD", selectedBreathingMethod.value)
                    putExtra("TIME_MINUTES", centralIndex.intValue)

                }

                // Start BreathingActivity
                launcher.launch(intent)
            })
        }
    }



}

