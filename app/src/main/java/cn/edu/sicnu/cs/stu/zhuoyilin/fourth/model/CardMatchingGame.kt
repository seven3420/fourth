package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model

/**
 * 翻牌配对游戏模型（Model层）
 * 三张匹配模式：每组3张相同的牌，翻开3张相同的牌为配对成功。
 */
class CardMatchingGame(groupCount: Int) {

    var score: Int = 0
        private set

    private val cards: MutableList<Card> = mutableListOf()

    // 当前翻开但尚未处理的卡片索引（最多3个）
    private val chosenIndices: MutableList<Int> = mutableListOf()
    private var isProcessing: Boolean = false

    init {
        // 生成 groupCount 组，每组3张相同
        val deck = Deck()
        val groupCards = mutableListOf<Card>()
        for (i in 0 until groupCount) {
            val card = deck.drawRandomCard()
            if (card != null) {
                repeat(3) {
                    groupCards.add(Card(card.identifier))
                }
            }
        }
        // 打乱顺序
        groupCards.shuffle()
        cards.addAll(groupCards)
    }

    fun cardAtIndex(index: Int): Card {
        return cards[index]
    }

    fun getCardCount(): Int = cards.size

    /**
     * 选择（翻开）某张牌
     */
    fun chooseCardAtIndex(index: Int) {
        if (isProcessing) return

        val card = cards[index]
        // 已配对的牌不能再选
        if (card.isMatched) return

        // 如果这张牌已经翻开，翻回去并重置
        if (card.isChosen) {
            card.isChosen = false
            chosenIndices.remove(index)
            return
        }

        // 翻开这张牌
        card.isChosen = true
        chosenIndices.add(index)
        score -= COST_TO_CHOOSE

        // 已选满3张，进入处理状态
        if (chosenIndices.size == CARDS_PER_GROUP) {
            isProcessing = true
            val c0 = cards[chosenIndices[0]]
            val c1 = cards[chosenIndices[1]]
            val c2 = cards[chosenIndices[2]]
            if (c0.match(c1, c2)) {
                // 三张匹配成功
                c0.isMatched = true
                c1.isMatched = true
                c2.isMatched = true
                score += MATCH_BONUS
            } else {
                // 三张不匹配，扣分
                score -= MISMATCH_PENALTY
            }
        }
    }

    /**
     * 通知游戏：当前三张翻开的牌处理完毕
     * 如果未匹配，需要翻回去
     */
    fun finishTurn(): Boolean {
        val matched = chosenIndices.size == CARDS_PER_GROUP &&
            cards[chosenIndices[0]].isMatched
        if (!matched) {
            for (i in chosenIndices) {
                cards[i].isChosen = false
            }
        }
        chosenIndices.clear()
        isProcessing = false
        return matched
    }

    /**
     * 游戏是否完成（所有牌都已配对）
     */
    fun isGameOver(): Boolean {
        return cards.all { it.isMatched }
    }

    /**
     * 重置游戏：所有牌翻回背面，取消匹配状态，重新洗牌，分数清零
     */
    fun reset() {
        chosenIndices.clear()
        isProcessing = false
        score = 0
        for (card in cards) {
            card.isChosen = false
            card.isMatched = false
        }
        cards.shuffle()
    }

    companion object {
        const val MISMATCH_PENALTY = 2
        const val MATCH_BONUS = 10
        const val COST_TO_CHOOSE = 1
        const val CARDS_PER_GROUP = 3
    }
}
