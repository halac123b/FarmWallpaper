package com.hanguyen.farmwallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/** Draws sky, ground and one tree whose size follows [growth]. */
object FarmRenderer {
    private val sky = Paint().apply { color = Color.rgb(135, 206, 235) }
    private val ground = Paint().apply { color = Color.rgb(96, 160, 64) }
    private val trunk = Paint().apply { color = Color.rgb(110, 74, 40) }
    private val leaves = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(34, 120, 50) }

    fun draw(canvas: Canvas, width: Int, height: Int, growth: Float) {
        val groundY = height * 0.7f
        canvas.drawRect(0f, 0f, width.toFloat(), groundY, sky)
        canvas.drawRect(0f, groundY, width.toFloat(), height.toFloat(), ground)

        val cx = width / 2f
        val trunkH = height * 0.25f * growth
        val trunkW = width * 0.06f * growth
        canvas.drawRect(cx - trunkW / 2, groundY - trunkH, cx + trunkW / 2, groundY, trunk)
        canvas.drawCircle(cx, groundY - trunkH, width * 0.18f * growth, leaves)
    }
}
