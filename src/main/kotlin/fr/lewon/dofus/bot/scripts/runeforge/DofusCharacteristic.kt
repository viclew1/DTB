package fr.lewon.dofus.bot.scripts.runeforge

import fr.lewon.dofus.bot.util.LevenshteinDistanceUtil

enum class DofusCharacteristic(
    val caracName: String,
    val runeName: String,
    val normalWeight: Float,
    val paWeight: Float? = null,
    val raWeight: Float? = null
) {

    FORCE("force", "fo", 1f, 3f, 10f),
    INTELLIGENCE("intelligence", "ine", 1f, 3f, 10f),
    CHANCE("chance", "cha", 1f, 3f, 10f),
    AGILITY("agilité", "age", 1f, 3f, 10f),
    VITALITY("vitalité", "vi", 1f, 3f, 10f),
    INITIATIVE("initiative", "ini", 1f, 3f, 10f),
    WISDOM("sagesse", "sa", 3f, 9f, 30f),
    PROSPECT("prospection", "prospe", 3f, 9f),
    POWER("puissance", "pui", 2f, 6f, 20f),
    EARTH_RES("résistance terre", "re terre", 2f),
    FIRE_RES("résistance feu", "re feu", 2f),
    WATER_RES("résistance eau", "re eau", 2f),
    AIR_RES("résistance air", "re air", 2f),
    NEUTRAL_RES("résistance neutre", "re neutre", 2f),
    EARTH_PER_RES("% résistance terre", "re per terre", 6f),
    FIRE_PER_RES("% résistance feu", "re per feu", 6f),
    WATER_PER_RES("% résistance eau", "re per eau", 6f),
    AIR_PER_RES("% résistance air", "re per air", 6f),
    NEUTRAL_PER_RES("% résistance neutre", "re per neutre", 6f),
    PUSH_RES("résistance poussée", "re pou", 2f, 6f),
    CRI_RES("résistance critiques", "re cri", 2f, 6f),
    AP_RES("esquive pa", "re pa", 7f, 21f),
    MP_RES("esquive pm", "re pm", 7f, 21f),
    AP_RET("retrait pa", "ret pa", 7f, 21f),
    MP_RET("retrait pm", "ret pm", 7f, 21f),
    POD("pods", "pod", 2.5f, 7.5f, 25f),
    TACKLE("tacle", "tacle", 4f, 12f),
    FLEE("fuite", "fuite", 4f, 12f),
    DAMAGE("dommages", "do", 20f),
    EARTH_DAMAGE("dommages terre", "do terre", 5f, 15f),
    NEUTRAL_DAMAGE("dommages neutre", "do neutre", 5f, 15f),
    FIRE_DAMAGE("dommages feu", "do feu", 5f, 15f),
    AIR_DAMAGE("dommages air", "do air", 5f, 15f),
    WATER_DAMAGE("dommages eau", "do eau", 5f, 15f),
    PUSH_DAMAGE("dommages poussée", "do pou", 5f, 15f),
    CRITICAL_DAMAGE("dommages critiques", "do cri", 5f, 15f),
    TRAP_DAMAGE("dommages pièges", "do pi", 5f, 15f),
    DISTANCE_DAMAGE("dommages distance", "do per di", 15f),
    WEAPON_DAMAGE("dommages d'armes", "do per ar", 15f),
    SPELL_DAMAGE("dommages aux sorts", "do per so", 15f),
    MELEE_DAMAGE("dommages mêlée", "do per me", 15f),
    DISTANCE_RES("resistance distance", "re per di", 15f),
    MELEE_RES("resistance mêlée", "re per me", 15f),
    TRAP_POWER("puissance (pièges)", "pui pie", 2f, 6f, 20f),
    HEAL("soins", "so", 10f),
    CRITICAL("critique", "cri", 10f),
    SUMMON("invocations", "invo", 30f),
    RANGE("portée", "po", 51f),
    AP("pa", "gapa", 100f),
    MP("pm", "gapm", 90f);

    companion object {
        fun closestFromName(name: String): DofusCharacteristic {
            val closestName = LevenshteinDistanceUtil.getClosestString(
                name.toLowerCase(),
                values().map { it.caracName.toLowerCase() })
            return values().first { it.caracName.equals(closestName, ignoreCase = true) }
        }
    }
}