package com.example.brelax.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.brelax.R


@Composable
fun BreathingModeSelector(
    selectedMode: String, // 新增這個參數
    onModeSelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            BPattern(
                text = "快速放鬆",
                onClick = { onModeSelected("快速放鬆") },
                isSelected = selectedMode == "快速放鬆" // 使用傳入的 selectedMode
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
        item {
            BPattern(
                text = "深度放鬆",
                onClick = { onModeSelected("深度放鬆") },
                isSelected = selectedMode == "深度放鬆" // 使用傳入的 selectedMode
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
        item {
            BPattern(
                text = "專注放鬆",
                onClick = { onModeSelected("專注放鬆") },
                isSelected = selectedMode == "專注放鬆" // 使用傳入的 selectedMode
            )
        }
    }
}



@Composable
fun Breathmethod1(
    selectedMode: String,
    selectedMethod: String, // 新增這個參數
    onMethodSelected: (String) -> Unit
) {
    if (selectedMode == "深度放鬆") {
        LazyRow(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                BPattern(
                    text = "4-7-8",
                    onClick = { onMethodSelected("4-7-8") },
                    isSelected = selectedMethod == "4-7-8" // 判斷是否被選中
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            item {
                BPattern(
                    text = "4-2-6",
                    onClick = { onMethodSelected("4-2-6") },
                    isSelected = selectedMethod == "4-2-6" // 判斷是否被選中
                )
            }
        }
    }
if (selectedMode == "快速放鬆"){
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        BPattern(text = "3-3-3",
            onClick = { onMethodSelected("3-3-3") },
            isSelected = selectedMethod == "3-3-3" // 判斷是否被選中
        )
    }
}
    if (selectedMode == "專注放鬆"){
        Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        BPattern(text = "Box",
            onClick = { onMethodSelected("Box") },
            isSelected = selectedMethod == "Box" // 判斷是否被選中
        )
    }
    }
}





@Preview
@Composable
fun Breathmethod2() {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        BPattern(text = "3-3-3")
    }
}

@Preview
@Composable
fun Breathmethod3() {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        BPattern(text = "Box")
    }
}

@Preview
@Composable
fun BPattern(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    text: String = "xxxx",
    onClick: () -> Unit = {},
    isSelected: Boolean = false // 新增參數來表示是否被選中
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .width(100.dp)
            .aspectRatio(3f)
            .clickable(onClick = onClick) // 使整個 Row 可點擊
            .background(if (isSelected) Color(0xFFFFFFFF) else Color(0x00FFFFFF)) // 根據 isSelected 來改變背景顏色
    ) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = if (isSelected) Color(0xFF4B4B4B) else Color(0xFF989898), // 字體顏色保持黑色

            )
        }

    }
}


@Composable
fun BreathingMethodInfo(
    title: String ="",
    shortD: String="",
    detailed: String="",
    inhale:String="",
    hold:String="",
    exhale:String=""
) {
    // 呼吸法的介紹區域，用 Column 來垂直排列文字
    Column(
        modifier = Modifier
            .background(Color(0xC4FFFFFF), shape = MaterialTheme.shapes.medium)  // 背景設為白色，使用圓角矩形
            .padding(15.dp)  // 內部再加 16dp 的邊距

    ) {
        // 顯示 "4-7-8 呼吸法" 的標題
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))  // 提供 8dp 的垂直間距
        // 簡單的說明
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .width(400.dp)
                .background(Color(0xFFCDEDFF))
                .aspectRatio(14.5f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = shortD,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold
                )
        }
        Spacer(modifier = Modifier.height(10.dp))
        // 更詳細的描述文字
        Text(
            text =detailed,
            lineHeight = 20.sp,
            fontSize = 12.sp  // 設置字體大小
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween ) {
            Breath(text = inhale,painter = painterResource(id = R.drawable.breath_s))
            Breath(text = hold,painter = painterResource(id = R.drawable.hold_s))
            Breath(text = exhale,painter = painterResource(id = R.drawable.breath_s))
        }
    }
}

