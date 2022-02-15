package cn.mykeli.forge.module.conf

import cn.mykeli.forge.util.ItemUtil
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.console
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.SecuredFile
import taboolib.platform.BukkitPlugin
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.hasLore
import java.io.File

/**
 * @author 小坤
 */
object ItemConfig {

    private val file by lazy {
        File(BukkitPlugin.getInstance().dataFolder, "item")
    }

    var map = HashMap<String, ItemStack>()

    /**
     * 加载文件夹所有的配置文件
     */
    fun loadConfig() {
        map = hashMapOf()
        if (!file.isDirectory || file.listFiles().isEmpty()) {
            releaseResourceFile("item/defaultItem.yml", true)
            releaseResourceFile("item/功能物品.yml", true)
        }
        file.listFiles().forEach {
            if (it.name.endsWith(".yml")) {
                val name = it.name.replace(".yml", "")
                val section = Yml(name).yml.getConfigurationSection("")
                section?.getKeys(false)?.forEach { key ->
                    map[key] = ItemUtil.getItem(section, key)
                }
            }
        }
        console().sendMessage(MessageConfig.itemLoad)
    }

    /**
     * 获取物品
     * @param key String
     * @return ItemStack
     */
    fun getItem(key: String): ItemStack {
        return if (map.containsKey(key)) {
            map[key]!!.clone()
        } else {
            console().sendMessage(MessageConfig.itemNoFound.replace("%item%", key))
            XMaterial.AIR.parseItem()!!
        }
    }

    /**
     * 获取物品
     * @param key String
     * @return ItemStack
     */
    fun getItem(key: String, player: Player): ItemStack {
        if (map.containsKey(key)) {
            val item = map[key]!!.clone()
            if (!item.hasItemMeta()) return item
            val meta = item.itemMeta
            if (!item.hasLore()) return item
            meta!!.lore = meta.lore!!.replacePlaceholder(player)
            item.itemMeta = meta
            return item
        } else {
            console().sendMessage(MessageConfig.itemNoFound.replace("%item%", key))
            return XMaterial.AIR.parseItem()!!
        }
    }

    /**
     * 用来获取配置文件的类
     * @author 小坤
     * @property yml SecuredFile
     * @constructor
     */
    class Yml constructor(name: String) {
        val yml = SecuredFile()

        init {
            try {
                yml.loadFromFile(
                    File(
                        file,
                        "/$name.yml"
                    )
                )
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

}



