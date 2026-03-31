package app.gamenative.ui.component

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.abs
import kotlin.random.Random

class DragonBackgroundView(context: Context) : View(context) {

    private var breathAnim = 0f
    private var fireAnim = 0f
    private var wingAnim = 0f
    private var reactionAnim = 0f
    private var blinkAnim = 0f
    private var smokeAnim = 0f
    private var tailAnim = 0f
    private var isSpittingFire = false
    private var reactionType = 0

    private val touchPoint = PointF(-1f, -1f)
    private val eyeLeft = PointF(0f, 0f)
    private val eyeRight = PointF(0f, 0f)

    // ---- PAINTS ----
    private val bodyFill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val bodyStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 3f; color = Color.argb(160, 40, 0, 0)
    }
    private val wingFill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val wingMembrane = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 1.8f
    }
    private val scalePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val scaleStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 0.8f; color = Color.argb(100, 20, 0, 0)
    }
    private val eyeGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val eyeIrisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(255, 255, 180, 0); style = Paint.Style.FILL
    }
    private val eyePupilPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(255, 0, 0, 0); style = Paint.Style.FILL
    }
    private val eyeShimmer = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(200, 255, 255, 255); style = Paint.Style.FILL
    }
    private val hornPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val clawPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val firePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val fireCorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val smokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeJoin = Paint.Join.ROUND; strokeCap = Paint.Cap.ROUND
    }
    private val controllerFill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val controllerDetail = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL; color = Color.argb(200, 20, 20, 30)
    }
    private val controllerBtn = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val controllerStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 1.5f; color = Color.argb(180, 80, 80, 100)
    }
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val ridgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    // ---- ANIMATORS ----
    private val breathAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 3500; repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE; interpolator = DecelerateInterpolator()
        addUpdateListener { breathAnim = it.animatedValue as Float; invalidate() }
        start()
    }
    private val fireAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 120; repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener { fireAnim = it.animatedValue as Float }
        start()
    }
    private val wingAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 4000; repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE; interpolator = DecelerateInterpolator()
        addUpdateListener { wingAnim = it.animatedValue as Float }
        start()
    }
    private val tailAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 5000; repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE; interpolator = LinearInterpolator()
        addUpdateListener { tailAnim = it.animatedValue as Float }
        start()
    }

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        alpha = 0.95f
        scheduleFire()
    }

    private fun scheduleFire() {
        postDelayed({
            isSpittingFire = true
            postDelayed({ isSpittingFire = false; scheduleFire() }, 1800 + Random.nextLong(1000))
        }, 2500 + Random.nextLong(3500))
    }

    fun setTouchPosition(x: Float, y: Float) {
        touchPoint.set(x, y)
        triggerReaction()
    }

    private fun triggerReaction() {
        reactionType = Random.nextInt(4)
        val anim = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 700
            addUpdateListener { reactionAnim = it.animatedValue as Float; invalidate() }
        }
        anim.start()
        if (reactionType == 1) {
            val blinkA = ValueAnimator.ofFloat(0f, 1f, 0f).apply {
                duration = 350
                addUpdateListener { blinkAnim = it.animatedValue as Float }
            }
            blinkA.start()
        }
        if (reactionType == 2) {
            val smokeA = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 900
                addUpdateListener { smokeAnim = it.animatedValue as Float }
            }
            smokeA.start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (width == 0 || height == 0) return
        val w = width.toFloat()
        val h = height.toFloat()
        val sc = min(w, h) / 520f
        val cx = w * 0.58f
        val cy = h * 0.60f
        val bOff = breathAnim * 5f * sc

        drawBodyGlow(canvas, cx, cy, sc)
        drawTail(canvas, cx, cy, sc)
        drawWings(canvas, cx, cy, sc, bOff)
        drawHindLegs(canvas, cx, cy, sc)
        drawBody(canvas, cx, cy, sc, bOff)
        drawNeck(canvas, cx, cy, sc, bOff)
        drawFrontArms(canvas, cx, cy, sc, bOff)
        drawHead(canvas, cx, cy, sc, bOff)

        if (isSpittingFire) drawFire(canvas, cx, cy, sc, bOff)
        if (smokeAnim > 0f) drawSmoke(canvas, cx, cy, sc, bOff)
        if (reactionType == 0 && reactionAnim > 0) drawRoarRings(canvas, cx, cy, sc, bOff)
        if (reactionType == 3 && reactionAnim > 0) drawSparkles(canvas, cx, cy, sc, bOff)
    }

    private fun drawBodyGlow(canvas: Canvas, cx: Float, cy: Float, sc: Float) {
        glowPaint.shader = RadialGradient(
            cx, cy, 160f * sc,
            intArrayOf(Color.argb(50, 200, 0, 0), Color.argb(20, 100, 0, 0), Color.TRANSPARENT),
            floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP,
        )
        canvas.drawCircle(cx, cy, 160f * sc, glowPaint)
    }

    private fun drawTail(canvas: Canvas, cx: Float, cy: Float, sc: Float) {
        val tSwing = (tailAnim - 0.5f) * 20f * sc
        val path = Path().apply {
            moveTo(cx + 20f * sc, cy + 55f * sc)
            cubicTo(cx + 80f * sc, cy + 100f * sc + tSwing,
                cx + 120f * sc, cy + 80f * sc + tSwing * 0.5f,
                cx + 150f * sc, cy + 50f * sc + tSwing * 0.3f)
            cubicTo(cx + 170f * sc, cy + 30f * sc,
                cx + 160f * sc, cy + 10f * sc,
                cx + 140f * sc, cy + 18f * sc)
            cubicTo(cx + 120f * sc, cy + 26f * sc,
                cx + 100f * sc, cy + 60f * sc,
                cx + 80f * sc, cy + 90f * sc + tSwing * 0.7f)
            cubicTo(cx + 50f * sc, cy + 120f * sc + tSwing,
                cx + 10f * sc, cy + 80f * sc,
                cx + 20f * sc, cy + 55f * sc)
            close()
        }
        bodyFill.shader = LinearGradient(cx + 20f * sc, cy + 55f * sc, cx + 150f * sc, cy + 50f * sc,
            Color.argb(220, 140, 10, 0), Color.argb(160, 80, 0, 0), Shader.TileMode.CLAMP)
        canvas.drawPath(path, bodyFill)
        bodyStroke.strokeWidth = 2f * sc
        canvas.drawPath(path, bodyStroke)
        drawTailSpikes(canvas, cx, cy, sc, tSwing)
    }

    private fun drawTailSpikes(canvas: Canvas, cx: Float, cy: Float, sc: Float, tSwing: Float) {
        val spikePts = listOf(
            PointF(cx + 85f * sc, cy + 90f * sc + tSwing * 0.7f) to PointF(cx + 95f * sc, cy + 70f * sc),
            PointF(cx + 110f * sc, cy + 70f * sc + tSwing * 0.5f) to PointF(cx + 122f * sc, cy + 50f * sc),
            PointF(cx + 135f * sc, cy + 50f * sc + tSwing * 0.3f) to PointF(cx + 148f * sc, cy + 30f * sc),
            PointF(cx + 148f * sc, cy + 30f * sc + tSwing * 0.2f) to PointF(cx + 162f * sc, cy + 10f * sc),
        )
        ridgePaint.color = Color.argb(220, 200, 80, 0)
        for ((base, tip) in spikePts) {
            val spk = Path().apply {
                moveTo(base.x - 6f * sc, base.y)
                lineTo(tip.x, tip.y)
                lineTo(base.x + 6f * sc, base.y)
                close()
            }
            canvas.drawPath(spk, ridgePaint)
        }
    }

    private fun drawWings(canvas: Canvas, cx: Float, cy: Float, sc: Float, bOff: Float) {
        val wFlap = (wingAnim - 0.5f) * 12f * sc

        val leftWing = Path().apply {
            moveTo(cx - 30f * sc, cy - 30f * sc - bOff)
            cubicTo(cx - 100f * sc, cy - 140f * sc - bOff + wFlap,
                cx - 200f * sc, cy - 100f * sc + wFlap,
                cx - 220f * sc, cy + 20f * sc + wFlap * 0.5f)
            cubicTo(cx - 200f * sc, cy + 10f * sc,
                cx - 140f * sc, cy - 10f * sc,
                cx - 80f * sc, cy - 5f * sc)
            cubicTo(cx - 60f * sc, cy - 10f * sc,
                cx - 40f * sc, cy - 20f * sc - bOff,
                cx - 30f * sc, cy - 30f * sc - bOff)
            close()
        }
        val rightWing = Path().apply {
            moveTo(cx + 30f * sc, cy - 30f * sc - bOff)
            cubicTo(cx + 90f * sc, cy - 150f * sc - bOff - wFlap,
                cx + 180f * sc, cy - 110f * sc - wFlap,
                cx + 200f * sc, cy + 10f * sc - wFlap * 0.5f)
            cubicTo(cx + 180f * sc, cy + 0f * sc,
                cx + 120f * sc, cy - 15f * sc,
                cx + 70f * sc, cy - 10f * sc)
            cubicTo(cx + 50f * sc, cy - 15f * sc,
                cx + 35f * sc, cy - 22f * sc - bOff,
                cx + 30f * sc, cy - 30f * sc - bOff)
            close()
        }
        wingFill.shader = LinearGradient(cx - 220f * sc, cy - 150f * sc, cx + 200f * sc, cy + 20f * sc,
            Color.argb(200, 90, 0, 0), Color.argb(160, 50, 0, 0), Shader.TileMode.CLAMP)
        canvas.drawPath(leftWing, wingFill)
        canvas.drawPath(rightWing, wingFill)

        drawWingRibs(canvas, cx, cy, sc, bOff, wFlap)

        wingFill.shader = null
        wingFill.color = Color.argb(80, 140, 10, 0)
        canvas.drawPath(leftWing, wingFill)
        canvas.drawPath(rightWing, wingFill)
    }

    private fun drawWingRibs(canvas: Canvas, cx: Float, cy: Float, sc: Float, bOff: Float, wFlap: Float) {
        wingMembrane.color = Color.argb(80, 60, 0, 0)
        wingMembrane.strokeWidth = 1.5f * sc
        val ribsLeft = listOf(
            Pair(cx - 30f * sc to cy - 30f * sc - bOff, cx - 180f * sc to cy + 10f * sc + wFlap * 0.3f),
            Pair(cx - 40f * sc to cy - 50f * sc - bOff, cx - 200f * sc to cy - 20f * sc + wFlap * 0.4f),
            Pair(cx - 50f * sc to cy - 70f * sc - bOff, cx - 210f * sc to cy - 50f * sc + wFlap * 0.5f),
            Pair(cx - 60f * sc to cy - 90f * sc - bOff, cx - 195f * sc to cy - 80f * sc + wFlap * 0.6f),
        )
        for ((from, to) in ribsLeft) {
            canvas.drawLine(from.first, from.second, to.first, to.second, wingMembrane)
        }
        val ribsRight = listOf(
            Pair(cx + 30f * sc to cy - 30f * sc - bOff, cx + 170f * sc to cy - 5f * sc - wFlap * 0.3f),
            Pair(cx + 40f * sc to cy - 55f * sc - bOff, cx + 195f * sc to cy - 40f * sc - wFlap * 0.4f),
            Pair(cx + 50f * sc to cy - 80f * sc - bOff, cx + 185f * sc to cy - 70f * sc - wFlap * 0.5f),
            Pair(cx + 60f * sc to cy - 100f * sc - bOff, cx + 160f * sc to cy - 95f * sc - wFlap * 0.6f),
        )
        for ((from, to) in ribsRight) {
            canvas.drawLine(from.first, from.second, to.first, to.second, wingMembrane)
        }
    }

    private fun drawHindLegs(canvas: Canvas, cx: Float, cy: Float, sc: Float) {
        drawHindLeg(canvas, cx - 40f * sc, cy + 60f * sc, sc, true)
        drawHindLeg(canvas, cx + 40f * sc, cy + 60f * sc, sc, false)
    }

    private fun drawHindLeg(canvas: Canvas, lx: Float, ly: Float, sc: Float, left: Boolean) {
        val dir = if (left) -1f else 1f
        bodyFill.shader = LinearGradient(lx, ly, lx + dir * 25f * sc, ly + 60f * sc,
            Color.argb(230, 150, 10, 0), Color.argb(200, 100, 0, 0), Shader.TileMode.CLAMP)
        val thigh = Path().apply {
            moveTo(lx - 14f * sc, ly)
            cubicTo(lx - 16f * sc, ly + 25f * sc, lx + dir * 20f * sc, ly + 30f * sc, lx + dir * 22f * sc, ly + 50f * sc)
            lineTo(lx + dir * 8f * sc, ly + 52f * sc)
            cubicTo(lx + dir * 5f * sc, ly + 32f * sc, lx + 8f * sc, ly + 25f * sc, lx + 10f * sc, ly)
            close()
        }
        canvas.drawPath(thigh, bodyFill)
        val foot = Path().apply {
            moveTo(lx + dir * 22f * sc, ly + 50f * sc)
            lineTo(lx + dir * 28f * sc, ly + 70f * sc)
            lineTo(lx - dir * 5f * sc, ly + 72f * sc)
            lineTo(lx + dir * 8f * sc, ly + 52f * sc)
            close()
        }
        bodyFill.shader = LinearGradient(lx, ly + 50f * sc, lx, ly + 72f * sc,
            Color.argb(220, 120, 5, 0), Color.argb(200, 60, 0, 0), Shader.TileMode.CLAMP)
        canvas.drawPath(foot, bodyFill)
        drawFootClaws(canvas, lx, ly, sc, dir, 70f)
    }

    private fun drawFootClaws(canvas: Canvas, lx: Float, ly: Float, sc: Float, dir: Float, yOff: Float) {
        clawPaint.shader = null
        clawPaint.color = Color.argb(240, 220, 200, 160)
        for (i in -1..2) {
            val cx2 = lx + (i * dir * 8f - dir * 5f) * sc
            val cy2 = ly + yOff * sc
            val claw = Path().apply {
                moveTo(cx2, cy2)
                lineTo(cx2 + dir * 10f * sc, cy2 + 12f * sc)
                lineTo(cx2 + dir * 4f * sc, cy2 + 2f * sc)
                close()
            }
            canvas.drawPath(claw, clawPaint)
        }
    }

    private fun drawBody(canvas: Canvas, cx: Float, cy: Float, sc: Float, bOff: Float) {
        bodyFill.shader = RadialGradient(cx - 15f * sc, cy, 75f * sc,
            intArrayOf(Color.argb(240, 200, 30, 0), Color.argb(230, 150, 10, 0), Color.argb(220, 90, 0, 0)),
            floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        val body = Path().apply {
            moveTo(cx - 55f * sc, cy - 20f * sc - bOff)
            cubicTo(cx - 70f * sc, cy - 50f * sc - bOff, cx - 50f * sc, cy - 70f * sc - bOff, cx, cy - 65f * sc - bOff)
            cubicTo(cx + 50f * sc, cy - 60f * sc - bOff, cx + 65f * sc, cy - 40f * sc - bOff, cx + 55f * sc, cy)
            cubicTo(cx + 45f * sc, cy + 45f * sc, cx + 20f * sc, cy + 65f * sc + bOff, cx, cy + 70f * sc + bOff)
            cubicTo(cx - 25f * sc, cy + 65f * sc + bOff, cx - 50f * sc, cy + 45f * sc, cx - 55f * sc, cy + 15f * sc)
            cubicTo(cx - 58f * sc, cy, cx - 58f * sc, cy - 10f * sc, cx - 55f * sc, cy - 20f * sc - bOff)
            close()
        }
        canvas.drawPath(body, bodyFill)
        bodyStroke.strokeWidth = 2.5f * sc
        canvas.drawPath(body, bodyStroke)
        drawBodyScales(canvas, cx, cy, sc, bOff)
        drawDorsalRidge(canvas, cx, cy, sc, bOff)
    }

    private fun drawBodyScales(canvas: Canvas, cx: Float, cy: Float, sc: Float, bOff: Float) {
        val rows = 5; val cols = 5
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val ox = if (r % 2 == 0) 0f else 13f * sc
                val sx = cx - 40f * sc + c * 18f * sc + ox
                val sy = cy - 30f * sc + r * 22f * sc - bOff * 0.4f
                val sw = 9f * sc; val sh = 6f * sc
                scalePaint.shader = RadialGradient(sx, sy - sh, sw,
                    Color.argb(180, 220 - r * 15, 40 + c * 5, 0),
                    Color.argb(100, 100 - r * 8, 5, 0), Shader.TileMode.CLAMP)
                val scl = Path().apply {
                    moveTo(sx, sy - sh)
                    cubicTo(sx + sw, sy - sh * 0.3f, sx + sw, sy + sh * 0.5f, sx, sy + sh * 0.2f)
                    cubicTo(sx - sw, sy + sh * 0.5f, sx - sw, sy - sh * 0.3f, sx, sy - sh)
                }
                canvas.drawPath(scl, scalePaint)
                canvas.drawPath(scl, scaleStroke)
            }
        }
    }

    private fun drawDorsalRidge(canvas: Canvas, cx: Float, cy: Float, sc: Float, bOff: Float) {
        ridgePaint.color = Color.argb(230, 220, 80, 0)
        val ridgePoints = listOf(
            cx - 10f * sc to cy - 68f * sc - bOff,
            cx - 5f * sc to cy - 80f * sc - bOff,
            cx + 5f * sc to cy - 75f * sc - bOff,
            cx + 10f * sc to cy - 62f * sc - bOff,
            cx + 15f * sc to cy - 70f * sc - bOff,
            cx + 18f * sc to cy - 55f * sc - bOff,
            cx + 22f * sc to cy - 63f * sc - bOff,
            cx + 20f * sc to cy - 48f * sc - bOff,
        )
        for (i in 0 until ridgePoints.size - 1 step 2) {
            val (bx, by) = ridgePoints[i]
            val (tx, ty) = ridgePoints[i + 1]
            val ridge = Path().apply {
                moveTo(bx - 5f * sc, by)
                lineTo(tx, ty)
                lineTo(bx + 5f * sc, by)
                close()
            }
            canvas.drawPath(ridge, ridgePaint)
        }
    }

    private fun drawNeck(canvas: Canvas, cx: Float, cy: Float, sc: Float, bOff: Float) {
        bodyFill.shader = LinearGradient(cx - 25f * sc, cy - 65f * sc, cx + 25f * sc, cy - 130f * sc - bOff,
            Color.argb(230, 180, 20, 0), Color.argb(220, 120, 5, 0), Shader.TileMode.CLAMP)
        val neck = Path().apply {
            moveTo(cx - 28f * sc, cy - 62f * sc - bOff)
            cubicTo(cx - 35f * sc, cy - 95f * sc - bOff, cx - 20f * sc, cy - 120f * sc - bOff, cx - 5f * sc, cy - 140f * sc - bOff)
            lineTo(cx + 20f * sc, cy - 138f * sc - bOff)
            cubicTo(cx + 10f * sc, cy - 118f * sc - bOff, cx + 5f * sc, cy - 92f * sc - bOff, cx + 15f * sc, cy - 62f * sc - bOff)
            close()
        }
        canvas.drawPath(neck, bodyFill)
        bodyStroke.strokeWidth = 1.5f * sc
        canvas.drawPath(neck, bodyStroke)
        drawNeckScutes(canvas, cx, cy, sc, bOff)
    }

    private fun drawNeckScutes(canvas: Canvas, cx: Float, cy: Float, sc: Float, bOff: Float) {
        val scutePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 240, 120, 20); style = Paint.Style.FILL
        }
        for (i in 0..3) {
            val t = i / 3f
            val sx = cx - 8f * sc + t * 10f * sc
            val sy = cy - 70f * sc - t * 60f * sc - bOff
            val scute = Path().apply {
                moveTo(sx, sy - 8f * sc)
                lineTo(sx + 7f * sc, sy)
                lineTo(sx, sy + 3f * sc)
                lineTo(sx - 7f * sc, sy)
                close()
            }
            canvas.drawPath(scute, scutePaint)
        }
    }

    private fun drawFrontArms(canvas: Canvas, cx: Float, cy: Float, sc: Float, bOff: Float) {
        drawLeftArm(canvas, cx, cy, sc, bOff)
        drawRightArm(canvas, cx, cy, sc, bOff)
    }

    private fun drawLeftArm(canvas: Canvas, cx: Float, cy: Float, sc: Float, bOff: Float) {
        val ax = cx - 50f * sc; val ay = cy - 5f * sc - bOff
        bodyFill.shader = LinearGradient(ax, ay, ax - 60f * sc, ay + 70f * sc,
            Color.argb(230, 160, 15, 0), Color.argb(210, 90, 0, 0), Shader.TileMode.CLAMP)
        val upper = Path().apply {
            moveTo(ax, ay)
            cubicTo(ax - 15f * sc, ay + 20f * sc, ax - 50f * sc, ay + 30f * sc, ax - 60f * sc, ay + 65f * sc)
            lineTo(ax - 40f * sc, ay + 68f * sc)
            cubicTo(ax - 30f * sc, ay + 35f * sc, ax + 5f * sc, ay + 22f * sc, ax + 15f * sc, ay + 2f * sc)
            close()
        }
        canvas.drawPath(upper, bodyFill)
        val forearm = Path().apply {
            moveTo(ax - 60f * sc, ay + 65f * sc)
            cubicTo(ax - 70f * sc, ay + 85f * sc, ax - 50f * sc, ay + 110f * sc, ax - 35f * sc, ay + 120f * sc)
            lineTo(ax - 25f * sc, ay + 112f * sc)
            cubicTo(ax - 38f * sc, ay + 102f * sc, ax - 52f * sc, ay + 80f * sc, ax - 40f * sc, ay + 62f * sc)
            close()
        }
        bodyFill.shader = LinearGradient(ax - 60f * sc, ay + 65f * sc, ax - 35f * sc, ay + 120f * sc,
            Color.argb(220, 140, 10, 0), Color.argb(200, 80, 0, 0), Shader.TileMode.CLAMP)
        canvas.drawPath(forearm, bodyFill)
        drawControllerHand(canvas, ax - 35f * sc, ay + 118f * sc, sc)
    }

    private fun drawRightArm(canvas: Canvas, cx: Float, cy: Float, sc: Float, bOff: Float) {
        val ax = cx + 48f * sc; val ay = cy - 10f * sc - bOff
        bodyFill.shader = LinearGradient(ax, ay, ax + 50f * sc, ay + 60f * sc,
            Color.argb(230, 160, 15, 0), Color.argb(210, 90, 0, 0), Shader.TileMode.CLAMP)
        val upper = Path().apply {
            moveTo(ax, ay)
            cubicTo(ax + 15f * sc, ay + 18f * sc, ax + 45f * sc, ay + 25f * sc, ax + 50f * sc, ay + 58f * sc)
            lineTo(ax + 33f * sc, ay + 60f * sc)
            cubicTo(ax + 28f * sc, ay + 28f * sc, ax - 5f * sc, ay + 20f * sc, ax - 10f * sc, ay + 2f * sc)
            close()
        }
        canvas.drawPath(upper, bodyFill)
        val forearm = Path().apply {
            moveTo(ax + 50f * sc, ay + 58f * sc)
            cubicTo(ax + 58f * sc, ay + 78f * sc, ax + 42f * sc, ay + 100f * sc, ax + 28f * sc, ay + 108f * sc)
            lineTo(ax + 20f * sc, ay + 100f * sc)
            cubicTo(ax + 32f * sc, ay + 93f * sc, ax + 45f * sc, ay + 72f * sc, ax + 34f * sc, ay + 55f * sc)
            close()
        }
        bodyFill.shader = LinearGradient(ax + 50f * sc, ay + 58f * sc, ax + 28f * sc, ay + 108f * sc,
            Color.argb(220, 130, 10, 0), Color.argb(200, 75, 0, 0), Shader.TileMode.CLAMP)
        canvas.drawPath(forearm, bodyFill)
        drawOpenClaw(canvas, ax + 25f * sc, ay + 106f * sc, sc)
    }

    private fun drawOpenClaw(canvas: Canvas, hx: Float, hy: Float, sc: Float) {
        clawPaint.color = Color.argb(240, 200, 180, 140)
        for (i in 0..3) {
            val angle = (i * 20f - 30f) * (Math.PI / 180f).toFloat()
            val cx2 = hx + cos(angle) * 12f * sc
            val cy2 = hy + sin(angle) * 12f * sc
            val claw = Path().apply {
                moveTo(cx2, cy2)
                lineTo(cx2 + cos(angle) * 14f * sc, cy2 + sin(angle) * 14f * sc)
                lineTo(cx2 + cos(angle + 0.4f) * 8f * sc, cy2 + sin(angle + 0.4f) * 8f * sc)
                close()
            }
            canvas.drawPath(claw, clawPaint)
        }
    }

    private fun drawControllerHand(canvas: Canvas, hx: Float, hy: Float, sc: Float) {
        val cw = 55f * sc; val ch = 30f * sc
        val clx = hx - cw * 0.5f; val cly = hy - ch * 0.3f

        clawPaint.color = Color.argb(240, 200, 180, 140)
        for (i in -1..2) {
            val fx = hx + i * 12f * sc - 10f * sc
            val fingerPath = Path().apply {
                moveTo(fx, cly + ch + 4f * sc)
                lineTo(fx - 5f * sc, cly + ch + 22f * sc)
                lineTo(fx + 5f * sc, cly + ch + 22f * sc)
                close()
            }
            canvas.drawPath(fingerPath, clawPaint)
        }
        val thumbPath = Path().apply {
            moveTo(clx - 8f * sc, cly + ch * 0.4f)
            lineTo(clx - 20f * sc, cly + ch * 0.7f)
            lineTo(clx - 10f * sc, cly + ch)
            close()
        }
        canvas.drawPath(thumbPath, clawPaint)

        controllerFill.shader = LinearGradient(clx, cly, clx, cly + ch,
            Color.argb(240, 35, 35, 50), Color.argb(240, 18, 18, 30), Shader.TileMode.CLAMP)
        val ctrl = Path().apply {
            moveTo(clx + 10f * sc, cly)
            cubicTo(clx - 2f * sc, cly, clx - 15f * sc, cly + 5f * sc, clx - 20f * sc, cly + ch * 0.4f)
            cubicTo(clx - 22f * sc, cly + ch * 0.6f, clx - 18f * sc, cly + ch, clx - 5f * sc, cly + ch)
            lineTo(clx + cw + 5f * sc, cly + ch)
            cubicTo(clx + cw + 18f * sc, cly + ch, clx + cw + 22f * sc, cly + ch * 0.6f, clx + cw + 20f * sc, cly + ch * 0.4f)
            cubicTo(clx + cw + 15f * sc, cly + 5f * sc, clx + cw + 2f * sc, cly, clx + cw - 10f * sc, cly)
            close()
        }
        canvas.drawPath(ctrl, controllerFill)
        canvas.drawPath(ctrl, controllerStroke)

        controllerFill.shader = RadialGradient(clx + cw * 0.5f, cly + ch * 0.5f, cw * 0.4f,
            Color.argb(80, 80, 80, 120), Color.TRANSPARENT, Shader.TileMode.CLAMP)
        canvas.drawPath(ctrl, controllerFill)

        drawControllerDetails(canvas, clx, cly, cw, ch, sc)
    }

    private fun drawControllerDetails(canvas: Canvas, clx: Float, cly: Float, cw: Float, ch: Float, sc: Float) {
        val midX = clx + cw * 0.5f
        val midY = cly + ch * 0.5f

        val dpadX = clx + 13f * sc; val dpadY = midY + 2f * sc; val dr = 5f * sc
        controllerDetail.color = Color.argb(220, 50, 50, 70)
        canvas.drawRect(RectF(dpadX - dr * 0.4f, dpadY - dr, dpadX + dr * 0.4f, dpadY + dr), controllerDetail)
        canvas.drawRect(RectF(dpadX - dr, dpadY - dr * 0.4f, dpadX + dr, dpadY + dr * 0.4f), controllerDetail)

        val btnX = clx + cw - 13f * sc; val btnY = midY + 1f * sc; val br = 3f * sc
        val btnColors = listOf(Color.argb(220, 50, 200, 50), Color.argb(220, 200, 50, 50),
            Color.argb(220, 50, 100, 220), Color.argb(220, 220, 200, 50))
        val btnPos = listOf(0f to -6f, -6f to 0f, 6f to 0f, 0f to 6f)
        for (i in 0..3) {
            val (dx, dy) = btnPos[i]
            controllerBtn.color = btnColors[i]
            canvas.drawCircle(btnX + dx * sc, btnY + dy * sc, br, controllerBtn)
        }

        val lStickX = clx + 22f * sc; val lStickY = midY - 5f * sc; val sr = 5f * sc
        controllerDetail.color = Color.argb(180, 40, 40, 55)
        canvas.drawCircle(lStickX, lStickY, sr, controllerDetail)
        controllerDetail.color = Color.argb(200, 60, 60, 80)
        canvas.drawCircle(lStickX, lStickY, sr * 0.65f, controllerDetail)

        val rStickX = clx + cw - 22f * sc; val rStickY = midY + 5f * sc
        controllerDetail.color = Color.argb(180, 40, 40, 55)
        canvas.drawCircle(rStickX, rStickY, sr, controllerDetail)
        controllerDetail.color = Color.argb(200, 60, 60, 80)
        canvas.drawCircle(rStickX, rStickY, sr * 0.65f, controllerDetail)

        val homePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 200, 50, 50); style = Paint.Style.FILL
        }
        canvas.drawCircle(midX, midY - 3f * sc, 3.5f * sc, homePaint)

        val shoulderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(150, 55, 55, 75); style = Paint.Style.FILL
        }
        canvas.drawRoundRect(RectF(clx + 2f * sc, cly - 5f * sc, clx + 18f * sc, cly + 1f * sc), 3f, 3f, shoulderPaint)
        canvas.drawRoundRect(RectF(clx + cw - 18f * sc, cly - 5f * sc, clx + cw - 2f * sc, cly + 1f * sc), 3f, 3f, shoulderPaint)
    }

    private fun drawHead(canvas: Canvas, cx: Float, cy: Float, sc: Float, bOff: Float) {
        val hx = cx + 5f * sc; val hy = cy - 165f * sc - bOff

        val headGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(hx, hy, 55f * sc,
                Color.argb(60, 255, 80, 0), Color.TRANSPARENT, Shader.TileMode.CLAMP)
            style = Paint.Style.FILL
        }
        canvas.drawCircle(hx, hy, 55f * sc, headGlow)

        bodyFill.shader = RadialGradient(hx - 10f * sc, hy, 42f * sc,
            intArrayOf(Color.argb(240, 210, 40, 0), Color.argb(235, 165, 15, 0), Color.argb(230, 110, 5, 0)),
            floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        val skull = Path().apply {
            moveTo(hx - 30f * sc, hy - 15f * sc)
            cubicTo(hx - 35f * sc, hy - 35f * sc, hx - 15f * sc, hy - 50f * sc, hx + 15f * sc, hy - 45f * sc)
            cubicTo(hx + 38f * sc, hy - 40f * sc, hx + 42f * sc, hy - 20f * sc, hx + 35f * sc, hy)
            cubicTo(hx + 30f * sc, hy + 15f * sc, hx + 50f * sc, hy + 20f * sc, hx + 55f * sc, hy + 35f * sc)
            cubicTo(hx + 48f * sc, hy + 45f * sc, hx + 20f * sc, hy + 48f * sc, hx, hy + 45f * sc)
            cubicTo(hx - 20f * sc, hy + 42f * sc, hx - 38f * sc, hy + 30f * sc, hx - 42f * sc, hy + 15f * sc)
            cubicTo(hx - 45f * sc, hy, hx - 38f * sc, hy - 8f * sc, hx - 30f * sc, hy - 15f * sc)
            close()
        }
        canvas.drawPath(skull, bodyFill)
        bodyStroke.strokeWidth = 2f * sc
        canvas.drawPath(skull, bodyStroke)

        bodyFill.shader = LinearGradient(hx + 35f * sc, hy, hx + 90f * sc, hy + 40f * sc,
            Color.argb(235, 185, 25, 0), Color.argb(220, 130, 8, 0), Shader.TileMode.CLAMP)
        val snout = Path().apply {
            moveTo(hx + 35f * sc, hy)
            cubicTo(hx + 60f * sc, hy - 10f * sc, hx + 88f * sc, hy, hx + 92f * sc, hy + 15f * sc)
            cubicTo(hx + 90f * sc, hy + 30f * sc, hx + 70f * sc, hy + 40f * sc, hx + 55f * sc, hy + 38f * sc)
            cubicTo(hx + 50f * sc, hy + 45f * sc, hx + 25f * sc, hy + 48f * sc, hx + 10f * sc, hy + 45f * sc)
            cubicTo(hx + 18f * sc, hy + 35f * sc, hx + 28f * sc, hy + 20f * sc, hx + 35f * sc, hy)
        }
        canvas.drawPath(snout, bodyFill)
        bodyStroke.strokeWidth = 1.8f * sc
        canvas.drawPath(snout, bodyStroke)

        drawJaw(canvas, hx, hy, sc)
        drawHeadScales(canvas, hx, hy, sc)
        drawHorns(canvas, hx, hy, sc)
        drawEyes(canvas, hx, hy, sc)
        drawNostrils(canvas, hx, hy, sc)
        drawFangs(canvas, hx, hy, sc)
    }

    private fun drawJaw(canvas: Canvas, hx: Float, hy: Float, sc: Float) {
        bodyFill.shader = LinearGradient(hx + 30f * sc, hy + 45f * sc, hx + 80f * sc, hy + 65f * sc,
            Color.argb(220, 150, 10, 0), Color.argb(200, 100, 0, 0), Shader.TileMode.CLAMP)
        val jaw = Path().apply {
            moveTo(hx + 30f * sc, hy + 45f * sc)
            cubicTo(hx + 55f * sc, hy + 50f * sc, hx + 80f * sc, hy + 48f * sc, hx + 90f * sc, hy + 38f * sc)
            lineTo(hx + 88f * sc, hy + 55f * sc)
            cubicTo(hx + 78f * sc, hy + 65f * sc, hx + 55f * sc, hy + 68f * sc, hx + 30f * sc, hy + 60f * sc)
            close()
        }
        canvas.drawPath(jaw, bodyFill)
        bodyStroke.strokeWidth = 1.5f * sc
        canvas.drawPath(jaw, bodyStroke)
    }

    private fun drawHeadScales(canvas: Canvas, hx: Float, hy: Float, sc: Float) {
        val hsc = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL; color = Color.argb(120, 240, 100, 10)
        }
        val positions = listOf(
            hx - 10f * sc to hy - 20f * sc, hx + 5f * sc to hy - 28f * sc,
            hx - 20f * sc to hy - 5f * sc, hx + 15f * sc to hy - 10f * sc,
            hx - 15f * sc to hy + 10f * sc, hx + 5f * sc to hy + 8f * sc,
        )
        for ((px, py) in positions) {
            canvas.drawOval(RectF(px - 5f * sc, py - 4f * sc, px + 5f * sc, py + 4f * sc), hsc)
        }
    }

    private fun drawHorns(canvas: Canvas, hx: Float, hy: Float, sc: Float) {
        hornPaint.shader = LinearGradient(hx - 20f * sc, hy - 45f * sc, hx - 25f * sc, hy - 90f * sc,
            Color.argb(240, 220, 120, 20), Color.argb(200, 100, 40, 0), Shader.TileMode.CLAMP)
        val leftHorn = Path().apply {
            moveTo(hx - 12f * sc, hy - 44f * sc)
            cubicTo(hx - 20f * sc, hy - 62f * sc, hx - 30f * sc, hy - 75f * sc, hx - 22f * sc, hy - 92f * sc)
            lineTo(hx - 16f * sc, hy - 88f * sc)
            cubicTo(hx - 22f * sc, hy - 74f * sc, hx - 14f * sc, hy - 60f * sc, hx - 5f * sc, hy - 42f * sc)
            close()
        }
        canvas.drawPath(leftHorn, hornPaint)
        hornPaint.shader = LinearGradient(hx + 18f * sc, hy - 42f * sc, hx + 8f * sc, hy - 80f * sc,
            Color.argb(240, 220, 120, 20), Color.argb(200, 100, 40, 0), Shader.TileMode.CLAMP)
        val rightHorn = Path().apply {
            moveTo(hx + 18f * sc, hy - 42f * sc)
            cubicTo(hx + 22f * sc, hy - 60f * sc, hx + 18f * sc, hy - 72f * sc, hx + 12f * sc, hy - 82f * sc)
            lineTo(hx + 18f * sc, hy - 80f * sc)
            cubicTo(hx + 25f * sc, hy - 70f * sc, hx + 30f * sc, hy - 57f * sc, hx + 28f * sc, hy - 40f * sc)
            close()
        }
        canvas.drawPath(rightHorn, hornPaint)

        val smallHornPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(200, 180, 90, 10); style = Paint.Style.FILL
        }
        val smallHornPath = Path().apply {
            moveTo(hx + 3f * sc, hy - 46f * sc)
            lineTo(hx + 8f * sc, hy - 60f * sc)
            lineTo(hx + 12f * sc, hy - 45f * sc)
            close()
        }
        canvas.drawPath(smallHornPath, smallHornPaint)
    }

    private fun drawEyes(canvas: Canvas, hx: Float, hy: Float, sc: Float) {
        val eyePositions = listOf(
            PointF(hx - 5f * sc, hy - 10f * sc),
            PointF(hx + 22f * sc, hy - 5f * sc),
        )
        eyeLeft.set(eyePositions[0])
        eyeRight.set(eyePositions[1])

        for (eye in eyePositions) {
            val er = 9f * sc
            eyeGlowPaint.shader = RadialGradient(eye.x, eye.y, er * 1.8f,
                Color.argb(100, 255, 180, 0), Color.TRANSPARENT, Shader.TileMode.CLAMP)
            canvas.drawCircle(eye.x, eye.y, er * 1.8f, eyeGlowPaint)

            val brow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(200, 60, 0, 0); style = Paint.Style.FILL
            }
            canvas.drawOval(RectF(eye.x - er * 1.2f, eye.y - er * 1.8f, eye.x + er * 1.2f, eye.y - er * 0.8f), brow)

            val isBlink = blinkAnim > 0.3f
            if (isBlink) {
                val blinkLid = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(230, 130, 5, 0); style = Paint.Style.FILL
                }
                canvas.drawOval(RectF(eye.x - er, eye.y - er * 0.4f, eye.x + er, eye.y + er * 0.4f), blinkLid)
                continue
            }

            val eyeBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(240, 10, 5, 0); style = Paint.Style.FILL
            }
            canvas.drawCircle(eye.x, eye.y, er, eyeBg)

            val angle = if (touchPoint.x >= 0) atan2(touchPoint.y - eye.y, touchPoint.x - eye.x) else 0.3f
            val maxOff = er * 0.35f
            val px = eye.x + cos(angle) * maxOff
            val py = eye.y + sin(angle) * maxOff

            eyeGlowPaint.shader = RadialGradient(px, py, er * 0.7f,
                Color.argb(240, 255, 200, 0), Color.argb(180, 200, 100, 0), Shader.TileMode.CLAMP)
            canvas.drawCircle(px, py, er * 0.72f, eyeGlowPaint)

            eyePupilPaint.color = Color.argb(255, 0, 0, 0)
            canvas.drawOval(RectF(px - er * 0.22f, py - er * 0.55f, px + er * 0.22f, py + er * 0.55f), eyePupilPaint)

            canvas.drawCircle(px - er * 0.25f, py - er * 0.3f, er * 0.15f, eyeShimmer)
            canvas.drawCircle(px + er * 0.2f, py - er * 0.15f, er * 0.08f, eyeShimmer)

            val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(160, 200, 80, 0); style = Paint.Style.STROKE; strokeWidth = 1.5f * sc
            }
            canvas.drawCircle(eye.x, eye.y, er, rimPaint)
        }
    }

    private fun drawNostrils(canvas: Canvas, hx: Float, hy: Float, sc: Float) {
        val nPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(200, 30, 0, 0); style = Paint.Style.FILL
        }
        canvas.drawOval(RectF(hx + 70f * sc, hy + 8f * sc, hx + 82f * sc, hy + 16f * sc), nPaint)
        canvas.drawOval(RectF(hx + 82f * sc, hy + 6f * sc, hx + 92f * sc, hy + 13f * sc), nPaint)
        if (breathAnim > 0.6f) {
            val vapPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(((breathAnim - 0.6f) / 0.4f * 60).toInt(), 200, 200, 220)
                style = Paint.Style.FILL
            }
            canvas.drawCircle(hx + 76f * sc, hy + 4f * sc, 5f * sc * breathAnim, vapPaint)
            canvas.drawCircle(hx + 87f * sc, hy + 2f * sc, 4f * sc * breathAnim, vapPaint)
        }
    }

    private fun drawFangs(canvas: Canvas, hx: Float, hy: Float, sc: Float) {
        val fangPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(240, 245, 240, 220); style = Paint.Style.FILL
        }
        for (i in 0..2) {
            val fx = hx + 42f * sc + i * 12f * sc
            val fang = Path().apply {
                moveTo(fx - 4f * sc, hy + 47f * sc)
                lineTo(fx, hy + 62f * sc)
                lineTo(fx + 4f * sc, hy + 47f * sc)
                close()
            }
            canvas.drawPath(fang, fangPaint)
        }
    }

    private fun drawFire(canvas: Canvas, cx: Float, cy: Float, sc: Float, bOff: Float) {
        val hx = cx + 95f * sc; val hy = cy - 150f * sc - bOff
        val rng = Random(System.currentTimeMillis() / 100)
        val layers = 10
        for (i in layers downTo 0) {
            val t = i.toFloat() / layers
            val jitter = (rng.nextFloat() - 0.5f) * 8f * sc
            val fw = (28f - t * 22f) * sc
            val fh = fw * 0.55f
            val fx = hx + t * 100f * sc
            val fy = hy + jitter * 0.3f
            val alpha = ((1f - t * 0.8f) * 220).toInt()
            val r = 255; val g = (20 + t * 180).toInt().coerceAtMost(230); val b = (t * 50).toInt()
            firePaint.color = Color.argb(alpha, r, g, b)
            canvas.drawOval(RectF(fx - fw, fy - fh, fx + fw, fy + fh), firePaint)
        }
        fireCorePaint.color = Color.argb(200, 255, 255, 180)
        canvas.drawCircle(hx + 5f * sc, hy, 8f * sc, fireCorePaint)
        fireCorePaint.color = Color.argb(120, 255, 255, 255)
        canvas.drawCircle(hx + 5f * sc, hy, 4f * sc, fireCorePaint)
    }

    private fun drawSmoke(canvas: Canvas, cx: Float, cy: Float, sc: Float, bOff: Float) {
        val hx = cx + 90f * sc; val hy = cy - 148f * sc - bOff
        for (i in 0..5) {
            val t = (smokeAnim + i * 0.16f) % 1f
            smokePaint.color = Color.argb(((1f - t) * 70).toInt(), 180, 180, 200)
            val sr = (6f + t * 22f) * sc
            canvas.drawCircle(hx + t * 40f * sc, hy - t * 30f * sc, sr, smokePaint)
        }
    }

    private fun drawRoarRings(canvas: Canvas, cx: Float, cy: Float, sc: Float, bOff: Float) {
        val hx = cx + 50f * sc; val hy = cy - 150f * sc - bOff
        for (ring in 0..2) {
            val t = ((reactionAnim + ring * 0.25f) % 1f)
            val roarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(((1f - t) * 160).toInt(), 255, 100, 0)
                style = Paint.Style.STROKE; strokeWidth = (3f - t * 2f) * sc
            }
            canvas.drawCircle(hx, hy, t * 80f * sc, roarPaint)
        }
    }

    private fun drawSparkles(canvas: Canvas, cx: Float, cy: Float, sc: Float, bOff: Float) {
        val hx = cx; val hy = cy - 80f * sc - bOff
        for (i in 0..11) {
            val angle = (i / 12f) * 2f * Math.PI.toFloat() + reactionAnim * 4f
            val dist = reactionAnim * 55f * sc
            val spk = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(((1f - reactionAnim) * 220).toInt(), 255, 220 - i * 10, 0)
                style = Paint.Style.FILL
            }
            canvas.drawCircle(hx + cos(angle) * dist, hy + sin(angle) * dist, (3.5f - reactionAnim * 2f) * sc, spk)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        breathAnimator.cancel(); fireAnimator.cancel(); wingAnimator.cancel(); tailAnimator.cancel()
    }
}
