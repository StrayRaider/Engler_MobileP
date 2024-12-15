package com.example.engler

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

class MainActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var resultTextView: TextView
    private lateinit var captureButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.previewView)
        resultTextView = findViewById(R.id.resultTextView)
        captureButton = findViewById(R.id.captureButton)

        // Request necessary permissions if needed
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 101)
        }

        captureButton.setOnClickListener {
            captureAndRecognizeText()
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
        // Capture the current image from the camera preview
        val bitmap = previewView.bitmap // Get a bitmap from the PreviewView
        if (bitmap != null) {
            val inputImage = InputImage.fromBitmap(bitmap, 0) // Convert Bitmap to InputImage for ML Kit
            recognizeText(inputImage)
        } else {
            Toast.makeText(this, "No image captured", Toast.LENGTH_SHORT).show()
        }
    }

    private fun recognizeText(inputImage: InputImage) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build())

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val detectedText = visionText.text
                if (detectedText.isNotEmpty()) {
                    resultTextView.text = detectedText // Update the text view with the recognized text
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
