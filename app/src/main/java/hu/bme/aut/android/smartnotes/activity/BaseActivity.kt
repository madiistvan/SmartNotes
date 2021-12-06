package hu.bme.aut.android.smartnotes.activity

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.Window
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import hu.bme.aut.android.smartnotes.R
import android.widget.RelativeLayout
import com.google.android.material.snackbar.Snackbar


abstract class BaseActivity : AppCompatActivity() {
    var loadingDialog: Dialog? = null

    fun showProgressDialog() {
        loadingDialog = Dialog(this)

        loadingDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        loadingDialog?.setCancelable(true)

        val relativeLayout = RelativeLayout(this)
        relativeLayout.layoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)

        val progressBar = ProgressBar(this,null , android.R.attr.progressBarStyleLarge)
        progressBar.indeterminateTintList = ColorStateList.valueOf(Color.YELLOW)


        progressBar.progressTintList = ColorStateList.valueOf(Color.YELLOW);
        val params = RelativeLayout.LayoutParams(150, 150)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)

        relativeLayout.addView(progressBar, params)

        loadingDialog?.window?.setContentView(relativeLayout, relativeLayout.layoutParams)
        loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog?.show()
    }


    protected fun hideProgressDialog() {
        loadingDialog?.let { dialog ->
            dialog.dismiss()
        }
        loadingDialog = null
    }

    protected fun snack(view:View,message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }
    protected fun initActionBar(nav: Boolean = true) {
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(nav)
        supportActionBar?.setIcon(R.drawable.baseline_note_add_24)
    }
}