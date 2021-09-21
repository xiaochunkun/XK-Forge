package cn.mykeli.forge.module.internal.command

import cn.mykeli.forge.Forge
import cn.mykeli.forge.module.conf.*
import cn.mykeli.forge.module.internal.ForgeMenu
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.common.platform.function.console
import taboolib.common.platform.function.onlinePlayers
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import taboolib.platform.BukkitAdapter
import taboolib.platform.type.BukkitPlayer
import taboolib.platform.util.hasName
import taboolib.platform.util.isNotAir

/**
 * @author 小坤
 * @date 2021/9/6 20:02
 */

@CommandHeader(
    "forge",
    ["dz"],
    permission = "XKForge.Use",
    permissionDefault = PermissionDefault.TRUE
)
object CommandHandler {

    @CommandBody
    val main = mainCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            helpCommand(sender)
        }
        incorrectSender { sender, _ ->
            sender.sendMessage("[&3XK-Forge&f] &e该命令仅支持游戏中执行".colored())
        }
        incorrectCommand { sender, context, index, state ->
            if (index == 1) {
                helpCommand(sender)
                return@incorrectCommand
            }
            when (context.argument(-1)) {
                "item" -> {
                    if (state == 2) {
                        if (sender is BukkitPlayer) {
                            sender.sendMessage("[&3XK-Forge&f] &e该参数为 &4get &e或者 &4give".colored())
                        } else {
                            sender.sendMessage("[&3XK-Forge&f] &e该参数为 &4give".colored())
                        }
                    } else {
                        if (context.argument(0) == "give") {
                            sender.sendMessage("[&3XK-Forge&f] &e请输入你想给予的玩家昵称".colored())
                        } else {
                            if (sender is BukkitPlayer) {
                                sender.sendMessage("[&3XK-Forge&f] &e请输入正确的物品名称".colored())
                            } else {
                                sender.sendMessage("[&3XK-Forge&f] &e该命令仅支持游戏中执行".colored())
                            }
                        }
                    }
                }
                "get" -> {
                    if (context.argument(-2) == "item") {
                        if (sender is BukkitPlayer) {
                            sender.sendMessage("[&3XK-Forge&f] &e请输入正确的物品名称".colored())
                        } else {
                            sender.sendMessage("[&3XK-Forge&f] &e该命令仅支持游戏中执行".colored())
                        }
                    }
                }
                "give" -> {
                    if (context.argument(-2) == "item") {
                        if (state == 2) {
                            sender.sendMessage("[&3XK-Forge&f] &e玩家 &4${context.argument(0)} &e不存在".colored())
                        } else {
                            sender.sendMessage("[&3XK-Forge&f] &e请输入你想给予 &4${context.argument(0)} &e的物品名称".colored())
                        }
                    }
                }
                "study" -> {
                    if (state == 2) {
                        sender.sendMessage("[&3XK-Forge&f] &e玩家 &4${context.argument(0)} &e不存在".colored())
                    } else {
                        sender.sendMessage("[&3XK-Forge&f] &e请输入你想让 &4${context.argument(0)} &e学会的图纸".colored())
                    }
                }
                "level" -> {
                    if (state == 2) {
                        sender.sendMessage("[&3XK-Forge&f] &e该参数为 &4add &e或者 &4set".colored())
                    } else {
                        sender.sendMessage("[&3XK-Forge&f] &e请输入你变更的玩家昵称".colored())
                    }
                }

                "add" -> {
                    if (context.argument(-2) == "level") {
                        if (state == 2) {
                            sender.sendMessage("[&3XK-Forge&f] &e玩家 &4${context.argument(0)} &e不存在".colored())
                        } else {
                            sender.sendMessage("[&3XK-Forge&f] &e请输入你要给 &4${context.argument(0)} &e增加的等级".colored())
                        }
                    }
                }

                "set" -> {
                    if (context.argument(-2) == "level") {
                        if (state == 2) {
                            sender.sendMessage("[&3XK-Forge&f] &e玩家 &4${context.argument(0)} &e不存在".colored())
                        } else {
                            sender.sendMessage("[&3XK-Forge&f] &e请输入你设置 &4${context.argument(0)} &e的等级".colored())
                        }
                    }
                }

                "exp" -> {
                    if (state == 2) {
                        sender.sendMessage("[&3XK-Forge&f] &e玩家 &4${context.argument(0)} &e不存在".colored())
                    } else {
                        sender.sendMessage("[&3XK-Forge&f] &e请输入你要给 &4${context.argument(0)} &e增加的经验".colored())
                    }
                }
            }
            when (index) {
                4 -> if (context.argument(-3) == "item") {
                    sender.sendMessage("[&3XK-Forge&f] &e请输入正确的物品名称".colored())
                }
                3 -> if (context.argument(-2) == "study") {
                    sender.sendMessage("[&3XK-Forge&f] &e请输入正确的图纸名称".colored())
                }
            }
            //这里是输出错误参数以及类型的地方
            /*
            for (i in (1 - index)..0) {
                println(context.argument(i))
            }
            println(index)
            println(state)
             */
        }
    }

    @CommandBody(optional = true, permission = "XKForge.Open")
    val open = subCommand {
        execute<Player> { player, _, _ ->
            ForgeMenu.openMenu(player)
        }
    }

    @CommandBody(optional = true, permission = "XKForge.Item")
    val item = subCommand {
        literal("get", optional = true) {
            dynamic {
                //补全物品库
                suggestion<Player> { _, _ ->
                    ItemConfig.map.keys.toList()
                }
                execute<Player> { player, _, argument ->
                    val args = argument.split(" ")
                    val amount = if (args.size >= 2) args[1].toInt() else 1
                    val item = ItemConfig.getItem(args[0])
                    if (item.isNotAir()) item.amount = amount
                    val name =
                        if (item.hasName() && item.itemMeta!!.displayName.length > 2) item.itemMeta!!.displayName else args[0]
                    player.inventory.addItem(item)
                    player.sendMessage(MessageConfig.commandItemGive.replace("%item%", name.colored()))
                    console().sendMessage("[&3XK-Forge&f]&e已将物品 &3${name.colored()} &e发送到${player.name}的背包".colored())
                }
            }
        }
        literal("give", optional = true) {
            dynamic {
                //补全玩家名
                suggestion<CommandSender> { _, _ ->
                    onlinePlayers().map {
                        it.name
                    }
                }
                //补全物品库
                dynamic {
                    suggestion<CommandSender> { _, _ ->
                        ItemConfig.map.keys.toList()
                    }
                    execute<CommandSender> { _, context, argument ->
                        val args = argument.split(" ")
                        val player = Bukkit.getPlayer(context.argument(-1)!!)
                        val amount = if (args.size >= 2) args[1].toInt() else 1
                        val item = ItemConfig.getItem(args[0])
                        if (item.isNotAir()) item.amount = amount
                        val name =
                            if (item.hasName() && item.itemMeta!!.displayName.length > 2) item.itemMeta!!.displayName else args[0]
                        player!!.inventory.addItem(item)
                        player.sendMessage(MessageConfig.commandItemGive.replace("%item%", name.colored()))
                        console().sendMessage("[&3XK-Forge&f]&e已将物品 &3${name.colored()} &e发送到${player.name}的背包".colored())
                    }
                }
            }
        }
        execute<CommandSender> { sender, _, _ ->
            MessageConfig.commandItemError.forEach {
                sender.sendMessage(it)
            }
        }
    }

    @CommandBody(optional = true, permission = "XKForge.Look")
    val look = subCommand {
        execute<Player> { player, _, _ ->
            val data = PlayerConfig.data[player]!!
            MessageConfig.commandLook.forEach {
                player.sendMessage(
                    it.replace("%level%", data.level.toString())
                        .replace("%exp%", data.exp.toString())
                        .replace("%needExp%", data.needExp().toString())
                        .replace("%name%", data.getLevelName())
                )
            }
        }
    }

    @CommandBody(optional = true, permission = "XKForge.Study")
    val study = subCommand {
        dynamic {
            suggestion<CommandSender> { _, _ ->
                onlinePlayers().map {
                    it.name
                }
            }
            dynamic {
                suggestion<CommandSender> { _, _ ->
                    MapConfig.map.keys.toList()
                }
                execute<CommandSender> { _, context, argument ->
                    val player = Bukkit.getPlayer(context.argument(-1)!!)
                    PlayerConfig.data[player]!!.addMap(argument)
                }
            }
        }
        execute<CommandSender> { sender, _, _ ->
            MessageConfig.commandStudyError.forEach {
                sender.sendMessage(it)
            }
        }
    }

    @CommandBody(optional = true, permission = "XKForge.Reload")
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            MessageConfig.loadConfig()
            ItemConfig.loadConfig()
            MapConfig.loadConfig()
            ProbabilityConfig.loadConfig()

            Config.loadConfig()
            StrengthItemConfig.loadConfig()
            LevelConfig.loadConfig()

            PlayerConfig.data.values.forEach {
                it.loadConfig()
            }
            sender.sendMessage(MessageConfig.commandReload)
        }
    }

    @CommandBody(optional = true, permission = "XKForge.Level")
    val level = subCommand {
        dynamic {
            suggestion<CommandSender> { _, _ ->
                listOf("add", "set")
            }
            dynamic {
                suggestion<CommandSender> { _, _ ->
                    onlinePlayers().map {
                        it.name
                    }
                }
                dynamic {
                    execute<CommandSender> { sender, context, argument ->
                        val player = Bukkit.getPlayer(context.argument(-1)!!)!!
                        when (context.argument(-2)) {
                            "set" -> {
                                val num = argument.toIntOrNull() ?: 0
                                if (num == 0) {
                                    sender.sendMessage("[&3XK-Forge&f] &e请输入正确数值".colored())
                                } else {
                                    PlayerConfig.data[player]!!.setPlayerLevel(num)
                                }

                            }
                            "add" -> {
                                val num = argument.toIntOrNull() ?: 0
                                if (num == 0) {
                                    sender.sendMessage("[&3XK-Forge&f] &e请输入正确数值".colored())
                                } else {
                                    PlayerConfig.data[player]!!.addLevel(num)
                                }
                            }
                        }
                    }
                }
            }
        }
        execute<CommandSender> { sender, _, _ ->
            MessageConfig.commandLevel.forEach {
                sender.sendMessage(it)
            }
        }
    }

    @CommandBody(optional = true, permission = "XKForge.Exp")
    val exp = subCommand {
        dynamic {
            suggestion<CommandSender> { _, _ ->
                onlinePlayers().map {
                    it.name
                }
            }
            dynamic {
                execute<CommandSender> { sender, context, argument ->
                    val player = Bukkit.getPlayer(context.argument(-1)!!)
                    val num = argument.toIntOrNull() ?: 0
                    if (num == 0) {
                        sender.sendMessage("[&3XK-Forge&f] &e请输入正确数值".colored())
                    } else {
                        PlayerConfig.data[player]!!.addExp(num)
                    }
                }
            }
        }
        execute<CommandSender> { sender, _, _ ->
            MessageConfig.commandExp.forEach {
                sender.sendMessage(it)
            }
        }
    }

    @CommandBody(optional = true, permission = "XKForge.History")
    val history = subCommand {
        execute<Player> { player, _, _ ->
            val historyItem = PlayerConfig.data[player]!!.historyItem
            if (historyItem.isEmpty()) return@execute
            player.sendMessage("[&3XK-Forge&f] 下面是你曾经锻造出的物品：".colored())
            historyItem.keys.forEach {
                historyItem[it]!!.forEach { (t, u) ->
                    TellrawJson()
                        .append("$it. $t").hoverText(u.toString()).sendTo(BukkitAdapter().adaptCommandSender(player))
                }
            }
        }
    }

    private fun helpCommand(proxySender: ProxyCommandSender) {
        proxySender.sendMessage("")
        TellrawJson()
            .append("  ").append("§3${Forge.plugin.description.name}")
            .hoverText("§7一个闻者伤心听者落泪的插件")
            .append(" ").append("v${Forge.plugin.description.version}")
            .hoverText("§7Plugin version: §2${Forge.plugin.description.version}")
            .sendTo(proxySender)
        proxySender.sendMessage("")

        TellrawJson()
            .append("  §7命令: ").append("§f/forge §8[...]")
            .hoverText("§f/forge §8[...]")
            .suggestCommand("/forge ")
            .sendTo(proxySender)
        proxySender.sendMessage("  §7参数:")

        fun send(name: String, desc: String) {
            TellrawJson()
                .append("    §8- ").append("§f$name")
                .hoverText("§f/forge $name §8- §7$desc")
                .suggestCommand("/forge $name")
                .sendTo(proxySender)
            proxySender.sendMessage("      §7$desc")
        }

        if (proxySender.hasPermission("XKForge.open")) {
            send("open", "打开锻造")
        }

        if (proxySender.hasPermission("XKForge.item")) {
            send("item", "给予物品")
        }

        if (proxySender.hasPermission("XKForge.history")) {
            send("history", "历史锻造")
        }

        if (proxySender.hasPermission("XKForge.look")) {
            send("look", "查看信息")
        }

        if (proxySender.hasPermission("XKForge.study")) {
            send("study", "学习图纸")
        }

        if (proxySender.hasPermission("XKForge.level")) {
            send("level", "等级操作")
        }

        if (proxySender.hasPermission("XKForge.exp")) {
            send("exp", "经验操作")
        }

        if (proxySender.hasPermission("XKForge.reload")) {
            send("reload", "重载配置")
        }
    }
}