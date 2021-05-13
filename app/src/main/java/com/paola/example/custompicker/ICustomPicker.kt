package com.paola.example.custompicker

import android.graphics.Typeface


interface ICustomPicker {

    /**
     * Get the count of current visible items in CustomPicker
     */
    fun getVisibleItemCount(): Int

    /**
     * Set the count of current visible items in CustomPicker
     * The count of current visible items in CustomPicker must greater than 1
     * Notice:count of current visible items in CustomPicker will always is an odd number, even you
     * can set an even number for it, it will be change to an odd number eventually
     * By default, the count of current visible items in CustomPicker is 7
     */
    fun setVisibleItemCount(count: Int)

    /**
     * Whether CustomPicker is cyclic or not
     */
    fun isCyclic(): Boolean

    /**
     * Set whether CustomPicker is cyclic or not
     * CustomPicker's items will be end to end and in an infinite loop if setCyclic true, and there
     * is no border whit scroll when CustomPicker in cyclic state
     */
    fun setCyclic(isCyclic: Boolean)


    fun setOnItemSelectedListener(listener: CustomPicker.OnItemSelectedListener?)

    /**
     * Get the position of current selected item in data source
     * Notice:The value by return will not change when CustomPicker scroll, this method will always
     * return the value which [.setSelectedItemPosition] set, the value this method
     * return will be changed if and only if call the
     * [.setSelectedItemPosition]
     * set a new value
     * If you only want to get the position of current selected item in data source, you can get it
     * through [OnItemSelectedListener] or call
     * [.getCurrentItemPosition] directly
     */
    fun getSelectedItemPosition(): Int

    /**
     * Set the position of current selected item in data source
     * Call this method and set a new value may be reinitialize the location of CustomPicker. For
     * example, you call this method after scroll the CustomPicker and set selected item position
     * with a new value, CustomPicker will clear the related parameters last scroll set and reset
     * series of data, and make the position 3 as a new starting point of CustomPicker, this behavior
     * maybe influenced some attribute you set last time, such as parameters of method in
     * [OnChangeListener] and
     * [OnItemSelectedListener], so you must always
     * consider the influence when you call this method set a new value
     * You should always set a value which greater than or equal to 0 and less than data source's
     * length
     * By default, position of current selected item in data source is 0
     */
    fun setSelectedItemPosition(position: Int)

    /**
     * Get the position of current selected item in data source
     * The difference between [.getSelectedItemPosition], the value this method return will
     * change by CustomPicker scrolled
     */
    fun getCurrentItemPosition(): Int

    /**
     * Get data source of CustomPicker
     */
    fun getData(): MutableList<Any>?

    /**
     * Set data source of CustomPicker
     * The data source can be any type, CustomPicker will change the data to string when it draw the
     * item.
     * There is a default data source when you not set the data source for CustomPicker.
     * Set data source for CustomPicker will reset state of it, you can refer to
     * [.setSelectedItemPosition] for more details.
     */
    fun setData(data: MutableList<Any>?)

    /**
     * Set items of CustomPicker if has same width
     * CustomPicker will traverse the data source to calculate each data text width to find out the
     * maximum text width for the final view width, this process maybe spends a lot of time and
     * reduce efficiency when data source has large amount data, in most large amount data case,
     * data text always has same width, you can call this method tell to CustomPicker your data
     * source has same width to save time and improve efficiency.
     * Sometimes the data source you set is positively has different text width, but maybe you know
     * the maximum width text's position in data source, then you can call
     * [.setMaximumWidthTextPosition] tell to CustomPicker where is the maximum width text
     * in data source, CustomPicker will calculate its width base on this text which found by
     * position. If you don't know the position of maximum width text in data source, but you have
     * maximum width text, you can call [.setMaximumWidthText] tell to CustomPicker
     * what maximum width text is directly, CustomPicker will calculate its width base on this text.
     */
    fun setSameWidth(hasSameSize: Boolean)

    /**
     *
     * Whether items has same width or not
     */
    fun hasSameWidth(): Boolean

    fun setOnChangeListener(listener: CustomPicker.OnChangeListener?)

    /**
     * Get maximum width text
     */
    fun getMaximumWidthText(): String?

