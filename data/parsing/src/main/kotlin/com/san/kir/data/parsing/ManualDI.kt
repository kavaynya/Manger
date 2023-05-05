package com.san.kir.data.parsing

import com.san.kir.core.internet.connectManager
import com.san.kir.core.utils.ManualDI

private var singletonSiteCatalogsManager: SiteCatalogsManager? = null

val ManualDI.siteCatalogsManager: SiteCatalogsManager
    get() = singletonSiteCatalogsManager ?: run {
        val instance = SiteCatalogsManager(context, connectManager)
        singletonSiteCatalogsManager = instance
        instance
    }
