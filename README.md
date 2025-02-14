MainActivity.kt
主介面 包含 tES Breath NIRS USER 等四個功能的選擇 可進入對應的頁面(目前只有Breath以及NIRS)

BreathingUI.kt
呼吸法選擇的頁面 可選擇需要的呼吸法、並且附上相關介紹、以及可選擇的呼吸分鐘數 選擇完可開始呼吸

breathing.kt 雲朵太陽在這裡
開始呼吸後會到這個頁面 這邊主要是根據不同的呼吸選擇 會讓雲朵有不同的開合模式 以及太陽的表現度

breathing end.kt
呼吸結束之後會進入這個完成畫面 可以回首頁或回到呼吸選擇的地方


Nirsdata.kt
NIRSadc數據的資料結構


BluetoothViewModel.kt
從nirs bluetooth中接收數據 把數據轉換成adc數據，再讓折線圖的頁面根據數據變化做反應
**數據轉換**以及存檔功能都在這裡


nirs bluetooth.kt
在主頁中 點選NIRS 開始測量會進到這個頁面 可以搜尋藍芽裝置 開始測量之後會跳轉進nirs.kt頁面 
從藍芽裝置接收到raw data之後會傳給BluetoothViewModel做處理
返回這個頁面可以點擊結束測量 可以把目前收集的原始數據 和轉換後的數據存檔
**其中的DEVICE_ADDRESS 可能需要手動改成使用的NIRS地址**
**結束連線的部分會有問題 無法正常結束連線 結束測量點擊多次可能會有空白表格覆蓋原本存檔的問題**

nirs.kt
從BluetoothViewModel監控數據的變化 有新的數據就會更新到折線圖中
這個頁面原本是要放12個channel的動態折線圖 不過由於
轉換數據可能錯誤 以及 數據處理和UI設計邏輯 等等的問題
折線圖會有問題 包含圖形錯誤 以及APP卡頓等問題

關於藍芽以及數據處理的部分 原本應該和UI部分分離 但受於寫程式的實力問題 最後只把數據轉換分離出來 
藍芽等功能還和UI綁在一起 造成卡頓 無法結束連接 等相關問題 
預計會在之後的版本中修復



