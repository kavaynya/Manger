package com.san.kir.manger.components.category

import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.san.kir.manger.R
import com.san.kir.manger.extending.ankoExtend.invisibleOrVisible
import com.san.kir.manger.room.models.Category
import com.san.kir.manger.utils.CATEGORY_ALL
import com.san.kir.manger.utils.ID
import com.san.kir.manger.utils.RecyclerViewAdapterFactory
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.alert
import org.jetbrains.anko.alignParentRight
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.baselineOf
import org.jetbrains.anko.centerVertically
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.leftOf
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.textView
import org.jetbrains.anko.wrapContent

class CategoryItemView(private val adapter: CategoryRecyclerPresenter)
    : RecyclerViewAdapterFactory.AnkoView<Category>() {
    private object Id {
        val delete = ID.generate()
    }

    private lateinit var root: RelativeLayout
    private lateinit var name: TextView
    private lateinit var visibleBtn: ImageView
    private lateinit var deleteBtn: ImageView

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        val sizeBtn = dip(35)

        relativeLayout {
            lparams(width = matchParent, height = dip(60))

            name = textView {
                textSize = 18f
            }.lparams(width = matchParent, height = wrapContent) {
                centerVertically()
                gravity = Gravity.CENTER_VERTICAL
                leftMargin = dip(16)
            }

            // переключение видимости
            visibleBtn = imageView {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }.lparams(width = sizeBtn, height = sizeBtn) {
                centerVertically()
                rightMargin = dip(2)
                leftOf(Id.delete)
                baselineOf(Id.delete)
            }

            // удаление
            deleteBtn = imageView {
                id = Id.delete
                scaleType = ImageView.ScaleType.CENTER_CROP
                backgroundResource = R.drawable.ic_action_delete_black
            }.lparams(width = sizeBtn, height = sizeBtn) {
                alignParentRight()
                centerVertically()
                rightMargin = dip(8)
            }

            root = this
        }
    }

    override fun bind(item: Category, isSelected: Boolean, position: Int) {
        root.onClick {
            CategoryEditDialog(root.context, item) { cat ->
                adapter.update(cat, oldName)
                bind(item, isSelected, position)
            }
        }

        name.text = item.name

        visibleBtn.backgroundResource =
                if (item.isVisible) R.drawable.ic_visibility
                else R.drawable.ic_visibility_off
        visibleBtn.onClick {
            item.isVisible = !item.isVisible
            adapter.update(item)
            bind(item, isSelected, position)
        }

        deleteBtn.invisibleOrVisible(item.name == CATEGORY_ALL)
        if (item.name == CATEGORY_ALL) {
            deleteBtn.isClickable = false
        } else {
            deleteBtn.onClick {
                root.context.alert(message = "Вы действительно хотите удалить?") {
                    positiveButton("Да") {
                        adapter.remove(item)
                    }
                    negativeButton("Нет") { }
                }.show()
            }
        }
    }
}