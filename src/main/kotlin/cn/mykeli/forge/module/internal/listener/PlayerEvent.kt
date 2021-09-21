package cn.mykeli.forge.module.internal.listener

import cn.mykeli.forge.module.conf.MapConfig
import cn.mykeli.forge.module.conf.MessageConfig
import cn.mykeli.forge.module.conf.PlayerConfig
import cn.mykeli.forge.module.internal.ForgeMenu
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.isAir

/**
 * @author 小坤
 */
object PlayerEvent {

    @SubscribeEvent
    private fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        PlayerConfig(player).loadConfig()
    }

    @SubscribeEvent
    private fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item
        if (item.isAir()) return
        val itemStack = item!!.clone()
        itemStack.amount = 1
        val key = MapConfig.getKey(itemStack) ?: return
        val data = PlayerConfig.data[player]!!
        if (MapConfig.map[key]!!.study) {
            if (data.addMap(key)) item.amount -= 1
        } else {
            if (MapConfig.map[key]!!.level > data.level) {
                player.sendMessage(MessageConfig.noLevel)
            } else {
                ForgeMenu.openForgeMenu(player, itemStack,false)
                item.amount -= 1
            }
        }
    }

}