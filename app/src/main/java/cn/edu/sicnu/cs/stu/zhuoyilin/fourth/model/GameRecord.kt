package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model

import java.io.Serializable

/**
 * 游戏历史记录，实现 Serializable 以支持对象序列化存储。
 */
data class GameRecord(
    var bestScore: Int = 0,
    var lastScore: Int = 0,
    var gamesPlayed: Int = 0,
    var lastPlayedAt: Long = 0L
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}
