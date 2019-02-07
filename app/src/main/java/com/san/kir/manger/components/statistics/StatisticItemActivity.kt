package com.san.kir.manger.components.statistics

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.MenuItem
import com.san.kir.manger.R
import com.san.kir.manger.extending.ThemedActionBarActivity
import com.san.kir.manger.view_models.StatisticViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.setContentView

class StatisticItemActivity : ThemedActionBarActivity() {
    val mViewModel by lazy {
        ViewModelProviders.of(this).get(StatisticViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val key = getString(R.string.settings_app_dark_theme_key)
        val default = getString(R.string.settings_app_dark_theme_default) == "true"
        val isDark = defaultSharedPreferences.getBoolean(key, default)
        setTheme(if (isDark) R.style.AppThemeDark else R.style.AppTheme)

        super.onCreate(savedInstanceState)

        launch(coroutineContext) {
            val manga = mViewModel.getStatisticItem(
                intent.getStringExtra("manga")
            )

            withContext(Dispatchers.Main) {
                StatisticItemFullView(manga).setContentView(this@StatisticItemActivity)

                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.title = manga.manga
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
