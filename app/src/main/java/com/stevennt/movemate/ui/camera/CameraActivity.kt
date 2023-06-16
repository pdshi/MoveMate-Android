/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/

package org.tensorflow.lite.examples.poseestimation

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.stevennt.movemate.R
import com.stevennt.movemate.data.Resource
import com.stevennt.movemate.ml.CameraSource
import com.stevennt.movemate.preference.UserPreferences
import com.stevennt.movemate.preference.UserPreferences.Companion.counter
import com.stevennt.movemate.preference.UserPreferences.Companion.currentWorkoutIndex
import com.stevennt.movemate.preference.UserPreferences.Companion.size
import com.stevennt.movemate.preference.UserPreferences.Companion.userRepsList
import com.stevennt.movemate.ui.ViewModelFactory
import com.stevennt.movemate.ui.camera.CameraViewModel
import com.stevennt.movemate.ui.utils.CustomButton
import com.stevennt.movemate.ui.utils.CustomEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.examples.poseestimation.data.Device
import org.tensorflow.lite.examples.poseestimation.ml.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import com.stevennt.movemate.preference.UserPreferences.Companion.workoutList
import com.stevennt.movemate.ui.schedule.ScheduleActivity

@Suppress("DEPRECATION")
class CameraActivity : AppCompatActivity() {
    companion object {
        private const val FRAGMENT_DIALOG = "dialog"
    }

    private lateinit var dataStore: DataStore<Preferences>
    private val viewModel: CameraViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private var currentWorkout: String = ""

    /** A [SurfaceView] for camera preview.   */
    private lateinit var surfaceView: SurfaceView

    /** Default pose estimation model is 1 (MoveNet Thunder)
     * 0 == MoveNet Lightning model
     **/
    private var modelPos = 0

    /** Default device is CPU */
    private var device = Device.CPU

