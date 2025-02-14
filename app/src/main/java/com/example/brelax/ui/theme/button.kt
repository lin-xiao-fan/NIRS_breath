package com.example.brelax.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brelax.R

@Preview
@Composable
fun PreviousButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    var isPressed by remember {
        mutableStateOf(false)
    }
    val color by animateColorAsState(
        targetValue =
        if (isPressed) Color.DarkGray.copy(alpha = 0.25f)
        else Color.Transparent,
        animationSpec = tween(100, easing = LinearEasing),
        label = "backgroundColor"
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(50.dp)
            .height(50.dp)
            .clip(shape = CircleShape)
            .background(color)
            .pointerInput(onClick) {
                awaitEachGesture {
                    awaitFirstDown().run { isPressed = true }
                    waitForUpOrCancellation()?.run {
                        isPressed = false
                        onClick()
                    } ?: run { isPressed = false }
                }
            }
    ) {
        Icon(
            Icons.Default.KeyboardArrowLeft,
            contentDescription = null,
            tint = Color.DarkGray,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Preview
@Composable
fun bluetoothbutton(
    modifier: Modifier = Modifier,
    text: String = "xxx",
    color: Color = Color.White,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .width(280.dp)
            .aspectRatio(1.8f)
            .clip(RoundedCornerShape(15.dp))
            .clickable(onClick = onClick)
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text(text = "點擊選擇裝置",
            modifier = modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = colorResource(id = R.color.tblack)

        )
    }
}


@Preview
@Composable
fun LatchButton(
    modifier: Modifier = Modifier,
    text: String = "Button",
    isEnable: Boolean = true,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .width(150.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(
                if (isEnable) Secondary
                else Color.LightGray
            )
            .clickable {
                if (isEnable) onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
        )
    }
}


@Composable
fun ClickButton(
    modifier: Modifier = Modifier,
    text: String = "Button",
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .width(150.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(Secondary)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,

        )
    }
}

@Preview
@Composable
fun NegativeButton(
    modifier: Modifier = Modifier,
    text: String = "Button",
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .width(150.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(50.dp))
            .border(
                width = 3.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(50.dp)
            )
            .background(Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
        )
    }
}