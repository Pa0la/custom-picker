package com.paola.example.custompicker

class CustomPicker {

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
}