@Composable
fun BreathingMethodInfo2(
    title: String,
    shortD: String,
    detailed: String,
    inhale:String,
    hold:String,
    exhale:String
) {
    // 呼吸法的介紹區域，用 Column 來垂直排列文字
    Column(
        modifier = Modifier
            .background(Color(0xC4FFFFFF), shape = MaterialTheme.shapes.medium)  // 背景設為白色，使用圓角矩形
            .padding(15.dp)  // 內部再加 16dp 的邊距

    ) {
        // 顯示 "4-7-8 呼吸法" 的標題
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))  // 提供 8dp 的垂直間距
        // 簡單的說明
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .width(400.dp)
                .background(Color(0xFFCDEDFF))
                .aspectRatio(14.5f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = shortD,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        // 更詳細的描述文字
        Text(
            text =detailed,
            lineHeight = 20.sp,
            fontSize = 12.sp  // 設置字體大小
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween ) {
            Breath2(text = inhale,painter = painterResource(id = R.drawable.breath_s))
            Breath2(text = hold,painter = painterResource(id = R.drawable.hold_s))
            Breath2(text = exhale,painter = painterResource(id = R.drawable.breath_s))
            Breath2(text = hold,painter = painterResource(id = R.drawable.hold_s))
        }
    }
}

@Preview
@Composable
fun BreathingDurationSelector() {
    // 呼吸時長選擇器，使用 Row 來橫向顯示 1 到 7 的選擇
    LazyRow(
        modifier = Modifier.fillMaxWidth(),  // 填滿可用的寬度
        horizontalArrangement = Arrangement.Center  // 子元素水平居中
    ) {
        items(10) { index ->  // 生成 1 到 10 的數字
            val number = index + 1  // 將索引轉換為從 1 開始的數字
            // 顯示每個數字，並給每個數字加上水平邊距
            Text(
                text = "$number",
                modifier = Modifier.padding(horizontal = 20.dp),  // 設置水平邊距
                fontSize = 25.sp
            )
        }
    }
}


@Composable
fun StartButton(modifier: Modifier = Modifier, color: Color = Color.White,text: String="xxxx", onClick: () -> Unit) {
    // 顯示一個按鈕來開始呼吸練習
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(Color(0xFFFFFFFFF))
            .width(200.dp)
            .aspectRatio(4.5f)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center, // 水平置中
        verticalAlignment = Alignment.CenterVertically // 垂直置中
    ) {
        Text(
            text = text,
            modifier = modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = colorResource(id = R.color.tblack)

        )

    }
}
@Preview
@Composable
fun StartButtonPreview() {
    Button1(
        color = Color.Blue, // 指定按鈕顏色
        onClick = { /* 預覽不需要點擊邏輯 */ }
    )
}


@Composable
fun Button1(modifier: Modifier = Modifier, color: Color = Color.White,text: String="xxxx" ,onClick: () -> Unit) {
    // 顯示一個按鈕來開始呼吸練習
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(Color(0xFFFFFFFF))
            .width(200.dp)
            .aspectRatio(4.5f)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = colorResource(id = R.color.tblack)
        )
    }
}






@Preview
@Composable
fun Breath(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    painter: Painter = painterResource(id = R.drawable.ic_missing_image),
    text: String="xxxxxx",
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFCDEDFF))
            .width(90.dp)
            .aspectRatio(3f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(8.dp)
                .aspectRatio(1f),
            painter = painter,
            contentDescription = null
        )
        Text(text = text,fontSize = 12.sp,)
    }}

@Preview
@Composable
fun Breath2(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    painter: Painter = painterResource(id = R.drawable.ic_missing_image),
    text: String="xxxxxx",
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFCDEDFF))
            .width(80.dp)
            .aspectRatio(3f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(8.dp)
                .aspectRatio(1f),
            painter = painter,
            contentDescription = null
        )
        Text(text = text,fontSize = 12.sp)
    }}



@Preview
@Composable
fun nirsdatachoose() {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        nButton(onClick = {}, text = "D1")
        nButton(onClick = {}, text = "D2")
        nButton(onClick = {}, text = "D3")
    }
}

@Preview
@Composable
fun nButton(modifier: Modifier = Modifier, color: Color = Color.White,text: String="xxxx",onClick: () -> Unit = {},) {
    // 顯示一個按鈕來開始呼吸練習
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .width(110.dp)
            .aspectRatio(3f)
            .background(brush = Brush.linearGradient(colors = listOf(
                Color(0xFFFFDAD0),
                Color(0xFFFFE9E2),
                Color(0xFFFFE9E2),
                Color(0xFFFFDAD0))
            )
            )
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = colorResource(id = R.color.tblack)
        )
    }
}

