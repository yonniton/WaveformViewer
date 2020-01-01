package me.yonniton.waveform.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/** https://stackoverflow.com/questions/38744579/show-waveform-of-audio#46860408 */
class PlayerVisualizerView : View {

    companion object {
        const val VISUALIZER_HEIGHT = 28
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    /** audio bytes */
    private lateinit var bytes: ByteArray

    /**
     * Percentage of audio sample scale
     * Should updated dynamically while audioPlayer is played
     */
    private var denseness: Float = 0f

    /** [Paint] for sample scale, draws played part of the audio sample */
    private val paintPlayedState = Paint()

    /** [Paint] for sample scale, draws un-played part of the audio sample */
    private val paintNotPlayedState = Paint()

    private var w: Int = 0
    private var h: Int = 0

    private fun init() {
        bytes = ByteArray(0)

        with(paintPlayedState) {
            strokeWidth = 1f
            isAntiAlias = true
            color = ContextCompat.getColor(context, android.R.color.holo_blue_dark)
        }
        with(paintNotPlayedState) {
            strokeWidth = 1f
            isAntiAlias = true
            color = ContextCompat.getColor(context, android.R.color.holo_blue_light)
        }
    }

    /** updates and redraws [PlayerVisualizerView] with the given audio-bytes */
    fun setAudioBytes(bytes: ByteArray) {
        this.bytes = bytes
        invalidate()
    }

    /**
     * Updates playback progress.
     *
     * @param percent
     * the playback progress:
     * - 0: file not played
     * - 1: fully played
     */
    fun setPlaybackPercent(@FloatRange(from = 0.0, to = 1.0) percent: Float) {
        denseness = ceil(w * percent)
        if (denseness < 0) {
            denseness = 0f
        } else if (denseness > w) {
            denseness = w.toFloat()
        }
        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        w = measuredWidth
        h = measuredHeight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (bytes.isEmpty() || w == 0) {
            return
        }

        val totalBarsCount = (w / dp(3f)).toFloat()
        if (totalBarsCount <= 0.1f) {
            return
        }

        var value: Byte
        val samplesCount = bytes.size * 8 / 5
        val samplesPerBar = samplesCount / totalBarsCount
        var barCounter = 0f
        var nextBarNum = 0

        val y = (h - dp(VISUALIZER_HEIGHT.toFloat())) / 2
        var barNum = 0
        var lastBarNum: Int
        var drawBarCount: Int

        for (a in 0 until samplesCount) {
            if (a != nextBarNum) {
                continue
            }
            drawBarCount = 0
            lastBarNum = nextBarNum
            while (lastBarNum == nextBarNum) {
                barCounter += samplesPerBar
                nextBarNum = barCounter.toInt()
                drawBarCount++
            }

            val bitPointer = a * 5
            val byteNum = bitPointer / Byte.SIZE_BITS
            val byteBitOffset = bitPointer - byteNum * Byte.SIZE_BITS
            val currentByteCount = Byte.SIZE_BITS - byteBitOffset
            val nextByteRest = 5 - currentByteCount
            value = (bytes[byteNum].toInt() shr byteBitOffset and (2 shl min(5, currentByteCount) - 1) - 1).toByte()
            if (nextByteRest > 0) {
                value = (value.toInt() shl nextByteRest).toByte()
                value = value or (bytes[byteNum + 1] and ((2 shl nextByteRest - 1) - 1).toByte())
            }

            for (b in 0 until drawBarCount) {
                val x = barNum * dp(3f)
                val left = x.toFloat()
                val top = (y + dp(VISUALIZER_HEIGHT - max(1f, VISUALIZER_HEIGHT * value / 31.0f))).toFloat()
                val right = (x + dp(2f)).toFloat()
                val bottom = (y + dp(VISUALIZER_HEIGHT.toFloat())).toFloat()
                if (x < denseness && x + dp(2f) < denseness) {
                    canvas.drawRect(left, top, right, bottom, paintNotPlayedState)
                } else {
                    canvas.drawRect(left, top, right, bottom, paintPlayedState)
                    if (x < denseness) {
                        canvas.drawRect(left, top, right, bottom, paintNotPlayedState)
                    }
                }
                barNum++
            }
        }
    }

    private fun dp(value: Float): Int {
        return value.takeIf { it > 0 }
            ?.let { ceil(context.resources.displayMetrics.density * it).toInt() }
            ?: 0
    }
}
