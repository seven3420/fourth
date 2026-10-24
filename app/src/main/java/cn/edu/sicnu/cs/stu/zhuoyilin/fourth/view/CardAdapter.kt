package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.R
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.controller.CardGameController.CardState
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.controller.CardGameController.GameState

class CardAdapter(
    private val context: Context,
    private val onCardSelected: (Int) -> Unit
) : BaseAdapter() {

    private var state = GameState(
        score = 0,
        cards = emptyList(),
        matchedPairs = 0,
        totalPairs = 0,
        isResolvingTurn = false
    )

    fun submitState(newState: GameState) {
        state = newState
        notifyDataSetChanged()
    }

    override fun getCount(): Int = state.cards.size

    override fun getItem(position: Int): CardState = state.cards[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_card, parent, false)
        val holder = (view.tag as? CardViewHolder) ?: CardViewHolder(view).also {
            view.tag = it
        }

        holder.bind(state.cards[position], position)
        return view
    }

    private inner class CardViewHolder(itemView: View) {
        private val root: View = itemView
        private val cardBack: ImageView = itemView.findViewById(R.id.iv_card_back)
        private val cardContent: TextView = itemView.findViewById(R.id.tv_card_content)

        fun bind(card: CardState, position: Int) {
            cardBack.visibility = if (card.isFaceUp) View.GONE else View.VISIBLE
            cardContent.visibility = if (card.isFaceUp) View.VISIBLE else View.GONE
            cardContent.text = card.identifier
            cardContent.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (card.isMatched) R.color.teal_700 else R.color.ink
                )
            )

            root.alpha = if (card.isMatched) MATCHED_ALPHA else 1f
            root.isEnabled = !state.isResolvingTurn && !card.isMatched
            root.contentDescription = context.getString(
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
