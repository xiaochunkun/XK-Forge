package cn.mykeli.forge.util

import cn.mykeli.forge.module.conf.Config
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bukkit.entity.Player
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.Table


/**
 * @author 小坤
 * @date 2021/10/20 10:43
 */
class SQLData() {
    private var source = Config.dbHost.createDataSource()

    private val tableData = Table("${Config.dbPrefix}_user", Config.dbHost) {
        add { id() }
        add("name") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                ColumnOptionSQL.KEY
                ColumnOptionSQL.NOTNULL
            }
        }
        add("level") { type(ColumnTypeSQL.INT, 11) { ColumnOptionSQL.NOTNULL } }
        add("exp") { type(ColumnTypeSQL.INT, 11) { ColumnOptionSQL.NOTNULL } }
        add("map") { type(ColumnTypeSQL.LONGTEXT) }
        add("item") { type(ColumnTypeSQL.LONGTEXT) }
    }

    init {
        tableData.createTable(source)
    }

    fun getLevel(player: Player): Int? {
        return tableData.select(source) {
            where("name" eq player.name)
            limit(1)
            rows("level")
        }.firstOrNull { getInt("level") }
    }

    fun getExp(player: Player): Int? {
        return tableData.select(source) {
            where("name" eq player.name)
            limit(1)
            rows("exp")
        }.firstOrNull { getInt("exp") }
    }

    fun getMap(player: Player): ArrayList<String>? {
        return Gson().fromJson(
            tableData.select(source) {
                where("name" eq player.name)
                limit(1)
                rows("map")
            }.firstOrNull {
                getString("map")
            } ?: return null,
            object : TypeToken<ArrayList<String>>() {}.type
        )
    }

    fun getItem(player: Player): HashMap<Int, HashMap<String, List<String>>>? {
        return Gson().fromJson(
            tableData.select(source) {
                where("name" eq player.name)
                limit(1)
                rows("item")
            }.firstOrNull {
                getString("item")
            } ?: return null,
            object : TypeToken<java.util.HashMap<Int, java.util.HashMap<String, List<String>>>>() {}.type
        )
    }

    fun updateExp(player: Player, level: Int, exp: Int) {
        if (getLevel(player) == null) {
            tableData.insert(source, "name", "level", "exp") { value(player.name, 1, 0) }
        } else {
            tableData.update(source) {
                set("level", level)
                set("exp", exp)
                where("name" eq player.name)
            }
        }
    }

    fun addMap(player: Player, map: ArrayList<String>) {
        tableData.update(source) {
            set("map", Gson().toJson(map))
            where("name" eq player.name)
        }
    }

    fun addItem(player: Player, item: HashMap<Int, HashMap<String, List<String>>>) {
        tableData.update(source) {
            set("item", Gson().toJson(item))
            where("name" eq player.name)
        }
    }
}
