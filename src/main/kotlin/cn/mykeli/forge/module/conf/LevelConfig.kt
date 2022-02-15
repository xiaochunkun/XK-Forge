package cn.mykeli.forge.module.conf

import cn.mykeli.forge.Forge
import taboolib.common.platform.function.console
import taboolib.module.configuration.util.getStringColored

/**
 * @author ：小坤
 */
object LevelConfig {
    var level = arrayListOf<Data>()
    var yml = Forge.levelYml

    fun loadConfig() {
        yml.reload()
        level = arrayListOf()
        yml.getConfigurationSection("")?.getKeys(false)?.forEach {
            val data = Data(
                it.toInt(),
                yml.getInt("$it.Exp"),
                yml.getStringColored("$it.Name")!!
            )
            level.add(data)
        }
        console().sendMessage(MessageConfig.levelLoad)
    }

    /**
     * 存储等级内容
     * @property exp Int
     * @property name String
     * @constructor
     */
    class Data constructor(val level: Int, val exp: Int, val name: String)
}