package subside.plugins.koth.utils

import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.plugin.java.JavaPlugin
import org.json.simple.parser.JSONParser
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.OutputStreamWriter

class FileLoader(val fileName: String) {


    fun <T: ConfigurationSerializable> load(plugin: JavaPlugin, clazz: Class<T>): T? {
        val data = FileLoader(fileName).load(plugin) ?: return null

        try {
            return clazz.getDeclaredMethod("deserialize", Map::class.java).also {
                it.isAccessible = true
            }.invoke(null, data) as? T
        } catch (e: Exception) {
        }

        return null
    }

    fun <T: ConfigurationSerializable> loadList(plugin: JavaPlugin, clazz: Class<T>): List<T> {
        val data = FileLoader(fileName).load(plugin) ?: return emptyList()

        try {
            val method = clazz.getDeclaredMethod("deserialize", Map::class.java).also {
                it.isAccessible = true
            }

            (data as? List<Any>)?.let {
                return (it.map { method.invoke(null, it) } as? List<T>) ?: emptyList()
            }
        } catch (e: Exception) {
        }

        return emptyList()
    }

    private fun load(plugin: JavaPlugin): Any? {
        try {
            val path = getFilePath(plugin)
            if (!File(path).exists()) {
                return null
            }
            return JSONParser().parse(FileReader(path))
        } catch (e: Exception) {
        }

        return null
    }

    fun save(plugin: JavaPlugin, data: ConfigurationSerializable) {
        saveInternal(plugin, data.serialize())
    }

    fun save(plugin: JavaPlugin, data: List<ConfigurationSerializable>) {
        saveInternal(plugin, data.map { it.serialize() })
    }

    private fun saveInternal(plugin: JavaPlugin, data: Any) {
        val osw: OutputStreamWriter
        try {
            val file = File(getFilePath(plugin))
            if (!file.exists()) {
                plugin.dataFolder.mkdirs()
                file.createNewFile()
            }

            // Write the objects to the file
            val fileStream = FileOutputStream(file)
            osw = OutputStreamWriter(fileStream, "UTF-8")
            osw.write(GsonFiler().toGsonString(plugin, data))

            // Flush the output to the file and close it
            osw.flush()
            osw.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getFilePath(plugin: JavaPlugin) =
        plugin.dataFolder.absolutePath + File.separatorChar + fileName

    class GsonFiler {
        private fun getGson(plugin: JavaPlugin): Any {
            return try {
                return com.google.gson.GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
            } catch (e: NoClassDefFoundError) {
                try {
                    var gsonBuilder: Any = Class.forName("org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder").getDeclaredConstructor().newInstance()

                    gsonBuilder = gsonBuilder.javaClass.getDeclaredMethod("setPrettyPrinting").invoke(gsonBuilder)
                    gsonBuilder = gsonBuilder.javaClass.getDeclaredMethod("disableHtmlEscaping").invoke(gsonBuilder)
                    return gsonBuilder.javaClass.getDeclaredMethod("create").invoke(gsonBuilder)
                } catch (e: Exception) {
                    plugin.logger.severe("Couldn't find GsonBuilder/JsonParser class!")
                    e.printStackTrace()
                }
            }
        }

        fun toGsonString(plugin: JavaPlugin, obj: Any): String {
            return getGson(plugin).javaClass
                .getDeclaredMethod("toJson", Any::class.java)
                .invoke(obj) as String
        }

        fun fromGsonString(plugin: JavaPlugin, data: String): Any {
            return getGson(plugin).javaClass
                .getDeclaredMethod("fromJson", String::class.java, Class::class.java)
                .invoke(data, Any::class.java)
        }

    }
}