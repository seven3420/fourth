package cn.edu.sicnu.cs.stu.zhuoyilin.fourth

import android.widget.GridView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.controller.CardGameController
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.controller.CardGameController.CardState
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.view.CardAdapter
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNavigationUiTest {

    @Test
    fun mainActivityShowsPlayerInformationAndActions() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.iv_avatar)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_name)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_phone)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_max_score)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_modify_info)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_go_game)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun playerCanOpenEditScreenAndCancelChanges() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btn_modify_info)).perform(click())
            onView(withId(R.id.et_name)).check(matches(isDisplayed())).perform(replaceText("李四"))
            onView(withId(R.id.cb_read)).perform(click())
            onView(withId(R.id.btn_cancel)).perform(click())
            onView(withId(R.id.tv_name)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun gameScreenRendersCardsAndRespondsToSelection() {
        ActivityScenario.launch(GameActivity::class.java).use {
            onView(withId(R.id.gv_card)).check(matches(isDisplayed()))
            onData(anything())
                .inAdapterView(withId(R.id.gv_card))
                .atPosition(0)
                .perform(click())
            onView(withId(R.id.tv_game_score)).check(matches(withText(containsString("-1"))))
            onView(withId(R.id.btn_reset)).perform(click())
            onView(withId(R.id.tv_game_score)).check(matches(withText(containsString("0"))))
        }
    }

    @Test
    fun gameRemainsInteractiveAfterMatchingPair() {
        ActivityScenario.launch(GameActivity::class.java).use { scenario ->
            onView(withId(R.id.gv_card)).check(matches(isDisplayed()))

            scenario.onActivity { activity ->
                val grid = activity.findViewById<GridView>(R.id.gv_card)
                val adapter = grid.adapter as CardAdapter
                val cards = (0 until adapter.count).map { adapter.getItem(it) as CardState }
                val firstIndex = 0
                val matchIndex = (1 until cards.size).first {
                    cards[it].identifier == cards[firstIndex].identifier
                }

                grid.getChildAt(firstIndex).performClick()
                grid.getChildAt(matchIndex).performClick()
            }

            Thread.sleep(CardGameController.TURN_DELAY_MILLIS + 250)

            scenario.onActivity { activity ->
                val grid = activity.findViewById<GridView>(R.id.gv_card)
                val adapter = grid.adapter as CardAdapter
                val nextIndex = (0 until adapter.count).first { index ->
                    !(adapter.getItem(index) as CardState).isMatched
                }

                grid.getChildAt(nextIndex).performClick()

                val nextCard = adapter.getItem(nextIndex) as CardState
                assertTrue(nextCard.isFaceUp)
            }
        }
    }
}
