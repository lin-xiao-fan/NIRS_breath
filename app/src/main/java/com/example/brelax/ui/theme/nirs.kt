package com.example.brelax.ui.theme

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.brelax.Nirsdata
import com.example.brelax.R
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.layout.fullWidth
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sin
import uk.me.berndporr.iirj.Butterworth
import kotlin.math.log
import kotlin.math.min

/*
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "home") {
                composable("nirsmeasurement") { NirsMeasurement(navController) }
                composable("measurement/{minutes}/{seconds}") { backStackEntry ->
                    val minutes = backStackEntry.arguments?.getInt("minutes") ?: 0
                    val seconds = backStackEntry.arguments?.getInt("seconds") ?: 0
                    TimerScreen(minutes, seconds)
                }
            }
        }
    }
}

 */

@Composable
fun NirsMeasurement(navController: NavController) {
    var selectedMinutes by remember { mutableStateOf(1) }
    var selectedSeconds by remember { mutableStateOf(0) }

    // 背景圖片和內容
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.nirsbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
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
            Image(
                painter = painterResource(id = R.drawable.nirscloud),
                contentDescription = "雲朵圖像",
                modifier = Modifier
                    .size(230.dp)
                    .padding(vertical = 20.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))

            TimePicker(
                selectedMinutes = selectedMinutes,
                onMinutesChange = { selectedMinutes = it },
                selectedSeconds = selectedSeconds,
                onSecondsChange = { selectedSeconds = it }
            )
            Spacer(modifier = Modifier.height(80.dp))
            Button1(
                onClick = {
                    navController.navigate("measurement")
                },
                text = "開始測量",
            )
        }
    }
}

@Composable
fun TimePicker(
    selectedMinutes: Int,
    onMinutesChange: (Int) -> Unit,
    selectedSeconds: Int,
    onSecondsChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 分鐘選擇
        WheelPicker(
            value = selectedMinutes,
            onValueChange = onMinutesChange,
            range = 0..59
        )
        Text(
            text = " : ",
            style = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold)
        )
        // 秒數選擇
        WheelPicker(
            value = selectedSeconds,
            onValueChange = onSecondsChange,
            range = 0..59
        )
    }
}

