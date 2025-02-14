package com.example

import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.brelax.ui.theme.BrelaxTheme
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.brelax.R
import com.example.brelax.ui.theme.BreathScreenUI
import com.example.brelax.ui.theme.NirsMeasurement
import com.example.brelax.ui.theme.NirsBluetooth
import com.example.brelax.ui.theme.StartButton
import com.example.brelax.ui.theme.Nirsdatascreen
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import com.example.brelax.ui.theme.BluetoothViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    private var permissionsState: MultiplePermissionsState? = null

    private val bluetoothViewModel: BluetoothViewModel by viewModels()
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun CheckPermission() {
        // 獲取當前上下文
        val context = LocalContext.current

        // 檢查設備是否支持藍牙
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(context, "Not support Bluetooth", Toast.LENGTH_SHORT).show()
            // 在這裡可能需要考慮如何安全地關閉活動或導航到其他畫面
            return // 直接返回，不繼續執行
        }

        // 準備權限列表
        val permissionList = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionList.add(Manifest.permission.BLUETOOTH_SCAN)
            permissionList.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        // 記錄權限狀態
        permissionsState = rememberMultiplePermissionsState(permissionList)

        // 如果未授予所有權限，則請求權限
        if (!permissionsState!!.allPermissionsGranted) {
            Log.d("test","not good")
            LaunchedEffect(Unit) {
                permissionsState!!.launchMultiplePermissionRequest()
            }
        }
        else{
            Log.d("test"," good")
        }
    }


    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 檢查藍牙功能是否可用
        val bluetoothAvailable = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
        val bluetoothLEAvailable = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
        Toast.makeText(this, "Bluetooth Available: $bluetoothAvailable\nBluetooth LE Available: $bluetoothLEAvailable", Toast.LENGTH_LONG).show()

        setContent {
            // 設定組合內容
            CheckPermission()

            // 設置導航
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { breathMainScreen(navController) }
                composable("nirs") { nirsMainScreen(navController) }
                composable("tes") { tesMainScreen(navController) }
                composable("user") { userMainScreen(navController) }
                composable("breathing") { BreathScreenbox(navController) }
                composable("breathingend") { BreathScreenUI(navController) }

                composable("Nirsbluetooth") { permissionsState?.let { it1 ->
                    NirsBluetooth(navController, this@MainActivity,
                        it1,bluetoothViewModel
                    )
                } }
                //composable("Bluetoothpage") { Bluetoothpage().route }
                composable("Nirsdatascreen") { Nirsdatascreen(navController,bluetoothViewModel) }

                composable("Nirsmeasurement") { NirsMeasurement(navController) }

            }
        }
    }
}


@Composable
fun breathMainScreen(navController: NavHostController) {
    var selectedItem by remember { mutableStateOf("Breath") }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.happybackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 使用 verticalScroll 實現滾動
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // 添加 verticalScroll
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            // 傳遞 selectedItem 和 navController 給 TopNavBar
            TopNavBar(
                selectedItem = selectedItem, // 獲取當前選中項目
                onItemSelected = { newItem ->
                    selectedItem = newItem // 更新選中狀態
                },
                navController = navController
            )
            Spacer(modifier = Modifier.height(150.dp))

            Image(
                painter = painterResource(id = R.drawable.suncloud),
                contentDescription = "Sun and Cloud",
                modifier = Modifier.size(230.dp)
            )
            Spacer(modifier = Modifier.height(150.dp))

            // Go Breathing 按鈕
            StartButton(
                text = "開始呼吸",
                onClick = { navController.navigate("breathing") },
                modifier = Modifier
                    .padding(10.dp)
                    .width(250.dp),


                    )

        }
    }
}

@Composable
fun nirsMainScreen(navController: NavHostController) {
    var selectedItem by remember { mutableStateOf("NIRS") }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.nirsbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 使用 verticalScroll 實現滾動
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // 添加 verticalScroll
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            TopNavBar(
                selectedItem = selectedItem, // 獲取當前選中項目
                onItemSelected = { newItem ->
                    selectedItem = newItem // 更新選中狀態
                },
                navController = navController
            )
            Spacer(modifier = Modifier.height(150.dp))

            Image(
                painter = painterResource(id = R.drawable.nirscloud),
                contentDescription = "Sun and Cloud",
                modifier = Modifier.size(230.dp)
            )
            Spacer(modifier = Modifier.height(150.dp))

            // Go Breathing 按鈕
            StartButton(
                text = "開始測量",
                onClick = { navController.navigate("nirsbluetooth") },
                modifier = Modifier
                    .padding(10.dp)
                    .width(250.dp),


            )

        }
    }
}

@Composable
fun tesMainScreen(navController: NavHostController) {
    var selectedItem by remember { mutableStateOf("tES") }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.tesbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 使用 verticalScroll 實現滾動
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // 添加 verticalScroll
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            TopNavBar(
                selectedItem = selectedItem, // 獲取當前選中項目
                onItemSelected = { newItem ->
                    selectedItem = newItem // 更新選中狀態
                },
                navController = navController
            )
            Spacer(modifier = Modifier.height(115.dp))

            Image(
                painter = painterResource(id = R.drawable.teshome),
                contentDescription = "Sun and Cloud",
                modifier = Modifier.size(300.dp)
            )
            Spacer(modifier = Modifier.height(115.dp))

            // Go Breathing 按鈕
            StartButton(
                text = "開始刺激",
                onClick = { navController.navigate("") },
                modifier = Modifier
                    .padding(10.dp)
                    .width(250.dp),


                )

        }
    }
}

