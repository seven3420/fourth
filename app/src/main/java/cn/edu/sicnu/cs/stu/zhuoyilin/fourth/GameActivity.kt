package cn.edu.sicnu.cs.stu.zhuoyilin.fourth

import android.content.res.Configuration
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.controller.CardGameController
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.controller.HandlerTurnScheduler
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.view.CardAdapter
import com.google.android.material.button.MaterialButton

class GameActivity : BaseActivity(), CardGameController.Listener {

    private lateinit var rvCard: RecyclerView
    private lateinit var tvScore: TextView
    private lateinit var tvProgress: TextView
    private lateinit var btnReset: MaterialButton
    private lateinit var btnExit: MaterialButton

    private lateinit var controller: CardGameController
    private lateinit var adapter: CardAdapter
    private var isReturningResult = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        supportActionBar?.title = getString(R.string.game_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rvCard = findViewById(R.id.rv_card)
        tvScore = findViewById(R.id.tv_game_score)
        tvProgress = findViewById(R.id.tv_game_progress)
        btnReset = findViewById(R.id.btn_reset)
        btnExit = findViewById(R.id.btn_exit)

        setupRecyclerView()

        adapter = CardAdapter { position ->
            controller.selectCard(position)
        }
        rvCard.adapter = adapter

        controller = CardGameController(
            groupCount = Constants.GROUP_COUNT,
            scheduler = HandlerTurnScheduler(),
            listener = this
        )
        controller.startGame()

        btnReset.setOnClickListener { controller.resetGame() }
        btnExit.setOnClickListener {
            finishWithResult()
        }
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finishWithResult()
                }
            }
        )
    }

    private fun setupRecyclerView() {
        // 竖屏4列，横屏6列
        val spanCount = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            6
        } else {
            4
        }
        rvCard.layoutManager = GridLayoutManager(this, spanCount)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 屏幕旋转时自动切换列数
        val spanCount = if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            6
        } else {
            4
        }
        rvCard.layoutManager = GridLayoutManager(this, spanCount)
    }

    override fun onSupportNavigateUp(): Boolean {
        finishWithResult()
        return true
    }

    override fun onStateChanged(state: CardGameController.GameState) {
        tvScore.text = getString(R.string.score_format, state.score)
        tvProgress.text = getString(
            R.string.group_progress_format,
            state.matchedGroups,
            state.totalGroups
        )
        adapter.submitState(state)
    }

    override fun onGameWon() {
        Toast.makeText(this, getString(R.string.game_win), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        if (::controller.isInitialized) {
            controller.close()
        }
        super.onDestroy()
    }

    private fun finishWithResult() {
        if (isReturningResult) return
        isReturningResult = true

        val result = android.content.Intent().apply {
            putExtra(Constants.EXTRA_GAME_SCORE, controller.score)
        }
        setResult(RESULT_OK, result)
        finish()
    }
}
