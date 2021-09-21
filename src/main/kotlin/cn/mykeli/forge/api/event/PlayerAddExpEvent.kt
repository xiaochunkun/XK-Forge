package cn.mykeli.forge.api.event

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * 玩家增加经验事件
 * @author 小坤
 */
class PlayerAddExpEvent(val player: Player, val num: Int) : BukkitProxyEvent()