@Composable
fun userMainScreen(navController: NavHostController) {
    var selectedItem by remember { mutableStateOf("USER") }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.userbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 使用 verticalScroll 實現滾動
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // 添加 verticalScroll
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            TopNavBar(
                selectedItem = selectedItem, // 獲取當前選中項目
                onItemSelected = { newItem ->
                    selectedItem = newItem // 更新選中狀態
                },
                navController = navController
            )
            Spacer(modifier = Modifier.height(150.dp))

            Image(
                painter = painterResource(id = R.drawable.nirscloud),
                contentDescription = "Sun and Cloud",
                modifier = Modifier.size(230.dp)
            )
            Spacer(modifier = Modifier.height(150.dp))

            // Go Breathing 按鈕
            StartButton(
                onClick = { navController.navigate("") },
                modifier = Modifier
                    .padding(10.dp)
                    .width(250.dp),


                )

        }
    }
}


@Composable
fun TopNavBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    navController: NavHostController

) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .width(280.dp)
                .aspectRatio(4f)
                .background(Color.White)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween // 平均分配按鈕間的空間
        ) {
            IconButton(
                painterNormal = painterResource(id = R.drawable.bolticon),
                painterSelected = painterResource(id = R.drawable.tes2),
                text = "tES",
                isSelected = selectedItem == "tES",
                normalTextColor = Color(0xFF585858), // 正常狀態的顏色
                selectedTextColor = Color(0xFFFFCD45), // 選中狀態的顏色
                onClick = {
                    onItemSelected("tES")
                    navController.navigate("tES") // 將這裡改為對應的頁面路徑
                }

            )
            IconButton(
                painterNormal = painterResource(id = R.drawable.breathicon),
                painterSelected = painterResource(id = R.drawable.breath2),
                text = "Breath",
                isSelected = selectedItem == "Breath",
                normalTextColor = Color(0xFF585858), // 正常狀態的顏色
                selectedTextColor = Color(0xFF2FB3FF), // 選中狀態的顏色
                onClick = {
                    onItemSelected("Breath")
                    navController.navigate("home") // 將這裡改為對應的頁面路徑
                }
            )
            IconButton(
                painterNormal = painterResource(id = R.drawable.nirsicon),
                painterSelected = painterResource(id = R.drawable.nirs2),
                text = "NIRS",
                normalTextColor = Color(0xFF585858), // 正常狀態的顏色
                selectedTextColor = Color(0xFFFFF9898), // 選中狀態的顏色
                isSelected = selectedItem == "NIRS",
                onClick = {
                    onItemSelected("NIRS")
                    navController.navigate("nirs") // 將這裡改為對應的頁面路徑
                }
            )
            IconButton(
                painterNormal = painterResource(id = R.drawable.usericon),
                painterSelected = painterResource(id = R.drawable.user2),
                text = "USER",
                normalTextColor = Color(0xFF585858), // 正常狀態的顏色
                selectedTextColor = Color(0xFFFFFA237), // 選中狀態的顏色
                isSelected = selectedItem == "USER",
                onClick = {
                    onItemSelected("USER")
                    navController.navigate("user") // 將這裡改為對應的頁面路徑
                }
            )
        }
    }



@Composable
fun IconButton(
    painterNormal: Painter,// 正常狀態的圖標
    painterSelected: Painter,// 選中狀態的圖標
    normalTextColor: Color, // 正常狀態的文字顏色
    selectedTextColor: Color, // 選中狀態的文字顏色
    text: String,
    isSelected: Boolean, // 按鈕是否被按下
    onClick: () -> Unit
) {
    val painter = if (isSelected) painterSelected else painterNormal // 根據按鈕狀態選擇圖標
    val textColor = if (isSelected) selectedTextColor else normalTextColor // 根據選中狀態選擇文字顏色

    Box(
        modifier = Modifier
            .width(65.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(60.dp),
            contentAlignment = Alignment.Center // 垂直和水平置中
        ) {
            // 圖標
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(30.dp)
                    .align(Alignment.TopCenter) // 文字置於底部
            )

            // 文字
            Text(
                text = text,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                color = textColor, // 使用計算出的文字顏色
                modifier = Modifier
                    .align(Alignment.BottomCenter) // 文字置於底部
                    .padding(top = 4.dp) // 添加與圖標的間距
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun breathMainScreen() {
    BrelaxTheme {
        breathMainScreen(rememberNavController()) // 提供 NavController
    }
}

@Preview(showBackground = true)
@Composable
fun nirsMainScreen() {
    BrelaxTheme {
        nirsMainScreen(rememberNavController()) // 提供 NavController
    }
}

@Preview(showBackground = true)
@Composable
fun tesMainScreen() {
    BrelaxTheme {
        tesMainScreen(rememberNavController()) // 提供 NavController
    }
}

@Preview(showBackground = true)
@Composable
fun userMainScreen() {
    BrelaxTheme {
        userMainScreen(rememberNavController()) // 提供 NavController
    }
}

@Preview(showBackground = true)
@Composable
fun TopNavBarPreview() {
    val navController = rememberNavController() // 創建導航控制器的實例

    TopNavBar(
        selectedItem = "Breath", // 設定預覽中選中的項目
        onItemSelected = { /* 處理選中項目邏輯 */ },
        navController = navController // 傳遞導航控制器
    )
}