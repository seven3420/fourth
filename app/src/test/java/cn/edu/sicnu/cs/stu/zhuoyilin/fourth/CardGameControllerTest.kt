package cn.edu.sicnu.cs.stu.zhuoyilin.fourth

import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.controller.CardGameController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CardGameControllerTest {

    @Test
    fun selectingFirstCardPublishesFaceUpState() {
        val harness = Harness(groupCount = 1)

        harness.controller.selectCard(0)

        val state = harness.listener.states.last()
        assertEquals(3, state.cards.size)
        assertEquals(1, state.cards.count { it.isFaceUp })
        assertEquals(-1, state.score)
    }

    @Test
    fun matchingThreeCardsAwardsBonusAndCompletesGame() {
        val harness = Harness(groupCount = 1)

        harness.controller.selectCard(0)
        harness.controller.selectCard(1)
        harness.controller.selectCard(2)

        assertTrue(harness.listener.states.last().isResolvingTurn)
        // 翻3张 -3，匹配 +10 = +7
        assertEquals(7, harness.listener.states.last().score)

        harness.scheduler.runPendingAction()

        val finalState = harness.listener.states.last()
        assertEquals(3, finalState.cards.count { it.isMatched })
        assertFalse(finalState.isResolvingTurn)
        assertEquals(1, harness.listener.winCount)
    }

    @Test
    fun mismatchedCardsAreHiddenAfterTurnResolution() {
        // 用2组=6张牌，前3张里必然有不同identifier
        val harness = Harness(groupCount = 2)
        val initialCards = harness.listener.states.first().cards

        // 找3张牌：第0张，和两张不同identifier的
        harness.controller.selectCard(0)
        val firstId = initialCards[0].identifier
        val diffIndex = initialCards.indexOfFirst { it.identifier != firstId }
        harness.controller.selectCard(diffIndex)

        // 再选第三张（随便选一个未翻开的）
        val thirdIndex = initialCards.indices.first { it != 0 && it != diffIndex }
        harness.controller.selectCard(thirdIndex)

        harness.scheduler.runPendingAction()

        val finalState = harness.listener.states.last()
        // 未匹配的牌应该都翻回去
        assertEquals(0, finalState.cards.count { it.isFaceUp && !it.isMatched })
        assertEquals(0, harness.listener.winCount)
    }

    @Test
    fun restartCancelsPendingTurnAndRestoresInitialState() {
        val harness = Harness(groupCount = 1)

        harness.controller.selectCard(0)
        harness.controller.selectCard(1)
        harness.scheduler.cancelCalls = 0

        harness.controller.startGame()

        val state = harness.listener.states.last()
        assertEquals(0, state.score)
        assertEquals(0, state.cards.count { it.isFaceUp })
        assertTrue(harness.scheduler.cancelCalls > 0)
        assertEquals(null, harness.scheduler.pendingAction)
    }

    private class Harness(groupCount: Int) {
        val scheduler = FakeTurnScheduler()
        val listener = RecordingListener()
        val controller = CardGameController(groupCount, scheduler, listener).also {
            it.startGame()
        }
    }

    private class FakeTurnScheduler : CardGameController.TurnScheduler {
        var pendingAction: (() -> Unit)? = null
        var cancelCalls = 0

        override fun schedule(delayMillis: Long, action: () -> Unit) {
            pendingAction = action
        }

        override fun cancel() {
            cancelCalls++
            pendingAction = null
        }

        fun runPendingAction() {
            val action = pendingAction
            assertNotNull(action)
            pendingAction = null
            action?.invoke()
        }
    }

    private class RecordingListener : CardGameController.Listener {
        val states = mutableListOf<CardGameController.GameState>()
        var winCount = 0

        override fun onStateChanged(state: CardGameController.GameState) {
            states.add(state)
        }

        override fun onGameWon() {
            winCount++
        }
    }
}
