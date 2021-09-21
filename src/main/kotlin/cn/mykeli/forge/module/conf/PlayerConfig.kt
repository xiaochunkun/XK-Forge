package cn.mykeli.forge.module.conf

import cn.mykeli.forge.Forge
import cn.mykeli.forge.api.event.PlayerAddExpEvent
import cn.mykeli.forge.api.event.PlayerForgeCommandEvent
import cn.mykeli.forge.api.event.PlayerForgeItemEvent
import cn.mykeli.forge.api.event.PlayerUpLevelEvent
import cn.mykeli.forge.util.ItemUtil
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.console
import taboolib.common5.compileJS
import taboolib.library.xseries.XMaterial
import taboolib.module.chat.colored
import taboolib.module.configuration.createLocal
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.hasLore
import taboolib.platform.util.hasName
import taboolib.platform.util.isAir
import java.util.regex.Pattern

/**
 * @author 小坤
 * @date 2021/9/7 23:04
 */
class PlayerConfig constructor(player: Player) {

    private val player = player
    private var yml = createLocal("player/${player.name}.yml")
    var level = 0
    var exp = 0
    private var map = arrayListOf<String>()
    var historyItem = HashMap<Int, HashMap<String, List<String>>>()

    companion object {
        val data = HashMap<Player, PlayerConfig>()
    }

    /**
     * 加载配置文件
     */
    fun loadConfig() {
        data[player] = this
        yml.reload()
        map = arrayListOf()
        yml.getStringList("Map").forEach {
            if (MapConfig.map.containsKey(it)){
                map.add(it)
            }
        }
        level = yml.getInt("Level", 1)
        exp = yml.getInt("Exp", 0)
        val section = yml.getConfigurationSection("Item")
        section?.getKeys(false)?.forEach {
            val hashMap = HashMap<String, List<String>>()
            hashMap[section.getString("$it.Name")] = section.getStringList("$it.Lore")
            historyItem[it.toInt()] = hashMap
        }
    }

    /**
     * 添加图纸
     * @param name String
     */
    fun addMap(name: String): Boolean {
        if (map.contains(name)) {
            player.sendMessage(MessageConfig.commandHasMap.replace("%item%", name))
            return false
        }
        if (MapConfig.map[name]!!.level > level) {
            player.sendMessage(MessageConfig.noLevel)
            return false
        }
        if (!MapConfig.map[name]!!.study) {
            player.sendMessage(MessageConfig.noStudy)
            return false
        }
        map.add(name)
        yml.set("Map", map)
        yml.saveToFile()
        val item = MapConfig.map[name]!!.map
        val displayName =
            if (item.hasName() && item.itemMeta!!.displayName.length > 2) item.itemMeta!!.displayName else name.colored()
        player.sendMessage(MessageConfig.commandStudy.replace("%item%", displayName))
        return true
    }

    /**
     * 添加经验
     */
    fun addExp(num: Int) {
        val addExpEvent = PlayerAddExpEvent(player, num)
        addExpEvent.call()
        if (addExpEvent.isCancelled) return
        val needExp = needExp()
        this.exp += addExpEvent.num
        if (this.exp >= needExp) {
            val upLevelEvent = PlayerUpLevelEvent(player, this.level, this.level + 1)
            upLevelEvent.call()
            if (!upLevelEvent.isCancelled) {
                this.level = upLevelEvent.newLevel
                player.sendMessage(
                    MessageConfig.upLevel.replace("%player%", player.name).replace("%level%", level.toString())
                )
            }
        }
        yml.set("Level", this.level)
        yml.set("Exp", this.exp)
        yml.saveToFile()
        player.sendMessage(
            MessageConfig.addExp.replace("%player%", player.name).replace("%exp%", num.toString())
        )
    }

    /**
     * 设置玩家等级
     * @param num Int
     */
    fun setPlayerLevel(num: Int) {
        this.level = num
        yml.set("Level", this.level)
        yml.saveToFile()
        player.sendMessage(
            MessageConfig.upLevel.replace("%player%", player.name).replace("%level%", level.toString())
        )
    }

    /**
     * 增加玩家等级
     * @param num Int
     */
    fun addLevel(num: Int) {
        this.level += num
        yml.set("Level", this.level)
        yml.saveToFile()
        player.sendMessage("")
        player.sendMessage(
            MessageConfig.upLevel.replace("%player%", player.name).replace("%level%", level.toString())
        )
    }

    /**
     * 获取玩家升级需要的经验值
     * @return Int
     */
    fun needExp(): Int {
        var needExp = 0
        LevelConfig.level.forEach {
            if (this.level == it.level) {
                needExp = it.exp - this.exp
                return needExp
            }
        }
        return needExp
    }

    /**
     * 获取玩家等级称谓
     * @return String
     */
    fun getLevelName(): String {
        val name = ""
        LevelConfig.level.forEach {
            if (this.level == it.level) {
                return it.name
            }
        }
        return name
    }

