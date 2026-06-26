package com.techtree.clevcaleb.ui

import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.core.content.getSystemService

/** Keeps the Android soft keyboard hidden for calculator-style fields that use a custom keypad. */
fun Modifier.disableSoftwareKeyboard(): Modifier = composed {
    val view = LocalView.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val imm = view.context.getSystemService<InputMethodManager>()

    fun suppressKeyboard() {
        view.findDescendantTextField()?.apply {
            showSoftInputOnFocus = false
            isLongClickable = false
            setTextIsSelectable(false)
        }
        keyboardController?.hide()
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    DisposableEffect(view) {
        suppressKeyboard()
        onDispose { }
    }

    SideEffect {
        suppressKeyboard()
    }

    this
        .onGloballyPositioned { suppressKeyboard() }
        .onFocusChanged { state ->
            if (state.isFocused) {
                suppressKeyboard()
            }
        }
}

private fun View.findDescendantTextField(): TextView? {
    if (this is TextView && this !is android.widget.Button) return this
    if (this is ViewGroup) {
        for (i in 0 until childCount) {
            getChildAt(i).findDescendantTextField()?.let { return it }
        }
    }
    return null
}
