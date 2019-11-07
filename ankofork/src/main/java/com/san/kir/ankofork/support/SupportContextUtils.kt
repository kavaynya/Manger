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

@file:Suppress("unused")
package com.san.kir.ankofork.support

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

inline val Fragment.defaultSharedPreferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(activity)

@Deprecated(message = "Use either activity or requireActivity", replaceWith = ReplaceWith("activity"))
inline val Fragment.act: FragmentActivity
    get() = requireActivity()

@Deprecated(message = "Use either context or requireContext", replaceWith = ReplaceWith("context"))
inline val Fragment.ctx: Context
    get() = requireActivity()
