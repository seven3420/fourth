package cn.edu.sicnu.cs.stu.zhuoyilin.fourth

import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model.Card
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model.CardMatchingGame
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model.Deck
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * 翻牌游戏 Model 层单元测试
 */
class CardMatchingGameTest {

    private lateinit var game: CardMatchingGame

    @Before
    fun setUp() {
        // 使用 2 对（4 张）进行快速测试
        game = CardMatchingGame(2)
    }

    @Test
    fun testGameInitialization() {
        // 2 对应该有 4 张卡片
        assertEquals(4, game.getCardCount())
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
    fun testMatchPair() {
        // 找到一对匹配的牌
        val card0 = game.cardAtIndex(0)
        var matchIndex = -1
        for (i in 1 until game.getCardCount()) {
            if (game.cardAtIndex(i).identifier == card0.identifier) {
                matchIndex = i
                break
            }
        }
        assertTrue("应该有一对相同的牌", matchIndex != -1)

        val scoreBefore = game.score
        // 翻第一张
        game.chooseCardAtIndex(0)
        // 翻第二张（配对的）
        game.chooseCardAtIndex(matchIndex)
        // 结束回合
        game.finishTurn()

        assertTrue(card0.isMatched)
        assertTrue(game.cardAtIndex(matchIndex).isMatched)
        // 分数变化：翻两张 -2，配对成功 +10，总共 +8
        assertEquals(scoreBefore + 8, game.score)
    }

    @Test
    fun testMismatchPair() {
        // 找到两张不同的牌
        val card0 = game.cardAtIndex(0)
        var diffIndex = -1
        for (i in 1 until game.getCardCount()) {
            if (game.cardAtIndex(i).identifier != card0.identifier) {
                diffIndex = i
                break
            }
        }
        assertTrue("应该有不同的牌", diffIndex != -1)

        val scoreBefore = game.score
        // 翻第一张
        game.chooseCardAtIndex(0)
        // 翻第二张（不配对的）
        game.chooseCardAtIndex(diffIndex)
        // 结束回合
        game.finishTurn()

        assertFalse(card0.isMatched)
        assertFalse(game.cardAtIndex(diffIndex).isMatched)
        assertFalse("未配对的牌应该翻回去", card0.isChosen)
        assertFalse("未配对的牌应该翻回去", game.cardAtIndex(diffIndex).isChosen)
        // 分数变化：翻两张 -2，配对失败 -2，总共 -4
        assertEquals(scoreBefore - 4, game.score)
    }

    @Test
    fun testGameOver() {
        assertFalse("游戏未开始时不应结束", game.isGameOver())

        // 翻完所有牌并配对
        val matchedIndices = mutableSetOf<Int>()
        while (matchedIndices.size < game.getCardCount()) {
            // 找第一对未匹配的
            var first = -1
            for (i in 0 until game.getCardCount()) {
                if (!game.cardAtIndex(i).isMatched) {
                    first = i
                    break
                }
            }
            if (first == -1) break

            var second = -1
            val firstCard = game.cardAtIndex(first)
            for (i in first + 1 until game.getCardCount()) {
                if (!game.cardAtIndex(i).isMatched &&
                    game.cardAtIndex(i).identifier == firstCard.identifier
                ) {
                    second = i
                    break
                }
            }

            game.chooseCardAtIndex(first)
            game.chooseCardAtIndex(second)
            game.finishTurn()
            matchedIndices.add(first)
            matchedIndices.add(second)
        }

        assertTrue("全部配对后游戏应结束", game.isGameOver())
    }

    @Test
    fun testCardIdentifier() {
        val card1 = Card("A")
        val card2 = Card("A")
        val card3 = Card("B")

        assertTrue(card1.match(card2))
        assertFalse(card1.match(card3))
        assertEquals("A", card1.toString())
    }

    @Test
    fun testDeckGeneratesCards() {
        val deck = Deck()
        // 从 Deck 抽足够的牌
        val cards = mutableListOf<Card>()
        while (true) {
            val card = deck.drawRandomCard() ?: break
            cards.add(card)
        }
        // Deck 应该能生成多个不同的点数
        assertTrue(cards.isNotEmpty())
        // 检查卡片有 identifier
        cards.forEach { assertNotNull(it.identifier) }
    }
}
