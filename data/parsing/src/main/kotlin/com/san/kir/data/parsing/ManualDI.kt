package com.san.kir.data.parsing

import com.san.kir.core.internet.connectManager
import com.san.kir.core.utils.ManualDI

public fun ManualDI.siteCatalogsManager(): SiteCatalogsManager =
    SiteCatalogsManager(application, connectManager())
