package cn.mykeli.forge.module.internal

import cn.mykeli.forge.module.conf.*
import cn.mykeli.forge.util.ItemUtil
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.ClickType
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked
import taboolib.platform.util.checkItem
import taboolib.platform.util.inventoryCenterSlots
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir

object ForgeMenu {

    /**
     * 打开主界面
     * @param player Player
     */
    fun openMenu(player: Player) {
        var num = 0
        player.openMenu<Linked<ItemStack>>(Config.mainTitle) {
            rows(Config.mainRows)
            slots(inventoryCenterSlots)
            Config.mainItem.keys.forEach {
                set(it, Config.mainItem[it]!!)
            }
            PlayerConfig.data[player]?.getMapItem()?.forEach {
                set(Config.mainMap[num].toInt(), it) {
                    if (this.clickType != ClickType.CLICK) return@set
                    if (it.isAir()) return@set
                    val item = this.currentItem
                    val data = MapConfig.map[MapConfig.getKey(item!!)]!!
                    val level = data.level
                    if (PlayerConfig.data[player]!!.level >= level) {
                        openForgeMenu(player, item, true)
                    } else {
                        player.sendMessage(MessageConfig.noLevel)
                        player.closeInventory()
                    }
                }
                num += 1
            }
            setPreviousPage(Config.mainLast) { _, _ ->
                Config.mainLastItem!!
            }
            setNextPage(Config.mainNext) { _, _ ->
                Config.mainNextItem!!
            }
        }
    }

    /**
     * 打开锻造界面
     * @param player Player
     * @param map ItemStack
     */
    fun openForgeMenu(player: Player, map: ItemStack, study: Boolean) {
        var num = 0
        player.openMenu<Basic>(Config.forgeTitle) {
            rows(Config.forgeRows)
            set(Config.forgeMap, map)
            set(Config.forgeForgeSlot, ItemConfig.getItem(Config.forgeForgeName))
            set(Config.forgeStrengthItemSlot, ItemConfig.getItem(Config.forgeStrengthItemName))
            Config.forgeItem.keys.forEach {
                set(it, Config.forgeItem[it]!!)
            }
            onClick {
                it.isCancelled = true
                // 监听玩家点击
                if (it.rawSlot < ((Config.forgeRows * 9) - 1)) return@onClick
                if (it.clickType != ClickType.CLICK) return@onClick
                val currentItem = it.currentItem
                if (currentItem.isAir()) return@onClick
                val item = currentItem!!.clone()
                item.amount = 1
                if (StrengthItemConfig.data.containsKey(item)) {
                    val itemStack = it.inventory.getItem(Config.forgeStrengthItemSlot)
                    if (itemStack.isNotAir()) {
                        val items = itemStack!!.clone()
                        items.amount = 1
                        if (StrengthItemConfig.data.containsKey(items)) {
                            player.inventory.addItem(itemStack)
                        }
                    }
                    it.inventory.setItem(Config.forgeStrengthItemSlot, currentItem)
                    it.currentItem = XMaterial.AIR.parseItem()!!
                }
            }

            val material = MapConfig.getMaterial(map)
            material.forEach {
                set(Config.forgeMaterial[num].toInt(), it)
                num += 1
            }

            /**
             * 监听点击开始锻造后
             */
            onClick(Config.forgeForgeSlot) {
                if (it.clickType != ClickType.CLICK) return@onClick
                val inv = it.inventory
                var hasItem = true
                //先判断是否满足  不然中途扣了的话  再加就麻烦了
                material.forEach { item ->
                    if (hasItem){
                        hasItem = player.checkItem(item, item.amount, false)
                    }
                    //if (!player.checkItem(item, item.amount, false) || !hasItem) hasItem = false
                }
                if (hasItem) {
                    material.forEach { item ->
                        player.checkItem(item, item.amount, true)
                    }
                    val strengthItem = inv.getItem(Config.forgeStrengthItemSlot)
                    val strength = ItemUtil.getStrength(strengthItem)
                    if (strength[1] > 0) {
                        if (strengthItem.isNotAir()) {
                            inv.checkItem(strengthItem!!, strength[1], true)
                        }
                    }
                    PlayerConfig.data[player]!!.forge(map, strength[0])
                    if (!study) inv.getItem(Config.forgeMap)!!.amount -= 1
                } else {
                    player.sendMessage(MessageConfig.noMaterial)
                }
                player.closeInventory()
            }

            /**
             * 监听关闭ui
             */
            onClose {
                val inv = it.inventory
                if (!study) {
                    val mapItem = inv.getItem(Config.forgeMap)
                    if (mapItem.isNotAir()) player.inventory.addItem(mapItem)
                }
                val itemStack = inv.getItem(Config.forgeStrengthItemSlot)
                if (itemStack.isAir()) return@onClose
                val item = itemStack!!.clone()
                item.amount = 1
                if (!StrengthItemConfig.data.containsKey(item)) return@onClose
                player.inventory.addItem(itemStack)
            }
        }
    }
}