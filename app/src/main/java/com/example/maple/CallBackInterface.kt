package com.example.maple

interface ModelCallBack {
    fun onCallBack(output: MutableMap<Int, Any>)
}

interface ReadDictCallBack {
    fun onCallBack(dict: HashMap<String, Int>)
}

interface PreprocessingCallBack {
    fun onCallBack(input: IntArray)
}

interface TimeCountCallback {
    fun onCallBack(millis: Long)
}