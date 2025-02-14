package com.example.brelax.ui.theme

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import uk.me.berndporr.iirj.Butterworth
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow

private const val TAG = "viewmodel"



//三維資料結構 拿來放轉換後的資料
//raw data 會給 若干筆數據 * 184 bytes
//轉換成 一樣的若干筆數據
data class ProcessedData(
    val records: List<Record>
)

//每筆數據12channel
data class Record(
    val channels: List<Channel>
)

//每個通道裡面有15筆 數據 因為 184bytes 變成 4bytes時間 + 180bytes 的data
//180bytes的data 拆成 12個一組 共15組 進行轉換
//每一組是 分別1-12channel的資料 有15組 所以進行轉置
//變成 有12組 分別是12個通道的數據 每個通道有15個數據
data class Channel(
    val values: List<Double>
)


fun <T> List<List<T>>.transpose(): List<List<T>> {
    if (this.isEmpty() || this[0].isEmpty()) return emptyList()
    val rowCount = this.size
    val colCount = this[0].size
    return List(colCount) { colIndex ->
        List(rowCount) { rowIndex ->
            this[rowIndex][colIndex]
        }
    }
}



@OptIn(ExperimentalUnsignedTypes::class)
fun convertToUnsigned(rawData: List<ByteArray>): MutableList<UByteArray> {
    val unsignedData = mutableListOf<UByteArray>()

    for (byteArray in rawData.toList() ) {
        // 對每個ByteArray的每個Byte進行無符號轉換
        val uByteArray: UByteArray = byteArray.map { it.toUByte() }.toUByteArray()
        unsignedData.add(uByteArray)
    }
    return unsignedData
}



fun convertData(byteArrayList: List<ByteArray>): ProcessedData {
    val records = mutableListOf<Record>()

    //Log.d("data bytes test","byte5 1 : "+ byteArrayList[0][5])

    for (n in byteArrayList.indices) {
        val byteArray = byteArrayList[n]

        //Log.d("data bytes test","byte5 $n : "+ byteArray[5])
        // 184 = 4 + 180
        val timePart = byteArray.take(4)
        val dataPart = byteArray.drop(4)

        // 將數據分為 3 個一組 180/3 = 60

        //將每組數據前面補0 變成4個byte
        val reshapedData = dataPart.toList().chunked(3) { group ->
            // 確保每個字節組長度是 4 字節，並在前面補零
            val paddedGroup = byteArrayOf(0) + group.toByteArray()
            val int32Value = ByteBuffer.wrap(paddedGroup)
                .order(ByteOrder.BIG_ENDIAN)
                .int
            int32Value
        }

        //val reshapedData = dataPart.toList().chunked(12)

        // 把數據轉置val transposedData = reshapedData.transpose()
        val transposedData = reshapedData.chunked(12).transpose()
        Log.d("Test", "Transposed shape: ${transposedData.size} x ${transposedData[0].size}")

        // 把數據轉成 mV
        val channels = mutableListOf<Channel>()
        for (i in transposedData.indices) {
            val values = transposedData[i].map {
                it / 2.0.pow(22) * 1.2
            }
            channels.add(Channel(values))
        }

        // 添加到記錄列表中
        records.add(Record(channels))
    }


    // 初始化濾波器
    val butterworth = Butterworth()
    val order = 4  // 濾波器階數
    val samplingFrequency = 250.0  // 取樣頻率
    val centerFrequency = 15.005  // 中心頻率
    val bandwidth = 29.99  // 帶寬
    butterworth.bandPass(order, samplingFrequency, centerFrequency, bandwidth)

    //Log.d("filiter test : ", "no filiter data : " + records[0].channels[0].values )


    // 對所有數據進行濾波
    val filteredRecords = records.map { record ->
        Record(
            record.channels.map { channel ->
                Channel(
                    channel.values.map { value ->
                        //butterworth.filter(value)
                        value
                    }
                )
            }
        )
    }

    Log.d("filiter test : ", "filiter data : " + filteredRecords[0].channels[0].values )
    //Log.d("filiter data record : ", " record : " + filteredRecords.size.toString())
    //Log.d("filiter data channel : ", " channel : " + filteredRecords[0].channels.size.toString())
    //Log.d("filiter data values: ", " values : " + filteredRecords[0].channels[0].values.size.toString())
    return ProcessedData(filteredRecords)
}

class BluetoothViewModel : ViewModel() {


    // 用來儲存測量數據
    @OptIn(ExperimentalUnsignedTypes::class)
    private val _measurementValues = MutableLiveData<List<UByteArray>>()

    @OptIn(ExperimentalUnsignedTypes::class)
    val measurementValues: LiveData<List<UByteArray>> get() = _measurementValues

