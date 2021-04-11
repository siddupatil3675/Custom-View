package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var animateWidth = 0f
    private var sweepAngle = 0f

    private var btnBackgroundColor = 0
    private var btnTextColor = 0
    private var circleColor = 0

    private var circleRadius = 30
    private var rectF: RectF

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 65.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private var valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
                valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat()).apply {
                    duration = 1000
                    addUpdateListener { animator ->
                        animator.repeatCount = ValueAnimator.INFINITE
                        animator.repeatMode = ValueAnimator.RESTART
                        animateWidth = animator.animatedValue as Float
                        sweepAngle = animateWidth / widthSize * -360
                        invalidate()
                    }
                }
                disableViewDuringAnimation(this, valueAnimator)
                valueAnimator.start()

            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
                animateWidth = 0f
                sweepAngle = 0f
                invalidate()
            }
        }
    }


    init {
        isClickable = true
        buttonState = ButtonState.Completed
        rectF = RectF()
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            btnBackgroundColor = getColor(R.styleable.LoadingButton_btnBackGroundColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
            btnTextColor = getColor(R.styleable.LoadingButton_btnTextColor, 0)
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        canvas?.drawRect(0f, heightSize.toFloat(), widthSize.toFloat(), 0f, paint)

        if (buttonState == ButtonState.Completed) {
            paint.color = btnTextColor
            canvas?.drawText(resources.getString(R.string.button_name),
                    widthSize.toFloat() / 2,
                    heightSize.toFloat() / 1.7f,
                    paint)
        } else
            if (buttonState == ButtonState.Loading) {
                paint.color = btnBackgroundColor
                canvas?.drawRect(0f, heightSize.toFloat(), animateWidth, 0f, paint)

                paint.color = btnTextColor
                canvas?.drawText(resources.getString(R.string.button_loading),
                        widthSize.toFloat() / 2,
                        (heightSize.toFloat() / 1.7).toFloat(),
                        paint)

                paint.color = circleColor
                canvas?.drawArc(rectF,
                        0f,
                        sweepAngle,
                        true,
                        paint)
            }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
                MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)

        rectF.set(widthSize.toFloat() - (3 * circleRadius),
                heightSize.toFloat() / 2 - circleRadius,
                widthSize.toFloat() - circleRadius,
                heightSize.toFloat() / 2 + circleRadius)
    }

    fun loaderStatus(status: ButtonState) {
        buttonState = status
    }

    private fun disableViewDuringAnimation(view: View, animator: ValueAnimator) {
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                view.isEnabled = true
            }
        })
    }

}