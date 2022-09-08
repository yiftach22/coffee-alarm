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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import yiftach.carmon.coffeealarm.*
import yiftach.carmon.coffeealarm.alarmTools.Alarm
import yiftach.carmon.coffeealarm.alarmTools.AlarmService
import yiftach.carmon.coffeealarm.databinding.ActivityRingBinding
import yiftach.carmon.coffeealarm.viewModels.RingActivityViewModel
import java.util.*

const val DATA = "data"


class RingActivity : AppCompatActivity() {
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    private lateinit var binding: ActivityRingBinding

    private val ringActivityViewModel:RingActivityViewModel by viewModels()

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.exitBtn.setOnClickListener {
            finish()
        }
        binding.exitBtn.visibility = View.GONE

        setSnoozeLayout()
        registerCameraLauncher()

    }


    private fun setSnoozeLayout(){
        val title = intent.getStringExtra(TITLE)?:"" // TODO show title
        val sp = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val snoozesLeft = sp.getInt(SNOOZES_LEFT, 0)

        if (snoozesLeft > 0) {
            binding.snoozeLayout.visibility = View.VISIBLE
            binding.snoozeNumberTv.text = snoozesLeft.toString()
            val snoozeLength = sp.getInt(SNOOZE_LENGTH, 0)
            binding.snoozeBtn.setOnClickListener {
                stopAlarm()
                setSnooze(snoozeLength, title, snoozesLeft-1)
            }
        } else {
            binding.snoozeLayout.visibility = View.GONE
        }

        binding.takePhotoBtn.setOnClickListener {
            setTakePhotoClickListener()
        }

    }


    private fun setTakePhotoClickListener(){
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
                CAMERA_PERMISSION_CODE
            )
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


    private fun setSnooze(snoozeLength:Int, title:String, snoozesLeft:Int){
        val calendar = Calendar.getInstance()
        var minutes = calendar.get(Calendar.MINUTE) + snoozeLength
        var hours = calendar.get(Calendar.HOUR_OF_DAY)
        if (minutes >= 60){
            minutes = minutes + snoozeLength - 60
            hours = if (hours==23) 0 else hours +1
        }
        val alarm = Alarm(
            Random().nextInt(Int.MAX_VALUE),
            hours,
            minutes,
            title,
            snoozesLeft,
            snoozeLength,
            false
            )
        alarm.schedule(this)
//        ringActivityViewModel.setAlarm(this, alarm)
        finish()
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
        if (results.isNotEmpty()) {
            val categories: Category = results[0].categories.first()
            val coffeeName = when (categories.label) {
                "black_coffee" -> "Black Coffee"
                "instant_coffee" -> "Instant Coffee"
                "water" -> "Water"
                else -> "Nothing"
            }
            resultText = "Found $coffeeName!"
            stopAlarm()



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
                binding.exitBtn.visibility = View.VISIBLE
                binding.takePhotoBtn.visibility = View.GONE
                binding.titleTv.text = resultText
            }
        } else {
            val text = "No Coffee Found. Try again"
            runOnUiThread{
                binding.titleTv.text = text
            }

        }
    }


    private fun stopAlarm(){
        val intentService = Intent(applicationContext, AlarmService::class.java)
        applicationContext.stopService(intentService)
        Alarm.removeAlarmFromSp(this)
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
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(packageManager) != null)
                    cameraLauncher.launch(intent)
            } else {
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
