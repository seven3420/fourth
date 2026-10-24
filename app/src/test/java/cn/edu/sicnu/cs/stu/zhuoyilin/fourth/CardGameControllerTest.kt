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
        val harness = Harness(pairCount = 2)

        harness.controller.selectCard(0)

        val state = harness.listener.states.last()
        assertEquals(4, state.cards.size)
        assertEquals(1, state.cards.count { it.isFaceUp })
        assertEquals(-1, state.score)
    }

    @Test
    fun matchingPairAwardsBonusAndCompletesGame() {
        val harness = Harness(pairCount = 1)

        harness.controller.selectCard(0)
        harness.controller.selectCard(1)

        assertTrue(harness.listener.states.last().isResolvingTurn)
        assertEquals(8, harness.listener.states.last().score)

        harness.scheduler.runPendingAction()

        val finalState = harness.listener.states.last()
        assertEquals(2, finalState.cards.count { it.isMatched })
        assertFalse(finalState.isResolvingTurn)
        assertEquals(1, harness.listener.winCount)
    }

    @Test
    fun mismatchedPairIsHiddenAfterTurnResolution() {
        val harness = Harness(pairCount = 2)
        val firstIdentifier = harness.listener.states.first().cards[0].identifier
        val differentIndex = harness.listener.states.first().cards.indexOfFirst {
            it.identifier != firstIdentifier
        }

        assertTrue(differentIndex > 0)
        harness.controller.selectCard(0)
        harness.controller.selectCard(differentIndex)
        harness.scheduler.runPendingAction()

        val finalState = harness.listener.states.last()
        assertEquals(0, finalState.cards.count { it.isFaceUp })
        assertEquals(-4, finalState.score)
        assertEquals(0, harness.listener.winCount)
    }

    @Test
    fun gameContinuesAfterMatchingFirstPair() {
        val harness = Harness(pairCount = 3)
        val initialCards = harness.listener.states.first().cards
        val firstIdentifier = initialCards[0].identifier
        val matchIndex = (1 until initialCards.size).first {
            initialCards[it].identifier == firstIdentifier
        }

        harness.controller.selectCard(0)
        harness.controller.selectCard(matchIndex)
        harness.scheduler.runPendingAction()

        val remainingIndices = initialCards.indices.filter {
            it != 0 && it != matchIndex
        }
        val firstAvailable = remainingIndices.first()
        val secondAvailable = remainingIndices.first {
            initialCards[it].identifier != initialCards[firstAvailable].identifier
        }

        harness.controller.selectCard(firstAvailable)
        harness.controller.selectCard(secondAvailable)

        assertTrue(harness.listener.states.last().isResolvingTurn)

        harness.scheduler.runPendingAction()

        val finalState = harness.listener.states.last()
        assertFalse(finalState.isResolvingTurn)
        assertEquals(0, finalState.cards.count { it.isFaceUp && !it.isMatched })
    }

    @Test
    fun restartCancelsPendingTurnAndRestoresInitialState() {
        val harness = Harness(pairCount = 2)
        val firstIdentifier = harness.listener.states.first().cards[0].identifier
        val cards = harness.listener.states.first().cards
        val matchIndex = (1 until cards.size).first {
            cards[it].identifier == firstIdentifier
        }

        harness.controller.selectCard(0)
        harness.controller.selectCard(matchIndex)
        harness.scheduler.cancelCalls = 0

        harness.controller.startGame()

        val state = harness.listener.states.last()
        assertEquals(0, state.score)
        assertEquals(0, state.cards.count { it.isFaceUp })
        assertTrue(harness.scheduler.cancelCalls > 0)
        assertEquals(null, harness.scheduler.pendingAction)
    }

    private class Harness(pairCount: Int) {
        val scheduler = FakeTurnScheduler()
        val listener = RecordingListener()
        val controller = CardGameController(pairCount, scheduler, listener).also {
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
