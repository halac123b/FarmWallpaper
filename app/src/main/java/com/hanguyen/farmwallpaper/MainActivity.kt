package com.hanguyen.farmwallpaper

import android.app.Activity
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.Gravity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.widget.Button
import android.widget.LinearLayout

class MainActivity : Activity() {

    private lateinit var farm: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        farm = object : View(this) {
            override fun onDraw(canvas: Canvas) {
                FarmRenderer.draw(canvas, width, height, FarmState.growth(context))
                postInvalidateOnAnimation()
            }
        }

        val plant = Button(this).apply {
            text = "Plant new seed"
            setOnClickListener { FarmState.plant(context) }
        }
        val setWallpaper = Button(this).apply {
            text = "Set as live wallpaper"
            setOnClickListener {
                startActivity(
                    Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).putExtra(
                        WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        ComponentName(context, FarmWallpaperService::class.java)
                    )
                )
            }
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            addView(farm, LinearLayout.LayoutParams(-1, 0, 1f))
            addView(plant, LinearLayout.LayoutParams(-1, -2))
            addView(setWallpaper, LinearLayout.LayoutParams(-1, -2))
        }
        // targetSdk 35 draws edge-to-edge, so keep the buttons clear of the nav/status bars.
        root.setOnApplyWindowInsetsListener { view, insets ->
            val bars = if (Build.VERSION.SDK_INT >= 30) {
                insets.getInsets(WindowInsets.Type.systemBars()).let {
                    intArrayOf(it.left, it.top, it.right, it.bottom)
                }
            } else {
                @Suppress("DEPRECATION")
                intArrayOf(
                    insets.systemWindowInsetLeft, insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight, insets.systemWindowInsetBottom
                )
            }
            view.setPadding(bars[0], 0, bars[2], bars[3])
            insets
        }
        setContentView(root)
    }
}
