package com.san.kir.background.logic

import java.util.concurrent.CancellationException

data object WorkComplete : CancellationException() {
    private fun readResolve(): Any = WorkComplete
}
