package com.san.kir.manger.components.Library

import android.content.res.Configuration
import android.support.v7.widget.GridLayoutManager
import android.view.Gravity
import android.view.View
import com.san.kir.manger.Extending.AnkoExtend.bind
import com.san.kir.manger.R
import com.san.kir.manger.components.Main.MainActivity
import com.san.kir.manger.dbflow.models.Category
import com.san.kir.manger.utils.ID
import com.san.kir.manger.utils.name_LAND_SPAN
import com.san.kir.manger.utils.name_PORT_SPAN
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.below
import org.jetbrains.anko.button
import org.jetbrains.anko.centerHorizontally
import org.jetbrains.anko.centerInParent
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textView
import org.jetbrains.anko.wrapContent

class LibraryPageView(category: Category,
                      fragment: LibraryFragment) : AnkoComponent<LibraryFragment> {
    private object _id {
        val text = ID.generate()
        val button = ID.generate()
    }

    val adapter = LibraryItemsAdapter(category, fragment)

    fun createView(fragment: LibraryFragment): View {
        return createView(AnkoContext.create(fragment.context, fragment))
    }

    override fun createView(ui: AnkoContext<LibraryFragment>) = with(ui) {
        val portrait = this.ctx.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val span = this.ctx.defaultSharedPreferences.getString(
                if (portrait) name_PORT_SPAN else name_LAND_SPAN,
                if (portrait) "2" else "3"
        ).toInt()

        relativeLayout {
            lparams(width = matchParent, height = matchParent)

            // текст при пустой странице
            textView {
                id = _id.text
                gravity = Gravity.CENTER
                setText(R.string.library_help)

                bind(adapter.isEmpty) { visibility = if (it) View.VISIBLE else View.GONE }
            }.lparams(width = matchParent, height = wrapContent) {
                centerInParent()
            }

            // кнопка перехода в каталоге
            button {
                id = _id.button

                setText(R.string.library_help_go)
                visibility = View.GONE

                onClick { startActivity<MainActivity>("launch" to "catalog") }
                bind(adapter.isEmpty) { visibility = if (it) View.VISIBLE else View.GONE }
            }.lparams(width = wrapContent, height = wrapContent) {
                centerHorizontally()
                below(_id.text)
            }

            // список элементов
            recyclerView {
                lparams(width = matchParent, height = matchParent)
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(this@with.ctx, span)
                adapter = this@LibraryPageView.adapter
            }
        }
    }
}
