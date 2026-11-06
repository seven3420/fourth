package cn.edu.sicnu.cs.stu.zhuoyilin.fourth

import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model.Card
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model.CardMatchingGame
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model.Deck
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * 翻牌游戏 Model 层单元测试（三张匹配模式）
 */
class CardMatchingGameTest {

    private lateinit var game: CardMatchingGame

    @Before
    fun setUp() {
        // 使用 1 组（3 张）进行快速测试
        game = CardMatchingGame(1)
    }

    @Test
    fun testGameInitialization() {
        // 1组应该有 3 张卡片
        assertEquals(3, game.getCardCount())
        // 初始分数为 0
        assertEquals(0, game.score)
        // 初始没有已配对的牌
        for (i in 0 until game.getCardCount()) {
            assertFalse(game.cardAtIndex(i).isChosen)
            assertFalse(game.cardAtIndex(i).isMatched)
        }
    }

    @Test
    fun testChooseFirstCard() {
        val beforeScore = game.score
        game.chooseCardAtIndex(0)
        val card = game.cardAtIndex(0)
        assertTrue("第一张牌应该被翻开", card.isChosen)
        // 翻牌要扣分
        assertEquals(beforeScore - 1, game.score)
    }

    @Test
    fun testMatchThree() {
        // 3张牌identifier都相同
        val scoreBefore = game.score
        // 翻3张
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.chooseCardAtIndex(2)
        // 结束回合
        game.finishTurn()

        assertTrue(game.cardAtIndex(0).isMatched)
        assertTrue(game.cardAtIndex(1).isMatched)
        assertTrue(game.cardAtIndex(2).isMatched)
        // 分数：翻3张 -3，匹配成功 +10，总共 +7
        assertEquals(scoreBefore + 7, game.score)
    }

    @Test
    fun testGameOver() {
        assertFalse("游戏未开始时不应结束", game.isGameOver())
        // 翻完3张匹配
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.chooseCardAtIndex(2)
        game.finishTurn()
        assertTrue("全部匹配后游戏应结束", game.isGameOver())
    }

    @Test
    fun testCardIdentifier() {
        val card1 = Card("A")
        val card2 = Card("A")
        val card3 = Card("A")
        val card4 = Card("B")

        assertTrue(card1.match(card2, card3))
        assertFalse(card1.match(card2, card4))
        assertEquals("A", card1.toString())
    }

    @Test
    fun testDeckGeneratesCards() {
        val deck = Deck()
        val cards = mutableListOf<Card>()
        while (true) {
            val card = deck.drawRandomCard() ?: break
            cards.add(card)
        }
        assertTrue(cards.isNotEmpty())
        cards.forEach { assertNotNull(it.identifier) }
    }
}
