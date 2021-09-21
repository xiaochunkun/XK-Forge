package cn.mykeli.forge.module.conf

import taboolib.common.platform.function.console
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.chat.colored
import taboolib.module.configuration.SecuredFile
import taboolib.platform.BukkitPlugin
import java.io.File

/**
 * @author 小坤
 */
object ProbabilityConfig {
    private val file by lazy {
        File(BukkitPlugin.getInstance().dataFolder, "probability")
    }

    var map = HashMap<String, LinkedHashMap<String,Data>>()

    fun loadConfig() {
        map = hashMapOf()
        if (!file.isDirectory || file.listFiles().isEmpty()) {
            releaseResourceFile("probability/defaultProbability.yml", true)
        }
        file.listFiles().forEach {
            if (it.name.endsWith(".yml")) {
                val name = it.name.replace(".yml", "")
                val section = Yml(name).yml.getConfigurationSection("")
                section?.getKeys(false)?.forEach { pKey ->
                    val sec = section.getConfigurationSection(pKey)
                    val pro = LinkedHashMap<String,Data>()
                    sec?.getKeys(false)?.forEach { key ->
                        val weight = sec.getInt("$key.Weight")
                        val attribute = HashMap<String, String>()
                        val se = sec.getConfigurationSection("$key.Attribute")
                        se?.getKeys(false)?.forEach { attKey ->
                            attribute[attKey] = se.getString(attKey)
                        }
                         pro[key]= Data(key, weight, attribute)
                    }
                    map[pKey] = pro
                }
            }
        }
        console().sendMessage(MessageConfig.proLoad)
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
                yml.load(
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

    class Data constructor(val name: String, val weight: Int, val attribute: HashMap<String, String>)
}