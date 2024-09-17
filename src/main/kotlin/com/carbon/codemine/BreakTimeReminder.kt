package com.carbon.codemine

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.awt.ComposePanel
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.ui.DialogWrapper
import java.util.*
import javax.swing.JComponent
import kotlin.concurrent.timerTask
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


class BreakTimeStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        val reminderTimer = service<ReminderTimer>()

        reminderTimer.startup();
    }
}


@Service
class ReminderTimer {

    private val timer = Timer(true)

    private var startAt: Long = 0

    private val remindPer = 1.minutes


    fun startup() {
        startAt = System.currentTimeMillis()
        timer.schedule(timerTask {
            runInEdt {
                RemindToBreakDialog().show()
            }

        }, remindPer.inWholeMilliseconds)
    }

    fun pause() {

    }

}

class RemindToBreakDialog : DialogWrapper(false) {

    init {
        title = "休息一下吧"
        isOKActionEnabled = false
        setCancelButtonText("稍后提醒")
        init()
    }

    override fun beforeShowCallback() {
        var breakDuration = 10.minutes
        val timer = Timer()
        timer.schedule(timerTask {
            breakDuration = breakDuration.minus(1.seconds)
            if (breakDuration.inWholeSeconds <= 0) {
                this@RemindToBreakDialog.close(OK_EXIT_CODE)
                cancel()
            }
            setOKButtonText(breakDuration.toString())

        }, 0, 1.seconds.inWholeMilliseconds)
    }

    override fun createCenterPanel(): JComponent {
        return ComposePanel().apply {
            setContent {
                Text("Code Mine")
            }
        }
    }

}
