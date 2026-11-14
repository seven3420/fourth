package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.Constants
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.R
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.data.FileStore
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model.GameRecord
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model.Student
import com.google.android.material.button.MaterialButton

/**
 * 个人信息 Fragment。
 *
 * 个人信息与游戏记录都通过对象序列化保存在文件中，
 * 退出应用后重新打开可以恢复。
 */
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var ivAvatar: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvStuid: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvHobby: TextView
    private lateinit var tvBestTwo: TextView
    private lateinit var tvBestThree: TextView
    private lateinit var tvLastScore: TextView
    private lateinit var btnModifyInfo: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivAvatar = view.findViewById(R.id.iv_avatar)
        tvName = view.findViewById(R.id.tv_name)
        tvGender = view.findViewById(R.id.tv_gender)
        tvStuid = view.findViewById(R.id.tv_stuid)
        tvPhone = view.findViewById(R.id.tv_phone)
        tvEmail = view.findViewById(R.id.tv_email)
        tvHobby = view.findViewById(R.id.tv_hobby)
        tvBestTwo = view.findViewById(R.id.tv_best_two)
        tvBestThree = view.findViewById(R.id.tv_best_three)
        tvLastScore = view.findViewById(R.id.tv_last_score)
        btnModifyInfo = view.findViewById(R.id.btn_modify_info)

        btnModifyInfo.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, EditProfileFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::tvName.isInitialized) {
            refreshUI()
        }
    }

    private fun refreshUI() {
        val student = loadStudent()

        showAvatar(student.avatarUri)
        tvName.text = getString(R.string.name_format, student.name)
        tvStuid.text = getString(R.string.stu_id_format, student.stuid)
        tvGender.text = getString(R.string.gender_format, student.gender)
        tvPhone.text = getString(R.string.phone_format, student.phone)
        tvEmail.text = getString(R.string.email_format, student.email)

        val hobbyList = mutableListOf<String>()
        if (student.travel) hobbyList.add(getString(R.string.hobby_travel))
        if (student.read) hobbyList.add(getString(R.string.hobby_read))
        if (student.gameHobby) hobbyList.add(getString(R.string.hobby_game))
        if (student.shop) hobbyList.add(getString(R.string.hobby_shop))
        tvHobby.text = getString(
            R.string.hobby_format,
            if (hobbyList.isEmpty()) getString(R.string.hobby_none)
            else hobbyList.joinToString(getString(R.string.hobby_separator))
        )

        val recordTwo = loadRecord(Constants.TWO_CARD_CARDS_PER_GROUP)
        val recordThree = loadRecord(Constants.THREE_CARD_CARDS_PER_GROUP)
        tvBestTwo.text = getString(R.string.best_score_two_format, recordTwo.bestScore)
        tvBestThree.text = getString(R.string.best_score_three_format, recordThree.bestScore)
        tvLastScore.text = getString(
            R.string.last_score_format,
            maxOf(recordTwo.lastScore, recordThree.lastScore)
        )

        // 应用标题固定显示“姓名+学号”
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.app_name)
    }

    private fun loadStudent(): Student {
        return FileStore.load<Student>(requireContext(), Constants.FILE_STUDENT)
            ?: Student(
                name = getString(R.string.default_name),
                gender = getString(R.string.default_gender),
                stuid = getString(R.string.default_stuid),
                phone = getString(R.string.default_phone),
                email = getString(R.string.default_email)
            )
    }

    private fun loadRecord(cardsPerGroup: Int): GameRecord {
        return FileStore.load<GameRecord>(
            requireContext(),
            Constants.gameRecordFile(cardsPerGroup)
        ) ?: GameRecord()
    }

    private fun showAvatar(uriString: String?) {
        if (uriString.isNullOrBlank()) {
            ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder)
            return
        }

        try {
            ivAvatar.setImageURI(uriString.toUri())
        } catch (_: SecurityException) {
            ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder)
        }
    }
}
