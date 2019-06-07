package io.github.z3r0c00l_2k.aquadroid.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class ChartXValueFormatter(val dateArray: ArrayList<String>) : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return dateArray.getOrNull(value.toInt()) ?: ""
    }
}