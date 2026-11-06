package cn.edu.sicnu.cs.stu.zhuoyilin.fourth

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.controller.CardGameController
import org.hamcrest.CoreMatchers.containsString
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
            onView(withId(R.id.rv_card)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_reset)).check(matches(isDisplayed()))
            onView(withId(R.id.tv_game_score)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun gameResetRestoresZeroScore() {
        ActivityScenario.launch(GameActivity::class.java).use {
            onView(withId(R.id.btn_reset)).perform(click())
            onView(withId(R.id.tv_game_score)).check(matches(withText(containsString("0"))))
        }
    }
}
