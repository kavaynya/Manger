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

package com.san.kir.ankofork

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.preference.PreferenceManager
import android.view.View
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

inline val AnkoContext<*>.resources: Resources
    get() = ctx.resources

inline val Context.defaultSharedPreferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(this)

inline val Fragment.act: FragmentActivity?
    get() = activity

inline val Fragment.ctx: FragmentActivity?
    get() = activity

inline fun <reified T : View> View.find(@IdRes id: Int): T = findViewById(id)
inline fun <reified T : View> Activity.find(@IdRes id: Int): T = findViewById(id)
inline fun <reified T : View> Fragment.find(@IdRes id: Int): T = view?.findViewById(id) as T
inline fun <reified T : View> Dialog.find(@IdRes id: Int): T = findViewById(id)

inline fun <reified T : View> Activity.findOptional(@IdRes id: Int): T? = findViewById(id) as? T

fun <T: Fragment> T.withArguments(vararg params: Pair<String, Any?>): T {
    arguments = bundleOf(*params)
    return this
}

inline val Context.configuration: Configuration
    get() = resources.configuration

inline val AnkoContext<*>.configuration: Configuration
    get() = ctx.resources.configuration

inline val Configuration.long: Boolean
    get() = (screenLayout and Configuration.SCREENLAYOUT_LONG_YES) != 0
