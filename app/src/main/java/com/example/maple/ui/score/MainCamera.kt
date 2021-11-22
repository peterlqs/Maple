package com.example.maple.ui.score

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.maple.R
import com.example.maple.data.Score
import com.example.maple.data.SubjectData
import com.example.maple.databinding.MainCameraFragmentBinding
import com.example.maple.getCurrentDateTime
import com.example.maple.toString
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainCamera : Fragment() {
    //ViewModel
    private val viewModel: ScoreViewModel by activityViewModels()

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<CropImageContractOptions>

    //db
    private var db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val email = auth.currentUser?.uid.toString()

    //Binding
    private lateinit var _binding: MainCameraFragmentBinding
    private val binding get() = _binding

    //Initialize TAG for debug
    companion object {
        const val TAG = "MainCamera"
    }

    //Set binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainCameraFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Input picture

        cropActivityResultLauncher =
            registerForActivityResult(CropImageContract()) { it ->
                it?.let { result ->
                    if (result.isSuccessful) {
                        //The recognizer
                        val recognizer =
                            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                        //Turn Uri to Bitmap
                        val image = getBitmapFromUri(result.uriContent)
                        val imageHeight = image.height //get original image height
                        val imageWidth = image.width //get original image width
//                //subject not first score
//                val firstScore = Bitmap.createBitmap(image, 0, imageHeight/15,imageWidth/5, imageHeight-imageHeight/15)
//                val firstScore2 = Bitmap.createBitmap(image, (imageWidth-imageWidth/3).toInt(), imageHeight/15,imageWidth/13,  imageHeight-imageHeight/15)
//                val firstScore3 = Bitmap.createBitmap(image, (imageWidth-imageWidth/4.6).toInt(), imageHeight/15,imageWidth/13,  imageHeight-imageHeight/15)
                        val allList = mutableListOf<List<String>>()
                        //Analyze
                        val imageAnalyze11 = InputImage.fromBitmap(image, 0)
                        val result = recognizer.process(imageAnalyze11)
                            .addOnSuccessListener { visionText ->
                                val allText = visionText.text.split("\n")
                                //NEW ADDITION WAIT FOR IT TO COMPLETE
                                try {


                                    val complete = mutableListOf<Score>()
                                    val mul1All = mutableListOf<List<String>>()
                                    //-------------------------------
                                    Log.d("Vision text : ", allText.toString())
                                    val filteredList = mutableListOf<String>()
                                    for (text in allText) {
                                        var numeric = true
                                        numeric = text.matches("-?\\d+(\\.\\d+)?".toRegex())
                                        if (text.contentEquals("")) filteredList.add(text)
                                        //If text is numeric or in list
                                        if (numeric || text.trim().lowercase() in listOf(
                                                "toán",
                                                "đđggk",
                                                "đđgck",
                                                "đ"
                                            )
                                        ) {
                                            filteredList.add(text)
                                        }
                                    }
                                    val allThings = filteredList
                                    //-------------------------------
                                    val listSubject = listOf(
                                        "toán",
                                        "vật lí",
                                        "hóa học",
                                        "sinh học",
                                        "tin học",
                                        "ngữ văn",
                                        "lịch Sử",
                                        "địa Lí",
                                        "ngoại ngừ",
                                        "GDCD",
                                        "công nghệ",
                                        "thê dục",
                                        "GD OP-AN",
                                        "nghể"
                                    )
//                                println(allThings.toString())
                                    val mul1Pos = allThings.indexOf("Toán") + 1
                                    val mul2Pos = allThings.indexOf("ĐĐGgk") + 1
                                    val mul3Pos = allThings.indexOf("ĐĐGck") + 1
                                    //Column 1
                                    val list1 =
                                        allThings.subList(mul1Pos, mul1Pos + 14)
                                            .map { it.toString() }
//                        println(allThings.subList(mul1Pos,mul1Pos+14))
                                    //Column 2
                                    val list2 = allThings.subList(mul1Pos + 14, mul1Pos + 14 * 2)
                                        .map { it.toString() }
//                        println(allThings.subList(mul1Pos+14, mul1Pos+14*2))
                                    //Column 3
                                    val list3 =
                                        allThings.subList(mul1Pos + 14 * 2, mul1Pos + 14 * 2 + 9)
                                            .map { it.toString() }
//                        println(allThings.subList(mul1Pos+14*2,mul1Pos+14*2+9))
                                    //Column 4
                                    val list4 =
                                        allThings.subList(mul1Pos + 14 * 2 + 9, allThings.size)
                                            .map { it.toString() }
//                        println(allThings.subList(mul1Pos+14*2+9,allThings.size))
                                    //Column 5
                                    val list5 =
                                        allThings.subList(mul2Pos, mul2Pos + 14)
                                            .map { it.toString() }
//                        println(allThings.subList(mul2Pos,mul2Pos+14))
                                    //Column 6
                                    val list6 =
                                        allThings.subList(mul3Pos, mul3Pos + 14)
                                            .map { it.toString() }
//                        println(allThings.subList(mul3Pos,mul3Pos+14))

                                    var count = 0
                                    //MUL1 only
                                    var count1 = 0
                                    var count2 = 0
                                    var count3 = 0

                                    for (i in list1) {
                                        val newList = mutableListOf<String>()
                                        newList.add(list1[count1])
                                        newList.add(list2[count1])
                                        if (listSubject[count1] in listOf(
                                                "toán",
                                                "vật lí",
                                                "hóa học",
                                                "sinh học",
                                                "tin học",
                                                "ngữ văn",
                                                "ngoại ngữ",
                                                "thể dục",
                                                "nghề"
                                            ) && count2 < list3.size
                                        ) {
                                            newList.add(list3[count2])
                                            count2 += 1
                                        }
                                        if (listSubject[count1] in listOf(
                                                "toán",
                                                "hóa học",
                                                "ngữ văn",
                                                "ngoại ngữ"
                                            ) && count3 < list4.size
                                        ) {
                                            newList.add(list4[count3])
                                            count3 += 1
                                        }
                                        mul1All.add(newList)
                                        count1 += 1
                                    }

                                    //All the score
                                    for (i in list1) {
                                        complete.add(
                                            Score(
                                                listSubject[count],
                                                mul1All[count],
                                                list5[count],
                                                list6[count]
                                            )
                                        )
                                        count += 1
                                    }
                                    Log.d("Result : ", complete.toString())
                                    allList.add(allThings)
                                    // Finish scanning
                                    var resultText = ""
                                    resultText += "Kết quả scan được\n\n"
                                    for (score in complete) {
                                        resultText += "${score.subject} :\n HS1 : ${score.score1}\n HS2 : ${score.score2}\n HS3 : ${score.score3}\n"
                                    }
                                    binding.editTxtAll.text = resultText
                                    binding.editTxtAll.visibility = VISIBLE
                                    binding.btnSubjects.visibility = VISIBLE

                                    val scoreList = mutableListOf<SubjectData>()
                                    binding.btnSubjects.setOnClickListener {
                                        for (score in complete) {
                                            Log.d(TAG, score.toString())
                                            Log.d(TAG, score.score2.isDigitsOnly().toString())
                                            for (score1 in score.score1) {
                                                val numeric =
                                                    score1.matches("-?\\d+(\\.\\d+)?".toRegex())
                                                if (numeric) {
                                                    // Default first semester is December
                                                    scoreList.add(
                                                        SubjectData(
                                                            12,
                                                            1,
                                                            score1.toDouble(),
                                                            score.subject
                                                        )
                                                    )
                                                }
                                            }
                                            if (score.score2.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                                                scoreList.add(
                                                    SubjectData(
                                                        12,
                                                        2,
                                                        score.score2.toDouble(),
                                                        score.subject
                                                    )
                                                )
                                            }
                                            if (score.score3.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                                                scoreList.add(
                                                    SubjectData(
                                                        12,
                                                        3,
                                                        score.score3.toDouble(),
                                                        score.subject
                                                    )
                                                )
                                            }
                                        }
                                        Log.d(TAG, scoreList.toString())
                                        // filtered scoreList ( no string )
                                        // because of null it looks like this
                                        // orginally updateData(score.score, score.month, score.mul, score.sub)
                                        for (score in scoreList) {
                                            score.score?.let { it1 ->
                                                score.month?.let { it2 ->
                                                    score.mul?.let { it3 ->
                                                        score.sub?.let { it4 ->
                                                            updateData(
                                                                it1,
                                                                it2, it3, it4
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        Navigation.findNavController(view)
                                            .navigate(R.id.action_mainCamera_to_mainScore)
                                    }
                                } catch (e: Exception) {
                                    Log.d(TAG, "HI")
                                    val filteredText = mutableListOf<String>()
                                    val filterWords = listOf(
                                        "seee",
                                        "se*",
                                        "e*",
                                        "*e",
                                        "sexo",
                                        "**",
                                        "ests",
                                        "sss",
                                        "eee",
                                        "eses",
                                        "ssu",
                                        "oxes",
                                        "ssen",
                                        "resse",
                                        "$",
                                        "uue",
                                        "sese",
                                        "eeu",
                                        "suu",
                                        "aee",
                                        "eese",
                                        "sess",
                                        "444",
                                        "aee",
                                        "esae",
                                        "exes",
                                        "ssee",
                                        "reens",
                                        "cevee",
                                        "s ess",
                                        "esee",
                                        "sote",
                                        "eeas",
                                        "btes",
                                        "becs",
                                        "eerad",
                                        "44",
                                        "66",
                                        "99"
                                    )
                                    for (text in allText) {
                                        var validWord = true
                                        for (filter in filterWords)
                                            if (filter in text.lowercase()) {
                                                validWord = false
                                                break
                                            }
                                        if (validWord) {
                                            filteredText.add(text.replace("|", ""))
                                        }
                                    }
                                    // Finish scanning
                                    var resultText = ""
                                    for (text in filteredText) {
                                        resultText += "$text "
                                    }
                                    binding.editTxtAll.text = resultText
                                    binding.editTxtAll.visibility = VISIBLE
                                    binding.btnSubjects.visibility = VISIBLE

                                    binding.btnSubjects.setOnClickListener {
                                        for (subject in filteredText) {
                                            addSubject(subject)
                                        }
                                        Navigation.findNavController(view)
                                            .navigate(R.id.action_mainCamera_to_mainScore)
                                    }

                                    Log.d(TAG, filteredText.toString())
                                }
                            }
                    }
                }
            }

        binding.btnSample.setOnClickListener {
            cropActivityResultLauncher.launch(
                options {
                    setGuidelines(CropImageView.Guidelines.ON)
                    setAspectRatio(17, 9)
                }
            )

        }

        binding.btnName.setOnClickListener {
            cropActivityResultLauncher.launch(
                options {
                    setGuidelines(CropImageView.Guidelines.ON)
                }
            )
        }


    }

    //Uri -> Bitmap
    private fun getBitmapFromUri(uri: Uri?): Bitmap {
        return MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
    }

    //Add subject on firebase
    private fun addSubject(subject: String) {
        //Create a complete subject ( have to set to 0 cuz it doesn't accept null/blank )
        val subjectName = hashMapOf(
            "month" to 0,
            "sub" to subject,
            "score" to 0,
            "mul" to 0
        )
        //Get data from viewModel, add new value then set new value to viewModel
        val originalData = viewModel.subjectData.value
        originalData?.add(
            SubjectData(
                0,
                0,
                0.0,
                subject
            )
        )
        viewModel.setSubjectData(originalData!!)
        //Update data on firebase
        db.collection("users")
            .document(auth.currentUser?.uid.toString())
            .collection("subjectScores")
            .add(subjectName).addOnSuccessListener {

                Log.d("Something", "DocumentSnapshot successfully written!")
//                Toast.makeText(context, "Thêm môn thành công", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Log.w(
                    "Something",
                    "Error writing document",
                    e
                )
            }
    }

    //Push data to Firebase
    val uid = Firebase.auth.uid!!
    private fun updateData(
        score: Double,
        month: Int,
        mul: Int,
        workingSubject: String,
    ) {
        //Set document id to time so first of all get the time
        //Random number cuz when u load bunch of it at a time it might overwrite each other
        var timeSet =
            "$month ${getCurrentDateTime().toString("yyyyMMdd HH:mm:ss")} ${(1..10000000).random()}"

        //Add new data to viewModel
        val subjectData = viewModel.subjectData.value
        subjectData!!.add(
            SubjectData(
                month,
                mul,
                score,
                workingSubject
            )
        )
        // Update the data in viewModel
        viewModel.setSubjectData(subjectData)

        // Add data to Firebase, you can set Object to firebase btw
        db.collection("users")
            .document(uid)
            .collection("subjectScores")
            .document(timeSet)
            .set(
                SubjectData(
                    month,
                    mul,
                    score,
                    workingSubject
                )
            )
            .addOnSuccessListener {
                Log.d("Something", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e -> Log.w("Something", "Error writing document", e) }
    }
}