    /**
     * 添加历史锻造物品
     */
    private fun addItem(item: ItemStack) {
        val num = historyItem.size
        if (item.isAir() || !item.hasItemMeta() || !item.hasName() || !item.hasLore()) return
        val name = item.itemMeta!!.displayName
        val lore = item.itemMeta!!.lore!!
        yml.set("Item.$num.Name", name)
        yml.set("Item.$num.Lore", lore)
        var hashMap = HashMap<String, List<String>>()
        hashMap[name] = lore
        this.historyItem[num] = hashMap
        yml.saveToFile()
    }

    /**
     * 开始锻造
     */
    fun forge(item: ItemStack, strength: Int) {
        val key = MapConfig.getKey(item) ?: return
        val type = MapConfig.map[key]!!.type
        if (type == "item") {
            val data = MapConfig.map[key] as MapConfig.ItemData
            val quality = ItemUtil.getQuality(data.probability)
            val probability = data.probability
            val itemStack = ItemConfig.getItem(data.item)
            val forgeItem = forgeItem(itemStack, quality, strength, probability)
            val event = PlayerForgeItemEvent(player, item, forgeItem)
            event.call()
            if (event.isCancelled) return
            player.inventory.addItem(event.itemStack)
            addItem(event.itemStack)
            addExp(data.exp)
            player.sendMessage(MessageConfig.itemSuccess)
        }
        if (type == "command") {
            val data = MapConfig.map[key] as MapConfig.CommandData
            val commands = data.command
            val event = PlayerForgeCommandEvent(player, item, commands)
            event.call()
            if (event.isCancelled) return
            //锻造执行命令
            event.command.forEach {
                var command = it.replace("%player%", player.name)
                if (Forge.papi) command = command.replacePlaceholder(player)
                if (command.contains("[command]")) {
                    try {
                        console().performCommand(command.replace("[command]", ""))
                    } catch (e: Exception) {
                        console().sendMessage(MessageConfig.commandError)
                    }
                } else if (command.contains("[op]")) {
                    command = command.replace("[op]", "")
                    if (player.isOp) {
                        try {
                            player.performCommand(command)
                        } catch (e: Exception) {
                            console().sendMessage(MessageConfig.commandError)
                        }
                    } else {
                        player.isOp = true
                        try {
                            player.performCommand(command)
                        } catch (e: Exception) {
                            console().sendMessage(MessageConfig.commandError)
                        }
                        player.isOp = false
                    }
                }
            }
            player.sendMessage(MessageConfig.commandSuccess)
        }
    }

    /**
     * 获取锻造返回的物品
     * @param item ItemStack
     * @param quality String
     * @param strength Int
     * @param attribute HashMap<String, String>
     * @return ItemStack
     */
    private fun forgeItem(
        item: ItemStack,
        quality: String,
        strength: Int,
        probability: String
    ): ItemStack {
        if (!item.hasLore()) return XMaterial.AIR.parseItem()!!
        val attribute = ProbabilityConfig.map[probability]!![quality]!!.attribute
        val list = arrayListOf<String>()
        val meta = item.itemMeta!!
        meta.lore?.forEach {
            var lore = it

            if (lore.contains("%author%")) {
                lore = lore.replace("%author%", player.name, true)
            }

            if (lore.contains("%quality%")) {
                lore = lore.replace("%quality%", quality.colored(), true)
            }

            if (lore.contains("%strength%")) {
                lore = lore.replace("%strength%", ItemUtil.getStrengthColor(strength), true)
            }

            if (lore.contains("%time%")) {
                lore = lore.replace("%time%", ItemUtil.getTime(), true)
            }

            if (lore.contains("<") && lore.contains(":") && lore.contains(">")) {
                //通过正则 取出对应数字   如果是<攻击力:50> 那么HashMap放的就是(攻击力,50)
                // 如果是<攻击力:50> - <攻击力2:100> 那么HashMap放的就是(攻击力,50)(攻击力2,100)
                val m = Pattern.compile("<(.*?):(.*?)>").matcher(lore)
                if (m.find()) {
                    var start = 0
                    while (m.find(start)) {
                        val key = m.group(1)
                        val num = m.group(2)
                        start = m.end()
                        if (!attribute.containsKey(key)) continue
                        val str = attribute[key]!!.replace("%值%", num).replace("%强度%", strength.toString())
                        val att = str.compileJS()?.eval() ?: continue
                        lore = lore.replace("<$key:$num>", att.toString())
                    }
                }
            }
            list.add(lore)
        }
        meta.lore = list
        item.itemMeta = meta
        return item
    }


    /**
     * 获取玩家全部图纸物品
     * @return ArrayList<ItemStack>
     */
    fun getMapItem(): ArrayList<ItemStack> {
        val list = arrayListOf<ItemStack>()
        map.forEach {
            try {
                list.add(MapConfig.map[it]!!.map)
            } catch (e: Exception) {
                console().sendMessage(MessageConfig.mapNoFound.replace("%item%", it))
            }
        }
        return list
    }

}