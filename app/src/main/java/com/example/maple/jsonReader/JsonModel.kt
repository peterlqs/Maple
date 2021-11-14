package com.example.maple.jsonReader

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset


class JsonModel {
    fun getintentDict(context: Context): String? {
        var json: String? = null
        val charset: Charset = Charsets.UTF_8
        try {
            val thisJsonFile = context.assets.open("intentDict.json")
            val size = thisJsonFile.available()
            val buffer = ByteArray(size)
            thisJsonFile.read(buffer)
            thisJsonFile.close()
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun getposmesDict(context: Context): String? {
        var json: String? = null
        val charset: Charset = Charsets.UTF_8
        try {
            val thisJsonFile = context.assets.open("posmesDict.json")
            val size = thisJsonFile.available()
            val buffer = ByteArray(size)
            thisJsonFile.read(buffer)
            thisJsonFile.close()
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun getpostagDict(context: Context): String? {
        var json: String? = null
        val charset: Charset = Charsets.UTF_8
        try {
            val thisJsonFile = context.assets.open("postagDict.json")
            val size = thisJsonFile.available()
            val buffer = ByteArray(size)
            thisJsonFile.read(buffer)
            thisJsonFile.close()
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun knowledgeJSON(context: Context): String? {
        var json: String? = null
        val charset: Charset = Charsets.UTF_8
        try {
            val thisJsonFile = context.assets.open("knowledge.json")
            val size = thisJsonFile.available()
            val buffer = ByteArray(size)
            thisJsonFile.read(buffer)
            thisJsonFile.close()
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun bi_gram_list(context: Context): String? {
        var json: String? = null
        val charset: Charset = Charsets.UTF_8
        try {
            val thisJsonFile = context.assets.open("bi_gram_list.json")
            val size = thisJsonFile.available()
            val buffer = ByteArray(size)
            thisJsonFile.read(buffer)
            thisJsonFile.close()
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun intent_label(context: Context): String? {
        var json: String? = null
        val charset: Charset = Charsets.UTF_8
        try {
            val thisJsonFile = context.assets.open("intent_label.json")
            val size = thisJsonFile.available()
            val buffer = ByteArray(size)
            thisJsonFile.read(buffer)
            thisJsonFile.close()
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun intent_responses(context: Context): String? {
        var json: String? = null
        val charset: Charset = Charsets.UTF_8
        try {
            val thisJsonFile = context.assets.open("intents.json")
            val size = thisJsonFile.available()
            val buffer = ByteArray(size)
            thisJsonFile.read(buffer)
            thisJsonFile.close()
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun intentDictGetter(context: Context): HashMap<String, Int> {
        val intentDict = hashMapOf<String, Int>()
        val jsonstring = getintentDict(context)
        val jsonobject = JSONObject(jsonstring)
        val keys = jsonobject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val values = jsonobject.getInt(key)
            intentDict[key] = values
        }
        return intentDict
    }

    fun posmesDictGetter(context: Context): HashMap<String, Int> {
        val pmDict = hashMapOf<String, Int>()
        val jsonstring = getposmesDict(context)
        val jsonobject = JSONObject(jsonstring)
        val keys = jsonobject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val values = jsonobject.getInt(key)
            pmDict[key] = values
        }
        return pmDict
    }

    fun postagDictGetter(context: Context): HashMap<String, Int> {
        val ptDict = hashMapOf<String, Int>()
        val jsonstring = getpostagDict(context)
        val jsonobject = JSONObject(jsonstring)
        val keys = jsonobject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val values = jsonobject.getInt(key)
            ptDict[key] = values
        }
        return ptDict
    }

    fun indexToPosTAG(context: Context): HashMap<Int, String> {
        val ptDict = hashMapOf<Int, String>()
        val jsonstring = getpostagDict(context)
        val jsonobject = JSONObject(jsonstring)
        val keys = jsonobject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val values = jsonobject.getInt(key)
            ptDict[values] = key
        }
        return ptDict
    }

    fun bgListGetter(context: Context): JSONArray {
        val jsonstring = bi_gram_list(context)
        val jsonobject = JSONObject(jsonstring)
        val bgList = jsonobject.getJSONArray("bi_gram_list")
        return bgList
    }

    fun intentLabelGetter(context: Context): JSONArray {
        val jsonstring = intent_label(context)
        val jsonobject = JSONObject(jsonstring)
        val label = jsonobject.getJSONArray("label")
        return label
    }

    fun repsonsesGetter(context: Context): HashMap<String, JSONArray> {
        val repDict = hashMapOf<String, JSONArray>()
        val jsonstring = intent_responses(context)
        val jsonobject = JSONObject(jsonstring)
        val intent = jsonobject.getJSONArray("intents")

        for (i in 0..(intent.length() - 1)) {
            val tag = intent.getJSONObject(i).getString("tag")
            val responses = intent.getJSONObject(i).getJSONArray("responses")
            repDict[tag] = responses
        }
        return repDict
    }

}