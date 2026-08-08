package com.etno2500pixel.jarvis.tools

import android.content.Context
import android.content.Intent
import android.net.Uri

class AndroidTools(
    private val context: Context
) {

    fun openUrl(url: String) {

        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(intent)
    }

    fun openApp(packageName: String): Boolean {

        val intent =
            context.packageManager
                .getLaunchIntentForPackage(packageName)
                ?: return false

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(intent)

        return true
    }

    fun executeSafeCommand(text: String): Boolean {

        val lower = text.lowercase()

        return when {

            lower.contains("youtube") ->
                openApp("com.google.android.youtube")

            lower.contains("chrome") ->
                openApp("com.android.chrome")

            lower.contains("google") -> {
                openUrl("https://www.google.com")
                true
            }

            else ->
                false
        }
    }
}
