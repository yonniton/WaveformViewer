package me.yonniton.waveform.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

/**
 * source: [Android Example](https://androidexample.com/Detect_Noise_Or_Blow_Sound_-_Set_Sound_Frequency_Thersold/index.php?view=article_discription&aid=108&aaid=130)
 */
class SoundLevelView(
    context: Context,
    attrs: AttributeSet?
) : View(context, attrs) {

    private val pipGreen: Drawable = ContextCompat.getDrawable(context, android.R.drawable.presence_audio_online)!!
    private val pipRed: Drawable = ContextCompat.getDrawable(context, android.R.drawable.presence_audio_busy)!!
    private val backgroundPaint: Paint
    private val pipHeight: Int
    private val pipWidth: Int
    private var threshold = 0
    private var volume = 0

    private fun drawLevel(volume: Int, threshold: Int) {
        if (volume == this.volume && threshold == this.threshold) return
        this.volume = volume
        this.threshold = threshold

        // invalidate onDraw and draw voice points
        invalidate()
    }

    /** volume vs threshold */
    var soundLevel: Pair<Int, Int>
        get() = volume to threshold
        set(pair) {
            drawLevel(pair.first, pair.second)
        }

    public override fun onDraw(canvas: Canvas) {
        canvas.drawPaint(backgroundPaint)
        0.rangeTo(volume).forEach { level ->
            val bar = pipGreen.takeIf { level < threshold }
                ?: pipRed
            with(bar) {
                setBounds((10 - level) * pipWidth, 0, (10 - level + 1) * pipWidth, pipHeight)
                draw(canvas)
            }
        }
    }

    init {

        pipWidth = pipGreen.intrinsicWidth
        minimumWidth = pipWidth * 10
        pipHeight = pipGreen.intrinsicHeight
        minimumHeight = pipHeight

        // paints canvas background color
        backgroundPaint = Paint().apply {
            color = Color.BLACK
        }
    }
}
