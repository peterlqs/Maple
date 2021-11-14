package com.example.maple.tools

class NumpyAlike {
    fun argmax(inp: MutableList<Float>): Int {
        var max: Float = -1.0f
        var finalIndex = -1
        for ((index, i) in inp.withIndex()) {
            if (max <= i) {
                max = i
                finalIndex = index
            }
        }
        return finalIndex
    }
}