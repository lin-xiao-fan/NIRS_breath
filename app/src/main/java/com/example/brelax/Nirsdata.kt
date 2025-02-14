package com.example.brelax

import androidx.room.Entity

@Entity
data class Nirsdata (
    var timestamp: Double,
    var s2RedD1: Double, var s2IrD1: Double, var s1RedD1: Double,
    var s1IrD1: Double, var s2RedD2: Double, var s2IrD2: Double,
    var s1RedD2: Double, var s1IrD2: Double, var s2RedD3: Double,
    var s2IrD3: Double, var s1RedD3: Double, var s1IrD3: Double
) {
    fun toDataList() = listOf(  s2RedD1, s2IrD1, s1RedD1,
                                s1IrD1, s2RedD2, s2IrD2,
                                s1RedD2, s1IrD2, s2RedD3,
                                s2IrD3, s1RedD3, s1IrD3)


}


val DEFAULT_Nirsdata =
    Nirsdata(0.0,0.0, 0.0, 0.0,
        0.0, 0.0, 0.0,
        0.0, 0.0, 0.0,
        0.0,0.0,0.0)