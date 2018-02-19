package com.tufei.todo.util

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

/**
 * @author tufei
 * @date 2018/2/19.
 */
private var toast: Toast? = null

@SuppressLint("ShowToast")
fun Context.showToast(tip: String, time: Int = Toast.LENGTH_SHORT) {
    if (time !in Toast.LENGTH_SHORT..Toast.LENGTH_LONG) {
        throw IllegalArgumentException("Only Toast.LENGTH_SHORT or Toast.LENGTH_LONG！")
    }
    toast?.apply {
        setText(tip)
        duration = time
    } ?: apply {
        toast = Toast.makeText(applicationContext, tip, time)
    }
    toast?.show()
}

