package com.example.engler

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.common.InputImage
import androidx.camera.view.PreviewView
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import android.view.MotionEvent

class MainActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var resultTextView: TextView
    private lateinit var captureButton: Button
    private lateinit var selectRegionButton: Button
    private lateinit var drawingView: DrawingView

    // Variables to store touch start and end points
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var endX: Float = 0f
    private var endY: Float = 0f
    private lateinit var selectedRegion: Rect
    private var isSelectingRegion = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.previewView)
        resultTextView = findViewById(R.id.resultTextView)
        captureButton = findViewById(R.id.captureButton)
        selectRegionButton = findViewById(R.id.selectRegionButton)
        drawingView = findViewById(R.id.drawingView)

        // Request necessary permissions if needed
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 101)
        }

        captureButton.setOnClickListener {
            captureAndRecognizeText()
        }

        selectRegionButton.setOnClickListener {
            // Enable region selection mode
            isSelectingRegion = true
            resultTextView.text = "Select a region on the camera preview."
        }

        previewView.setOnTouchListener { _, event ->
            if (isSelectingRegion) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Capture the starting touch coordinates
                        startX = event.x
                        startY = event.y
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // Track the movement and update the ending touch coordinates
                        endX = event.x
                        endY = event.y
                        // Update the rectangle as the user drags their finger
                        drawingView.setSelection(startX, startY, endX, endY)
                    }
                    MotionEvent.ACTION_UP -> {
                        // Once the user lifts their finger, finalize the selection
                        selectedRegion = Rect(
                            startX.toInt(),
                            startY.toInt(),
                            endX.toInt(),
                            endY.toInt()
                        )
                        // Show a message and update text
                        resultTextView.text = "Region selected. Now capture the image."
                        drawingView.setSelectedRegion(selectedRegion)
                        isSelectingRegion = false
                    }
                }
                true
            } else {
                false
            }
        }

        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview)

        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureAndRecognizeText() {
        // Capture the current bitmap from the camera preview
        val bitmap = previewView.bitmap
        if (bitmap != null) {
            val inputImage: InputImage
            // Check if a region is selected
            if (this::selectedRegion.isInitialized && selectedRegion.width() > 0 && selectedRegion.height() > 0) {
                // Crop the image to the selected region
                val croppedBitmap = cropBitmap(bitmap, selectedRegion)
                inputImage = InputImage.fromBitmap(croppedBitmap, 0)
            } else {
                // If no region is selected, capture the full image
                inputImage = InputImage.fromBitmap(bitmap, 0)
            }
            recognizeText(inputImage)
        } else {
            Toast.makeText(this, "No image captured", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cropBitmap(bitmap: Bitmap, region: Rect): Bitmap {
        // Crop the bitmap based on the selected region
        return Bitmap.createBitmap(bitmap, region.left, region.top, region.width(), region.height())
    }

    private fun recognizeText(inputImage: InputImage) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build())

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val detectedText = visionText.text
                if (detectedText.isNotEmpty()) {
                    resultTextView.text = detectedText // Display detected text in the TextView
                } else {
                    resultTextView.text = "No text detected"
                }
            }
            .addOnFailureListener { e ->
                Log.e("TextRecognition", "Text recognition failed", e)
                Toast.makeText(this, "Failed to recognize text", Toast.LENGTH_SHORT).show()
            }
    }
}