    private val _processdata = MutableLiveData<ProcessedData>()
    val processdata: LiveData<ProcessedData> get() = _processdata

    private val _allProcessedData = mutableListOf<ProcessedData>()
    @OptIn(ExperimentalUnsignedTypes::class)
    private val _allrawdData = mutableListOf<UByteArray>()

    @OptIn(ExperimentalUnsignedTypes::class)
    fun reset(){
        _allrawdData.clear()
        _allProcessedData.clear()
    }

    var rawtest = false

    // 接收到測量數據
    @OptIn(ExperimentalUnsignedTypes::class)
    fun updateMeasurementValues(rawdata: List<ByteArray>) {

        val rawdataCopy = convertToUnsigned( rawdata ).toList()

        _measurementValues.value = rawdataCopy.toList()


        if ( !rawtest ) {
            for ( i in rawdataCopy ) {
                Log.d("raw data test" , "raw data : ")
                for ( j in i ) {
                    Log.d("raw data test" , j.toString())
                }


            }
            rawtest = true
        }



        for ( i in rawdataCopy ) {
            _allrawdData.add(i)
            Log.d("raw data" , "raw data size : " + i.size )
        }


        //把data 丟去數據處理
        val afterconvertData = convertData(rawdata)
        _processdata.value = afterconvertData

        //處理好的數據 放到_processdata 讓UI頁面可以觀察到

        _allProcessedData.add(afterconvertData)
        //Log.d("all size", "all size" + _allProcessedData.size )
        //Log.d("all size", "all size current size" + _allProcessedData[_allProcessedData.size-1].records.size )
    }

    fun saveProcessedDataToCSV(context: Context) {
        // 檔案路徑設置
        val file = File(context.filesDir, "test_data.csv")
        val path = context.filesDir.absolutePath
        Log.d("CSV Path", "File saved at: $path/processed_data.csv")
        // 使用 BufferedWriter 來高效寫入 CSV 檔案
        BufferedWriter(FileWriter(file)).use { writer ->
            // 寫入表頭：c1, c2, ..., c12
            writer.write("S2red_D1,S2IR_D1,S1red_D1,S1IR_D1,S2red_D2,S2IR_D2,S1red_D2,S1IR_D2,S2red_D3,S2IR_D3,S1red_D3,S1IR_D3\n")

            // 遍歷所有的 ProcessedData
            //Log.d("data csv", "all data: " + _allProcessedData.size.toString()) //總共 接收幾次
            //Log.d("data csv", "all data D record: " +_allProcessedData[0].records.size.toString()) //每次有幾筆
            //Log.d("data csv", "all data record channel: " +_allProcessedData[0].records[0].channels.size.toString()) //每筆幾個通道
            //Log.d("data csv", "all data record channel data " +_allProcessedData[0].records[0].channels[0].values.size.toString())//每個通道裡有幾個數據
            for (processedData in _allProcessedData) {
                // 假設每個 channel 都有相同數量的數據
                val numDataPoints = processedData.records.size
                //Log.d("data csv", "data record size : " + processedData.records.size.toString()) //每次有幾筆
                // 寫入每一組數據
                for (record in processedData.records) {
                    // 準備一個暫存的列表，用來存放所有通道的數據
                    val channelData = record.channels.map { it.values }

                    // 轉置資料：將 12 行 15 列變成 15 行 12 列
                    val transposedData = (0 until channelData.first().size).map { rowIndex ->
                        channelData.map { channel -> channel[rowIndex] }
                    }

                    //Log.d("row size", "row size : " + )
                    // 將轉置後的數據寫入 CSV
                    for (row in transposedData) {
                        writer.write(row.joinToString(",") + "\n")
                    }
                }

            }
        }
        _allProcessedData.clear()
        //Log.d("clear test ", "celar all ? : " + _allProcessedData.isEmpty() )
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun saveRawDataToCSV(context: Context) {
        // 檔案路徑設置
        val file = File(context.filesDir, "test_raw_data.csv")
        val path = context.filesDir.absolutePath

        // 使用 BufferedWriter 來高效寫入 CSV 檔案
        BufferedWriter(FileWriter(file)).use { writer ->

            for (data in _allrawdData) {
                writer.write(data.joinToString(",") + "\n") // 將 ByteArray 轉為逗號分隔的字串
            }


        }
        _allrawdData.clear()
        //Log.d("clear test ", "celar all ? : " + _allProcessedData.isEmpty() )
    }


    @OptIn(ExperimentalUnsignedTypes::class)
    fun getsize(): Int {
        return measurementValues.value?.size ?: -1
    }
}