    private lateinit var tvScore: TextView
    private lateinit var tvFPS: TextView
    private lateinit var spnDevice: Spinner
    private lateinit var spnModel: Spinner
    private lateinit var tvClassificationValue1: TextView
    private lateinit var tvClassificationValue2: TextView
    private lateinit var swClassification: SwitchCompat
    private lateinit var vClassificationOption: View
    private lateinit var btnBack: ImageView
    private lateinit var btnNext: CustomButton
    private lateinit var tvWorkout: TextView
    private lateinit var tvCounter: TextView
    private var cameraSource: CameraSource? = null
    private var isClassifyPose = false
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                openCamera()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to  change their
                // decision.
                ErrorDialog.newInstance(getString(R.string.tfe_pe_request_permission))
                    .show(supportFragmentManager, FRAGMENT_DIALOG)
            }
        }
    private var changeModelListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do nothing
        }

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            changeModel(position)
        }
    }

    private var changeDeviceListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            changeDevice(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do nothing
        }
    }

    private var setClassificationListener =
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            showClassificationResult(isChecked)
            isClassifyPose = isChecked
            isPoseClassifier()
        }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_camera)
        supportActionBar?.hide()

        // keep screen on while app is running
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        tvScore = findViewById(R.id.tvScore)
        tvFPS = findViewById(R.id.tvFps)
        spnModel = findViewById(R.id.spnModel)
        spnDevice = findViewById(R.id.spnDevice)
        surfaceView = findViewById(R.id.surfaceView)
        tvClassificationValue1 = findViewById(R.id.tvClassificationValue1)
        tvClassificationValue2 = findViewById(R.id.tvClassificationValue2)
        swClassification = findViewById(R.id.swPoseClassification)
        vClassificationOption = findViewById(R.id.vClassificationOption)
        btnBack = findViewById(R.id.back_camera)
        btnNext = findViewById(R.id.btn_camera_next)
        tvWorkout = findViewById(R.id.tv_workout)
        tvCounter = findViewById(R.id.tv_counter)

        initSpinner()
        spnModel.setSelection(modelPos)

        dataStore = PreferenceDataStoreFactory.create(
            produceFile = {
                File(application.filesDir, "user_prefs.preferences_pb")
            }
        )

        swClassification.setOnCheckedChangeListener(setClassificationListener)
        if (!isCameraPermissionGranted()) {
            requestPermission()
        }

        btnBack.setOnClickListener{
            onBackPressed()
        }

        val userPreferences = UserPreferences.getInstance(dataStore)
        val userSessionFlow = userPreferences.getUserSession()

        btnNext.setOnClickListener{
            if(currentWorkoutIndex == 0){
                lifecycleScope.launch {
                    userSessionFlow.collect { userSession ->
                        val sessionToken = userSession.token
                        if (!sessionToken.isNullOrEmpty()) {
                            viewModel.inputUserHistory(sessionToken, "pushup", 30,  counter)
                                .observe(this@CameraActivity){ result ->
                                    when(result){
                                        is Resource.Loading -> {}

                                        is Resource.Success -> {
                                            counter = 0
                                            currentWorkoutIndex = 1
                                            runOnUiThread {
                                                recreate()
                                            }
                                            size += 1
                                        }

                                        is Resource.Error -> {
                                            Toast.makeText(this@CameraActivity, result.message, Toast.LENGTH_SHORT).show()
                                            Log.d(this@CameraActivity.toString(), "msg: ${result.message}")
                                        }
                                    }
                                }
                        }
                    }
                }
            } else {
                lifecycleScope.launch {
                    userSessionFlow.collect { userSession ->
                        val sessionToken = userSession.token
                        if (!sessionToken.isNullOrEmpty()) {
                            viewModel.inputUserHistory(sessionToken, "situp", 30,  counter)
                                .observe(this@CameraActivity){ result ->
                                    when(result){
                                        is Resource.Loading -> {}

                                        is Resource.Success -> {
                                            counter = 0
                                            currentWorkoutIndex = 0
                                            runOnUiThread {
                                                recreate()
                                            }
                                            size += 1
                                        }

                                        is Resource.Error -> {
                                            Toast.makeText(this@CameraActivity, result.message, Toast.LENGTH_SHORT).show()
                                            Log.d(this@CameraActivity.toString(), "msg: ${result.message}")
                                        }
                                    }
                                }
                        }
                    }
                }
            }

            if(size == workoutList.size) {
                size = 0
                val intent = Intent(this@CameraActivity, ScheduleActivity::class.java)
                startActivity(intent)
                runOnUiThread {
                    recreate()
                }
            }
        }

        Log.d("index", currentWorkoutIndex.toString())
    }

    private fun CustomEditText.listener() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                //btnNext.isEnabled =
            }

        })
    }

    override fun onStart() {
        super.onStart()
        openCamera()
    }

    override fun onResume() {
        cameraSource?.resume()
        super.onResume()
    }

    override fun onPause() {
        cameraSource?.close()
        cameraSource = null
        super.onPause()
    }

    // check if permission is granted or not.
    private fun isCameraPermissionGranted(): Boolean {
        return checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    // open camera
    private fun openCamera() {
        if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource =
                    CameraSource(surfaceView, object : CameraSource.CameraSourceListener {
                        override fun onFPSListener(fps: Int) {
                            tvFPS.text = getString(R.string.tfe_pe_tv_fps, fps)
                        }

                        val userPreferences = UserPreferences.getInstance(dataStore)
                        val userRepsFlow = userPreferences.getUserReps()



                        override fun onDetectedInfo(
                            personScore: Float?,
                            poseLabels: List<Pair<String, Float>>?
                        ) {
                            tvScore.text = getString(R.string.tfe_pe_tv_score, personScore ?: 0f)
                            poseLabels?.sortedByDescending { it.second }?.let {
                                tvClassificationValue1.text = getString(
                                    R.string.tfe_pe_tv_classification_value,
                                    convertPoseLabels(if (it.isNotEmpty()) it[0] else null)
                                )
                                tvClassificationValue2.text = getString(
                                    R.string.tfe_pe_tv_classification_value,
                                    convertPoseLabels(if (it.size >= 2) it[1] else null)
                                )

                                /*if (it[0].first == "down" && it[0].second.toDouble() == 1.0) {
                                    counter++
                                    runOnUiThread{
                                        tvCounter.text = counter.toString()
                                    }
                                    //Log.d("counter", counter.toString())
                                }*/

                                if (it[0].first == "down" && it[0].second.toDouble() >= 0.95) {
                                    Log.d("name", it[0].first)
                                    if (it.size >= 2 && it[0].first == "up") {
                                        val secondPoseScore = it[1].second.toDouble()
                                        if (secondPoseScore >= 0.95) {
                                            counter++
                                            runOnUiThread {
                                                tvCounter.text = counter.toString()
                                            }
                                        }
                                        //Log.d("counter", counter.toString())
                                    }
                                }
                            }
                        }

                    }).apply {
                        prepareCamera()
                    }
                isPoseClassifier()
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera()
                }
            }
            createPoseEstimator()
        }
    }

    private fun convertPoseLabels(pair: Pair<String, Float>?): String {
        if (pair == null) return "empty"
        return "${pair.first} (${String.format("%.2f", pair.second)})"
    }

    private fun isPoseClassifier() {
        if(currentWorkoutIndex == 0){
            cameraSource?.setClassifier(if (isClassifyPose) PoseClassifier.create(this,
                "pose_classifier_situp.tflite", "pose_labels.txt") else null)
            //Log.d("index pose class 0", currentWorkoutIndex.toString())
            currentWorkoutIndex = 1
            tvWorkout.text = "Sit Up: "
        } else if (currentWorkoutIndex == 1) {
            cameraSource?.setClassifier(if (isClassifyPose) PoseClassifier.create(this,
                "pose_classifier_pushup.tflite", "pose_labels.txt") else null)
            //Log.d("index pose class 1", currentWorkoutIndex.toString())
            currentWorkoutIndex = 0
            tvWorkout.text = "Push Up: "
        }

    }

    // Initialize spinners to let user select model/accelerator.
    private fun initSpinner() {

        ArrayAdapter.createFromResource(
            this,
            R.array.tfe_pe_models_array,
            android.R.layout.simple_spinner_item,
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spnModel.adapter = adapter
            spnModel.onItemSelectedListener = changeModelListener
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.tfe_pe_device_name,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnDevice.solidColor
            spnDevice.adapter = adapter
            spnDevice.onItemSelectedListener = changeDeviceListener
        }
    }

    // Change model when app is running
    private fun changeModel(position: Int) {
        if (modelPos == position) return
        modelPos = position
        createPoseEstimator()
    }

    // Change device (accelerator) type when app is running
    private fun changeDevice(position: Int) {
        val targetDevice = when (position) {
            0 -> Device.CPU
            1 -> Device.GPU
            else -> Device.NNAPI
        }
        if (device == targetDevice) return
        device = targetDevice
        createPoseEstimator()
    }

    private fun createPoseEstimator() {
        // For MoveNet MultiPose, hide score and disable pose classifier as the model returns
        // multiple Person instances.
        val poseDetector = when (modelPos) {
            0 -> {
                // MoveNet Lightning (SinglePose)
                showPoseClassifier(true)
                showDetectionScore(true)
                MoveNet.create(this, device, ModelType.Lightning)
            }
            else -> {
                null
            }
        }
        poseDetector?.let { detector ->
            cameraSource?.setDetector(detector)
        }
    }

    // Show/hide the pose classification option.
    private fun showPoseClassifier(isVisible: Boolean) {
        tvWorkout.text = " "
        vClassificationOption.visibility = if (isVisible) View.VISIBLE else View.GONE
        if (!isVisible) {
            tvWorkout.text = " "
            swClassification.isChecked = false
        }
    }

    // Show/hide the detection score.
    private fun showDetectionScore(isVisible: Boolean) {
        tvScore.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    // Show/hide classification result.
    private fun showClassificationResult(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        tvClassificationValue1.visibility = visibility
        tvClassificationValue2.visibility = visibility
    }

    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                // You can use the API that requires the permission.
                openCamera()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Shows an error message dialog.
     */
    class ErrorDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                .setMessage(requireArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // do nothing
                }
                .create()

        companion object {

            @JvmStatic
            private val ARG_MESSAGE = "message"

            @JvmStatic
            fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
                arguments = Bundle().apply { putString(ARG_MESSAGE, message) }
            }
        }
    }
}
