package cn.mykeli.forge.api

import cn.mykeli.forge.module.conf.PlayerConfig
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

/**
 * @author 小坤
 */
object HookPlaceholderAPI : PlaceholderExpansion {

    override val identifier = "forge"

    override fun onPlaceholderRequest(player: Player, args: String): String {
        if (player.isOnline) {
            val data = PlayerConfig.data[player]!!
            return when (args.lowercase()) {
                "level" -> data.level
                "exp" -> data.exp
                "needexp" -> data.needExp()
                "name" -> data.getLevelName()
                else -> ""
            }.toString()
        }
        return ""
    }
}

