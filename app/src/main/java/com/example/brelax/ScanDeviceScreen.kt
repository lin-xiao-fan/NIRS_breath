package com.example.brelax

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import com.example.brelax.ui.theme.ClickButton

private const val TITLE_ID = "title"
private const val LIST_ID = "list"
private const val BUTTON_ID = "button"

@Preview(name = "scanDevice", group = "bluetoothUI")
@Composable
fun ScanDeviceScreen(
    deviceList: List<DeviceInfo> = listOf(
        DeviceInfo("Device 01", "00:00:00:00", mapOf()),
        DeviceInfo("Device 02", "00:00:00:00", mapOf())
    ),
    isScanning: Boolean = false,
    onScan: (Boolean) -> Unit = {},
    onConnect: (DeviceInfo) -> Unit = {}
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize(),
        constraintSet = scanDeviceConstrain()
    ) {
        Text(
            modifier = Modifier.layoutId(TITLE_ID),
            text = stringResource(id = R.string.device_list)
        )
        LazyColumn(
            modifier = Modifier.layoutId(LIST_ID),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(deviceList) { device ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 3.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onConnect(device) }
                ) {
                    Text(
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                        text = device.name,
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                        text = device.address,
                    )
                }
            }
        }
        ClickButton(
            modifier = Modifier.layoutId(BUTTON_ID),
            text =
            if (isScanning) stringResource(id = R.string.scanning)
            else stringResource(id = R.string.start_scan),
            onClick = { onScan(!isScanning) }
        )
    }
}

private fun scanDeviceConstrain(): ConstraintSet {
    return ConstraintSet {
        val title = createRefFor(TITLE_ID)
        val list = createRefFor(LIST_ID)
        val button = createRefFor(BUTTON_ID)
        constrain(title) {
            top.linkTo(parent.top, 8.dp)
            start.linkTo(parent.start, 8.dp)
            width = Dimension.wrapContent
            height = Dimension.wrapContent
        }
        constrain(button) {
            bottom.linkTo(parent.bottom, 16.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.wrapContent
            height = Dimension.wrapContent
        }
        constrain(list) {
            top.linkTo(title.bottom)
            bottom.linkTo(button.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }
    }
}