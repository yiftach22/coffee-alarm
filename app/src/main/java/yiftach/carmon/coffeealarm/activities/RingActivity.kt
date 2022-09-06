package yiftach.carmon.coffeealarm.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import yiftach.carmon.coffeealarm.alarmTools.AlarmService
import yiftach.carmon.coffeealarm.SNOOZES_LEFT
import yiftach.carmon.coffeealarm.databinding.ActivityRingBinding

const val DATA = "data"


class RingActivity : AppCompatActivity() {
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    private lateinit var binding:ActivityRingBinding

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRingBinding.inflate(layoutInflater)
        setContentView(binding.root);

        binding.exitBtn.setOnClickListener {
            finish()
        }

        registerCameraLauncher()

        val sp = this.getPreferences(Context.MODE_PRIVATE)
        val numberOfSnoozes = sp.getInt(SNOOZES_LEFT, 0)
        if (numberOfSnoozes >0){
            binding.snoozeLayout.visibility = View.VISIBLE
            binding.snoozeBtn.setOnClickListener {
                // TODO set snooze
            }
        } else {
            binding.snoozeLayout.visibility = View.GONE
        }




        binding.takePhotoBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA,
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(packageManager) != null)
                    cameraLauncher.launch(intent)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE)
            }
        }
    }

    private fun registerCameraLauncher() {
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                    val bundle = result.data!!.extras
                    val bitmap = bundle?.get(DATA) as Bitmap
                    binding.imageView.setImageBitmap(bitmap)
                    lifecycleScope.launch(Dispatchers.Default) { runObjectDetection(bitmap) }
                }

            }
    }

    /**
     * runObjectDetection(bitmap: Bitmap)
     *      TFLite Object Detection function
     */
    private fun runObjectDetection(bitmap: Bitmap) {
        val image = TensorImage.fromBitmap(bitmap)
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(5)
            .setScoreThreshold(0.5f)
            .build()
        val detector = ObjectDetector.createFromFileAndOptions(
            this, // the application context
            "model.tflite", // must be same as the filename in assets folder
            options
        )
        val results = detector.detect(image)
        var resultText = ""


        // check results
        if (results.isNotEmpty()){
            val categories: Category = results[0].categories.first()
            Log.d("printResult", categories.toString())
            val coffeeName = when(categories.label){
                "black_coffee" -> "Black Coffee"
                "instant_coffee" -> "Instant Coffee"
                "water" -> "Water"
                else -> "Nothing"
            }
            resultText = "Found $coffeeName!"
            val intentService = Intent(applicationContext, AlarmService::class.java)
            applicationContext.stopService(intentService)
        }

        val resultToDisplay = results.map {
            // Get the top-1 category and craft the display text
            val category = it.categories.first()
            val text = "${category.label}, ${category.score.times(100).toInt()}%"

            // Create a data object to display the detection result
            DetectionResult(it.boundingBox, text)
        }
        // Draw the detection result on the bitmap and show it.
        val imgWithResult = drawDetectionResult(bitmap, resultToDisplay)
        runOnUiThread {
            binding.imageView.setImageBitmap(imgWithResult)
            if (resultText == ""){
                resultText = "No Coffee Found. Try again"
            }
            binding.titleTv.text = resultText
            binding.exitBtn.visibility = View.VISIBLE
            binding.takePhotoBtn.visibility = View.GONE


        }
    }



    /**
     * drawDetectionResult(bitmap: Bitmap, detectionResults: List<DetectionResult>
     *      Draw a box around each objects and show the object's name.
     */
    private fun drawDetectionResult(
        bitmap: Bitmap,
        detectionResults: List<DetectionResult>
    ): Bitmap {
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val pen = Paint()
        pen.textAlign = Paint.Align.LEFT

        detectionResults.forEach {
            // draw bounding box
            pen.color = Color.RED
            pen.strokeWidth = 1F
            pen.style = Paint.Style.STROKE
            val box = it.boundingBox
            canvas.drawRect(box, pen)
        }
        return outputBitmap
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(packageManager) != null)
                    cameraLauncher.launch(intent)
            }else{
                Toast.makeText(this, "Camera permission must be granted", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

/**
 * DetectionResult
 *      A class to store the visualization info of a detected object.
 */
data class DetectionResult(val boundingBox: RectF, val text: String)
