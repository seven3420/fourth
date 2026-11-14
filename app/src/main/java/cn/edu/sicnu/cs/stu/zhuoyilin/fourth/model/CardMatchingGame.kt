package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model

import java.io.Serializable

/**
 * 翻牌配对游戏模型（Model 层）。
 *
 * @param groupCount 牌组数量，例如 8 组
 * @param cardsPerGroup 每组牌的张数：2 表示两张牌配对，3 表示三张牌配对
 *
 * 该类实现 Serializable，可直接通过 ObjectOutputStream 保存到文件中，
 * 再次打开应用时读取文件即可恢复上次的游戏状态。
 */
class CardMatchingGame(
    val groupCount: Int,
    val cardsPerGroup: Int = DEFAULT_CARDS_PER_GROUP
) : Serializable {

    var score: Int = 0
        private set

    private val cards: MutableList<Card> = mutableListOf()

    // 当前已翻开、等待结算的卡片下标
    private val chosenIndices: MutableList<Int> = mutableListOf()
    private var isProcessing: Boolean = false

    init {
        // 生成 groupCount 组卡片，每组 cardsPerGroup 张相同的牌
        val deck = Deck()
        val generatedCards = mutableListOf<Card>()
        for (i in 0 until groupCount) {
            val card = deck.drawRandomCard() ?: break
            repeat(cardsPerGroup) {
                generatedCards.add(Card(card.identifier))
            }
        }
        // 打乱顺序
        generatedCards.shuffle()
        cards.addAll(generatedCards)
    }

    fun cardAtIndex(index: Int): Card {
        return cards[index]
    }

    fun getCardCount(): Int = cards.size

    /**
     * 已完成的组数
     */
    fun getMatchedGroupCount(): Int = cards.count { it.isMatched } / cardsPerGroup

    /**
     * 选择（翻开）某张牌
     */
    fun chooseCardAtIndex(index: Int) {
        if (isProcessing) return
        if (index !in cards.indices) return

        val card = cards[index]
        // 已配对的牌不能再选
        if (card.isMatched) return

        // 再次点击已翻开的牌：翻回去
        if (card.isChosen) {
            card.isChosen = false
            chosenIndices.remove(index)
            return
        }

        card.isChosen = true
        chosenIndices.add(index)
        score -= COST_TO_CHOOSE

        // 翻开的牌达到每组数量时进行结算
        if (chosenIndices.size == cardsPerGroup) {
            val chosenCards = chosenIndices.map { cards[it] }
            val isSameGroup = chosenCards.all { it.identifier == chosenCards[0].identifier }
            if (isSameGroup) {
                chosenCards.forEach { it.isMatched = true }
                score += MATCH_BONUS
            } else {
                score -= MISMATCH_PENALTY
            }
            isProcessing = true
        }
    }

    /**
     * 通知游戏：当前翻开的一组牌处理完毕。
     * 如果未配对成功，需要把牌翻回去。
     *
     * @return 本次是否配对成功
     */
    fun finishTurn(): Boolean {
        val matched = chosenIndices.isNotEmpty() && chosenIndices.all { cards[it].isMatched }
        if (chosenIndices.isNotEmpty() && chosenIndices.any { !cards[it].isMatched }) {
            chosenIndices.forEach { cards[it].isChosen = false }
        }
        chosenIndices.clear()
        isProcessing = false
        return matched
    }

    /**
     * 从文件恢复游戏时调用：如果上次退出时有一组牌正处于结算中，
     * 先完成结算，避免恢复后无法继续操作。未完成结算的翻牌保持原状。
     */
    fun resolvePendingTurnIfNeeded() {
        if (isProcessing) {
            finishTurn()
        }
    }

    /**
     * 游戏是否完成（所有牌都已配对）
     */
    fun isGameOver(): Boolean {
        return cards.isNotEmpty() && cards.all { it.isMatched }
    }

    companion object {
        const val MISMATCH_PENALTY = 2
        const val MATCH_BONUS = 10
        const val COST_TO_CHOOSE = 1
        const val DEFAULT_CARDS_PER_GROUP = 2

        private const val serialVersionUID = 1L
    }
}
