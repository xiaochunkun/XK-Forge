package cn.mykeli.forge.api.event

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * 玩家升级事件
 * @author 小坤
 * @date 2021/9/8 20:26
 */
class PlayerUpLevelEvent(val player: Player, val oldLevel: Int, val newLevel: Int) : BukkitProxyEvent()