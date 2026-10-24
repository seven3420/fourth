package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model

/**
 * 卡片类，每张卡片有一个标识（identifier），
 * 两张 identifier 相同的卡片为一对。
 */
class Card(val identifier: String, var isChosen: Boolean = false, var isMatched: Boolean = false) {

    override fun toString(): String = identifier

    fun match(otherCard: Card): Boolean {
        return this.identifier == otherCard.identifier
    }
}
