package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.data

import android.content.Context
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * 对象序列化存储工具。
 *
 * 写文件：Context.openFileOutput + ObjectOutputStream
 * 读文件：Context.openFileInput + ObjectInputStream
 *
 * 被读写的对象必须实现 Serializable 接口。
 */
object FileStore {

    fun save(context: Context, fileName: String, value: Serializable) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { file ->
            ObjectOutputStream(file).use { stream ->
                stream.writeObject(value)
            }
        }
    }

    inline fun <reified T> load(context: Context, fileName: String): T? {
        return try {
            context.openFileInput(fileName).use { file ->
                ObjectInputStream(file).use { stream ->
                    stream.readObject() as? T
                }
            }
        } catch (_: Exception) {
            // 文件不存在或反序列化失败时视为没有历史数据
            null
        }
    }

    fun delete(context: Context, fileName: String) {
        context.deleteFile(fileName)
    }
}
