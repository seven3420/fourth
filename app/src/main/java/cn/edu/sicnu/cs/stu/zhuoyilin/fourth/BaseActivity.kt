package cn.edu.sicnu.cs.stu.zhuoyilin.fourth

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnPreDraw

abstract class BaseActivity : AppCompatActivity() {

    override fun onContentChanged() {
        super.onContentChanged()

        val content = findViewById<View>(android.R.id.content)
        content.doOnPreDraw {
            val initialPadding = intArrayOf(
                content.paddingLeft,
                content.paddingTop,
                content.paddingRight,
                content.paddingBottom
            )
            val actionBarBottom =
                findViewById<View>(androidx.appcompat.R.id.action_bar_container)?.bottom ?: 0
            val actionBarOverlap =
                (actionBarBottom - content.top).coerceAtLeast(0)

            fun applyInsets(insets: WindowInsetsCompat?) {
                val systemBars = insets?.getInsets(
                    WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout()
                )
                val topInset = maxOf(systemBars?.top ?: 0, actionBarOverlap)
                content.setPadding(
                    initialPadding[0] + (systemBars?.left ?: 0),
                    initialPadding[1] + topInset,
                    initialPadding[2] + (systemBars?.right ?: 0),
                    initialPadding[3] + (systemBars?.bottom ?: 0)
                )
            }

            applyInsets(ViewCompat.getRootWindowInsets(content))
            ViewCompat.setOnApplyWindowInsetsListener(content) { _, insets ->
                applyInsets(insets)
                insets
            }
        }
    }
}
