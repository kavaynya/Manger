package com.san.kir.core.utils

import android.content.Context

public fun Long.formatTime(): String {
    return TimeFormat(this).toString()
}

public class TimeFormat(seconds: Long) {
    private var days: Long = 0
    private var hours: Long = 0
    private var minutes: Long = 0
    private var seconds: Long = 0

    init {
        this.seconds = seconds % 60

        val minutes = seconds / 60

        this.minutes = minutes % 60

        val hours = minutes / 60

        this.hours = hours % 24

        val days = hours / 24

        this.days = days
    }

    public fun toString(context: Context = ManualDI.application): String {
        if (days == 0L && hours == 0L && minutes == 0L && seconds == 0L)
            return context.getString(R.string.time_format_seconds, 0)

        val builder = StringBuilder()

        if (days != 0L) {
            builder.append(context.getString(R.string.time_format_days, days))
            builder.append(" ")
        }
        if (hours != 0L) {
            builder.append(context.getString(R.string.time_format_hours, hours))
            builder.append(" ")
        }
        if (minutes != 0L) {
            builder.append(context.getString(R.string.time_format_minutes, minutes))
            builder.append(" ")
        }
        if (seconds != 0L)
            builder.append(context.getString(R.string.time_format_seconds, seconds))

        return builder.toString()
    }
}
