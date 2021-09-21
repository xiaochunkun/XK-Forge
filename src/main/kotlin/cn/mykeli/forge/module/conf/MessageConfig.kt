package cn.mykeli.forge.module.conf

import cn.mykeli.forge.Forge
import taboolib.common.platform.function.console
import taboolib.module.chat.colored
import taboolib.module.configuration.util.getStringListColored

/**
 * @author 小坤
 */
object MessageConfig {
    var yml = Forge.msgYml
    var itemLoad = "[&3XK-Forge&f]&4 item&e文件夹配置 &a加载完成"
    var mapLoad = "[&3XK-Forge&f]&4 map&e文件夹配置 &a加载完成"
    var proLoad = "[&3XK-Forge&f]&4 probability&e文件夹配置 &a加载完成"

    var configLoad = "[&3XK-Forge&f]&e config.yml &a加载完成"
    var levelLoad = "[&3XK-Forge&f]&e level.yml &a加载完成"
    var strItemLoad = "[&3XK-Forge&f]&e item.yml &a加载完成"
    var msgLoad = "[&3XK-Forge&f]&e message.yml &a加载完成"

    var itemNoFound = "[&3XK-Forge&f]&e %item% &4未在item文件夹下的配置文件中找到"
    var mapNoFound = "[&3XK-Forge&f]&e %item% &4未在map文件夹下的配置文件中找到"
    var materialNoFound = "[&3XK-Forge&f]&e该类型 &4 %material% &e不存在"

    var addExp = "[&3XK-Forge&f]&a恭喜 &e%player% &a增加 &4%item% &a点锻造经验"
    var upLevel = "[&3XK-Forge&f]&a恭喜 &e%player% &a升到 &4%level% &a级"

    var noMaterial = "[&3XK-Forge&f]&4背包中的材料不足！"
    var noLevel = "[&3XK-Forge&f]&4你的锻造等级不够"
    var noStudy = "[&3XK-Forge&f]&e该图纸无法学习"

    var itemSuccess = "[&3XK-Forge&f]&e锻造完成!！&a物品已经发送到背包！！"
    var commandSuccess = "[&3XK-Forge&f]&e锻造完成!！&a命令组已经执行！！"

    var commandError = "[&3XK-Forge&f]被执行的命令出现问题！%command%"

    var commandItemGive = "[&3XK-Forge&f]&e已将物品 %item% &e发送到你的背包"
    var commandLook = listOf<String>()
    var commandItemError = listOf<String>()
    var commandStudy = "[&3XK-Forge&f]&e恭喜你已成功学会 %item%"
    var commandStudyError = listOf<String>()
    var commandReload = "[&3XK-Forge&f]&e重载完成！！"
    var commandLevel = listOf<String>()
    var commandExp = listOf<String>()
    var commandLevelSet = "[&3XK-Forge&f]&e已将你的等级设置成%level%"
    var commandLevelAdd = "[&3XK-Forge&f]&e已将你的等级增加%level%"
    var commandHasMap = "[&3XK-Forge&f]&4你已经学会 %item% !&e请勿重复学习"

    fun loadConfig() {
        yml.reload()
        itemLoad = yml.getString("ItemLoad", "[&3XK-Forge&f]&4 item&e文件夹配置 &a加载完成").colored()
        mapLoad = yml.getString("MapLoad", "[&3XK-Forge&f]&4 map&e文件夹配置 &a加载完成").colored()
        proLoad = yml.getString("ProbabilityLoad", "[&3XK-Forge&f]&4 probability&e文件夹配置 &a加载完成").colored()

        configLoad = yml.getString("ConfigLoad", "[&3XK-Forge&f]&e config.yml &a加载完成").colored()
        levelLoad = yml.getString("LevelLoad", "[&3XK-Forge&f]&e level.yml &a加载完成").colored()
        strItemLoad = yml.getString("StrengthItemLoad", "[&3XK-Forge&f]&e item.yml &a加载完成").colored()
        msgLoad = yml.getString("MessageLoad", "[&3XK-Forge&f]&e message.yml &a加载完成").colored()

        itemNoFound = yml.getString("ItemNoFound", "[&3XK-Forge&f]&e %item% &4未在item文件夹下的配置文件中找到").colored()
        mapNoFound = yml.getString("MapNoFound", "[&3XK-Forge&f]&e %item% &4未在map文件夹下的配置文件中找到").colored()
        materialNoFound = yml.getString("MaterialNoFound", "[&3XK-Forge&f]&e该类型 &4 %material% &e不存在").colored()

        addExp = yml.getString("AddExp", "[&3XK-Forge&f]&a恭喜 &e%player% &a增加 &4%item% &a点锻造经验").colored()
        upLevel = yml.getString("UpLevel", "[&3XK-Forge&f]&a恭喜 &e%player% &a升到 &4%level% &a级").colored()

        noMaterial = yml.getString("NoMaterial", "[&3XK-Forge&f]&4背包中的材料不足！").colored()
        noLevel = yml.getString("NoLevel","[&3XK-Forge&f]&4你的锻造等级不够").colored()
        noStudy = yml.getString("NoStudy","[&3XK-Forge&f]&e该图纸无法学习").colored()

        itemSuccess = yml.getString("ItemSuccess", "[&3XK-Forge&f]&e锻造完成!！&a物品已经发送到背包！！").colored()
        commandSuccess = yml.getString("CommandSuccess", "[&3XK-Forge&f]&e锻造完成!！&a命令组已经执行！").colored()

        commandError = yml.getString("CommandError", "[&3XK-Forge&f]被执行的命令出现问题！%command%").colored()
        commandItemGive = yml.getString("Command.ItemGive", "[&3XK-Forge&f]&e已将物品 %item% &e发送到你的背包").colored()
        commandLook = yml.getStringListColored("Command.Look")
        commandItemError = yml.getStringListColored("Command.ItemError")
        commandStudy = yml.getString("Command.Study", "[&3XK-Forge&f]&e恭喜你已成功学会 %item%").colored()
        commandStudyError = yml.getStringListColored("Command.StudyError")
        commandReload = yml.getString("Command.Reload","[&3XK-Forge&f]&e重载完成！！").colored()
        commandLevel = yml.getStringListColored("Command.Level")
        commandExp = yml.getStringListColored("Command.Exp")
        commandLevelSet = yml.getString("Command.LevelSet","&3XK-Forge&f]&e已将你的等级设置成%level%").colored()
        commandLevelAdd = yml.getString("Command.LevelAdd","[&3XK-Forge&f]&e已将你的等级增加%level%").colored()
        commandHasMap = yml.getString("Command.HasMap","[&3XK-Forge&f]&4你已经学会 %item% !&e请勿重复学习").colored()

        console().sendMessage(msgLoad)
    }
}