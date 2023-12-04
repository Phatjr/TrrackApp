package com.phat.trackerapp.screen.home.model

import com.horen.chart.barchart.IBarData

class TrackerBarData(var nameTracker: String, var valueTracker: Float):IBarData {

    override fun getValue(): Float {
        return valueTracker
    }

    override fun getLabelName(): String {
      return nameTracker
    }

}