@Composable
fun WheelPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = value)
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.size(100.dp, 150.dp)) {
        // LazyColumn 實現滾動選取器
        LazyColumn(
            state = listState,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(range.toList()) { index, item ->
                val isSelected = index == listState.firstVisibleItemIndex + 1
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = 9.8.dp) // 控制每個項目的間距
                ) {
                    Text(
                        text = String.format("%02d", item),
                        style = TextStyle(
                            fontSize = if (isSelected) 30.sp else 24.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else Color.Gray
                        ),
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 固定的選取區塊，用透明背景表示
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(60.dp))
                .align(Alignment.Center)
                .background(Color.White.copy(alpha = 0.5f))
        )
    }

    // 自動吸附到最近的數字
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val firstVisibleItemIndex = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset

            if (offset > 50) {
                coroutineScope.launch {
                    listState.animateScrollToItem(firstVisibleItemIndex + 1)
                    onValueChange(range.toList()[firstVisibleItemIndex + 1])
                }
            } else {
                coroutineScope.launch {
                    listState.animateScrollToItem(firstVisibleItemIndex)
                    onValueChange(range.toList()[firstVisibleItemIndex])
                }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun PreviewNirsMeasurementScreen() {
    // 在預覽中，忽略 navController
    NirsMeasurement(navController = rememberNavController())
}




/*
@Composable
fun TimerScreen(minutes: Int, seconds: Int,) {
    var timeLeft by remember { mutableStateOf((minutes * 60 + seconds).toLong()) }
    var isTimerRunning by remember { mutableStateOf(true) } // 是否正在計時

    // 倒數計時器
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            object : CountDownTimer(60000, 1000) { // 60秒倒數
                override fun onTick(millisUntilFinished: Long) {
                    timeLeft = millisUntilFinished / 1000
                }

                override fun onFinish() {
                    isTimerRunning = false
                }
            }.start()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.nirsbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // NIRS測量中標題
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "NIRS測量中",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.tblack),
            )

            Spacer(modifier = Modifier.height(150.dp))
            // 雲的圖示
            Image(
                painter = painterResource(id = R.drawable.nirscloud), // 這裡要添加自己的雲圖標資源
                contentDescription = "Cloud Icon",
                modifier = Modifier.size(230.dp)
            )

            Spacer(modifier = Modifier.height(80.dp))

            // 倒數計時顯示
            Text(
                text = String.format("%01d:%02d", timeLeft / 60, timeLeft % 60),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.tblack)
            )

            Spacer(modifier = Modifier.height(40.dp))


        }
    }
}





@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        TimerScreen(minutes = 1, seconds = 30)
    }
}
*/

/*

fun datatf(bluetoothViewModel: BluetoothViewModel) {
    // 从 ViewModel 获取数据
    val measurementValues = bluetoothViewModel.measurementValues.value // 获取 LiveData 的值

    // 确保数据不为空
    if (measurementValues != null) {
        if (measurementValues.isNotEmpty()) {
            // 取出第一笔数据
            val firstData = measurementValues?.get(0)

            // 将 ByteArray 转换为 FloatArray
            val floatArray = firstData?.let { byteArrayToFloatArray(it) }



            // 假设此时转换后的数据是需要进行其他处理的
            // 这里可以继续做数据处理，比如使用 convertData 函数等
            val afeFs = 1000f  // 示例：采样频率为 1000 Hz
            val (voltageData, timeData) = convertData(listOf(floatArray), afeFs)

            // 打印转换后的电压数据和时间数据
            println("Voltage Data: $voltageData")
            println("Time Data: $timeData")
        } else {
            println("No data available in measurementValues.")
        }
    }
}

// 假设这是你之前提到的字节转浮点的函数
fun byteArrayToFloatArray(byteArray: ByteArray): FloatArray {
    val floatArray = FloatArray(byteArray.size / 4)
    for (i in byteArray.indices step 4) {
        val floatBits = ((byteArray[i].toInt() and 0xFF) shl 24) or
                ((byteArray[i + 1].toInt() and 0xFF) shl 16) or
                ((byteArray[i + 2].toInt() and 0xFF) shl 8) or
                (byteArray[i + 3].toInt() and 0xFF)
        floatArray[i / 4] = Float.fromBits(floatBits)
    }
    return floatArray
}

// 假设这是你提到的 convertData 函数
fun convertData(data: List<FloatArray?>, afeFs: Float): Pair<List<Float>, List<Float>> {
    // 假设数据转换逻辑如下：
    val voltageData = mutableListOf<Float>()
    val timeData = mutableListOf<Float>()

    data.forEach { floatArray ->
        if (floatArray != null) {
            for (i in floatArray.indices) {
                // 假设每个数据需要进行缩放操作
                voltageData.add( ( (floatArray?.get(i) ?: 0.0f) / (2f.pow(22)) * 1.2f ) )

                timeData.add(i / afeFs)  // 假设时间是基于采样频率的
            }
        }
    }

    return Pair(voltageData, timeData)
}


 */





private const val TAG = "Nirsdatascreen"






var test = false
var time : Double = 0.0




@Composable
fun Nirsdatascreen(navController: NavController, bluetoothViewModel: BluetoothViewModel) {
    // 观察 LiveData 中的测量数据
    val measurementValues by bluetoothViewModel.processdata.observeAsState()

    //var mVData by remember { mutableStateOf<ProcessedData>() }
    // 使用 remember 和 mutableStateOf 来管理 _nirsdatalist 状态
    val _nirsdatalist = remember { mutableStateOf(mutableListOf<Nirsdata>()) }

    LaunchedEffect(measurementValues) {
        if (measurementValues?.records?.isNotEmpty() == true) {
            //mVData = convertData(measurementValues)

            val temp = Nirsdata(
                timestamp = time,
                s2RedD1 = measurementValues!!.records[0].channels[0].values[0],
                s2IrD1 = measurementValues!!.records[0].channels[1].values[0],
                s1RedD1 = measurementValues!!.records[0].channels[2].values[0],
                s1IrD1 = measurementValues!!.records[0].channels[3].values[0],
                s2RedD2 = measurementValues!!.records[0].channels[4].values[0],
                s2IrD2 = measurementValues!!.records[0].channels[5].values[0],
                s1RedD2 = measurementValues!!.records[0].channels[6].values[0],
                s1IrD2 = measurementValues!!.records[0].channels[7].values[0],
                s2RedD3 = measurementValues!!.records[0].channels[8].values[0],
                s2IrD3 = measurementValues!!.records[0].channels[9].values[0],
                s1RedD3 = measurementValues!!.records[0].channels[10].values[0],
                s1IrD3 = measurementValues!!.records[0].channels[11].values[0],
            )
            time += 0.5

            // 直接更新 nirsdatalist
            _nirsdatalist.value.add(temp)
            //Log.d(TAG, "currentList size : " + _nirsdatalist.value.size)
        }
    }

    // 使用 remember 管理数据，而不是 LiveData
    val nirsdatalist = _nirsdatalist.value

    // 显示数据大小和其他内容
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {



        // 将 `DataView` 包装为一个项目
        item {
            DataView(
                title = "NIRS",
                data = nirsdatalist
            )
        }
    }
}



@Composable
fun LineChart(lineData: FloatArray, index: Int) {
    val chartHeight = 200.dp
    val chartWidth = 300.dp

    Canvas(modifier = Modifier
        .height(chartHeight)
        .fillMaxWidth()) {
        // 設置畫布的大小和比例
        val width = size.width
        val height = size.height

        // 計算每個數據點在畫布上的位置
        val padding = 50f
        val maxValue = lineData.maxOrNull() ?: 0f
        val minValue = lineData.minOrNull() ?: 0f

        val xSpacing = (width - 2 * padding) / (lineData.size - 1)
        val yScalingFactor = (height - 2 * padding) / (maxValue - minValue)

        // 繪製折線
        for (i in lineData.indices) {
            val startX = padding + i * xSpacing
            val startY = height - padding - (lineData[i] - minValue) * yScalingFactor
            val endX = padding + (i + 1) * xSpacing
            val endY = height - padding - (lineData[i + 1] - minValue) * yScalingFactor

            drawLine(
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                color = Color.Blue, // 每條折線顏色可以根據需要修改
                strokeWidth = 3f
            )
        }

        // 顯示每個數據點（選擇性）
        for (i in lineData.indices) {
            val x = padding + i * xSpacing
            val y = height - padding - (lineData[i] - minValue) * yScalingFactor
            drawCircle(
                color = Color.Red,
                radius = 5f,
                center = Offset(x, y)
            )
        }
    }
}



// 生成測量數據，範圍在 -15 到 +15 之間
fun generateMeasurementValues(): List<String> {
    return listOf(
        "S2red_D1: ${(-15..15).random()}",
        "S2IR_D1: ${(-15..15).random()}",
        "S1red_D1: ${(-15..15).random()}",
        "S1IR_D1: ${(-15..15).random()}",
        "S2red_D2: ${(-15..15).random()}",
        "S2IR_D2: ${(-15..15).random()}",
        "S1red_D2: ${(-15..15).random()}",
        "S1IR_D2: ${(-15..15).random()}",
        "S2red_D3: ${(-15..15).random()}",
        "S2IR_D3: ${(-15..15).random()}",
        "S1red_D3: ${(-15..15).random()}",
        "S1IR_D3: ${(-15..15).random()}"
    )
}


@Composable
fun ChartViewScreen(
    leftData: List<Nirsdata> = fakeDataGen(180),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        DataView(
            title = stringResource(id = R.string.left_foot),
            data = leftData
        )
    }
}

