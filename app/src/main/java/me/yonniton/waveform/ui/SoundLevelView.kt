package me.yonniton.waveform.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import me.yonniton.waveform.R

/**
 * source: [Android Example](https://androidexample.com/Detect_Noise_Or_Blow_Sound_-_Set_Sound_Frequency_Thersold/index.php?view=article_discription&aid=108&aaid=130)
 */
class SoundLevelView(
    context: Context,
    attrs: AttributeSet?
) : View(context, attrs) {

    private val barGreen: Drawable = ContextCompat.getDrawable(context, R.drawable.greenbar)!!
    private val barRed: Drawable = ContextCompat.getDrawable(context, R.drawable.redbar)!!
    private val backgroundPaint: Paint
    private val barHeight: Int
    private val barWidth: Int
    private var mThreshold = 0
    private var mVol = 0

    fun setLevel(volume: Int, threshold: Int) {
        if (volume == mVol && threshold == mThreshold) return
        mVol = volume
        mThreshold = threshold

        // invalidate onDraw and draw voice points
        invalidate()
    }

    public override fun onDraw(canvas: Canvas) {
        canvas.drawPaint(backgroundPaint)
        for (i in 0..mVol) {
            val bar: Drawable = if (i < mThreshold) barGreen else barRed
            bar.setBounds((10 - i) * barWidth, 0, (10 - i + 1) * barWidth, barHeight)
            bar.draw(canvas)
        }
    }

    init {

        barWidth = barGreen.intrinsicWidth
        minimumWidth = barWidth * 10
        barHeight = barGreen.intrinsicHeight
        minimumHeight = barHeight

        // paints canvas background color
        backgroundPaint = Paint().apply {
            color = Color.BLACK
        }
    }
}
