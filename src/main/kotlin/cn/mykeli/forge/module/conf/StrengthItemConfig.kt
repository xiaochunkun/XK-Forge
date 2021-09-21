package cn.mykeli.forge.module.conf

import cn.mykeli.forge.Forge
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.console
import taboolib.module.configuration.util.getStringColored

/**
 * @author 小坤
 */
object StrengthItemConfig {

    var data = HashMap<ItemStack, Data>()
    private var yml = Forge.itemYml

    fun loadConfig() {
        yml.reload()
        data = hashMapOf()
        yml.getConfigurationSection("").getKeys(false).forEach {
            val item = Data(
                yml.getInt("$it.Num"),
                yml.getBoolean("$it.Add")
            )
            data[ItemConfig.getItem(it)] = item
        }
        console().sendMessage(MessageConfig.strItemLoad)
    }

    /**
     * 存储强度石
     * @property num Int
     * @property add Boolean
     * @constructor
     */
    class Data constructor( val num: Int, val add: Boolean)
}