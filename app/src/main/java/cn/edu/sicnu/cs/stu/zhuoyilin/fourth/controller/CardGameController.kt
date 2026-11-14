package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.controller

import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model.CardMatchingGame

/**
 * Controller for the card matching game. It owns the model, coordinates turn
 * resolution and publishes immutable snapshots to the View layer.
 *
 * @param groupCount 牌组数量
 * @param cardsPerGroup 每组牌的张数（2 张牌游戏 / 3 张牌游戏）
 */
class CardGameController(
    private val groupCount: Int,
    private val scheduler: TurnScheduler,
    private val listener: Listener,
    private val cardsPerGroup: Int = CardMatchingGame.DEFAULT_CARDS_PER_GROUP
) {

    interface Listener {
        fun onStateChanged(state: GameState)
        fun onGameWon()
    }

    interface TurnScheduler {
        fun schedule(delayMillis: Long, action: () -> Unit)
        fun cancel()
    }

    data class CardState(
        val identifier: String,
        val isFaceUp: Boolean,
        val isMatched: Boolean
    )

    data class GameState(
        val score: Int,
        val cards: List<CardState>,
        val matchedGroups: Int,
        val totalGroups: Int,
        val isResolvingTurn: Boolean
    )

    private var game: CardMatchingGame? = null
    private var isTurnScheduled = false
    private var isClosed = false

    val score: Int
        get() = game?.score ?: 0

    /**
     * 开始游戏。[restoredGame] 不为 null 时恢复上次保存的游戏进度。
     */
    fun startGame(restoredGame: CardMatchingGame? = null) {
        scheduler.cancel()
        isTurnScheduled = false
        isClosed = false
        game = restoredGame ?: CardMatchingGame(groupCount, cardsPerGroup)
        // 恢复时可能存在上次未结算的一组牌，先结算避免卡住
        restoredGame?.resolvePendingTurnIfNeeded()
        publishState()
    }

    fun selectCard(index: Int) {
        val currentGame = game ?: return
        if (index !in 0 until currentGame.getCardCount()) return

        val selectedCard = currentGame.cardAtIndex(index)
        if (selectedCard.isMatched || isTurnScheduled) return

        val chosenCount = (0 until currentGame.getCardCount()).count { cardIndex ->
            val card = currentGame.cardAtIndex(cardIndex)
            card.isChosen && !card.isMatched
        }
        val isRevealingLastCard = !selectedCard.isChosen && chosenCount == cardsPerGroup - 1

        currentGame.chooseCardAtIndex(index)

        if (isRevealingLastCard) {
            isTurnScheduled = true
            scheduler.schedule(TURN_DELAY_MILLIS) { resolveTurn() }
        }

        publishState()
    }

    fun close() {
        isClosed = true
        scheduler.cancel()
        isTurnScheduled = false
    }

    /**
     * 当前游戏对象，用于序列化保存进度。
     */
    fun snapshot(): CardMatchingGame? = game

    private fun resolveTurn() {
        if (isClosed) return

        val currentGame = game ?: return
        isTurnScheduled = false
        currentGame.finishTurn()
        publishState()

        if (currentGame.isGameOver()) {
            listener.onGameWon()
        }
    }

    private fun publishState() {
        val currentGame = game ?: return
        val cards = (0 until currentGame.getCardCount()).map { index ->
            val card = currentGame.cardAtIndex(index)
            CardState(
                identifier = card.identifier,
                isFaceUp = card.isChosen || card.isMatched,
                isMatched = card.isMatched
            )
        }

        listener.onStateChanged(
            GameState(
                score = currentGame.score,
                cards = cards,
                matchedGroups = currentGame.getMatchedGroupCount(),
                totalGroups = groupCount,
                isResolvingTurn = isTurnScheduled
            )
        )
    }

    companion object {
        const val TURN_DELAY_MILLIS = 650L
    }
}
