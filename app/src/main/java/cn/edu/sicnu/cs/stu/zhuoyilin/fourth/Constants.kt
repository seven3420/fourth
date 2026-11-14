package cn.edu.sicnu.cs.stu.zhuoyilin.fourth

/**
 * 全局常量
 */
object Constants {
    // Fragment 参数
    const val ARG_CARDS_PER_GROUP = "cardsPerGroup"
    const val ARG_GROUP_COUNT = "groupCount"

    // 序列化文件名
    const val FILE_STUDENT = "student_info.dat"
    const val FILE_GAME_STATE_PREFIX = "game_state_"
    const val FILE_GAME_RECORD_PREFIX = "game_record_"

    // 两种翻牌游戏配置
    const val TWO_CARD_CARDS_PER_GROUP = 2
    const val TWO_CARD_GROUP_COUNT = 8 // 8 组，共 16 张

    const val THREE_CARD_CARDS_PER_GROUP = 3
    const val THREE_CARD_GROUP_COUNT = 4 // 4 组，共 12 张

    fun gameStateFile(cardsPerGroup: Int): String =
        "$FILE_GAME_STATE_PREFIX$cardsPerGroup.dat"

    fun gameRecordFile(cardsPerGroup: Int): String =
        "$FILE_GAME_RECORD_PREFIX$cardsPerGroup.dat"
}