//@Preview
@Composable
private fun DataView(
    title: String = "",
    data: List<Nirsdata>
) {
    Log.d(TAG, "chart data size : " + data.size)


    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,

        )
        Spacer(modifier = Modifier.size(8.dp))
        DataChart(data)
        Spacer(modifier = Modifier.size(8.dp))
        /*
        DataInfo(
            data = if (data.isEmpty()) DEFAULT_Nirsdata
            else data.last()
        )

         */
    }
}

@Composable
fun DataChart(data: List<Nirsdata>) {
    // 使用 remember 保存每条折线图的状态
    val entries = getEntries(data)

    // 只在数据改变时更新图表范围
    val axisMinValue = remember(data) { data.minOfOrNull { it.s2RedD1 } ?: 0.0 }
    val axisMaxValue = remember(data) { data.maxOfOrNull { it.s2RedD1 } ?: 0.0 }

    val axisRange = remember(axisMinValue, axisMaxValue) { axisMinValue to axisMaxValue }

    Column {
        for (i in entries.indices) {
            // 使用 remember 避免每次更新时重绘图表
            Chart(
                chart = lineChart(
                    lines = listOf(colorLineSpec(color = Blue))
                ),
                model = entries[i],
                horizontalLayout = HorizontalLayout.fullWidth(),
                startAxis = rememberStartAxis(
                    guideline = null,
                    valueFormatter = { value, _ ->
                        String.format("% 4f", value.toDouble())
                    }
                ),
                bottomAxis = rememberBottomAxis(
                    guideline = null,
                    tick = null,
                    itemPlacer = AxisItemPlacer.Horizontal.default(
                        spacing = if (data.size < 5) 1 else data.size / 5,
                    ),
                    valueFormatter = { value, _ ->
                        // 将值格式化为秒数
                        String.format("%.02f秒", value)
                    },
                    label = axisLabelComponent(
                        horizontalPadding = 0.dp
                    )
                ),
                chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false)
            )
        }
    }
}




