/*
 * Copyright 2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.san.kir.ankofork

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import androidx.annotation.DimenRes
import androidx.fragment.app.Fragment

const val LDPI: Int = DisplayMetrics.DENSITY_LOW
const val MDPI: Int = DisplayMetrics.DENSITY_MEDIUM
const val HDPI: Int = DisplayMetrics.DENSITY_HIGH

const val TVDPI: Int = DisplayMetrics.DENSITY_TV
const val XHDPI: Int = DisplayMetrics.DENSITY_XHIGH
const val XXHDPI: Int = DisplayMetrics.DENSITY_XXHIGH
const val XXXHDPI: Int = DisplayMetrics.DENSITY_XXXHIGH

const val MAXDPI: Int = 0xfffe

//returns dip(dp) dimension value in pixels
fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()
fun Context.dip(value: Float): Int = (value * resources.displayMetrics.density).toInt()

//return sp dimension value in pixels
fun Context.sp(value: Int): Int = (value * resources.displayMetrics.scaledDensity).toInt()
fun Context.sp(value: Float): Int = (value * resources.displayMetrics.scaledDensity).toInt()

//converts px value into dip or sp
fun Context.px2dip(px: Int): Float = px.toFloat() / resources.displayMetrics.density
fun Context.px2sp(px: Int): Float = px.toFloat() / resources.displayMetrics.scaledDensity

fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)


//the same for nested DSL components
inline fun AnkoContext<*>.dip(value: Int): Int = ctx.dip(value)
inline fun AnkoContext<*>.dip(value: Float): Int = ctx.dip(value)
inline fun AnkoContext<*>.sp(value: Int): Int = ctx.sp(value)
inline fun AnkoContext<*>.sp(value: Float): Int = ctx.sp(value)
inline fun AnkoContext<*>.px2dip(px: Int): Float = ctx.px2dip(px)
inline fun AnkoContext<*>.px2sp(px: Int): Float = ctx.px2sp(px)
inline fun AnkoContext<*>.dimen(@DimenRes resource: Int): Int = ctx.dimen(resource)

//the same for the views
inline fun View.dip(value: Int): Int = context.dip(value)
inline fun View.dip(value: Float): Int = context.dip(value)
inline fun View.sp(value: Int): Int = context.sp(value)
inline fun View.sp(value: Float): Int = context.sp(value)
inline fun View.px2dip(px: Int): Float = context.px2dip(px)
inline fun View.px2sp(px: Int): Float = context.px2sp(px)
inline fun View.dimen(@DimenRes resource: Int): Int = context.dimen(resource)

//the same for Fragments
inline fun Fragment.dip(value: Int): Int = requireActivity().dip(value)
inline fun Fragment.dip(value: Float): Int = requireActivity().dip(value)
inline fun Fragment.sp(value: Int): Int = requireActivity().sp(value)
inline fun Fragment.sp(value: Float): Int = requireActivity().sp(value)
inline fun Fragment.px2dip(px: Int): Float = requireActivity().px2dip(px)
inline fun Fragment.px2sp(px: Int): Float = requireActivity().px2sp(px)
inline fun Fragment.dimen(@DimenRes resource: Int): Int = requireActivity().dimen(resource)
