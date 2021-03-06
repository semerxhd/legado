package io.legado.app.lib.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.widget.EdgeEffect
import android.widget.ScrollView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.legado.app.R
import io.legado.app.help.AppConfig
import io.legado.app.utils.getCompatColor
import kotlinx.android.synthetic.main.activity_main.view.*
import org.jetbrains.anko.backgroundColor


/**
 * @author Karim Abou Zeid (kabouzeid)
 */
object ATH {

    @SuppressLint("CommitPrefEdits")
    fun didThemeValuesChange(context: Context, since: Long): Boolean {
        return ThemeStore.isConfigured(context) && ThemeStore.prefs(context).getLong(
            ThemeStorePrefKeys.VALUES_CHANGED,
            -1
        ) > since
    }

    fun setStatusBarColorAuto(activity: Activity, fullScreen: Boolean) {
        val isTransparentStatusBar = AppConfig.isTransparentStatusBar
        setStatusBarColor(
            activity,
            ThemeStore.statusBarColor(activity, isTransparentStatusBar),
            isTransparentStatusBar, fullScreen
        )
    }

    fun setStatusBarColor(
        activity: Activity,
        color: Int,
        isTransparentStatusBar: Boolean,
        fullScreen: Boolean
    ) {
        if (fullScreen) {
            if (isTransparentStatusBar) {
                activity.window.statusBarColor = Color.TRANSPARENT
            } else {
                activity.window.statusBarColor = activity.getCompatColor(R.color.status_bar_bag)
            }
        } else {
            activity.window.statusBarColor = color
        }
        setLightStatusBarAuto(activity, color)
    }

    fun setLightStatusBarAuto(activity: Activity, bgColor: Int) {
        setLightStatusBar(activity, ColorUtils.isColorLight(bgColor))
    }

    fun setLightStatusBar(activity: Activity, enabled: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = activity.window.decorView
            val systemUiVisibility = decorView.systemUiVisibility
            if (enabled) {
                decorView.systemUiVisibility =
                    systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decorView.systemUiVisibility =
                    systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }

    fun setLightNavigationBar(activity: Activity, enabled: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val decorView = activity.window.decorView
            var systemUiVisibility = decorView.systemUiVisibility
            systemUiVisibility = if (enabled) {
                systemUiVisibility or SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                systemUiVisibility and SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            }
            decorView.systemUiVisibility = systemUiVisibility
        }
    }

    fun setLightNavigationBarAuto(activity: Activity, bgColor: Int) {
        setLightNavigationBar(activity, ColorUtils.isColorLight(bgColor))
    }

    fun setNavigationBarColorAuto(activity: Activity) {
        setNavigationBarColor(activity, ThemeStore.navigationBarColor(activity))
    }

    fun setNavigationBarColor(activity: Activity, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.navigationBarColor = color
            setLightNavigationBarAuto(activity, color)
        }
    }

    fun setTaskDescriptionColorAuto(activity: Activity) {
        setTaskDescriptionColor(activity, ThemeStore.primaryColor(activity))
    }

    fun setTaskDescriptionColor(activity: Activity, @ColorInt color: Int) {
        val color1: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            color1 = ColorUtils.stripAlpha(color)
            @Suppress("DEPRECATION")
            activity.setTaskDescription(
                ActivityManager.TaskDescription(
                    activity.title as String,
                    null,
                    color1
                )
            )
        }
    }

    fun setTint(
        view: View,
        @ColorInt color: Int,
        isDark: Boolean = AppConfig.isNightTheme(view.context)
    ) {
        TintHelper.setTintAuto(view, color, false, isDark)
    }

    fun setBackgroundTint(
        view: View, @ColorInt color: Int,
        isDark: Boolean = AppConfig.isNightTheme
    ) {
        TintHelper.setTintAuto(view, color, true, isDark)
    }

    fun setAlertDialogTint(dialog: AlertDialog): AlertDialog {
        val colorStateList = Selector.colorBuild()
            .setDefaultColor(ThemeStore.accentColor(dialog.context))
            .setPressedColor(ColorUtils.darkenColor(ThemeStore.accentColor(dialog.context)))
            .create()
        if (dialog.getButton(AlertDialog.BUTTON_NEGATIVE) != null) {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(colorStateList)
        }
        if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorStateList)
        }
        if (dialog.getButton(AlertDialog.BUTTON_NEUTRAL) != null) {
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(colorStateList)
        }
        return dialog
    }

    fun setEdgeEffectColor(view: RecyclerView?, @ColorInt color: Int) {
        view?.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
            override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                val edgeEffect = super.createEdgeEffect(view, direction)
                edgeEffect.color = color
                return edgeEffect
            }
        }
    }

    fun setEdgeEffectColor(viewPager: ViewPager?, @ColorInt color: Int) {
        try {
            val clazz = ViewPager::class.java
            for (name in arrayOf("mLeftEdge", "mRightEdge")) {
                val field = clazz.getDeclaredField(name)
                field.isAccessible = true
                val edge = field.get(viewPager)
                (edge as EdgeEffect).color = color
            }
        } catch (ignored: Exception) {
        }
    }

    fun setEdgeEffectColor(scrollView: ScrollView?, @ColorInt color: Int) {
        try {
            val clazz = ScrollView::class.java
            for (name in arrayOf("mEdgeGlowTop", "mEdgeGlowBottom")) {
                val field = clazz.getDeclaredField(name)
                field.isAccessible = true
                val edge = field.get(scrollView)
                (edge as EdgeEffect).color = color
            }
        } catch (ignored: Exception) {
        }
    }

    //**************************************************************Directly*************************************************************//

    fun applyBottomNavigationColor(bottomBar: BottomNavigationView?) {
        bottomBar?.apply {
            setBackgroundColor(ThemeStore.backgroundColor(context))
            val colorStateList = Selector.colorBuild()
                .setDefaultColor(context.getCompatColor(R.color.btn_bg_press_tp))
                .setSelectedColor(ThemeStore.accentColor(bottom_navigation_view.context)).create()
            itemIconTintList = colorStateList
            itemTextColor = colorStateList
            setBackgroundColor(ThemeStore.bottomBackground(context))
        }
    }

    fun applyAccentTint(view: View?) {
        view?.apply {
            setTint(this, context.accentColor)
        }
    }

    fun applyBackgroundTint(view: View?) {
        view?.apply {
            if (background == null) {
                backgroundColor = context.backgroundColor
            } else {
                setBackgroundTint(this, context.backgroundColor)
            }
        }
    }

    fun applyEdgeEffectColor(view: View?) {
        when (view) {
            is RecyclerView -> view.edgeEffectFactory = DEFAULT_EFFECT_FACTORY
            is ViewPager -> setEdgeEffectColor(view, ThemeStore.primaryColor(view.context))
            is ScrollView -> setEdgeEffectColor(view, ThemeStore.primaryColor(view.context))
        }
    }

    private val DEFAULT_EFFECT_FACTORY = object : RecyclerView.EdgeEffectFactory() {
        override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
            val edgeEffect = super.createEdgeEffect(view, direction)
            edgeEffect.color = ThemeStore.primaryColor(view.context)
            return edgeEffect
        }
    }
}