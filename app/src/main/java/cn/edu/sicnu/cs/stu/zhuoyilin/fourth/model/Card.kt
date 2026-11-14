package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model

import java.io.Serializable

/**
 * 卡片类，每张卡片有一个标识（identifier），
 * identifier 相同的卡片属于同一组。
 *
 * 实现 Serializable 以便通过对象序列化保存游戏状态。
 */
class Card(
    val identifier: String,
    var isChosen: Boolean = false,
    var isMatched: Boolean = false
) : Serializable {

    override fun toString(): String = identifier

    fun match(otherCard: Card): Boolean {
        return this.identifier == otherCard.identifier
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
