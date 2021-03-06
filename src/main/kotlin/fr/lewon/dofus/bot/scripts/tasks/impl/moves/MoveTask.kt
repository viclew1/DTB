package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.json.DTBPoint
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import fr.lewon.dofus.bot.util.*
import java.awt.image.BufferedImage

abstract class MoveTask(
    private val direction: Directions,
    controller: DofusTreasureBotGUIController,
    parentLogItem: LogItem?
) :
    DofusBotTask<Pair<Int, Int>>(controller, parentLogItem) {

    override fun onFailed(exception: Exception, logItem: LogItem) {
        controller.closeLog("KO - ${exception.localizedMessage}", logItem)
    }

    override fun onSucceeded(value: Pair<Int, Int>, logItem: LogItem) {
        controller.closeLog("OK - [${value.first},${value.second}]", logItem)
    }

    override fun onStarted(parentLogItem: LogItem?): LogItem {
        return controller.log("Moving toward [$direction] ...", parentLogItem)
    }

    override fun execute(logItem: LogItem): Pair<Int, Int> {
        val gameImage: BufferedImage = controller.captureGameImage()
        val location = GameInfoUtil.getLocation(gameImage) ?: throw Exception("Couldn't get location")
        val moveDest =
            DTBConfigManager.config.registeredMoveLocationsByMap["${location.first}_${location.second}"]?.get(direction)
                ?.let { processMove(location, it) }
                ?: processMove(location, getMoveDest())
        moveDest?.let {
            MovesHistory.addMove(direction)
            return it
        }
        throw Exception("Failed to move toward [$direction] from [$location].")
    }

    private fun processMove(oldLocation: Pair<Int, Int>, moveDest: DTBPoint): Pair<Int, Int>? {
        val screenBounds = controller.getGameScreen().defaultConfiguration.bounds
        RobotUtil.click(
            screenBounds.x + moveDest.first,
            screenBounds.y + moveDest.second
        )
        RobotUtil.move(
            screenBounds.x + DTBConfigManager.config.mouseRestPos.first,
            screenBounds.y + DTBConfigManager.config.mouseRestPos.second
        )

        val startTimeMillis = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTimeMillis < DTBConfigManager.config.moveTimeout * 1000) {
            val newLocation = GameInfoUtil.getLocation(controller.captureGameImage())
            if (newLocation != null && oldLocation != newLocation) {
                return newLocation
            }
        }
        return null
    }

    protected abstract fun getMoveDest(): DTBPoint

}