package cn.mykeli.forge.api.event

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author 小坤
 * @date 2021/9/9 20:21
 */
class PlayerForgeCommandEvent(val player: Player, val map: ItemStack, val command: List<String>) : BukkitProxyEvent()