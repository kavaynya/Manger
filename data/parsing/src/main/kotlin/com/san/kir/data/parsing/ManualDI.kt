package com.san.kir.data.parsing

import com.san.kir.core.internet.connectManager
import com.san.kir.core.utils.ManualDI

fun ManualDI.siteCatalogManager(): SiteCatalogsManager =
    SiteCatalogsManager(application, connectManager())
