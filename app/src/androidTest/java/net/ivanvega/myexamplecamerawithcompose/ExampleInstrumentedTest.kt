package net.ivanvega.myexamplecamerawithcompose

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Prueba instrumentada, que se ejecutará en un dispositivo Android.
 *
 * Consulte [documentación de prueba] (http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("net.ivanvega.myexamplecamerawithcompose", appContext.packageName)
    }
}