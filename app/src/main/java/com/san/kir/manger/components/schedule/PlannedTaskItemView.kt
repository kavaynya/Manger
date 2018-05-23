package com.san.kir.manger.components.schedule

import android.content.Context
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import com.san.kir.manger.components.main.Main
import com.san.kir.manger.room.dao.updateAsync
import com.san.kir.manger.room.models.PlannedAddEdit
import com.san.kir.manger.room.models.PlannedPeriod
import com.san.kir.manger.room.models.PlannedTask
import com.san.kir.manger.room.models.PlannedType
import com.san.kir.manger.utils.ID
import com.san.kir.manger.utils.RecyclerViewAdapterFactory
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.alignParentEnd
import org.jetbrains.anko.alignParentStart
import org.jetbrains.anko.bottomOf
import org.jetbrains.anko.centerVertically
import org.jetbrains.anko.dip
import org.jetbrains.anko.leftOf
import org.jetbrains.anko.lines
import org.jetbrains.anko.margin
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.switch
import org.jetbrains.anko.textView
import java.util.*

class PlannedTaskItemView(act: ScheduleActivity) :
    RecyclerViewAdapterFactory.AnkoView<PlannedTask>() {

    private val alarmManager = ScheduleManager(act)

    private lateinit var ctx: Context
    private lateinit var root: RelativeLayout
    private lateinit var name: TextView
    private lateinit var updateText: TextView
    private lateinit var switch: Switch

    private object Id {
        val name = ID.generate()
        val switch = ID.generate()
    }

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        relativeLayout {
            lparams {
                margin = dip(3)
            }

            switch = switch {
                id = Id.switch
            }.lparams {
                alignParentEnd()
                centerVertically()
                margin = dip(3)
            }

            name = textView {
                id = Id.name
                textSize = 18f
                lines = 1
            }.lparams {
                margin = dip(3)
                leftOf(Id.switch)
            }

            updateText = textView().lparams {
                alignParentStart()
                bottomOf(Id.name)
                leftOf(Id.switch)
                margin = dip(3)
            }

            root = this
        }
    }

    override fun bind(item: PlannedTask, isSelected: Boolean, position: Int) {
        ctx = root.context

        when (item.type) {
            PlannedType.MANGA -> {
                name.text = "Манга: ${item.manga}"
            }
            PlannedType.CATEGORY -> {
                name.text = "Категория: ${item.category}"
            }
            PlannedType.GROUP -> {
                name.text = "Группа: ${item.groupName}"
            }
        }

        if (item.period == PlannedPeriod.DAY)
            updateText.text =
                    "Обновляется раз в день в ${item.hour}:${String.format("%02d", item.minute)}"
        else {
            updateText.text = when (item.dayOfWeek) {
                Calendar.MONDAY -> "Обновляется каждый понедельник в ${item.hour}:${String.format(
                    "%02d",
                    item.minute
                )}"
                Calendar.TUESDAY -> "Обновляется каждый вторник в ${item.hour}:${String.format(
                    "%02d",
                    item.minute
                )}"
                Calendar.WEDNESDAY -> "Обновляется каждую среду в ${item.hour}:${String.format(
                    "%02d",
                    item.minute
                )}"
                Calendar.THURSDAY -> "Обновляется каждый четверг в ${item.hour}:${String.format(
                    "%02d",
                    item.minute
                )}"
                Calendar.FRIDAY -> "Обновляется каждую пятницу в ${item.hour}:${String.format(
                    "%02d",
                    item.minute
                )}"
                Calendar.SATURDAY -> "Обновляется каждую субботу в ${item.hour}:${String.format(
                    "%02d",
                    item.minute
                )}"
                Calendar.SUNDAY -> "Обновляется каждое воскресение в ${item.hour}:${String.format(
                    "%02d",
                    item.minute
                )}"
                else -> "Черт знает когда оно обновляется"
            }
        }

        switch.isChecked = item.isEnabled
        switch.onClick {
            item.isEnabled = !item.isEnabled
            Main.db.plannedDao.updateAsync(item)

            if (item.isEnabled) {
                alarmManager.add(item)
            } else {
                alarmManager.cancel(item)
            }
        }

        root.onClick {
            ctx.startActivity<AddEditPlannedTaskActivity>(PlannedAddEdit.edit to item)
        }
    }
}