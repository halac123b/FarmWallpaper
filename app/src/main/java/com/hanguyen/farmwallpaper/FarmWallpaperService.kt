package com.hanguyen.farmwallpaper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder

class FarmWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = FarmEngine()

    private inner class FarmEngine : Engine() {
        private val handler = Handler(Looper.getMainLooper())
        private var visible = false
        private var downX = 0f
        private var downY = 0f

        private val frame = object : Runnable {
            override fun run() {
                drawFrame()
                if (visible) handler.postDelayed(this, FRAME_MILLIS)
            }
        }

        private val stateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) = drawFrame()
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(true)
            val filter = IntentFilter(FarmState.ACTION_STATE_CHANGED)
            if (Build.VERSION.SDK_INT >= 33) {
                registerReceiver(stateReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(stateReceiver, filter)
            }
        }

        override fun onDestroy() {
            handler.removeCallbacks(frame)
            unregisterReceiver(stateReceiver)
            super.onDestroy()
        }

        // Stop drawing while hidden to save battery.
        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            handler.removeCallbacks(frame)
            if (visible) handler.post(frame)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            drawFrame()
        }

        override fun onTouchEvent(event: MotionEvent) {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> { downX = event.x; downY = event.y }
                MotionEvent.ACTION_UP -> {
                    val slop = 24f * resources.displayMetrics.density
                    if (Math.abs(event.x - downX) < slop && Math.abs(event.y - downY) < slop) openApp()
                }
            }
        }

        private fun openApp() {
            startActivity(
                Intent(this@FarmWallpaperService, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        private fun drawFrame() {
            val holder = surfaceHolder
            val canvas = holder.lockCanvas() ?: return
            try {
                FarmRenderer.draw(
                    canvas,
                    canvas.width,
                    canvas.height,
                    FarmState.growth(this@FarmWallpaperService)
                )
            } finally {
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    private companion object {
        const val FRAME_MILLIS = 100L
    }
}
