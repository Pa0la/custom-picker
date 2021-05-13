package com.paola.example.custompicker


import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller
import java.util.*


class CustomPicker(context: Context, attrs: AttributeSet) : View(context, attrs), Runnable,
    ICustomPicker {

    /**
     * @see OnChangeListener.onScrollStateChanged
     */
    val SCROLL_STATE_IDLE = 0

    /**
     * @see OnChangeListener.onScrollStateChanged
     */
    val SCROLL_STATE_DRAGGING = 1

    /**
     * @see OnChangeListener.onScrollStateChanged
     */
    val SCROLL_STATE_SCROLLING = 2

    /**
     * @see .setItemAlign
     */
    val ALIGN_CENTER = 0

    /**
     * @see .setItemAlign
     */
    val ALIGN_LEFT = 1

    /**
     * @see .setItemAlign
     */
    val ALIGN_RIGHT = 2

    private var mHandler: Handler = Handler()

    private var mPaint: Paint? = null
    private var mScroller: Scroller? = null
    private var mTracker: VelocityTracker? = null

    /**
     * Determines whether the current scrolling animation is triggered by touchEvent or setSelectedItemPosition.
     * User added eventListeners will only be fired after touchEvents.
     */
    private var isTouchTriggered = false

    /**
     * 相关监听器
     *
     * @see OnhangeListener,OnItemSelectedListener
     */
    private var mOnItemSelectedListener: OnItemSelectedListener? = null
    private var mOnChangeListener: OnChangeListener? = null

    private var mRectDrawn: Rect? = null
    private var mRectIndicatorHead: Rect? = null
    private var mRectIndicatorFoot: Rect? = null
    private var mRectCurrentItem: Rect? = null

    private var mCamera: Camera? = null
    private var mMatrixRotate: Matrix? = null
    private var mMatrixDepth: Matrix? = null

    private var mData: MutableList<Any>

    /**
     * @see .setMaximumWidthText
     */
    private var mMaxWidthText: String? = null

    /**
     * @see .setVisibleItemCount
     */
    private var mVisibleItemCount = 0

    /**
     * @see .setVisibleItemCount
     */
    private var mDrawnItemCount: Int = 0

    private var mHalfDrawnItemCount = 0
    private var mTextMaxWidth = 0
    private var mTextMaxHeight: Int = 0

    /**
     * @see .setItemTextColor
     * @see .setSelectedItemTextColor
     */
    private var mItemTextColor = 0

    /**
     * @see .setItemTextColor
     * @see .setSelectedItemTextColor
     */
    private var mSelectedItemTextColor: Int = 0

    /**
     * @see .setItemTextSize
     */
    private var mItemTextSize = 0

    /**
     * @see .setIndicatorSize
     */
    private var mIndicatorSize = 0

    /**
     * @see .setIndicatorColor
     */
    private var mIndicatorColor = 0

    /**
     * @see .setCurtainColor
     */
    private var mCurtainColor = 0

    /**
     * @see .setItemSpace
     */
    private var mItemSpace = 0

    /**
     * @see .setItemAlign
     */
    private var mItemAlign = 0

    private var mItemHeight = 0
    private var mHalfItemHeight: Int = 0
    private var mHalfHeight = 0

    /**
     * @see .setSelectedItemPosition
     */
    private var mSelectedItemPosition = 0

    /**
     * @see .getCurrentItemPosition
     */
    private var mCurrentItemPosition = 0

    private var mMinFlingY = 0
    private var mMaxFlingY: Int = 0
    private var mMinimumVelocity = 50
    private var mMaximumVelocity: Int = 8000
    private var mCenterX = 0
    private var mCenterY: Int = 0
    private var mDrawnCenterX = 0
    private var mDrawnCenterY: Int = 0
    private var mScrollOffsetY = 0
    private var mTextMaxWidthPosition = 0
    private var mLastPointY = 0
    private var mDownPointY = 0
    private var mTouchSlop = 8

    /**
     * @see .setSameWidth
     */
    private var hasSameWidth = false

    /**
     * @see .setIndicator
     */
    private var hasIndicator = false

    /**
     * @see .setCurtain
     */
    private var hasCurtain = false

    /**
     * @see .setAtmospheric
     */
    private var hasAtmospheric = false

    /**
     * @see .setCyclic
     */
    private var isCyclic = false

    /**
     * @see .setCurved
     */
    private var isCurved = false

    private var isClick = false
    private var isForceFinishScroll = false

    /**
     * Font typeface path from assets
     */
    private var fontPath: String? = null

    interface OnItemSelectedListener {
        fun onItemSelected(picker: CustomPicker?, data: Any?, position: Int)
    }

    interface OnChangeListener {
        /**
         * Invoke when CustomPicker scroll stopped
         * CustomPicker will return a distance offset which between current scroll position and
         * initial position, this offset is a positive or a negative, positive means CustomPicker is
         * scrolling from bottom to top, negative means CustomPicker is scrolling from top to bottom
         *
         *
         * Distance offset which between current scroll position and initial position
         */
        fun onScrolled(offset: Int)

        /**
         * Invoke when CustomPicker scroll stopped
         * This method will be called when CustomPicker stop and return current selected item data's
         * position in list
         *
         * Current selected item data's position in list
         */
        fun onSelected(position: Int)

        /**
         *
         *
         * Invoke when CustomPicker's scroll state changed
         * The state of CustomPicker always between idle, dragging, and scrolling, this method will
         * be called when they switch
         *
         *
         * State of CustomPicker, only one of the following
         * [CustomPicker.SCROLL_STATE_IDLE]
         * Express CustomPicker in state of idle
         * [CustomPicker.SCROLL_STATE_DRAGGING]
         * Express CustomPicker in state of dragging
         * [CustomPicker.SCROLL_STATE_SCROLLING]
         * Express CustomPicker in state of scrolling
         */
        fun onScrollStateChanged(state: Int)
    }


    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomPicker,
            0, 0
        ).apply {

            try {
                val idData: Int = getResourceId(R.styleable.CustomPicker_custom_picker_data, 0)
                mData = Arrays.asList(
                    resources
                        .getStringArray(if (idData == 0) R.array.ArrayWeek else idData)
                )
                mItemTextSize = getDimensionPixelSize(
                    R.styleable.CustomPicker_custom_picker_item_text_size,
                    resources.getDimensionPixelSize(R.dimen.ItemTextSize)
                )
                mVisibleItemCount =
                    getInt(R.styleable.CustomPicker_custom_picker_visible_item_count, 7)
                mSelectedItemPosition =
                    getInt(R.styleable.CustomPicker_custom_picker_selected_item_position, 0)
                hasSameWidth = getBoolean(R.styleable.CustomPicker_custom_picker_same_width, false)
                mTextMaxWidthPosition =
                    getInt(R.styleable.CustomPicker_custom_picker_maximum_width_text_position, -1)
                mMaxWidthText = getString(R.styleable.CustomPicker_custom_picker_maximum_width_text)
                mSelectedItemTextColor =
                    getColor(R.styleable.CustomPicker_custom_picker_selected_item_text_color, -1)
                mItemTextColor =
                    getColor(R.styleable.CustomPicker_custom_picker_item_text_color, -0x777778)
                mItemSpace = getDimensionPixelSize(
                    R.styleable.CustomPicker_custom_picker_item_space,
                    resources.getDimensionPixelSize(R.dimen.ItemSpace)
                )
                isCyclic = getBoolean(R.styleable.CustomPicker_custom_picker_cyclic, false)
                hasIndicator = getBoolean(R.styleable.CustomPicker_custom_picker_indicator, false)
                mIndicatorColor =
                    getColor(R.styleable.CustomPicker_custom_picker_indicator_color, -0x11cccd)
                mIndicatorSize = getDimensionPixelSize(
                    R.styleable.CustomPicker_custom_picker_indicator_size,
                    resources.getDimensionPixelSize(R.dimen.IndicatorSize)
                )
                hasCurtain = getBoolean(R.styleable.CustomPicker_custom_picker_curtain, false)
                mCurtainColor =
                    getColor(R.styleable.CustomPicker_custom_picker_curtain_color, -0x77000001)
                hasAtmospheric =
                    getBoolean(R.styleable.CustomPicker_custom_picker_atmospheric, false)
                isCurved = getBoolean(R.styleable.CustomPicker_custom_picker_curved, false)
                mItemAlign = getInt(R.styleable.CustomPicker_custom_picker_item_align, ALIGN_CENTER)
                fontPath = getString(R.styleable.CustomPicker_custom_picker_font_path)
            } finally {
                recycle()
                // Update relevant parameters when the count of visible item changed
                updateVisibleItemCount()

                mPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG or Paint.LINEAR_TEXT_FLAG)
                mPaint!!.textSize = mItemTextSize.toFloat()

                if (fontPath != null) {
                    val typeface = Typeface.createFromAsset(context.assets, fontPath)
                    setTypeface(typeface)
                }
                // Update alignment of text
                updateItemTextAlign()

                // Correct sizes of text
                computeTextSize()

                mScroller = Scroller(getContext())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                    val conf = ViewConfiguration.get(getContext())
                    mMinimumVelocity = conf.scaledMinimumFlingVelocity
                    mMaximumVelocity = conf.scaledMaximumFlingVelocity
                    mTouchSlop = conf.scaledTouchSlop
                }
                mRectDrawn = Rect()

                mRectIndicatorHead = Rect()
                mRectIndicatorFoot = Rect()

                mRectCurrentItem = Rect()

                mCamera = Camera()

                mMatrixRotate = Matrix()
                mMatrixDepth = Matrix()
            }
        }
    }

    private fun updateVisibleItemCount() {
        if (mVisibleItemCount < 2) throw ArithmeticException("CustomPicker's visible item count can not be less than 2!")
        // Be sure count of visible item is odd number
        if (mVisibleItemCount % 2 == 0) mVisibleItemCount += 1
        mDrawnItemCount = mVisibleItemCount + 2
        mHalfDrawnItemCount = mDrawnItemCount / 2
    }

    private fun computeTextSize() {
        mTextMaxHeight = 0
        mTextMaxWidth = mTextMaxHeight
        if (hasSameWidth) {
            mTextMaxWidth = mPaint!!.measureText(mData!![0].toString()).toInt()
        } else if (isPosInRang(mTextMaxWidthPosition)) {
            mTextMaxWidth = mPaint!!.measureText(mData!![mTextMaxWidthPosition].toString()).toInt()
        } else if (!TextUtils.isEmpty(mMaxWidthText)) {
            mTextMaxWidth = mPaint!!.measureText(mMaxWidthText).toInt()
        } else {
            for (obj in mData!!) {
                val text = obj.toString()
                val width = mPaint!!.measureText(text).toInt()
                mTextMaxWidth = Math.max(mTextMaxWidth, width)
            }
        }
        val metrics = mPaint!!.fontMetrics
        mTextMaxHeight = (metrics.bottom - metrics.top).toInt()
    }

    private fun updateItemTextAlign() {
        when (mItemAlign) {
            ALIGN_LEFT -> mPaint!!.textAlign = Paint.Align.LEFT
            ALIGN_RIGHT -> mPaint!!.textAlign = Paint.Align.RIGHT
            else -> mPaint!!.textAlign = Paint.Align.CENTER
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val modeWidth = MeasureSpec.getMode(widthMeasureSpec)
        val modeHeight = MeasureSpec.getMode(heightMeasureSpec)
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec)

        // Correct sizes of original content
        var resultWidth = mTextMaxWidth
        var resultHeight = mTextMaxHeight * mVisibleItemCount + mItemSpace * (mVisibleItemCount - 1)

        // Correct view sizes again if curved is enable
        if (isCurved) {
            resultHeight = (2 * resultHeight / Math.PI).toInt()
        }

        // Consideration padding influence the view sizes
        resultWidth += paddingLeft + paddingRight
        resultHeight += paddingTop + paddingBottom

        // Consideration sizes of parent can influence the view sizes
        resultWidth = measureSize(modeWidth, sizeWidth, resultWidth)
        resultHeight = measureSize(modeHeight, sizeHeight, resultHeight)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    private fun measureSize(mode: Int, sizeExpect: Int, sizeActual: Int): Int {
        var realSize: Int
        if (mode == MeasureSpec.EXACTLY) {
            realSize = sizeExpect
        } else {
            realSize = sizeActual
            if (mode == MeasureSpec.AT_MOST) realSize = Math.min(realSize, sizeExpect)
        }
        return realSize
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        // Set content region
        mRectDrawn!![paddingLeft, paddingTop, width - paddingRight] = height - paddingBottom

        // Get the center coordinates of content region
        mCenterX = mRectDrawn!!.centerX()
        mCenterY = mRectDrawn!!.centerY()

        // Correct item drawn center
        computeDrawnCenter()
        mHalfHeight = mRectDrawn!!.height() / 2
        mItemHeight = mRectDrawn!!.height() / mVisibleItemCount
        mHalfItemHeight = mItemHeight / 2

        // Initialize fling max Y-coordinates
        computeFlingLimitY()

        // Correct region of indicator
        computeIndicatorRect()

        // Correct region of current select item
        computeCurrentItemRect()
    }

    private fun computeDrawnCenter() {
        mDrawnCenterX = when (mItemAlign) {
            ALIGN_LEFT -> mRectDrawn!!.left
            ALIGN_RIGHT -> mRectDrawn!!.right
            else -> mCenterX
        }
        mDrawnCenterY = (mCenterY - (mPaint!!.ascent() + mPaint!!.descent()) / 2).toInt()
    }

    private fun computeFlingLimitY() {
        val currentItemOffset = mSelectedItemPosition * mItemHeight
        mMinFlingY =
            if (isCyclic) Int.MIN_VALUE else -mItemHeight * (mData!!.size - 1) + currentItemOffset
        mMaxFlingY = if (isCyclic) Int.MAX_VALUE else currentItemOffset
    }

    private fun computeIndicatorRect() {
        if (!hasIndicator) return
        val halfIndicatorSize = mIndicatorSize / 2
        val indicatorHeadCenterY = mCenterY + mHalfItemHeight
        val indicatorFootCenterY = mCenterY - mHalfItemHeight
        mRectIndicatorHead!![mRectDrawn!!.left, indicatorHeadCenterY - halfIndicatorSize, mRectDrawn!!.right] =
            indicatorHeadCenterY + halfIndicatorSize
        mRectIndicatorFoot!![mRectDrawn!!.left, indicatorFootCenterY - halfIndicatorSize, mRectDrawn!!.right] =
            indicatorFootCenterY + halfIndicatorSize
    }

    private fun computeCurrentItemRect() {
        if (!hasCurtain && mSelectedItemTextColor == -1) return
        mRectCurrentItem!![mRectDrawn!!.left, mCenterY - mHalfItemHeight, mRectDrawn!!.right] =
            mCenterY + mHalfItemHeight
    }

    override fun onDraw(canvas: Canvas) {
        mOnChangeListener?.onScrolled(mScrollOffsetY)
        if (mData!!.size == 0) return
        val drawnDataStartPos = -mScrollOffsetY / mItemHeight - mHalfDrawnItemCount
        var drawnDataPos = drawnDataStartPos + mSelectedItemPosition
        var drawnOffsetPos = -mHalfDrawnItemCount
        while (drawnDataPos < drawnDataStartPos + mSelectedItemPosition + mDrawnItemCount) {
            var data = ""
            if (isCyclic) {
                var actualPos = drawnDataPos % mData!!.size
                actualPos = if (actualPos < 0) actualPos + mData!!.size else actualPos
                data = mData!![actualPos].toString()
            } else {
                if (isPosInRang(drawnDataPos)) data = mData!![drawnDataPos].toString()
            }
            mPaint!!.color = mItemTextColor
            mPaint!!.style = Paint.Style.FILL
            val mDrawnItemCenterY =
                mDrawnCenterY + drawnOffsetPos * mItemHeight + mScrollOffsetY % mItemHeight
            var distanceToCenter = 0
            if (isCurved) {
                // Correct ratio of item's drawn center to center
                val ratio = (mDrawnCenterY - Math.abs(mDrawnCenterY - mDrawnItemCenterY) -
                        mRectDrawn!!.top) * 1.0f / (mDrawnCenterY - mRectDrawn!!.top)

                // Correct unit
                var unit = 0
                if (mDrawnItemCenterY > mDrawnCenterY) unit =
                    1 else if (mDrawnItemCenterY < mDrawnCenterY) unit = -1
                var degree = -(1 - ratio) * 90 * unit
                if (degree < -90) degree = -90f
                if (degree > 90) degree = 90f
                distanceToCenter = computeSpace(degree.toInt())
                var transX = mCenterX
                when (mItemAlign) {
                    ALIGN_LEFT -> transX = mRectDrawn!!.left
                    ALIGN_RIGHT -> transX = mRectDrawn!!.right
                }
                val transY = mCenterY - distanceToCenter
                mCamera!!.save()
                mCamera!!.rotateX(degree)
                mCamera!!.getMatrix(mMatrixRotate)
                mCamera!!.restore()
                mMatrixRotate!!.preTranslate(-transX.toFloat(), -transY.toFloat())
                mMatrixRotate!!.postTranslate(transX.toFloat(), transY.toFloat())
                mCamera!!.save()
                mCamera!!.translate(0f, 0f, computeDepth(degree.toInt()).toFloat())
                mCamera!!.getMatrix(mMatrixDepth)
                mCamera!!.restore()
                mMatrixDepth!!.preTranslate(-transX.toFloat(), -transY.toFloat())
                mMatrixDepth!!.postTranslate(transX.toFloat(), transY.toFloat())
                mMatrixRotate!!.postConcat(mMatrixDepth)
            }
            if (hasAtmospheric) {
                var alpha = ((mDrawnCenterY - Math.abs(mDrawnCenterY - mDrawnItemCenterY)) *
                        1.0f / mDrawnCenterY * 255).toInt()
                alpha = if (alpha < 0) 0 else alpha
                mPaint!!.alpha = alpha
            }
            // Correct item's drawn centerY base on curved state
            val drawnCenterY = if (isCurved) mDrawnCenterY - distanceToCenter else mDrawnItemCenterY

            // Judges need to draw different color for current item or not
            if (mSelectedItemTextColor != -1) {
                canvas.save()
                if (isCurved) canvas.concat(mMatrixRotate)
                canvas.clipRect(mRectCurrentItem!!, Region.Op.DIFFERENCE)
                canvas.drawText(data, mDrawnCenterX.toFloat(), drawnCenterY.toFloat(), mPaint!!)
                canvas.restore()
                mPaint!!.color = mSelectedItemTextColor
                canvas.save()
                if (isCurved) canvas.concat(mMatrixRotate)
                canvas.clipRect(mRectCurrentItem!!)
                canvas.drawText(data, mDrawnCenterX.toFloat(), drawnCenterY.toFloat(), mPaint!!)
                canvas.restore()
            } else {
                canvas.save()
                canvas.clipRect(mRectDrawn!!)
                if (isCurved) canvas.concat(mMatrixRotate)
                canvas.drawText(data, mDrawnCenterX.toFloat(), drawnCenterY.toFloat(), mPaint!!)
                canvas.restore()
            }
            drawnDataPos++
            drawnOffsetPos++
        }
        // Need to draw curtain or not
        if (hasCurtain) {
            mPaint!!.color = mCurtainColor
            mPaint!!.style = Paint.Style.FILL
            canvas.drawRect(mRectCurrentItem!!, mPaint!!)
        }
        // Need to draw indicator or not
        if (hasIndicator) {
            mPaint!!.color = mIndicatorColor
            mPaint!!.style = Paint.Style.FILL
            canvas.drawRect(mRectIndicatorHead!!, mPaint!!)
            canvas.drawRect(mRectIndicatorFoot!!, mPaint!!)
        }
    }

    private fun isPosInRang(position: Int): Boolean {
        return position >= 0 && position < mData!!.size
    }

    private fun computeSpace(degree: Int): Int {
        return (Math.sin(Math.toRadians(degree.toDouble())) * mHalfHeight).toInt()
    }

    private fun computeDepth(degree: Int): Int {
        return (mHalfHeight - Math.cos(Math.toRadians(degree.toDouble())) * mHalfHeight).toInt()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isTouchTriggered = true
                if (null != parent) parent.requestDisallowInterceptTouchEvent(true)
                if (null == mTracker) mTracker = VelocityTracker.obtain() else mTracker!!.clear()
                mTracker!!.addMovement(event)
                if (!mScroller!!.isFinished) {
                    mScroller!!.abortAnimation()
                    isForceFinishScroll = true
                }
                run {
                    mLastPointY = event.y.toInt()
                    mDownPointY = mLastPointY
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (Math.abs(mDownPointY - event.y) < mTouchSlop) {
                    isClick = true
                } else {
                    isClick = false
                    mTracker!!.addMovement(event)
                    mOnChangeListener?.onScrollStateChanged(SCROLL_STATE_DRAGGING)

                    // Scroll CustomPicker's content
                    val move = event.y - mLastPointY
                    if (Math.abs(move) < 1) {
                        Log.d(CustomPicker::class.java.simpleName, " ACTION_MOVE")
                    } else {
                        mScrollOffsetY += move.toInt()
                        mLastPointY = event.y.toInt()
                        invalidate()
                    }

                }

            }
            MotionEvent.ACTION_UP -> {
                if (null != parent) parent.requestDisallowInterceptTouchEvent(false)
                if (isClick && !isForceFinishScroll) {
                    Log.d(CustomPicker::class.java.simpleName, " ACTION_UP")
                } else {
                    mTracker!!.addMovement(event)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) mTracker!!.computeCurrentVelocity(
                        1000,
                        mMaximumVelocity.toFloat()
                    ) else mTracker!!.computeCurrentVelocity(1000)

                    // Judges the CustomPicker is scroll or fling base on current velocity
                    isForceFinishScroll = false
                    val velocity = mTracker!!.yVelocity.toInt()
                    if (Math.abs(velocity) > mMinimumVelocity) {
                        mScroller!!.fling(
                            0,
                            mScrollOffsetY,
                            0,
                            velocity,
                            0,
                            0,
                            mMinFlingY,
                            mMaxFlingY
                        )
                        mScroller!!.finalY = mScroller!!.finalY +
                                computeDistanceToEndPoint(mScroller!!.finalY % mItemHeight)
                    } else {
                        mScroller!!.startScroll(
                            0, mScrollOffsetY, 0,
                            computeDistanceToEndPoint(mScrollOffsetY % mItemHeight)
                        )
                    }
                    // Correct coordinates
                    if (!isCyclic) if (mScroller!!.finalY > mMaxFlingY) mScroller!!.finalY =
                        mMaxFlingY else if (mScroller!!.finalY < mMinFlingY) mScroller!!.finalY =
                        mMinFlingY
                    mHandler.post(this)
                    if (null != mTracker) {
                        mTracker!!.recycle()
                        mTracker = null
                    }
                }

            }
            MotionEvent.ACTION_CANCEL -> {
                if (null != parent) parent.requestDisallowInterceptTouchEvent(false)
                if (null != mTracker) {
                    mTracker!!.recycle()
                    mTracker = null
                }
            }
        }
        return true
    }

    private fun computeDistanceToEndPoint(remainder: Int): Int {
        return if (Math.abs(remainder) > mHalfItemHeight) if (mScrollOffsetY < 0) -mItemHeight - remainder else mItemHeight - remainder else -remainder
    }

    override fun run() {
        if (null == mData || mData!!.size == 0) return
        if (mScroller!!.isFinished && !isForceFinishScroll) {
            if (mItemHeight == 0) return
            var position = (-mScrollOffsetY / mItemHeight + mSelectedItemPosition) % mData!!.size
            position = if (position < 0) position + mData!!.size else position
            mCurrentItemPosition = position
            if (null != mOnItemSelectedListener && isTouchTriggered)
                mOnItemSelectedListener!!.onItemSelected(
                    this,
                    mData!![position], position
                )
            if (null != mOnChangeListener && isTouchTriggered) {
                mOnChangeListener!!.onSelected(position)
                mOnChangeListener!!.onScrollStateChanged(SCROLL_STATE_IDLE)
            }
        }
        if (mScroller!!.computeScrollOffset()) {
            mOnChangeListener?.onScrollStateChanged(SCROLL_STATE_SCROLLING)
            mScrollOffsetY = mScroller!!.currY
            postInvalidate()
            mHandler.postDelayed(this, 16)
        }
    }

    override fun getVisibleItemCount(): Int {
        return mVisibleItemCount
    }

    override fun setVisibleItemCount(count: Int) {
        mVisibleItemCount = count
        updateVisibleItemCount()
        requestLayout()
    }

    override fun isCyclic(): Boolean {
        return isCyclic
    }

    override fun setCyclic(isCyclic: Boolean) {
        this.isCyclic = isCyclic
        computeFlingLimitY()
        invalidate()
    }

    override fun setOnItemSelectedListener(listener: OnItemSelectedListener?) {
        mOnItemSelectedListener = listener
    }


    override fun getSelectedItemPosition(): Int {
        return mSelectedItemPosition
    }

    override fun setSelectedItemPosition(position: Int) {
        setSelectedItemPosition(position, true)
    }

    fun setSelectedItemPosition(position: Int, animated: Boolean) {
        var position = position
        isTouchTriggered = false
        if (animated && mScroller!!.isFinished) { // We go non-animated regardless of "animated" parameter if scroller is in motion
            val length: Int = getData().size
            var itemDifference = position - mCurrentItemPosition
            if (itemDifference == 0) return
            if (isCyclic && Math.abs(itemDifference) > length / 2) { // Find the shortest path if it's cyclic
                itemDifference += if (itemDifference > 0) -length else length
            }
            mScroller!!.startScroll(0, mScroller!!.currY, 0, -itemDifference * mItemHeight)
            mHandler.post(this)
        } else {
            if (!mScroller!!.isFinished) mScroller!!.abortAnimation()
            position = Math.min(position, mData!!.size - 1)
            position = Math.max(position, 0)
            mSelectedItemPosition = position
            mCurrentItemPosition = position
            mScrollOffsetY = 0
            computeFlingLimitY()
            requestLayout()
            invalidate()
        }
    }

    override fun getCurrentItemPosition(): Int {
        return mCurrentItemPosition
    }

    override fun getData(): MutableList<Any> {
        return mData
    }

    override fun setData(data: MutableList<Any>?) {
        if (null == data) throw NullPointerException("CustomPicker's data can not be null!")
        mData = data

        if (mSelectedItemPosition > data.size - 1 || mCurrentItemPosition > data.size - 1) {
            mCurrentItemPosition = data.size - 1
            mSelectedItemPosition = mCurrentItemPosition
        } else {
            mSelectedItemPosition = mCurrentItemPosition
        }
        mScrollOffsetY = 0
        computeTextSize()
        computeFlingLimitY()
        requestLayout()
        invalidate()
    }

    override fun setSameWidth(hasSameWidth: Boolean) {
        this.hasSameWidth = hasSameWidth
        computeTextSize()
        requestLayout()
        invalidate()
    }

    override fun hasSameWidth(): Boolean {
        return hasSameWidth
    }

    override fun setOnChangeListener(listener: OnChangeListener?) {
        mOnChangeListener = listener
    }

    override fun getMaximumWidthText(): String? {
        return mMaxWidthText
    }

    override fun setMaximumWidthText(text: String?) {
        if (null == text) throw NullPointerException("Maximum width text can not be null!")
        mMaxWidthText = text
        computeTextSize()
        requestLayout()
        invalidate()
    }

    override fun getMaximumWidthTextPosition(): Int {
        return mTextMaxWidthPosition
    }

    override fun setMaximumWidthTextPosition(position: Int) {
        if (!isPosInRang(position)) throw ArrayIndexOutOfBoundsException(
            "Maximum width text Position must in [0, " +
                    mData!!.size + "), but current is " + position
        )
        mTextMaxWidthPosition = position
        computeTextSize()
        requestLayout()
        invalidate()
    }

    override fun getSelectedItemTextColor(): Int {
        return mSelectedItemTextColor
    }

    override fun setSelectedItemTextColor(color: Int) {
        mSelectedItemTextColor = color
        computeCurrentItemRect()
        invalidate()
    }

    override fun getItemTextColor(): Int {
        return mItemTextColor
    }

    override fun setItemTextColor(color: Int) {
        mItemTextColor = color
        invalidate()
    }

    override fun getItemTextSize(): Int {
        return mItemTextSize
    }

    override fun setItemTextSize(size: Int) {
        mItemTextSize = size
        mPaint!!.textSize = mItemTextSize.toFloat()
        computeTextSize()
        requestLayout()
        invalidate()
    }

    override fun getItemSpace(): Int {
        return mItemSpace
    }

    override fun setItemSpace(space: Int) {
        mItemSpace = space
        requestLayout()
        invalidate()
    }

    override fun setIndicator(hasIndicator: Boolean) {
        this.hasIndicator = hasIndicator
        computeIndicatorRect()
        invalidate()
    }

    override fun hasIndicator(): Boolean {
        return hasIndicator
    }

    override fun getIndicatorSize(): Int {
        return mIndicatorSize
    }

    override fun setIndicatorSize(size: Int) {
        mIndicatorSize = size
        computeIndicatorRect()
        invalidate()
    }

    override fun getIndicatorColor(): Int {
        return mIndicatorColor
    }

    override fun setIndicatorColor(color: Int) {
        mIndicatorColor = color
        invalidate()
    }

    override fun setCurtain(hasCurtain: Boolean) {
        this.hasCurtain = hasCurtain
        computeCurrentItemRect()
        invalidate()
    }

    override fun hasCurtain(): Boolean {
        return hasCurtain
    }

    override fun getCurtainColor(): Int {
        return mCurtainColor
    }

    override fun setCurtainColor(color: Int) {
        mCurtainColor = color
        invalidate()
    }

    override fun setAtmospheric(hasAtmospheric: Boolean) {
        this.hasAtmospheric = hasAtmospheric
        invalidate()
    }

    override fun hasAtmospheric(): Boolean {
        return hasAtmospheric
    }

    override fun isCurved(): Boolean {
        return isCurved
    }

    override fun setCurved(isCurved: Boolean) {
        this.isCurved = isCurved
        requestLayout()
        invalidate()
    }

    override fun getItemAlign(): Int {
        return mItemAlign
    }

    override fun setItemAlign(align: Int) {
        mItemAlign = align
        updateItemTextAlign()
        computeDrawnCenter()
        invalidate()
    }

    override fun getTypeface(): Typeface? {
        return if (null != mPaint) mPaint!!.typeface else null
    }

    override fun setTypeface(tf: Typeface?) {
        if (null != mPaint) mPaint!!.typeface = tf
        computeTextSize()
        requestLayout()
        invalidate()
    }

}