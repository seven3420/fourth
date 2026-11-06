package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model

/**
 * 卡片类，每张卡片有一个标识（identifier），
 * 三张 identifier 相同的卡片为一组。
 */
class Card(val identifier: String, var isChosen: Boolean = false, var isMatched: Boolean = false) {

    override fun toString(): String = identifier

    /** 两张相同判定（保留兼容旧测试） */
    fun match(otherCard: Card): Boolean {
        return this.identifier == otherCard.identifier
    }

    /** 三张相同判定（实验要求：三张牌匹配） */
    fun match(c1: Card, c2: Card): Boolean {
        return this.identifier == c1.identifier && this.identifier == c2.identifier
    }
}
