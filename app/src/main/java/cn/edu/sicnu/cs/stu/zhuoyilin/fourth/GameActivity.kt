package cn.edu.sicnu.cs.stu.zhuoyilin.fourth

import android.os.Bundle
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.controller.CardGameController
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.controller.HandlerTurnScheduler
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.view.CardAdapter
import com.google.android.material.button.MaterialButton

class GameActivity : BaseActivity(), CardGameController.Listener {

    private lateinit var gvCard: GridView
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

        gvCard = findViewById(R.id.gv_card)
        tvScore = findViewById(R.id.tv_game_score)
        tvProgress = findViewById(R.id.tv_game_progress)
        btnReset = findViewById(R.id.btn_reset)
        btnExit = findViewById(R.id.btn_exit)

        adapter = CardAdapter(this) { position ->
            controller.selectCard(position)
        }
        gvCard.adapter = adapter

        controller = CardGameController(
            pairCount = Constants.PAIR_COUNT,
            scheduler = HandlerTurnScheduler(),
            listener = this
        )
        controller.startGame()

        btnReset.setOnClickListener { controller.startGame() }
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

    override fun onSupportNavigateUp(): Boolean {
        finishWithResult()
        return true
    }

    override fun onStateChanged(state: CardGameController.GameState) {
        tvScore.text = getString(R.string.score_format, state.score)
        tvProgress.text = getString(
            R.string.pair_progress_format,
            state.matchedPairs,
            state.totalPairs
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
