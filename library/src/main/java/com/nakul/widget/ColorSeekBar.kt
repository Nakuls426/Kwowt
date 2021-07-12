package com.nakul.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.roundToInt


class ColorSeekBar : View {

    private var mColorSeeds = intArrayOf(
        -0x1000000, -0x66ff01, -0xffff01, -0xff0100, -0xff0001,
        -0x10000, -0xff01, -0x9a00, -0x100, -0x1, -0x1000000
    )
    private var mAlpha = 0
    private var mOnColorChangeLister: OnColorChangeListener? = null
    private var mContext: Context? = null
    private var mIsShowAlphaBar = false
    private var mIsShowColorBar = true
    private var mIsVertical = false
    private var mMovingColorBar = false
    private var mMovingAlphaBar = false
    private var mTransparentBitmap: Bitmap? = null
    private var mColorRect: RectF? = null
    private var mThumbHeight = 20
    private var mThumbRadius = 0f
    private var mBarHeight = 2
    private var mColorRectPaint: Paint? = null
    private var realLeft = 0
    private var realRight = 0
    private var mBarWidth = 0
    private var mMaxPosition = 0
    private var mAlphaRect = RectF()
    private var mColorBarPosition = 0
    private var mAlphaBarPosition = 0
    private var mDisabledColor = 0
    private var mBarMargin = 5
    private var mAlphaMinPosition = 0
    private var mAlphaMaxPosition = 255
    private var mBarRadius = 0

    private val mCachedColors: ArrayList<Int> = ArrayList()

    private var mColorsToInvoke = -1
    private var mInit = false
    private var mFirstDraw = true
    private var mShowThumb = true
    private var mOnInitDoneListener: OnInitDoneListener? = null

    private val colorPaint: Paint = Paint()
    private val alphaThumbGradientPaint: Paint = Paint()
    private val alphaBarPaint: Paint = Paint()
    private val mDisabledPaint: Paint = Paint()
    private val thumbGradientPaint: Paint = Paint()

    constructor(context: Context?) : super(context)  {
        init(context,null,0,0)
    }

