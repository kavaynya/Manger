package com.san.kir.manger.components.viewer

import android.R.id
import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.ActivityInfo
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
import android.graphics.Point
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import com.san.kir.manger.R
import com.san.kir.manger.eventBus.negative
import com.san.kir.manger.eventBus.positive
import com.san.kir.manger.extending.ThemedActionBarActivity
import com.san.kir.manger.room.models.Chapter
import com.san.kir.manger.view_models.ViewerViewModel
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.setContentView
import kotlin.properties.Delegates.observable

class ViewerActivity : ThemedActionBarActivity() {
    companion object { // константы для сохранения настроек
        var LEFT_PART_SCREEN = 0 // Левая часть экрана
        var RIGHT_PART_SCREEN = 0 // Правая часть экрана
    }

    val mViewModel by lazy {
        ViewModelProviders.of(this).get(ViewerViewModel::class.java)
    }

    var isBar by observable(true) { _, old, new ->
        if (old != new) {
            if (!new) {
                supportActionBar!!.hide() //Скрыть бар сверху
                presenter.isBottomBar.negative() // Скрыть нижний бар
            } else {
                supportActionBar!!.show() // Показать бар сверху
                presenter.isBottomBar.positive()// Показать нижний бар
            }
        }
    }

    // Режимы листания страниц
    var isTapControl = false // Нажатия на экран
    private var isKeyControl = false // Кнопки громкости

    var chapter = Chapter()
    private var isAlternative = false

    val presenter by lazy { ViewerPresenter(this) }
    val mView by lazy { ViewerView(presenter) }

    private var readTime = 0L

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defaultSharedPreferences.apply {
            val orientationKey = getString(R.string.settings_viewer_orientation_key)
            val orientationDefault = getString(R.string.settings_viewer_orientation_default)

            requestedOrientation = when (getString(orientationKey, orientationDefault)) {
                getString(R.string.settings_viewer_orientation_auto) -> SCREEN_ORIENTATION_SENSOR
                getString(R.string.settings_viewer_orientation_port) -> SCREEN_ORIENTATION_PORTRAIT
                getString(R.string.settings_viewer_orientation_port_rev) -> SCREEN_ORIENTATION_REVERSE_PORTRAIT
                getString(R.string.settings_viewer_orientation_land) -> SCREEN_ORIENTATION_LANDSCAPE
                getString(R.string.settings_viewer_orientation_land_rev) -> SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                getString(R.string.settings_viewer_orientation_auto_port) -> SCREEN_ORIENTATION_SENSOR_PORTRAIT
                getString(R.string.settings_viewer_orientation_auto_land) -> SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                else -> SCREEN_ORIENTATION_SENSOR
            }

            val controlKey = getString(R.string.settings_viewer_control_key)
            val controlDefault = resources.getStringArray(R.array.settings_viewer_control_default)

            getStringSet(controlKey, controlDefault.toSet())?.forEach {
                when (it) {
                    getString(R.string.settings_viewer_control_taps) -> isTapControl = true
                    getString(R.string.settings_viewer_control_swipes) -> presenter.isSwipeControl.positive()
                    getString(R.string.settings_viewer_control_keys) -> isKeyControl = true
                }
            }

        }

        mView.setContentView(this) // Установка разметки

        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Кнопка назад в верхнем баре
        supportActionBar?.setShowHideAnimationEnabled(true) // Анимация скрытия, сокрытия

        intent.apply {
            chapter = getParcelableExtra("chapter")
            isAlternative = getBooleanExtra("is", false)
        }

        title = chapter.name // Смена заголвка
        readTime = System.currentTimeMillis()
    }

    override fun onResume() {
        super.onResume()

        val point = Point() // Хранилище для данных экрана
        windowManager.defaultDisplay.getSize(point) // Сохранение данных в хранилище
        LEFT_PART_SCREEN = point.x / 3 // Установка данных
        RIGHT_PART_SCREEN = point.x * 2 / 3 // Установка данных

        // Загрузка настроек
        defaultSharedPreferences.apply {
            val key = getString(R.string.settings_viewer_show_bar_key)
            val default = getString(R.string.settings_viewer_show_bar_default) == "true"
            isBar = getBoolean(key, default)
        }

        presenter.configManager(chapter, isAlternative).invokeOnCompletion {
            presenter.isLoad.negative()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // Установка режима во весь экрана без верхней строки и навигационных кнопок
        if (hasFocus) { // Срабатывает только если был получен фокус
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Переключение слайдов с помощью клавиш громкости
        if (isKeyControl) {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> presenter.nextPage()
                KeyEvent.KEYCODE_VOLUME_UP -> presenter.prevPage()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        // Сохранение настроек
        defaultSharedPreferences
            .edit()
            .putBoolean(getString(R.string.settings_viewer_show_bar_key), isBar)
            .apply()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
    }

    override fun onDestroy() {
        super.onDestroy()
        val time = (System.currentTimeMillis() - readTime) / 1000
        if (time > 0) {
            mViewModel.updateStatisticInfo(chapter.manga, time)
        }
    }
}
