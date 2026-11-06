package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.controller

import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model.CardMatchingGame

/**
 * Controller for the card matching game. It owns the model, coordinates turn
 * resolution and publishes immutable snapshots to the View layer.
 * 三张匹配模式。
 */
class CardGameController(
    private val groupCount: Int,
    private val scheduler: TurnScheduler,
    private val listener: Listener
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

    fun startGame() {
        scheduler.cancel()
        isTurnScheduled = false
        isClosed = false
        game = CardMatchingGame(groupCount)
        publishState()
    }

    /**
     * 重置当前游戏（不重新创建Model，调用Model的reset方法）
     */
    fun resetGame() {
        scheduler.cancel()
        isTurnScheduled = false
        game?.reset()
        publishState()
    }

    fun selectCard(index: Int) {
        val currentGame = game ?: return
        if (index !in 0 until currentGame.getCardCount()) return

        val selectedCard = currentGame.cardAtIndex(index)
        if (selectedCard.isMatched || isTurnScheduled) return

        // 统计当前已翻开但未匹配的牌数量
        val faceUpCount = (0 until currentGame.getCardCount()).count { cardIndex ->
            val card = currentGame.cardAtIndex(cardIndex)
            card.isChosen && !card.isMatched
        }
        val isRevealingThirdCard = !selectedCard.isChosen && faceUpCount == 2

        currentGame.chooseCardAtIndex(index)

        if (isRevealingThirdCard) {
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
                matchedGroups = cards.count { it.isMatched } / CARDS_PER_GROUP,
                totalGroups = groupCount,
                isResolvingTurn = isTurnScheduled
            )
        )
    }

    companion object {
        const val TURN_DELAY_MILLIS = 650L
        private const val CARDS_PER_GROUP = 3
    }
}