    private fun init(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        applyStyle(context,attrs,defStyleAttr,defStyleRes)

    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context,attrs,0,0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context,attrs,defStyleAttr,0)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context,attrs,defStyleAttr,defStyleRes)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var mViewWidth = widthMeasureSpec
        var mViewHeight = heightMeasureSpec

        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)

        val barHeight = if (mIsShowAlphaBar && mIsShowColorBar) mBarHeight * 2 else mBarHeight
        val thumbHeight = if (mIsShowAlphaBar && mIsShowColorBar) mThumbHeight * 2 else mThumbHeight

        if (isVertical()) {
            if (widthSpecMode == MeasureSpec.AT_MOST || widthSpecMode == MeasureSpec.UNSPECIFIED) {
                mViewWidth = thumbHeight + barHeight + mBarMargin;
                setMeasuredDimension(mViewWidth, mViewHeight);
            }

        } else {
            if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
                mViewHeight = thumbHeight + barHeight + mBarMargin;
                setMeasuredDimension(mViewWidth, mViewHeight);
            }
        }
    }

    private fun applyStyle(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        mContext = context!!

        val typedArray = context.obtainStyledAttributes(attrs,R.styleable.ColorSeekBar,defStyleAttr,defStyleRes)
        val colorsId: Int = typedArray.getResourceId(R.styleable.ColorSeekBar_colorSeeds, 0)
        mMaxPosition = typedArray.getInteger(R.styleable.ColorSeekBar_maxPosition, 100)
        mColorBarPosition = typedArray.getInteger(R.styleable.ColorSeekBar_colorBarPosition, 0)
        mAlphaBarPosition = typedArray.getInteger(R.styleable.ColorSeekBar_alphaBarPosition, mAlphaMinPosition)
        mDisabledColor = typedArray.getInteger(R.styleable.ColorSeekBar_disabledColor, Color.GRAY)
        mIsVertical = typedArray.getBoolean(R.styleable.ColorSeekBar_isVertical, false)
        mIsShowAlphaBar = typedArray.getBoolean(R.styleable.ColorSeekBar_showAlphaBar, false)
        mIsShowColorBar = typedArray.getBoolean(R.styleable.ColorSeekBar_showColorBar, true)
        mShowThumb = typedArray.getBoolean(R.styleable.ColorSeekBar_showThumb, true)
        val backgroundColor: Int = typedArray.getColor(R.styleable.ColorSeekBar_bgColor, Color.TRANSPARENT)
        mBarHeight = typedArray.getDimension(R.styleable.ColorSeekBar_barHeight, dp2px(2F).toFloat()).toInt()
        mBarRadius = typedArray.getDimension(R.styleable.ColorSeekBar_barRadius, 10F).toInt()
        mThumbHeight =
            typedArray.getDimension(R.styleable.ColorSeekBar_thumbHeight, dp2px(30F).toFloat()).toInt()
        mBarMargin = typedArray.getDimension(R.styleable.ColorSeekBar_barMargin, dp2px(5F).toFloat()).toInt()
        typedArray.recycle()

        mDisabledPaint.isAntiAlias = true
        mDisabledPaint.color = mDisabledColor

        if (colorsId != 0) {
            mColorSeeds = getColorsById(colorsId)
        }

        setBackgroundColor(backgroundColor)
    }

    private fun getColorsById(colorsId: Int): IntArray {
        return if (isInEditMode) {
            val s = mContext!!.resources.getStringArray(colorsId)
            val colors = IntArray(s.size)
            for (j in s.indices) {
                colors[j] = Color.parseColor(s[j])
            }
            colors
        } else {
            val typedArray = mContext!!.resources.obtainTypedArray(colorsId)
            val colors = IntArray(typedArray.length())
            for (j in 0 until typedArray.length()) {
                colors[j] = typedArray.getColor(j, Color.BLACK)
            }
            typedArray.recycle()
            colors
        }
    }

    private fun init() {
        Log.d("","")
        //init size
        mThumbRadius = (mThumbHeight / 2).toFloat()
        val mPaddingSize = mThumbRadius.toInt()
        val viewBottom = height - paddingBottom - mPaddingSize
        val viewRight = width - paddingRight - mPaddingSize
        //init left right top bottom
        realLeft = paddingLeft + mPaddingSize
        realRight = if (mIsVertical) viewBottom else viewRight
        val realTop = paddingTop + mPaddingSize
        mBarWidth = realRight - realLeft

        //init rect
        mColorRect = RectF(
            realLeft.toFloat(), realTop.toFloat(), realRight.toFloat(),
            (realTop + mBarHeight).toFloat()
        )

        val mColorGradient = LinearGradient(0F, 0F, mColorRect!!.width(), 0F, mColorSeeds, null, Shader.TileMode.CLAMP)
        mColorRectPaint = Paint()
        mColorRectPaint!!.shader = mColorGradient
        mColorRectPaint!!.isAntiAlias = true
        cacheColors()
        setAlphaValue()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //   Logger.i("onSizeChanged")

        mTransparentBitmap = if (mIsVertical) {
            Bitmap.createBitmap(h, w, Bitmap.Config.ARGB_8888)
        } else {
            Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        }
        with(mTransparentBitmap) { this?.eraseColor(Color.TRANSPARENT) }
        init()
        mInit = true
        if (mColorsToInvoke != -1) {
            setColor(mColorsToInvoke)
        }
    }

     private fun cacheColors() {
        if (mBarWidth < 1) {
            return
        }
        mCachedColors.clear()
        for (i in 0..mMaxPosition) {
              mCachedColors.add(pickColor(i))
        }
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        if (mIsVertical) {
            canvas!!.rotate(-90F)

            canvas.translate((-height).toFloat(), 0F)
            canvas.scale(-1F, 1F, (height / 2).toFloat(), (width / 2).toFloat())
        }

        val color = if (isEnabled) getColor(false) else mDisabledColor

        val colorStartTransparent =
            Color.argb(mAlphaMaxPosition, Color.red(color), Color.green(color), Color.blue(color))
        val colorEndTransparent =
            Color.argb(mAlphaMinPosition, Color.red(color), Color.green(color), Color.blue(color))
        val toAlpha = intArrayOf(colorStartTransparent, colorEndTransparent)

        if (mIsShowColorBar) {
            val colorPosition = mColorBarPosition.toFloat() / mMaxPosition * mBarWidth
            colorPaint.isAntiAlias = true
            colorPaint.color = color
            //clear
            canvas!!.drawBitmap(mTransparentBitmap!!, 0F, 0F, null)

            //draw color bar
            canvas.drawRoundRect(
                mColorRect!!, mBarRadius.toFloat(), mBarRadius.toFloat(),
                (if (isEnabled) mColorRectPaint else mDisabledPaint)!!
            )
            //draw color bar thumb

            if (mShowThumb) {
                val thumbX = colorPosition + realLeft
                val thumbY = mColorRect!!.top + mColorRect!!.height() / 2
                canvas.drawCircle(thumbX, thumbY, (mBarHeight / 2 + 5).toFloat(), colorPaint)

                //draw color bar thumb radial gradient shader
                val thumbShader = RadialGradient(
                    thumbX,
                    thumbY,
                    mThumbRadius,
                    toAlpha,
                    null,
                    Shader.TileMode.MIRROR
                )
                thumbGradientPaint.isAntiAlias = true
                thumbGradientPaint.shader = thumbShader
                canvas.drawCircle(thumbX, thumbY, (mThumbHeight / 2).toFloat(), thumbGradientPaint)
            }
        }


        if (mIsShowAlphaBar) {
            //init rect
            mAlphaRect = if (mIsShowColorBar) {
                val top =
                    if (mIsShowColorBar) (mThumbHeight + mThumbRadius + mBarHeight + mBarMargin).toInt() else (mThumbHeight + mThumbRadius + mBarMargin).toInt()
                RectF(
                    realLeft.toFloat(), top.toFloat(), realRight.toFloat(),
                    (top + mBarHeight).toFloat()
                )
            } else {
                RectF(mColorRect)
            }

            //draw alpha bar
            alphaBarPaint.isAntiAlias = true
            val alphaBarShader =
                LinearGradient(0F, 0F, mAlphaRect.width(), 0F, toAlpha, null, Shader.TileMode.CLAMP)
            alphaBarPaint.shader = alphaBarShader
            canvas!!.drawRect(mAlphaRect, alphaBarPaint)

            //draw alpha bar thumb
            if (mShowThumb) {
                val alphaPosition =
                    (mAlphaBarPosition - mAlphaMinPosition).toFloat() / (mAlphaMaxPosition - mAlphaMinPosition) * mBarWidth
                val alphaThumbX = alphaPosition + realLeft
                val alphaThumbY = mAlphaRect.top + mAlphaRect.height() / 2
                canvas.drawCircle(
                    alphaThumbX,
                    alphaThumbY,
                    (mBarHeight / 2 + 5).toFloat(),
                    colorPaint
                )

                //draw alpha bar thumb radial gradient shader
                val alphaThumbShader = RadialGradient(
                    alphaThumbX,
                    alphaThumbY,
                    mThumbRadius,
                    toAlpha,
                    null,
                    Shader.TileMode.MIRROR
                )
                alphaThumbGradientPaint.isAntiAlias = true
                alphaThumbGradientPaint.shader = alphaThumbShader
                canvas.drawCircle(
                    alphaThumbX, alphaThumbY,
                    (mThumbHeight / 2).toFloat(), alphaThumbGradientPaint
                )
            }
        }

        if (mFirstDraw) {
            mOnColorChangeLister?.onColorChangeListener(
                mColorBarPosition,
                mAlphaBarPosition,
                getColor()
            )
            mFirstDraw = false
            mOnInitDoneListener?.done()
        }
        super.onDraw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isEnabled) {
            return true
        }
        val x = if (mIsVertical) event!!.y else event!!.x
        val y = if (mIsVertical) event.x else event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (mIsShowColorBar && mColorRect?.let { isOnBar(it, x, y) } == true) {
                mMovingColorBar = true
                val value = (x - realLeft) / mBarWidth * mMaxPosition
                setColorBarPosition(value.toInt())
            } else if (mIsShowAlphaBar && isOnBar(mAlphaRect, x, y)) {
                mMovingAlphaBar = true
            }
            MotionEvent.ACTION_MOVE -> {
                parent.requestDisallowInterceptTouchEvent(true)
                if (mMovingColorBar) {
                    val value = (x - realLeft) / mBarWidth * mMaxPosition
                    setColorBarPosition(value.toInt())
                } else if (mIsShowAlphaBar) {
                    if (mMovingAlphaBar) {
                        val value =
                            (x - realLeft) / mBarWidth.toFloat() * (mAlphaMaxPosition - mAlphaMinPosition) + mAlphaMinPosition
                        mAlphaBarPosition = value.toInt()
                        if (mAlphaBarPosition < mAlphaMinPosition) {
                            mAlphaBarPosition = mAlphaMinPosition
                        } else if (mAlphaBarPosition > mAlphaMaxPosition) {
                            mAlphaBarPosition = mAlphaMaxPosition
                        }
                        setAlphaValue()
                    }
                }
                if (mOnColorChangeLister != null && (mMovingAlphaBar || mMovingColorBar)) {
                    mOnColorChangeLister!!.onColorChangeListener(
                        mColorBarPosition,
                        mAlphaBarPosition,
                        getColor()
                    )
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                mMovingColorBar = false
                mMovingAlphaBar = false
            }
            else -> {
            }
        }
        return true
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
    }

    fun setAlphaMaxPosition(alphaMaxPosition: Int) {
        mAlphaMaxPosition = alphaMaxPosition
        if (mAlphaMaxPosition > 255) {
            mAlphaMaxPosition = 255
        } else if (mAlphaMaxPosition <= mAlphaMinPosition) {
            mAlphaMaxPosition = mAlphaMinPosition + 1
        }
        if (mAlphaBarPosition > mAlphaMinPosition) {
            mAlphaBarPosition = mAlphaMaxPosition
        }
        invalidate()
    }

    fun getAlphaMaxPosition(): Int {
        return mAlphaMaxPosition
    }

    fun setAlphaMinPosition(alphaMinPosition: Int) {
        mAlphaMinPosition = alphaMinPosition
        if (mAlphaMinPosition >= mAlphaMaxPosition) {
            mAlphaMinPosition = mAlphaMaxPosition - 1
        } else if (mAlphaMinPosition < 0) {
            mAlphaMinPosition = 0
        }
        if (mAlphaBarPosition < mAlphaMinPosition) {
            mAlphaBarPosition = mAlphaMinPosition
        }
        invalidate()
    }

    fun getAlphaMinPosition(): Int {
        return mAlphaMinPosition
    }


    private fun isOnBar(r: RectF, x: Float, y: Float): Boolean {
        return r.left - mThumbRadius < x && x < r.right + mThumbRadius && r.top - mThumbRadius < y && y < r.bottom + mThumbRadius
    }

    fun isFirstDraw(): Boolean {
        return mFirstDraw
    }

    private fun pickColor(value:Int) :Int = pickColor((value.toFloat() / mMaxPosition * mBarWidth))

    private fun pickColor(position: Float): Int {
        val unit = position / mBarWidth
        if (unit <= 0.0) {
            return mColorSeeds[0]
        }
        if (unit >= 1) {
            return mColorSeeds[mColorSeeds.size - 1]
        }
        var colorPosition: Float = unit * (mColorSeeds.size - 1)
        val i = colorPosition.toInt()
        colorPosition -= i.toFloat()
        val c0 = mColorSeeds[i]
        val c1 = mColorSeeds[i + 1]
        //         mAlpha = mix(Color.alpha(c0), Color.alpha(c1), colorPosition);
        val mRed: Int = mix(Color.red(c0), Color.red(c1), colorPosition)
        val mGreen: Int = mix(Color.green(c0), Color.green(c1), colorPosition)
        val mBlue: Int = mix(Color.blue(c0), Color.blue(c1), colorPosition)
        return Color.rgb(mRed, mGreen, mBlue)
    }

    private fun mix(start: Int, end: Int, colorPosition: Float): Int =  start + (colorPosition * (end - start)).roundToInt()

    fun getColor(): Int = getColor(mIsShowAlphaBar)

    private fun getColor(withAlpha: Boolean): Int {
        //pick mode
        if (mColorBarPosition >= mCachedColors.size) {
            val color: Int = pickColor(mColorBarPosition)
            return if (withAlpha) {
                color
            } else {
                Color.argb(getAlphaValue(), Color.red(color), Color.green(color), Color.blue(color))
            }
        }

        //cache mode
        val color = mCachedColors[mColorBarPosition]
        return if (withAlpha) {
            Color.argb(getAlphaValue(), Color.red(color), Color.green(color), Color.blue(color))
        } else color
    }

    fun getAlphaBarPosition(): Int = mAlphaBarPosition

    fun getAlphaValue(): Int =  mAlpha


    interface OnColorChangeListener {
        fun onColorChangeListener(colorBarPosition: Int, alphaBarPosition: Int, color: Int)
    }

    fun setOnColorChangeListener(onColorChangeListener: OnColorChangeListener?) {
        mOnColorChangeLister = onColorChangeListener
    }

    private fun dp2px(dpValue: Float): Int {
        val scale = mContext!!.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun setColorSeeds(colors: IntArray) {
        mColorSeeds = colors
        init()
        invalidate()
        if (mOnColorChangeLister != null) {
            mOnColorChangeLister!!.onColorChangeListener(
                mColorBarPosition,
                mAlphaBarPosition,
                getColor()
            )
        }
    }

    fun getColorIndexPosition(color: Int): Int {
        return mCachedColors.indexOf(
            Color.argb(
                255,
                Color.red(color),
                Color.green(color),
                Color.blue(color)
            )
        )
    }

    fun getColors(): List<Int?> {
        return mCachedColors
    }

    fun isShowAlphaBar(): Boolean {
        return mIsShowAlphaBar
    }

    private fun refreshLayoutParams() {
        layoutParams = layoutParams
    }

    private fun isVertical(): Boolean {
        return mIsVertical
    }

    fun setShowAlphaBar(show: Boolean) {
        mIsShowAlphaBar = show
        refreshLayoutParams()
        invalidate()
        if (mOnColorChangeLister != null) {
            mOnColorChangeLister!!.onColorChangeListener(
                mColorBarPosition,
                mAlphaBarPosition,
                getColor()
            )
        }
    }

    fun setBarHeight(dp: Float) {
        mBarHeight = dp2px(dp)
        refreshLayoutParams()
        invalidate()
    }

    fun setBarHeightPx(px: Int) {
        mBarHeight = px
        refreshLayoutParams()
        invalidate()
    }

    private fun setAlphaValue() {
        mAlpha = 255 - mAlphaBarPosition
    }

    private fun setAlphaValue(alpha: Int) {
        mAlpha = alpha;
        mAlphaBarPosition = 255 - mAlpha;
    }

    fun setAlphaBarPosition(position: Int)  = setPosition(mColorBarPosition, position)

    fun getMaxValue(): Int = mMaxPosition

    fun setMaxPosition(value: Int) {
        mMaxPosition = value
        invalidate()
        cacheColors()
    }

    fun setBarMargin(mBarMargin: Float) {
        this.mBarMargin = dp2px(mBarMargin)
        refreshLayoutParams()
        invalidate()
    }

    fun setBarMarginPx(mBarMargin: Int) {
        this.mBarMargin = mBarMargin
        refreshLayoutParams()
        invalidate()
    }

    private fun setColorBarPosition(value: Int) = setPosition(value,mAlphaBarPosition)

    fun setPosition(colorBarPos: Int, mAlphaBarPosition: Int) {
        mColorBarPosition = colorBarPos
        mColorBarPosition =
            if (mColorBarPosition > mMaxPosition) mMaxPosition else mColorBarPosition
        mColorBarPosition = if (mColorBarPosition < 0) 0 else mColorBarPosition
        this.mAlphaBarPosition = mAlphaBarPosition
        setAlphaValue()
        invalidate()
        mOnColorChangeLister?.onColorChangeListener(
            mColorBarPosition,
            mAlphaBarPosition,
            getColor()
        )
    }

    fun setOnInitDoneListener(listener: OnInitDoneListener?) {
        mOnInitDoneListener = listener
    }

    fun setColor(color: Int) {
        val withoutAlphaColor = Color.rgb(Color.red(color), Color.green(color), Color.blue(color))
        if (mInit) {
            val value = mCachedColors.indexOf(withoutAlphaColor)
            if (mIsShowAlphaBar) {
                setAlphaValue(Color.alpha(color))
            }
            setColorBarPosition(value)
        } else {
            mColorsToInvoke = color
        }
    }

    fun setThumbHeight(dp: Float) {
        mThumbHeight = dp2px(dp)
        mThumbRadius = (mThumbHeight / 2).toFloat()
        refreshLayoutParams()
        invalidate()
    }

    fun setThumbHeightPx(px: Int) {
        mThumbHeight = px
        mThumbRadius = (mThumbHeight / 2).toFloat()
        refreshLayoutParams()
        invalidate()
    }

    fun getBarHeight(): Int {
        return mBarHeight
    }

    fun getThumbHeight(): Int {
        return mThumbHeight
    }

    fun getBarMargin(): Int {
        return mBarMargin
    }

    fun getColorBarValue(): Float {
        return mColorBarPosition.toFloat()
    }

    interface OnInitDoneListener {
        fun done()
    }

    fun getColorBarPosition(): Int {
        return mColorBarPosition
    }

    fun getDisabledColor(): Int {
        return mDisabledColor
    }

    fun setDisabledColor(disabledColor: Int) {
        mDisabledColor = disabledColor
        mDisabledPaint.color = disabledColor
    }

    fun isShowThumb(): Boolean {
        return mShowThumb
    }

    fun setShowThumb(showThumb: Boolean) {
        mShowThumb = showThumb
        invalidate()
    }

    fun getBarRadius(): Int {
        return mBarRadius
    }

    /**
     * Set bar radius with px unit
     *
     * @param barRadiusInPx
     */
    fun setBarRadius(barRadiusInPx: Int) {
        mBarRadius = barRadiusInPx
        invalidate()
    }

    fun isIsShowColorBar(): Boolean {
        return mIsShowColorBar
    }

    fun setShowColorBar(isShowColorBar: Boolean) {
        mIsShowColorBar = isShowColorBar
        refreshLayoutParams()
        invalidate()
    }

}