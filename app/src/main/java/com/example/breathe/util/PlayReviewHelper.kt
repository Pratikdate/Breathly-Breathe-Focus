package com.shanacoder.breathly.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.android.play.core.review.ReviewManagerFactory

object PlayReviewHelper {

    /**
     * Attempts to launch the Google Play In-App Review flow.
     * If the In-App Review API is unavailable (e.g. debug build, unreleased app, device without Play Store, or quota reached),
     * it automatically falls back to opening the Google Play Store listing page directly.
     */
    fun launchReviewFlow(context: Context) {
        val activity = context.findActivity()
        val manager = ReviewManagerFactory.create(context)
        val request = manager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful && activity != null) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    // In-app review flow completed or dismissed
                }
            } else {
                // Fallback to opening Play Store listing in browser/app
                openPlayStoreListing(context)
            }
        }
    }

    /**
     * Directly opens the Google Play Store app page for this package.
     */
    fun openPlayStoreListing(context: Context) {
        val packageName = context.packageName
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(webIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Unable to open Google Play Store", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun Context.findActivity(): Activity? {
        var current = this
        while (current is ContextWrapper) {
            if (current is Activity) return current
            current = current.baseContext
        }
        return null
    }
}
