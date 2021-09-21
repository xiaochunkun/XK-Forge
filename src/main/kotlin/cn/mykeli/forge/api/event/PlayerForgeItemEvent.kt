package cn.mykeli.forge.api.event

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author 小坤
 * @date 2021/9/8 23:54
 */
class PlayerForgeItemEvent(val player: Player, val map: ItemStack, val itemStack: ItemStack) : BukkitProxyEvent()