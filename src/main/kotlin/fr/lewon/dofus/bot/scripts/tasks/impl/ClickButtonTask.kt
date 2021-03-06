package fr.lewon.dofus.bot.scripts.tasks.impl

import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.util.DTBConfigManager
import fr.lewon.dofus.bot.util.GameInfoUtil
import fr.lewon.dofus.bot.util.RobotUtil

class ClickButtonTask(
    controller: DofusTreasureBotGUIController,
    parentLogItem: LogItem?,
    private val imagePath: String
) : DofusBotTask<Boolean>(controller, parentLogItem) {

    override fun execute(logItem: LogItem): Boolean {
        val buttonLocation = GameInfoUtil.getButtonCenter(controller.captureGameImage(), imagePath)
            ?: throw Exception("Failed to locate button")
        val screenBounds = controller.getGameScreen().defaultConfiguration.bounds
        val restPos = DTBConfigManager.config.mouseRestPos
        RobotUtil.click(
            screenBounds.x + buttonLocation.first,
            screenBounds.y + buttonLocation.second
        )
        RobotUtil.move(
            screenBounds.x + restPos.first,
            screenBounds.y + restPos.second
        )
        return true
    }

    override fun onFailed(exception: Exception, logItem: LogItem) {
        controller.closeLog("KO - ${exception.localizedMessage}", logItem)
    }

    override fun onSucceeded(value: Boolean, logItem: LogItem) {
        controller.closeLog("OK", logItem)
    }

    override fun onStarted(parentLogItem: LogItem?): LogItem {
        return controller.log("Clicking [$imagePath] button ... ", parentLogItem)
    }
}