package cn.mykeli.forge.module.conf

import taboolib.common.platform.function.console
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.util.random
import taboolib.module.chat.colored
import taboolib.module.configuration.SecuredFile
import taboolib.module.configuration.util.getStringListColored
import taboolib.platform.BukkitPlugin
import java.io.File

/**
 * @author 小坤
 */
object ProbabilityConfig {
    private val file by lazy {
        File(BukkitPlugin.getInstance().dataFolder, "probability")
    }

    var map = hashMapOf<String, LinkedHashMap<String, Data>>()

    fun loadConfig() {
        map = hashMapOf()
        if (!file.isDirectory || file.listFiles() == null) {
            releaseResourceFile("probability/defaultProbability.yml", true)
            releaseResourceFile("probability/random.yml", true)
        }
        file.listFiles()?.forEach file@{
            if (it.name.endsWith(".yml")) {
                val name = it.name.replace(".yml", "")
                val section = Yml(name).yml.getConfigurationSection("")
                section?.getKeys(false)?.forEach section@{ pKey -> //节点遍历
                    val type = section.getString("$pKey.Type", "forge")!!
                    val sec = section.getConfigurationSection(pKey)
                    val pro = LinkedHashMap<String, Data>()
                    sec?.getKeys(false)?.forEach sec@{ key -> //品质遍历
                        if (key.equals("Type", true)) return@sec
                        val weight = sec.getInt("$key.Weight")
                        val attribute = HashMap<String, Any>()
                        val se = sec.getConfigurationSection("$key.Attribute") ?: return@sec
                        if (type.equals("forge", true)) {
                            se.getKeys(false).forEach { attKey -> //属性遍历
                                attribute[attKey] = se.getString(attKey)!!
                            }
                        }
                        if (type.equals("random", true)) {
                            se.getKeys(false).forEach { attKey ->
                                attribute["<$attKey>"] = RandomSub(
                                    Type.valueOf(se.getString("$attKey.Type", "fixed")!!.uppercase()),
                                    se.getString("$attKey.Value", "&7粗糙")!!.colored(),
                                    se.getInt("$attKey.Value1", 0),
                                    se.getInt("$attKey.Value2", 1),
                                    se.getInt("$attKey.Base", 0),
                                    se.getInt("$attKey.Add", 1),
                                    se.getInt("$attKey.Decimal", 2),
                                    se.getDouble("$attKey.Start", 0.00),
                                    se.getDouble("$attKey.End", 1.00),
                                    se.getStringListColored("$attKey.List"),
                                    se.getStringList("$attKey.Lore").colored()
                                )
                            }
                        }
                        pro[key] = Data(key, type, weight, attribute)
                    }
                    map[pKey] = pro
                }
            }
        }
        console().sendMessage(MessageConfig.proLoad)

    }

    fun getAttribute(attribute: HashMap<String, Any>, strength: Int): HashMap<String, Any> {
        val attMap = hashMapOf<String, Any>()
        attribute.keys.forEach {
            val data = attribute[it] as RandomSub
            when (data.type) {
                Type.FIXED -> {
                    attMap[it] = data.value
                }
                Type.RANDOM -> {
                    attMap[it] = random(data.value1, data.value2).toString()
                }
                Type.STRENGTH -> {
                    attMap[it] = (data.base + data.add * strength).toString()
                }
                Type.DOUBLE -> {
                    attMap[it] = String.format("%.${data.decimal}f", (random(data.start, data.end)))
                }
                Type.LIST -> {
                    attMap[it] = data.list[random(data.list.size)]
                }
                Type.LORE -> {
                    attMap[it] = data.lore
                }
            }
        }
        return attMap
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

    open class Data constructor(
        val name: String,
        val type: String,
        val weight: Int,
        val attribute: HashMap<String, Any>
    )

    class RandomSub constructor(
        val type: Type,
        val value: String,
        val value1: Int,
        val value2: Int,
        val base: Int,
        val add: Int,
        val decimal: Int,
        val start: Double,
        val end: Double,
        val list: List<String>,
        val lore: List<String>
    )

    enum class Type {
        FIXED,
        RANDOM,
        STRENGTH,
        DOUBLE,
        LIST,
        LORE
    }
}