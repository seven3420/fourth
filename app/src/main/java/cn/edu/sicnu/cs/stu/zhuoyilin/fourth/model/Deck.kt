package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model

/**
 * 牌堆类，生成成对的卡片。
 * 例如生成8对，共16张卡片。
 */
class Deck {

    // 使用的卡片内容（点数）
    companion object {
        val cardContents = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
    }

    private val cards = mutableListOf<Card>()

    init {
        for (content in cardContents) {
            // 每个内容生成一张牌
            cards.add(Card(content))
        }
    }

    /**
     * 抽取一张随机牌并从牌堆中移除
     */
    fun drawRandomCard(): Card? {
        if (cards.isEmpty()) return null
        val index = (0 until cards.size).random()
        return cards.removeAt(index)
    }
}
