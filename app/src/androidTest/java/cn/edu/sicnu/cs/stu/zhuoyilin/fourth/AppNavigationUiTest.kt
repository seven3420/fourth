package cn.edu.sicnu.cs.stu.zhuoyilin.fourth

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.view.CardAdapter
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNavigationUiTest {

    @Before
    fun clearSavedFiles() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteFile(Constants.FILE_STUDENT)
        context.deleteFile(Constants.gameStateFile(Constants.TWO_CARD_CARDS_PER_GROUP))
        context.deleteFile(Constants.gameStateFile(Constants.THREE_CARD_CARDS_PER_GROUP))
        context.deleteFile(Constants.gameRecordFile(Constants.TWO_CARD_CARDS_PER_GROUP))
        context.deleteFile(Constants.gameRecordFile(Constants.THREE_CARD_CARDS_PER_GROUP))
    }

    @Test
    fun mainActivityShowsProfileFragmentAndBottomNavigation() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.bottom_nav)).check(matches(isDisplayed()))
            onView(withId(R.id.iv_avatar)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_name)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_best_two)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_best_three)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_modify_info)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun playerCanOpenEditFragmentAndCancelChanges() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btn_modify_info)).perform(click())
            onView(withId(R.id.et_name)).check(matches(isDisplayed())).perform(replaceText("李四"))
            onView(withId(R.id.cb_read)).perform(click())
            onView(withId(R.id.btn_cancel)).perform(click())
            onView(withId(R.id.tv_name)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun switchToTwoCardGameFragment() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.nav_game_2)).perform(click())
            onView(withId(R.id.rv_card)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_game_score)).check(matches(withText(containsString("0"))))

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_card)
                val adapter = recyclerView.adapter as CardAdapter
                assertEquals(16, adapter.itemCount)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                assertEquals(4, layoutManager.spanCount)
                recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
            }

            onView(withId(R.id.tv_game_score)).check(matches(withText(containsString("-1"))))
        }
    }

    @Test
    fun switchToThreeCardGameFragment() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.nav_game_3)).perform(click())
            onView(withId(R.id.rv_card)).check(matches(isDisplayed()))

            scenario.onActivity { activity ->
                val recyclerView = activity.findViewById<RecyclerView>(R.id.rv_card)
                val adapter = recyclerView.adapter as CardAdapter
                // 3 张牌游戏为 4 组，共 12 张牌
                assertEquals(12, adapter.itemCount)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                assertEquals(3, layoutManager.spanCount)
            }
        }
    }
}
