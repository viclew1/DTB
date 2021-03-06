package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.util.GameInfoUtil
import fr.lewon.dofus.bot.util.RobotUtil
import fr.lewon.dofus.bot.util.WindowsUtil
import java.util.*

object RestartGameScript : DofusBotScript("Restart game") {

    override fun getParameters(): List<DofusBotScriptParameter> {
        return emptyList()
    }

    override fun getStats(): List<Pair<String, String>> {
        return emptyList()
    }

    override fun getDescription(): String {
        return "Restarts the game, helps with getting rid of the memory saturation occurring after the game being opened for a while"
    }

    override fun doExecute(
        controller: DofusTreasureBotGUIController,
        logItem: LogItem?,
        parameters: Map<String, DofusBotScriptParameter>
    ) {
        if (WindowsUtil.isGameOpen()) {
            execTimeoutOpe({ openMenu() }, { imgFound("exit_dofus.png", 0.8) })
            execTimeoutOpe({ click("exit_dofus.png") }, { imgFound("yes.png", 0.8) })
            execTimeoutOpe({ click("yes.png") }, { !WindowsUtil.isGameOpen() })
            WindowsUtil.cleanCache(controller, logItem)
            sleep(2500)
        }

        WindowsUtil.openGame(controller, logItem)
        val moveScreenLog = controller.log("Moving game to screen [${controller.getGameScreen().iDstring}]...", logItem)
        WindowsUtil.bringGameToFront(controller.getGameScreen())
        controller.closeLog("OK", moveScreenLog)

        val loginLog = controller.log("Waiting for the login panel to be shown ...", logItem)
        execTimeoutOpe({}, { imgFound("updater_warning.png", 0.8) || imgFound("login_panel.png", 0.9) })
        if (imgFound("ok_cancel.png", 0.8)) {
            click("ok_cancel.png", loginLog)
        }
        execTimeoutOpe({}, { imgFound("login_panel.png", 0.8) })
        controller.closeLog("OK", loginLog)

        if (imgFound("login_panel.png", 0.8)) {
            val user = controller.getUser()
            val enterLoginsLog = controller.log("Login with username [${user.login}]", logItem)
            clickPoint(830, 350)
            sleep(1000)
            RobotUtil.write(user.login)
            sleep(200)
            clickPoint(830, 410)
            sleep(1000)
            RobotUtil.write(String(Base64.getDecoder().decode(user.password)))
            sleep(200)
            RobotUtil.enter()
            controller.closeLog("OK", enterLoginsLog)
        }

        val loginOrReadyLog = controller.log("Waiting for the character selection or the final login ...", logItem)
        execTimeoutOpe({}, {
            imgFound("enter_game.png", 0.8) || GameInfoUtil.getLocation(controller.captureGameImage()) != null
        }, 180)
        controller.closeLog("OK", loginOrReadyLog)

        if (imgFound("enter_game.png", 0.8)) {
            RobotUtil.enter()
            val readyLog = controller.log("Waiting for the character to be logged in ...", logItem)
            execTimeoutOpe({ }, { GameInfoUtil.getLocation(controller.captureGameImage()) != null }, 180)
            controller.closeLog("OK", readyLog)
        }

    }

}