package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.R
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.controller.CardGameController.CardState
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.controller.CardGameController.GameState

/**
 * RecyclerView 适配器，负责将卡片状态绑定到 item 视图。
 */
class CardAdapter(
    private val onCardSelected: (Int) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private var state = GameState(
        score = 0,
        cards = emptyList(),
        matchedGroups = 0,
        totalGroups = 0,
        isResolvingTurn = false
    )

    fun submitState(newState: GameState) {
        state = newState
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = state.cards.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(state.cards[position], position, state)
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val root: View = itemView
        private val cardBack: ImageView = itemView.findViewById(R.id.iv_card_back)
        private val cardContent: TextView = itemView.findViewById(R.id.tv_card_content)

        fun bind(card: CardState, position: Int, state: GameState) {
            cardBack.visibility = if (card.isFaceUp) View.GONE else View.VISIBLE
            cardContent.visibility = if (card.isFaceUp) View.VISIBLE else View.GONE
            cardContent.text = card.identifier
            cardContent.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    if (card.isMatched) R.color.teal_700 else R.color.ink
                )
            )

            root.alpha = if (card.isMatched) MATCHED_ALPHA else 1f
            root.isEnabled = !state.isResolvingTurn && !card.isMatched
            root.contentDescription = itemView.context.getString(
                if (card.isFaceUp) {
                    R.string.card_face_description
                } else {
                    R.string.card_back_description
                },
                position + 1
            )
            root.setOnClickListener {
                if (!state.isResolvingTurn && !card.isMatched) {
                    onCardSelected(position)
                }
            }
        }
    }

    companion object {
        private const val MATCHED_ALPHA = 0.72f
    }
}
