package com.matchify.presentation.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.matchify.R
import com.matchify.presentation.SignUpActivity

abstract class BaseActivity : AppCompatActivity() {
    private var statusDialog: Dialog? = null

    protected fun showStatusDialog(
        isSuccess: Boolean,
        title: String,
        message: String,
        buttonText: String,
        onButtonClick: () -> Unit = {}
    ) {
        Dialog(this, R.style.TransparentDialog).apply {
            setContentView(R.layout.dialog_status)
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            findViewById<ImageView>(R.id.ivStatus)?.setImageResource(
                if (isSuccess) R.drawable.ic_success_check else R.drawable.ic_fail_check
            )
            findViewById<TextView>(R.id.tvTitle)?.text = title
            findViewById<TextView>(R.id.tvMessage)?.text = message
            findViewById<MaterialButton>(R.id.btnAction)?.apply {
                text = buttonText
                setOnClickListener {
                    dismiss()
                    onButtonClick()
                }
            }
            show()
        }
    }

    protected fun showLoading(message: String = "Please wait...") {
        statusDialog?.dismiss()
        statusDialog = Dialog(this, R.style.TransparentDialog).apply {
            setContentView(R.layout.dialog_status)
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            findViewById<ImageView>(R.id.ivStatus)?.setImageResource(R.drawable.ic_loading_animation_anim)
            findViewById<TextView>(R.id.tvTitle)?.text = message
            findViewById<TextView>(R.id.tvMessage)?.visibility = View.GONE
            findViewById<MaterialButton>(R.id.btnAction)?.visibility = View.GONE

            show()
        }
    }

    protected fun hideLoading() {
        statusDialog?.dismiss()
        statusDialog = null
    }

    companion object {
        private var activityReferences = 0
        private var isActivityChangingConfigurations = false
    }

    override fun onStart() {
        super.onStart()
        activityReferences++
    }

    override fun onStop() {
        super.onStop()
        isActivityChangingConfigurations = isChangingConfigurations
        if (!isActivityChangingConfigurations) {
            activityReferences--
            if (activityReferences == 0) {
                // App is going to background, clear the data
                getSharedPreferences(SignUpActivity.USER_DATA_PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply()
            }
        }
    }

    protected fun showError(message: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, null)
        val textView = layout.findViewById<TextView>(R.id.tvToastMessage)
        textView.text = message

        Toast(applicationContext).apply {
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }

    override fun onDestroy() {
        statusDialog?.dismiss()
        super.onDestroy()
    }
}