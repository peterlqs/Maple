package com.example.maple.BotBackend.Bot

import android.content.Context
import android.util.Log
import com.example.maple.ModelCallBack
import com.example.maple.jsonReader.JsonModel
import com.example.maple.tools.NumpyAlike
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.json.JSONArray
import org.tensorflow.lite.Interpreter
import java.util.*

class BotBackend {
    private val myJSON = JsonModel()
    private val np = NumpyAlike()

    fun initializeModel2(inp: IntArray, myCallBack: ModelCallBack) {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel(
                "FoxBotTest", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                conditions
            )
            .addOnSuccessListener { model: CustomModel? ->
                // Download complete. Depending on your app, you could enable the ML
                // feature, or switch from the local model to the remote model, etc.

                // The CustomModel object contains the local path of the model file,
                // which you can use to instantiate a TensorFlow Lite interpreter.
                val modelFile = model?.file
                if (modelFile != null) {
                    Log.e("Model", "Get Completed")
                    var interpreter = Interpreter(modelFile)

                    val inputs: Array<FloatArray> = arrayOf(inp.map { it.toFloat() }.toFloatArray())
                    val outputs: Array<FloatArray> =
                        arrayOf(floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f))
                    interpreter.run(inputs, outputs)
                    val realOutput = mutableListOf<Int>()
                    for (i in 0..(outputs[0].size - 1)) {
                        realOutput.add(Math.round(outputs[0][i]))
                    }


                }
            }
    }

    fun initializeModel(
        intentInput: IntArray,
        posInput: IntArray,
        context: Context,
        myCallBack: ModelCallBack
    ) {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel(
                "FennecBotV1_2", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                conditions
            )
            .addOnSuccessListener { model: CustomModel? ->

                val modelFile = model?.file
                if (modelFile != null) {
                    Log.e("Model", "Get Completed")
                    var interpreter = Interpreter(modelFile)
                    val intentArray: Array<IntArray> = arrayOf(intentInput)
                    val posArray: Array<IntArray> = arrayOf(posInput)
                    val inputs: Array<Array<IntArray>> = arrayOf(intentArray, posArray)

                    val a = Array(71) { FloatArray(14) }
                    val posOutputShape: Array<Array<FloatArray>> = arrayOf(a)
                    val intentOutputShape: Array<FloatArray> = arrayOf(FloatArray(13))

                    val outputs = mutableMapOf<Int, Any>()
                    outputs[0] = posOutputShape
                    outputs[1] = intentOutputShape
                    interpreter.runForMultipleInputsOutputs(inputs, outputs)

                    val intentOutput: Array<FloatArray> = outputs[1] as Array<FloatArray>
                    val posOutput: Array<Array<FloatArray>> = outputs[0] as Array<Array<FloatArray>>
                    val posOutputSeq = mutableListOf<String>()
                    val pred = myJSON.intentLabelGetter(context)[accept_accuracy(
                        intentOutput[0].toMutableList(),
                        0.8f
                    )].toString()
                    for ((index, i) in posOutput[0].toMutableList().withIndex()) {
                        posOutputSeq.add(myJSON.indexToPosTAG(context)[np.argmax(i.toMutableList())].toString())
                    }

                    myCallBack.onCallBack(outputs)
                }
            }
    }

    fun posExtractor(
        inp: String,
        posOutput: MutableList<String>,
        context: Context
    ): MutableList<MutableList<String>> {
        val preprocessPOS = mutableListOf<String>()
        val output = mutableListOf<MutableList<String>>()
        val inp_bg = bi_gram_ize(inp, context)
        for (i in posOutput) {
            if (i != "<PAD>") {
                preprocessPOS.add(i)
            }
        }
        for (i in 0..(preprocessPOS.size - 1)) {
            if (preprocessPOS[i] != "<PAD>") {
                output.add(mutableListOf(inp_bg[i], preprocessPOS[i]))
            }
        }
        return output
    }

    fun bi_gram_checker(inp: String, context: Context): Boolean {
        val bgList = myJSON.bgListGetter(context)
        for (i in 0..(bgList.length() - 1)) {
            if (inp == bgList[i]) {
                return true
            }
        }
        return false
    }

    fun accept_accuracy(inp: MutableList<Float>, acc: Float): Int {
        if (inp.maxOrNull()!!.toDouble() > acc.toDouble()) {
            return np.argmax(inp)
        } else {
            return inp.size
        }
    }

    fun posFilter(inp: MutableList<MutableList<String>>, pos_type: String): MutableList<String> {
        val output = mutableListOf<String>()
        for (i in inp) {
            if (i[1] == pos_type) {
                output.add(i[0])
            }
        }
        return output
    }

    fun botAction(intent: String, context: Context): String {
        when (intent) {
            "asking me" -> return "!ask"
            "question_from_user" -> return "!entities"
            else -> {
                val responsesDict: HashMap<String, JSONArray> = myJSON.repsonsesGetter(context)
                val responsesList = responsesDict[intent]!!
                val rep = responsesList[(0..(responsesList.length() - 1)).random()]
                return rep as String
            }
        }
    }

    fun preprocess(
        inp: String,
        dataDict: HashMap<String, Int>,
        maxlen: Int,
        context: Context
    ): IntArray {
        val bgInp = bi_gram_ize(inp, context)
        val inpTokenized = fit_on_text(bgInp, dataDict)
        val paddedInp = pad_sequences(inpTokenized, maxlen)
        return paddedInp.toIntArray()
    }

    fun bi_gram_ize(inp: String, context: Context): MutableList<String> {
        val list_tokens = inp.lowercase(Locale.getDefault()).split(" ").toMutableList()
        list_tokens.add("")
        var myContinue = false
        val tokens = mutableListOf<String>()
        for (i in 0..(list_tokens.size - 2)) {
            if (myContinue) {
                myContinue = false
                continue
            }
            if (list_tokens[i + 1] != "") {
                var pairtext = list_tokens[i] + "_" + list_tokens[i + 1]
                if (bi_gram_checker(pairtext, context)) {
                    myContinue = true
                    tokens.add(pairtext)
                }
            }
            if (myContinue == false) {
                tokens.add(list_tokens[i])
            }
        }
        return tokens
    }


    fun fit_on_text(
        list_inp: MutableList<String>,
        dataDict: HashMap<String, Int>
    ): MutableList<Int> {
        val output = mutableListOf<Int>()
        for (i in list_inp) {
            if (dataDict[i] != null) {
                dataDict[i]?.let { output.add(it) }
            }
        }
        return output
    }

    fun pad_sequences(list_idx: MutableList<Int>, maxlen: Int): MutableList<Int> {
        if (list_idx.size < maxlen) {
            while (list_idx.size != maxlen) {
                list_idx.add(0, 0)
            }
        } else {
            while (list_idx.size != maxlen) {
                list_idx.removeAt(0)
            }
        }
        return list_idx
    }
}