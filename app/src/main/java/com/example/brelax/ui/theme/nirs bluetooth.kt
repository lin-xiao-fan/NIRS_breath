package com.example.brelax.ui.theme

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.brelax.R
import com.example.brelax.UUIDList
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionStatus
import java.util.UUID

private const val TAG = "NirsBluetooth"
private const val DEVICE_ADDRESS = "F0:58:B9:54:BA:8B"  // 設備的藍牙地址
private const val DEVICE_ADDRESS2 = "C7:9C:45:DC:93:DF"  // 設備的藍牙地址
private const val SCAN_DURATION = 3000L  // 掃描時長（毫秒）
var isSampling = false


@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun NirsBluetooth(navController: NavController, context: Context, permissionsState: MultiplePermissionsState,bluetoothViewModel: BluetoothViewModel) {
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter = bluetoothManager.adapter

    var showDialog by remember { mutableStateOf(false) }
    var deniedPermissionNames by remember { mutableStateOf("") }
    var scannedDevices by remember { mutableStateOf(mutableSetOf<String>()) }
    var connectionStatus by remember { mutableStateOf("") }
    var isScanning by remember { mutableStateOf(false) }

    //var measurementValues by remember { mutableStateOf(listOf<ByteArray>()) }
    //val bluetoothViewModel: BluetoothViewModel = viewModel()
    val measurementValues by bluetoothViewModel.measurementValues.observeAsState(emptyList())
    if (!permissionsState.allPermissionsGranted) {
        LaunchedEffect(Unit) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    if ( !isSampling ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.nirsbackground),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(80.dp))
                Text(
                    text = "選擇測量時間",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.tblack),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 20.dp)
                )
                Spacer(modifier = Modifier.height(40.dp))

                Button(onClick = {
                    if (permissionsState.allPermissionsGranted) {
                        scannedDevices.clear()
                        isScanning = true
                        startScan(bluetoothAdapter) { device ->
                            val (deviceName, deviceAddress) = device.split(" - ")
                            if ((deviceAddress == DEVICE_ADDRESS) && scannedDevices.add(device)) {
                                Log.d(TAG, "Device found: $device")
                            }
                        }

                        Handler(Looper.getMainLooper()).postDelayed({
                            stopScan(bluetoothAdapter)
                            isScanning = false
                        }, SCAN_DURATION)
                    } else {
                        deniedPermissionNames = permissionsState.permissions.filter {
                            it.status is PermissionStatus.Denied
                        }.joinToString(", ") { it.permission }

                        Toast.makeText(
                            context,
                            "請授予以下權限: $deniedPermissionNames",
                            Toast.LENGTH_SHORT
                        ).show()
                        showDialog = true
                    }
                }) {
                    Text("開始掃描")
                }

                Spacer(modifier = Modifier.height(20.dp))

                DeviceList(
                    scannedDevices,
                    isScanning,
                    bluetoothAdapter,
                    context,
                    onConnectionStatusChanged = {
                        connectionStatus = it
                    },
                    bluetoothViewModel
                )

                Text(text = connectionStatus, modifier = Modifier.padding(8.dp))

                Spacer(modifier = Modifier.height(80.dp))
                Button(onClick = {

                    navController.navigate("Nirsdatascreen")
                    isSampling = true
                    bluetoothViewModel.reset()
                }) {
                    Text("開始測量")
                }

                if (showDialog) {
                    PermissionDialog(deniedPermissionNames, onDismiss = { showDialog = false }) {
                        permissionsState.launchMultiplePermissionRequest()
                        showDialog = false
                    }
                }
            }
        }

    }
    else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(80.dp))
                Button(onClick = {
                    //disconnectFromDevice()
                    bluetoothViewModel.saveProcessedDataToCSV(context)
                    bluetoothViewModel.saveRawDataToCSV(context)

                    isSampling = false
                    // 顯示斷開連接訊息或導航至其他頁面
                    navController.navigate("Nirsbluetooth")
                }) {
                    Text("結束連接")
                }
            }
        }
    }
}



