package com.san.kir.manger

//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
//import androidx.navigation.testing.TestNavHostController
//import androidx.test.core.app.ApplicationProvider
//import com.san.kir.library.LibraryScreen
import org.junit.Rule
import org.junit.Test

class LibraryInstrumentedTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun libraryTest() {
//        val nav = TestNavHostController(ApplicationProvider.getApplicationContext())
//        composeRule.setContent {
//            MaterialTheme(colors = darkColors()) {
//                LibraryScreen(nav = nav)
//            }
//        }

        composeRule.onRoot().printToLog("TAG")
    }
}
