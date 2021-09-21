package cn.mykeli.forge

import cn.mykeli.forge.module.conf.*
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.console
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile
import taboolib.platform.BukkitPlugin

/**
 * @author 小坤
 */
object Forge {

    @Config("config.yml", true)
    lateinit var conf: SecuredFile
        private set

    @Config("level.yml", true)
    lateinit var levelYml: SecuredFile
        private set

    @Config("message.yml", true)
    lateinit var msgYml: SecuredFile
        private set

    @Config("item.yml", true)
    lateinit var itemYml: SecuredFile
        private set

    val plugin by lazy { BukkitPlugin.getInstance() }

    var mm = false

    var papi = false

    @Awake(LifeCycle.ENABLE)
    fun enable() {
        console().sendMessage("__   __ _   __     ______                   ")
        console().sendMessage("\\ \\ / /| | / /     |  ___|                  ")
        console().sendMessage(" \\ V / | |/ /______| |_ ___  _ __ __ _  ___ ")
        console().sendMessage(" /   \\ |    \\______|  _/ _ \\| '__/ _` |/ _ \\")
        console().sendMessage("/ /^\\ \\| |\\  \\     | || (_) | | | (_| |  __/")
        console().sendMessage("\\/   \\/\\_| \\_/     \\_| \\___/|_|  \\__, |\\___|")
        console().sendMessage("                                  __/ | ")
        console().sendMessage("                                 |___/    ")

        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) mm = true
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) papi = true

        MessageConfig.loadConfig()
        ItemConfig.loadConfig()
        MapConfig.loadConfig()
        ProbabilityConfig.loadConfig()

        cn.mykeli.forge.module.conf.Config.loadConfig()
        StrengthItemConfig.loadConfig()
        LevelConfig.loadConfig()



    }
}