package cn.mykeli.forge.module.conf

import cn.mykeli.forge.Forge
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.console
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.util.getStringColored

/**
 * @author 小坤
 */
object Config {
    var yml = Forge.conf
    var strengthColor = "54c6e93b2a"
    var strength = "||"
    var timeFormat = "yyyy-MM-dd HH:mm:ss"

    var mainTitle = ""
    var mainRows = 6
    var mainLast = 45
    var mainLastItem = XMaterial.AIR.parseItem()
    var mainNext = 53
    var mainNextItem = XMaterial.AIR.parseItem()
    var mainItem = HashMap<Int, ItemStack>()
    var mainMap = listOf<String>()

    var forgeTitle = ""
    var forgeRows = 6
    var forgeMap = 11
    var forgeForgeSlot = 13
    var forgeForgeName = "锻造按钮"
    var forgeStrengthItemSlot = 15
    var forgeStrengthItemName = "显示强度石位置"
    var forgeItem = HashMap<Int, ItemStack>()
    var forgeMaterial = listOf<String>()

    fun loadConfig() {
        yml.reload()
        strengthColor = yml.getString("StrengthColor", "54c6e93b2a")
        strength = yml.getString("Strength", "||")
        timeFormat = yml.getString("TimeFormat", "yyyy-MM-dd HH:mm:ss")

        mainTitle = yml.getStringColored("UI.Main.Title")!!
        mainRows = yml.getInt("UI.Main.Rows")
        mainLast = yml.getInt("UI.Main.Last.Slot")
        mainLastItem = ItemConfig.getItem(yml.getString("UI.Main.Last.Name"))
        mainNext = yml.getInt("UI.Main.Next.Slot")
        mainNextItem = ItemConfig.getItem(yml.getString("UI.Main.Next.Name"))
        mainMap = yml.getString("UI.Main.Map").replace(" ", "").split(",")
        val mainItemSection = yml.getConfigurationSection("UI.Main.Item")
        mainItemSection?.getKeys(false)?.forEach {
            mainItem[it.toInt()] = ItemConfig.getItem(mainItemSection.getString(it))
        }

        forgeTitle = yml.getStringColored("UI.Forge.Title")!!
        forgeRows = yml.getInt("UI.Forge.Rows")
        forgeMap = yml.getInt("UI.Forge.Map")
        forgeForgeSlot = yml.getInt("UI.Forge.Forge.Slot")
        forgeForgeName = yml.getString("UI.Forge.Forge.Name")
        forgeStrengthItemSlot = yml.getInt("UI.Forge.StrengthItem.Slot")
        forgeStrengthItemName = yml.getString("UI.Forge.StrengthItem.Name")
        forgeMaterial = yml.getString("UI.Forge.Material").replace(" ", "").split(",")
        val forgeItemSection = yml.getConfigurationSection("UI.Forge.Item")
        forgeItemSection?.getKeys(false)?.forEach {
            forgeItem[it.toInt()] = ItemConfig.getItem(forgeItemSection.getString(it))
        }

        console().sendMessage(MessageConfig.configLoad)
    }


    class Data constructor(
        val name: String,
        val material: XMaterial,
        val lore: List<String>,
        val unbreakable: Boolean,
        val itemFlags: ArrayList<ItemFlag>,
        val enchantments: HashMap<Enchantment, Int>,
        val item: ItemStack,
        val index: Int,
        val slot: Int
    )
}