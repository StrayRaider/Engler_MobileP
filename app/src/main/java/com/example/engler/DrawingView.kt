package com.example.engler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PathEffect
import android.graphics.Rect
import android.graphics.DashPathEffect
import android.util.AttributeSet
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var selectionRect: Rect? = null
    private val strokePaint = Paint().apply {
        color = 0x990000FF.toInt() // Semi-transparent blue for the dashed border
        strokeWidth = 8f
        style = Paint.Style.STROKE
        // Set dashed lines
        pathEffect = DashPathEffect(floatArrayOf(20f, 10f), 0f)
    }

    private val fillPaint = Paint().apply {
        color = 0x660000FF // Semi-transparent blue fill
        style = Paint.Style.FILL
    }

    private val shadowPaint = Paint().apply {
        color = 0x80000000.toInt() // Semi-transparent black for shadow
        strokeWidth = 12f
        style = Paint.Style.STROKE
        // Add blur effect to create a glowing border
        setShadowLayer(10f, 0f, 0f, 0x55000000)
    }

    // Function to set the selection area based on touch events
    fun setSelection(startX: Float, startY: Float, endX: Float, endY: Float) {
        selectionRect = Rect(
            startX.toInt(),
            startY.toInt(),
            endX.toInt(),
            endY.toInt()
        )
        invalidate() // Redraw the view to reflect the changes
    }

    // Function to set the final selected region
    fun setSelectedRegion(rect: Rect) {
        selectionRect = rect
        invalidate() // Redraw the view with the finalized region
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw shadow effect
        selectionRect?.let {
            canvas.drawRect(it, shadowPaint) // Draw shadow for glow effect
            canvas.drawRect(it, fillPaint) // Fill the selection with semi-transparent blue
            canvas.drawRect(it, strokePaint) // Draw the dashed border
        }
    }
}