@Composable
fun DeviceList(
    devices: Set<String>,
    isScanning: Boolean,
    bluetoothAdapter: BluetoothAdapter,
    context: Context,
    onConnectionStatusChanged: (String) -> Unit,
    bluetoothViewModel: BluetoothViewModel = viewModel()
) {
    LazyColumn {
        if (!isScanning) {
            items(devices.toList()) { device ->
                DeviceRow(device, bluetoothAdapter, context, onConnectionStatusChanged, bluetoothViewModel)
            }
        } else {
            item { Text("掃描中...") }
        }
    }
}

@Composable
fun DeviceRow(
    device: String,
    bluetoothAdapter: BluetoothAdapter,
    context: Context,
    onConnectionStatusChanged: (String) -> Unit,
    bluetoothViewModel: BluetoothViewModel = viewModel()
) {
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = device, modifier = Modifier.weight(1f))
        Button(onClick = {
            onConnectionStatusChanged("正在連接到 $device...")
            connectToDevice(bluetoothAdapter, DEVICE_ADDRESS, context, onConnectionResult = { success ->
                onConnectionStatusChanged(
                    if (success) "已成功連接到 $device" else "連接到 $device 失敗"
                )
            }) { data ->
                // 這裡顯示接收到的數據
                bluetoothViewModel.updateMeasurementValues(data)
                //onDataReceived(data)
                Log.d(TAG, "updateMeasurementValues")
                Log.d(TAG, "Received data: ${data.size}")
                Log.d(TAG, "view data: ${bluetoothViewModel.getsize()}")
            }
        }) {
            Text("連接")
        }
    }
}

@Composable
fun PermissionDialog(
    deniedPermissions: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("權限請求") },
        text = { Text("請授予以下權限: $deniedPermissions") },
        confirmButton = {
            Button(onClick = { onConfirm() }) {
                Text("同意")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("取消")
            }
        }
    )
}

@SuppressLint("MissingPermission")
fun startScan(bluetoothAdapter: BluetoothAdapter, onDeviceFound: (String) -> Unit) {
    val scanner = bluetoothAdapter.bluetoothLeScanner
    val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val deviceName = result.device.name ?: "Unknown Device"
            val deviceAddress = result.device.address
            onDeviceFound("$deviceName - $deviceAddress")
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            results.forEach { result ->
                val deviceName = result.device.name ?: "Unknown Device"
                val deviceAddress = result.device.address
                onDeviceFound("$deviceName - $deviceAddress")
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "Scan failed with error code $errorCode")
        }
    }

    Log.d(TAG, "Starting Bluetooth scan")
    scanner.startScan(scanCallback)
}

@SuppressLint("MissingPermission")
fun stopScan(bluetoothAdapter: BluetoothAdapter) {
    val scanner = bluetoothAdapter.bluetoothLeScanner
    scanner.stopScan(object : ScanCallback() {})
    Log.d(TAG, "Bluetooth scan stopped")
}


// 儲存接收到的數據
private val collectedData = mutableListOf<ByteArray>()
// 設定 Handler 來處理半秒後回傳數據
private val handler = Handler(Looper.getMainLooper())



private var isDataCollectionRunning = false // 用來標誌數據收集是否正在進行




