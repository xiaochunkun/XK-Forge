package cn.mykeli.forge.util

import cn.mykeli.forge.Forge
import cn.mykeli.forge.module.conf.Config
import cn.mykeli.forge.module.conf.MessageConfig
import cn.mykeli.forge.module.conf.ProbabilityConfig
import cn.mykeli.forge.module.conf.StrengthItemConfig
import io.lumine.xikage.mythicmobs.MythicMobs
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter
import io.lumine.xikage.mythicmobs.items.MythicItem
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.console
import taboolib.common.util.random
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.util.getStringColored
import taboolib.module.configuration.util.getStringListColored
import taboolib.platform.util.buildItem
import taboolib.platform.util.isAir
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * @author 小坤
 * @date 2021/9/6 22:48
 */
object ItemUtil {

    /**
     * 读取section的ItemStack
     * @param section ConfigurationSection
     * @param key String
     * @return ItemStack
     */
    fun getItem(section: ConfigurationSection, key: String): ItemStack {
        if (section.getString("$key.Type").equals("mythic", true)) return getMMItem(section.getString(("$key.Name")))
        var material: XMaterial
        try {
            material = XMaterial.matchXMaterial(section.getString("$key.Material")!!).get()
        } catch (e: Exception) {
            console().sendMessage(
                MessageConfig.materialNoFound.replace(
                    "%material%",
                    section.getString("$key.Material")
                )
            )
            return XMaterial.AIR.parseItem()!!
        }
        return buildItem(material) {
            this.name = section.getStringColored(("$key.Name"))
            this.lore.addAll(section.getStringListColored("$key.Lore"))
            this.isUnbreakable = section.getBoolean("$key.Unbreakable", false)
            section.getStringList("$key.ItemFlags").forEach { flag ->
                var itemFlag = flag
                if (!(itemFlag.contains("HIDE_", true))) {
                    itemFlag = "HIDE_$itemFlag"
                }
                try {
                    this.flags.add(ItemFlag.valueOf(itemFlag))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            section.getStringList("$key.Enchantments").forEach { enchant ->
                try {
                    val en = Enchantment.getByName(enchant.split(":")[0])!!
                    val i = enchant.split(":")[1].toInt()
                    this.enchants[en] = i
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    /**
     * 在有MM插件的情况下获取MM的物品
     * @param key String
     * @return ItemStack
     */
    private fun getMMItem(key: String): ItemStack {
        if (!Forge.mm) return XMaterial.AIR.parseItem()!!
        val mythicItemOptional: Optional<MythicItem> =
            MythicMobs.inst().itemManager.getItem(key)
        var item = XMaterial.AIR.parseItem()!!
        if (mythicItemOptional.isPresent) {
            val mythicItem: MythicItem = mythicItemOptional.get()
            item = BukkitAdapter.adapt(mythicItem.generateItemStack(1, null, null))
        }
        return item
    }

    /**
     * 获取强度
     * @param item ItemStack?
     * @return IntArray
     */
    fun getStrength(item: ItemStack?): IntArray {
        var random = random(10) + 1
        val array = intArrayOf(random, 0)
        if (random >= 10) {
            array[0] = 10
            return array
        }
        if (item.isAir()) return array
        val itemStack = item!!.clone()
        itemStack.amount = 1
        if (!StrengthItemConfig.data.containsKey(itemStack)) return array
        val data = StrengthItemConfig.data[itemStack]
        val amount = item.amount
        for (i in 1..amount) {
            array[0] += data!!.num
            array[1] += 1
            if (array[0] >= 10) break
            if (!data.add) break
        }
        return array
    }

    /**
     * 获取品质
     * @param quality String
     */
    fun getQuality(quality: String): String {
        val probability = ProbabilityConfig.map[quality]!!
        var maxWeight = 0
        var weight = 0
        var level = ""
        probability.values.forEach {
            maxWeight += it.weight
        }
        val random = random(maxWeight)
        probability.values.forEach {
            weight += it.weight
            if (weight > random) {
                level = it.name
                return level
            }
        }
        return level
    }

    /**
     * 获取强度颜色字符
     * @param num Int
     * @return String
     */
    fun getStrengthColor(num: Int): String {
        val color = Config.strengthColor.substring(num - 1, num)
        var string = ""
        for (i in 0 until 10) {
            string += if (i < num) {
                "§$color${Config.strength}"
            } else {
                "§8${Config.strength}"
            }
        }
        return string
    }

    /**
     * 获取当前时间
     * @return String
     */
    fun getTime(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(Config.timeFormat))
    }

}