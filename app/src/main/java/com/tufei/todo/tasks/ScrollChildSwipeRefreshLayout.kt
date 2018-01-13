package com.tufei.todo.tasks

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.View

/**
 * Extends [SwipeRefreshLayout] to support non-direct descendant scrolling views.
 *
 *
 * [SwipeRefreshLayout] works as expected when a scroll view is a direct child: it triggers
 * the refresh only when the view is on top. This class adds a way (@link #setScrollUpChild} to
 * define which view controls this behavior.
 * @author tufei
 * @date 2018/1/13.
 */
class ScrollChildSwipeRefreshLayout @JvmOverloads constructor(context: Context,
                                                              attrs: AttributeSet? = null)
    : SwipeRefreshLayout(context, attrs) {

    var scrollUpChild: View? = null

    override fun canChildScrollUp() =
            scrollUpChild?.canScrollVertically(-1) ?: super.canChildScrollUp()
}