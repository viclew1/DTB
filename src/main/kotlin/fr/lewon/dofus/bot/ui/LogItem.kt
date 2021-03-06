package fr.lewon.dofus.bot.ui

import java.util.*

class LogItem(var message: String) {

    private val subLogs = LinkedList<LogItem>()
    private var closeMessage: String? = null

    fun addSubItem(logItem: LogItem) {
        while (subLogs.size > 30) {
            subLogs.removeFirst()
        }
        subLogs.add(logItem)
    }

    override fun toString(): String {
        return displayLog(0)
    }

    private fun displayLog(depth: Int): String {
        var prefix = ""
        for (i in 0 until depth) {
            prefix += " - "
        }
        var ret = prefix + message
        for (subLog in subLogs) {
            ret += "\n" + subLog.displayLog(depth + 1)
        }
        if (closeMessage != null) {
            ret += if (subLogs.isEmpty()) {
                " $closeMessage"
            } else {
                "\n$prefix$closeMessage"
            }
        }
        return ret
    }

    fun closeLog(message: String) {
        closeMessage = message
    }
}