private fun getSingleEntry(data: List<Nirsdata>): ChartEntryModel {
    // 只選擇 s2RedD1 這條數據線
    return data.map { it.timestamp to it.s2RedD1 }.toTypedArray().let { entryModelOf(*it) }
}




@Composable
private fun colorLineSpec(color: Color) = LineChart.LineSpec(lineColor = color.toArgb())

private fun getEntries(data: List<Nirsdata>): List<ChartEntryModel> {
    return listOf(
        data.map { it.timestamp to it.s2RedD1 }.toTypedArray().let { entryModelOf(*it) },
        data.map { it.timestamp to it.s2IrD1 }.toTypedArray().let { entryModelOf(*it) },
        data.map { it.timestamp to it.s1RedD1 }.toTypedArray().let { entryModelOf(*it) },
        data.map { it.timestamp to it.s1IrD1 }.toTypedArray().let { entryModelOf(*it) },
        data.map { it.timestamp to it.s2RedD2 }.toTypedArray().let { entryModelOf(*it) },
        data.map { it.timestamp to it.s2IrD2 }.toTypedArray().let { entryModelOf(*it) },
        data.map { it.timestamp to it.s1RedD2 }.toTypedArray().let { entryModelOf(*it) },
        data.map { it.timestamp to it.s1IrD2 }.toTypedArray().let { entryModelOf(*it) },
        data.map { it.timestamp to it.s2RedD3 }.toTypedArray().let { entryModelOf(*it) },
        data.map { it.timestamp to it.s2IrD3 }.toTypedArray().let { entryModelOf(*it) },
        data.map { it.timestamp to it.s1RedD3 }.toTypedArray().let { entryModelOf(*it) },
        data.map { it.timestamp to it.s1IrD3 }.toTypedArray().let { entryModelOf(*it) }
    )
}



private fun fakeDataGen(count: Int): List<Nirsdata> {

    fun denormalize(num: Float): Double = ((num + 1) / 2 * 4095).toInt().toDouble()

    val result = mutableListOf<Nirsdata>()
    for (i in 0 until count) {
        result.add(
            Nirsdata(
                0.0,
                denormalize(sin(1 * i.toFloat() * 0.01745f)),
                denormalize(sin(2 * i.toFloat() * 0.01745f)),
                denormalize(sin(3 * i.toFloat() * 0.01745f)),
                denormalize(sin(4 * i.toFloat() * 0.01745f)),
                denormalize(sin(5 * i.toFloat() * 0.01745f)),
                denormalize(sin(6 * i.toFloat() * 0.01745f)),
                denormalize(sin(1 * i.toFloat() * 0.01745f)),
                denormalize(sin(2 * i.toFloat() * 0.01745f)),
                denormalize(sin(3 * i.toFloat() * 0.01745f)),
                denormalize(sin(4 * i.toFloat() * 0.01745f)),
                denormalize(sin(5 * i.toFloat() * 0.01745f)),
                denormalize(sin(6 * i.toFloat() * 0.01745f))
            )
        )
    }
    return result.toList()
}

/*
s2RedD1, s2IrD1, s1RedD1,
s1IrD1, s2RedD2, s2IrD2,
s1RedD2, s1IrD2, s2RedD3,
s2IrD3, s1RedD3, s1IrD3

 */
@Composable
private fun DataInfo(data: Nirsdata) {
    val state = remember { mutableIntStateOf(0) }

    Row(
        modifier = Modifier
            .wrapContentWidth()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HA: ",

            )
            Text(
                text = "LT: ",
            )
            Text(
                text = "M1: ",
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "M5: ",

            )
            Text(
                text = "ARCH: ",

            )
            Text(
                text = "MH: ",

            )
        }
    }
}

