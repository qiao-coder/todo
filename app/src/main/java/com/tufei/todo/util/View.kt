package com.tufei.todo.util

import android.support.design.widget.Snackbar
import android.view.View

/**
 * @author tufei
 * @te 2018/1/14.
 */
fun View.showToast(msg: String) {
    Snackbar.make(this, msg, Snackbar.LENGTH_LONG).show()
}