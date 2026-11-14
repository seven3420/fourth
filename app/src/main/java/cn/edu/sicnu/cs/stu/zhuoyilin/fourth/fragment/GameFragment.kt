package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.Constants
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.R
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.controller.CardGameController
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.controller.HandlerTurnScheduler
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.data.FileStore
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model.CardMatchingGame
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model.GameRecord
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.view.CardAdapter
import com.google.android.material.button.MaterialButton
import java.util.Date

/**
 * 翻牌游戏 Fragment。
 *
 * 通过参数区分“2 张牌”和“3 张牌”两种玩法：
 * 2 张牌：翻开两张相同的牌即配对成功；
 * 3 张牌：翻开三张相同的牌才配对成功。
 *
 * 游戏状态（CardMatchingGame）与历史记录（GameRecord）都使用对象序列化保存，
 * 退出应用再打开可以继续上次的游戏并看到历史最高分。
 */
class GameFragment : Fragment(R.layout.fragment_game), CardGameController.Listener {

    private lateinit var rvCard: RecyclerView
    private lateinit var tvScore: TextView
    private lateinit var tvProgress: TextView
    private lateinit var tvRecord: TextView
    private lateinit var btnReset: MaterialButton

    private lateinit var controller: CardGameController
    private lateinit var adapter: CardAdapter

    private var cardsPerGroup = Constants.TWO_CARD_CARDS_PER_GROUP
    private var groupCount = Constants.TWO_CARD_GROUP_COUNT
    private var record = GameRecord()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardsPerGroup = requireArguments().getInt(
            Constants.ARG_CARDS_PER_GROUP,
            Constants.TWO_CARD_CARDS_PER_GROUP
        )
        groupCount = requireArguments().getInt(
            Constants.ARG_GROUP_COUNT,
            Constants.TWO_CARD_GROUP_COUNT
        )

        rvCard = view.findViewById(R.id.rv_card)
        tvScore = view.findViewById(R.id.tv_game_score)
        tvProgress = view.findViewById(R.id.tv_game_progress)
        tvRecord = view.findViewById(R.id.tv_game_record)
        btnReset = view.findViewById(R.id.btn_reset)

        val columns = if (cardsPerGroup == Constants.THREE_CARD_CARDS_PER_GROUP) {
            COLUMNS_THREE_CARD
        } else {
            COLUMNS_TWO_CARD
        }
        rvCard.layoutManager = GridLayoutManager(requireContext(), columns)

        adapter = CardAdapter { position ->
            controller.selectCard(position)
        }
        rvCard.adapter = adapter

        record = FileStore.load<GameRecord>(
            requireContext(),
            Constants.gameRecordFile(cardsPerGroup)
        ) ?: GameRecord()

        controller = CardGameController(
            groupCount = groupCount,
            scheduler = HandlerTurnScheduler(),
            listener = this,
            cardsPerGroup = cardsPerGroup
        )

        val restoredGame = FileStore.load<CardMatchingGame>(
            requireContext(),
            Constants.gameStateFile(cardsPerGroup)
        )?.takeIf {
            it.cardsPerGroup == cardsPerGroup && it.groupCount == groupCount && !it.isGameOver()
        }

        controller.startGame(restoredGame)
        updateRecordText()

        btnReset.setOnClickListener {
            controller.startGame()
            FileStore.delete(requireContext(), Constants.gameStateFile(cardsPerGroup))
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(
            if (cardsPerGroup == Constants.THREE_CARD_CARDS_PER_GROUP) {
                R.string.game_three_card_title
            } else {
                R.string.game_two_card_title
            }
        )
    }

    override fun onPause() {
        super.onPause()
        saveGameState()
    }

    override fun onDestroyView() {
        if (::controller.isInitialized) {
            controller.close()
        }
        if (::rvCard.isInitialized) {
            rvCard.adapter = null
        }
        super.onDestroyView()
    }

    override fun onStateChanged(state: CardGameController.GameState) {
        tvScore.text = getString(R.string.score_format, state.score)
        tvProgress.text = getString(
            R.string.pair_progress_format,
            state.matchedGroups,
            state.totalGroups
        )
        adapter.submitState(state)
    }

    override fun onGameWon() {
        Toast.makeText(requireContext(), getString(R.string.game_win), Toast.LENGTH_SHORT).show()

        record.bestScore = maxOf(record.bestScore, controller.score)
        record.lastScore = controller.score
        record.gamesPlayed += 1
        record.lastPlayedAt = Date().time
        saveRecord()
        updateRecordText()
    }

    private fun updateRecordText() {
        tvRecord.text = getString(
            R.string.game_record_format,
            record.bestScore,
            record.lastScore
        )
    }

    private fun saveRecord() {
        FileStore.save(requireContext(), Constants.gameRecordFile(cardsPerGroup), record)
    }

    private fun saveGameState() {
        saveRecord()
        if (!::controller.isInitialized) return

        val game = controller.snapshot() ?: return
        val context = context ?: return
        if (game.isGameOver()) {
            FileStore.delete(context, Constants.gameStateFile(cardsPerGroup))
        } else {
            FileStore.save(context, Constants.gameStateFile(cardsPerGroup), game)
        }
    }

    companion object {
        private const val COLUMNS_TWO_CARD = 4
        private const val COLUMNS_THREE_CARD = 3

        fun newInstance(cardsPerGroup: Int, groupCount: Int): GameFragment {
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putInt(Constants.ARG_CARDS_PER_GROUP, cardsPerGroup)
                    putInt(Constants.ARG_GROUP_COUNT, groupCount)
                }
            }
        }
    }
}
