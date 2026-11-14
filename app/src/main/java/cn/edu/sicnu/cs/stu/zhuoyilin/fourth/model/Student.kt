package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model

import java.io.Serializable

/**
 * 个人信息数据类，实现 Serializable 以支持对象序列化存储。
 */
data class Student(
    var name: String = "",
    var gender: String = "",
    var stuid: String = "",
    var phone: String = "",
    var email: String = "",
    var avatarUri: String? = null,
    var travel: Boolean = false,
    var read: Boolean = false,
    var gameHobby: Boolean = false,
    var shop: Boolean = false
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}