    /**
     *
     * Set maximum width text
     */
    fun setMaximumWidthText(text: String?)

    /**
     * Get the position of maximum width text in data source
     */
    fun getMaximumWidthTextPosition(): Int

    /**
     * Set the position of maximum width text in data source
     */
    fun setMaximumWidthTextPosition(position: Int)

    /**
     * Get text color of current selected item
     * For example 0xFF123456
     */
    fun getSelectedItemTextColor(): Int

    /**
     * Set text color of current selected item
     * For example 0xFF123456
     */
    fun setSelectedItemTextColor(color: Int)

    /**
     * Get text color of items
     * For example 0xFF123456
     */
    fun getItemTextColor(): Int

    /**
     * Set text color of items
     * For example 0xFF123456
     */
    fun setItemTextColor(color: Int)

    /**
     *
     * Get text size of items
     * Unit in px
     */
    fun getItemTextSize(): Int

    /**
     * Set text size of items
     * Unit in px
     */
    fun setItemTextSize(size: Int)

    /**
     * Get space between items
     * Unit in px
     */
    fun getItemSpace(): Int

    /**
     * Set space between items
     * Unit in px
     */
    fun setItemSpace(space: Int)

    /**
     * Set whether CustomPicker display indicator or not
     * CustomPicker will draw two lines above an below current selected item if display indicator
     * Notice:Indicator's size will not participate in CustomPicker's size calculation, it will drawn
     * above the content
     */
    fun setIndicator(hasIndicator: Boolean)

    /**
     *
     * Whether CustomPicker display indicator or not
     */
    fun hasIndicator(): Boolean

    /**
     * Get size of indicator
     * Unit in px
     */
    fun getIndicatorSize(): Int

    /**
     *
     * Set size of indicator
     * Unit in px
     */
    fun setIndicatorSize(size: Int)

    /**
     * Get color of indicator
     * For example 0xFF123456
     */
    fun getIndicatorColor(): Int

    /**
     *
     * Set color of indicator
     * For example 0xFF123456
     */
    fun setIndicatorColor(color: Int)

    /**
     * Set whether CustomPicker display curtain or not
     * CustomPicker will draw a rectangle as big as current selected item and fill specify color
     * above content if curtain display
     */
    fun setCurtain(hasCurtain: Boolean)

    /**
     * Whether CustomPicker display curtain or not
     */
    fun hasCurtain(): Boolean

    /**
     * Get color of curtain
     * For example 0xFF123456
     */
    fun getCurtainColor(): Int

    /**
     * Set color of curtain
     * For example 0xFF123456
     */
    fun setCurtainColor(color: Int)

    /**
     * Set whether CustomPicker has atmospheric or not
     * CustomPicker's items will be transparent from center to ends if atmospheric display
     */
    fun setAtmospheric(hasAtmospheric: Boolean)

    /**
     * Whether CustomPicker has atmospheric or not
     */
    fun hasAtmospheric(): Boolean

    /**
     * Whether CustomPicker enable curved effect or not
     */
    fun isCurved(): Boolean

    /**

     * Set whether CustomPicker enable curved effect or not
     * If setCurved true, CustomPicker will display with curved effect looks like ends bend into
     * screen with perspective.
     * CustomPicker's curved effect base on strict geometric model, some parameters relate with size
     * maybe invalidated, for example each item size looks like different because of perspective in
     * curved, the space between items looks like have a little difference
     *
     */
    fun setCurved(isCurved: Boolean)

    /**
     * Get alignment of CustomPicker
     */
    fun getItemAlign(): Int

    /**
     * Set alignment of CustomPicker
     * The default alignment of CustomPicker is [CustomPicker.ALIGN_CENTER]：
     * [CustomPicker.ALIGN_CENTER]
     * [CustomPicker.ALIGN_LEFT]
     * [CustomPicker.ALIGN_RIGHT]
     */
    fun setItemAlign(align: Int)

    /**
     *
     * Get typeface of item text
     */
    fun getTypeface(): Typeface?

    /**
     * Set typeface of item text
     * Set typeface of item text maybe cause CustomPicker size change

     */
    fun setTypeface(tf: Typeface?)
}