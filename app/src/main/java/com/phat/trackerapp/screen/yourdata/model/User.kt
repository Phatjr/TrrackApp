package com.phat.trackerapp.screen.yourdata.model

data class User(
    var gender: Int = 0,
    var age: Int,
    var height: Float,
    var weight: Float,
    var unitHeight: Int = 0,
    var unitWeight: Int = 0
) {
//    female = 0 male = 1, others = 2
}