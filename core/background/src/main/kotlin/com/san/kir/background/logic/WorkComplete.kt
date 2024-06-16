package com.san.kir.background.logic

import java.util.concurrent.CancellationException

internal data object WorkComplete : CancellationException() {
    private fun readResolve(): Any = WorkComplete
}
