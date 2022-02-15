package cn.mykeli.forge.module.conf

import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.console
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.SecuredFile
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir
import java.io.File

/**
 * @author 小坤
 */
object MapConfig {

    private val file by lazy {
        File(BukkitPlugin.getInstance().dataFolder, "map")
    }

    var map = HashMap<String, Data>()


    /**
     * 加载文件夹所有的配置文件
     */
    fun loadConfig() {
        map = hashMapOf()
        if (!file.isDirectory || file.listFiles().isEmpty()) {
            releaseResourceFile("map/defaultMap.yml", true)
            releaseResourceFile("map/defaultCommand.yml", true)
        }
        file.listFiles().forEach {
            if (it.name.endsWith(".yml")) {
                val name = it.name.replace(".yml", "")
                val section = Yml(name).yml.getConfigurationSection("")
                section?.getKeys(false)?.forEach { key ->
                    val study = section.getBoolean("$key.Study")
                    val level = section.getInt("$key.Level")
                    val exp = section.getInt("$key.Exp")
                    val sec = section.getConfigurationSection("$key.Material")
                    val material = HashMap<String, Int>()
                    sec?.getKeys(false)?.forEach { k ->
                        material[k] = sec.getInt(k)
                    }
                    val map = ItemConfig.getItem(section.getString("$key.Map")!!)
                    if (section.getString("$key.Type").equals("item", true)) {
                        val item = section.getString("$key.Item")!!
                        val probability = section.getString("$key.Probability")!!
                        this.map[key] = ItemData(
                            "item",
                            study,
                            level,
                            exp,
                            map,
                            item,
                            material,
                            probability
                        )
                    }
                    if (section.getString("$key.Type").equals("command", true)) {
                        val command = section.getStringList("$key.Command")
                        this.map[key] = CommandData(
                            "command",
                            study,
                            level,
                            exp,
                            map,
                            command,
                            material
                        )
                    }
                }
            }
        }
        console().sendMessage(MessageConfig.mapLoad)
    }

    /**
     * 获取图纸在集合的key
     * @param item ItemStack
     * @return String?
     */
    fun getKey(item: ItemStack): String? {
        if (item.isAir()) return null
        val itemStack = item.clone()
        itemStack.amount = 1
        map.keys.forEach {
            val data = map[it]
            if (data!!.map == itemStack) {
                return it
            }
        }
        return null
    }

    /**
     * 获取图纸的材料
     * @param item ItemStack
     * @return ArrayList<ItemStack>
     */
    fun getMaterial(item: ItemStack): ArrayList<ItemStack> {
        val key = getKey(item)
        val list = arrayListOf<ItemStack>()
        if (key == null) {
            list.add(XMaterial.AIR.parseItem()!!)
            return list
        }
        val material = map[key]!!.material
        material.keys.forEach {
            val itemStack = ItemConfig.getItem(it)
            if (itemStack.isNotAir()) itemStack.amount = material[it]!!
            list.add(itemStack)
        }
        return list
    }

    /**
     * 用来获取配置文件的类
     * @author 小坤
     * @property yml SecuredFile
     * @constructor
     */
    private class Yml constructor(name: String) {
        val yml = SecuredFile()

        init {
            try {
                yml.loadFromFile(
                    File(
                        file,
                        "/$name.yml"
                    )
                )
                yml.reload()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    open class Data(
        val type: String,
        val study: Boolean,
        val level: Int,
        val exp: Int,
        val map: ItemStack,
        val material: HashMap<String, Int>
    )

    class ItemData(
        type: String,
        study: Boolean,
        level: Int,
        exp: Int,
        map: ItemStack,
        val item: String,
        material: HashMap<String, Int>,
        val probability: String
    ) :
        Data(type, study, level, exp, map, material)

    class CommandData(
        type: String,
        study: Boolean,
        level: Int,
        exp: Int,
        map: ItemStack,
        val command: List<String>,
        material: HashMap<String, Int>,
    ) :
        Data(type, study, level, exp, map, material)

}