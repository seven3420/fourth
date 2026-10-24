package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model

/**
 * 翻牌配对游戏模型（Model层）
 * 经典成对匹配模式：8对共16张卡，翻开两张相同的牌为配对成功。
 */
class CardMatchingGame(pairCount: Int) {

    var score: Int = 0
        private set

    private val cards: MutableList<Card> = mutableListOf()

    // 当前翻开但尚未处理的卡片（最多2张）
    private var firstChosenIndex: Int = -1
    private var secondChosenIndex: Int = -1
    private var isProcessing: Boolean = false

    init {
        // 生成 pairCount 对卡片
        val deck = Deck()
        val pairCards = mutableListOf<Card>()
        for (i in 0 until pairCount) {
            val card = deck.drawRandomCard()
            if (card != null) {
                pairCards.add(card) // 第一张
                pairCards.add(Card(card.identifier)) // 配对的第二张
            }
        }
        // 打乱顺序
        pairCards.shuffle()
        cards.addAll(pairCards)
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
            firstChosenIndex = -1
            secondChosenIndex = -1
            return
        }

        // 第一次翻牌
        if (firstChosenIndex == -1) {
            card.isChosen = true
            firstChosenIndex = index
            score -= COST_TO_CHOOSE
        }
        // 第二次翻牌
        else if (secondChosenIndex == -1) {
            card.isChosen = true
            secondChosenIndex = index
            score -= COST_TO_CHOOSE
            isProcessing = true

            val firstCard = cards[firstChosenIndex]
            val secondCard = cards[secondChosenIndex]

            if (firstCard.match(secondCard)) {
                // 配对成功
                firstCard.isMatched = true
                secondCard.isMatched = true
                score += MATCH_BONUS
            } else {
                // 配对失败，扣分并准备翻回去
                score -= MISMATCH_PENALTY
            }
        }
        // 已有两张翻开，等UI刷新后由UI层调用 markChosen 或 resetChosen
    }

    /**
     * 通知游戏：当前两张翻开的牌处理完毕
     * 如果未匹配，需要翻回去
     */
    fun finishTurn(): Boolean {
        val matched = secondChosenIndex != -1 && cards[firstChosenIndex].isMatched
        if (secondChosenIndex != -1 && !cards[secondChosenIndex].isMatched) {
            cards[firstChosenIndex].isChosen = false
            cards[secondChosenIndex].isChosen = false
        }
        firstChosenIndex = -1
        secondChosenIndex = -1
        isProcessing = false
        return matched
    }

    /**
     * 游戏是否完成（所有牌都已配对）
     */
    fun isGameOver(): Boolean {
        return cards.all { it.isMatched }
    }

    companion object {
        const val MISMATCH_PENALTY = 2
        const val MATCH_BONUS = 10
        const val COST_TO_CHOOSE = 1
    }
}
