package cn.edu.sicnu.cs.stu.zhuoyilin.fourth

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.fragment.GameFragment
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.fragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * 多 Fragment 应用主界面：
 * 通过底部导航栏在“个人信息 / 2 张牌游戏 / 3 张牌游戏”三个 Fragment 之间切换。
 */
class MainActivity : BaseActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav)

        if (savedInstanceState == null) {
            switchTo(R.id.nav_profile)
        }

        bottomNav.setOnItemSelectedListener { item ->
            switchTo(item.itemId)
            true
        }
    }

    private fun switchTo(itemId: Int) {
        // 切换标签时清除编辑页等返回栈，避免叠加
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        val fragment: Fragment = when (itemId) {
            R.id.nav_game_2 -> GameFragment.newInstance(
                Constants.TWO_CARD_CARDS_PER_GROUP,
                Constants.TWO_CARD_GROUP_COUNT
            )

            R.id.nav_game_3 -> GameFragment.newInstance(
                Constants.THREE_CARD_CARDS_PER_GROUP,
                Constants.THREE_CARD_GROUP_COUNT
            )

            else -> ProfileFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}