@SuppressLint("MissingPermission")
fun connectToDevice(
    bluetoothAdapter: BluetoothAdapter,
    deviceAddress: String,
    context: Context,
    onConnectionResult: (Boolean) -> Unit,
    onDataReceived: (List<ByteArray>) -> Unit  // 接收数据的回调
) {
    val device = bluetoothAdapter.getRemoteDevice(deviceAddress)

    val bluetoothGatt = device.connectGatt(context, false, object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(TAG, "Connected to device: $deviceAddress")
                    // 请求MTU
                    gatt.requestMtu(247)
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "Disconnected from device: $deviceAddress")
                    onConnectionResult(false)
                    gatt.close()
                }
            }
        }



        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            // 打印出MTU的大小
            Log.d(TAG, "MTU changed: $mtu")
            // 开始服务发现
            gatt.discoverServices()
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.d(TAG, "IN onServicesDiscovered")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Services discovered successfully")

                // 寻找特定服务和特征
                val service = gatt.getService(UUID.fromString("00001A00-0000-1000-8000-00805f9b34fb"))
                if (service != null) {
                    Log.d(TAG, "Service found: ${service.uuid}")
                    val characteristic = service.getCharacteristic(UUID.fromString("00001A01-0000-1000-8000-00805f9b34fb"))
                    if (characteristic != null) {
                        Log.d(TAG, "Characteristic found: ${characteristic.uuid}")



                        // 检查特征的支持特性
                        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ != 0) {
                            Log.d(TAG, "Characteristic supports READ")
                        }
                        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE != 0) {
                            Log.d(TAG, "Characteristic supports WRITE")
                        }
                        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) {
                            Log.d(TAG, "Characteristic supports NOTIFY")
                            // 在這裡調用 signalNotify 來啟用通知
                            //signalNotify(gatt, true)  // 啟用通知


                        }
                        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE != 0) {
                            Log.d(TAG, "Characteristic supports INDICATE")
                        }

                        // 启用通知
                        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) {
                            gatt.setCharacteristicNotification(characteristic, true)

                            val descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                            if (descriptor != null) {
                                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                val result = gatt.writeDescriptor(descriptor)
                                Log.d(TAG, "Descriptor write initiated: $result")
                            } else {
                                Log.e(TAG, "Descriptor not found for characteristic: ${characteristic.uuid}")
                            }

                            Log.d(TAG, "Notifications enabled for characteristic")
                        } else {
                            Log.e(TAG, "Characteristic does not support notifications")
                        }
                    } else {
                        Log.e(TAG, "Characteristic not found")
                    }
                } else {
                    Log.e(TAG, "Service not found")
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }



        private fun processCollectedData() {
            // 處理數據
            if (collectedData.isNotEmpty()) {
                // 這裡可以處理數據，並且回傳
                // 比如將 collectedData 轉為字符串或其他格式
                Log.d(TAG, "return data ")
                // 回傳數據
                onDataReceived(collectedData)



                // 清空已收集的數據
                collectedData.clear()
            }


            // 設置標誌為數據收集已經完成
            isDataCollectionRunning = false
        }

        private fun startDataCollection() {
            // 設置定時器：每隔 500 毫秒處理一次數據
            isDataCollectionRunning = true
            handler.postDelayed({
                processCollectedData()
            }, 1000)  // 半秒後執行 processCollectedData()
        }



        @Deprecated("Deprecated in Java")
        @Suppress("DEPRECATION")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
        ) {
            //Log.d(TAG, "old my people")

            val data = characteristic.value
            if (data != null && data.isNotEmpty()) {
                //val dataString = String(data, Charsets.UTF_8)
                //數據處理 ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                // 將接收到的數據加入到 collectedData 列表
                collectedData.add(data)

                // 開始數據收集，每500ms後處理一次
                if (!isDataCollectionRunning) {
                    startDataCollection()
                }
            } else {
                Log.d(TAG, "No data received or data is empty")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            //Log.d(TAG, "old my people")

            val data = characteristic.value
            if (data != null && data.isNotEmpty()) {
                //val dataString = String(data, Charsets.UTF_8)
                //數據處理 ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                // 將接收到的數據加入到 collectedData 列表
                collectedData.add(data)

                // 開始數據收集，每500ms後處理一次
                if (!isDataCollectionRunning) {
                    startDataCollection()
                }
            } else {
                Log.d(TAG, "No data received or data is empty")
            }
        }



        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Descriptor write successful")
            } else {
                Log.e(TAG, "Descriptor write failed")
            }
        }
    })





    onConnectionResult(true)
}




