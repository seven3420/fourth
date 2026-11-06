package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.controller

import android.os.Handler
import android.os.Looper

class HandlerTurnScheduler : CardGameController.TurnScheduler {

    private val handler = Handler(Looper.getMainLooper())
    private var pendingAction: Runnable? = null

    override fun schedule(delayMillis: Long, action: () -> Unit) {
        cancel()
        val runnable = Runnable {
            pendingAction = null
            action()
        }
        pendingAction = runnable
        handler.postDelayed(runnable, delayMillis)
    }

    override fun cancel() {
        pendingAction?.let(handler::removeCallbacks)
        pendingAction = null
    }
}
