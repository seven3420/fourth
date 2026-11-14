package cn.edu.sicnu.cs.stu.zhuoyilin.fourth.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.Constants
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.R
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.data.FileStore
import cn.edu.sicnu.cs.stu.zhuoyilin.fourth.model.Student
import com.google.android.material.button.MaterialButton

/**
 * 修改个人信息 Fragment。
 * 点击保存后把 Student 对象序列化写入文件，并在返回时恢复个人信息页面。
 */
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var ivAvatar: ImageView
    private lateinit var etName: EditText
    private lateinit var etStuid: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var rbMale: RadioButton
    private lateinit var rbFemale: RadioButton
    private lateinit var cbTravel: CheckBox
    private lateinit var cbRead: CheckBox
    private lateinit var cbGame: CheckBox
    private lateinit var cbShop: CheckBox

    private var selectedAvatarUri: String? = null

    private val avatarPicker = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri ?: return@registerForActivityResult
        try {
            requireContext().contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: SecurityException) {
            // 部分文档提供方只授予临时读取权限
        }
        selectedAvatarUri = uri.toString()
        ivAvatar.setImageURI(uri)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivAvatar = view.findViewById(R.id.iv_edit_avatar)
        etName = view.findViewById(R.id.et_name)
        etStuid = view.findViewById(R.id.et_stuid)
        etPhone = view.findViewById(R.id.et_phone)
        etEmail = view.findViewById(R.id.et_email)
        rbMale = view.findViewById(R.id.rb_male)
        rbFemale = view.findViewById(R.id.rb_female)
        cbTravel = view.findViewById(R.id.cb_travel)
        cbRead = view.findViewById(R.id.cb_read)
        cbGame = view.findViewById(R.id.cb_game)
        cbShop = view.findViewById(R.id.cb_shop)

        val btnPickAvatar: ImageButton = view.findViewById(R.id.btn_pick_avatar)
        val btnSave: MaterialButton = view.findViewById(R.id.btn_save)
        val btnCancel: MaterialButton = view.findViewById(R.id.btn_cancel)

        populateFields()

        btnPickAvatar.setOnClickListener {
            avatarPicker.launch(arrayOf(getString(R.string.mime_type_image)))
        }

        btnSave.setOnClickListener {
            saveStudent()
        }

        btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.edit_title)
    }

    private fun populateFields() {
        val student = FileStore.load<Student>(requireContext(), Constants.FILE_STUDENT)
            ?: Student(
                name = getString(R.string.default_name),
                gender = getString(R.string.default_gender),
                stuid = getString(R.string.default_stuid),
                phone = getString(R.string.default_phone),
                email = getString(R.string.default_email)
            )

        etName.setText(student.name)
        etStuid.setText(student.stuid)
        etPhone.setText(student.phone)
        etEmail.setText(student.email)

        if (student.gender == getString(R.string.female)) {
            rbFemale.isChecked = true
        } else {
            rbMale.isChecked = true
        }

        cbTravel.isChecked = student.travel
        cbRead.isChecked = student.read
        cbGame.isChecked = student.gameHobby
        cbShop.isChecked = student.shop

        selectedAvatarUri = student.avatarUri
        showAvatar(selectedAvatarUri)
    }

    private fun saveStudent() {
        val student = Student(
            name = etName.text.toString().trim(),
            gender = if (rbMale.isChecked) getString(R.string.male) else getString(R.string.female),
            stuid = etStuid.text.toString().trim(),
            phone = etPhone.text.toString().trim(),
            email = etEmail.text.toString().trim(),
            avatarUri = selectedAvatarUri,
            travel = cbTravel.isChecked,
            read = cbRead.isChecked,
            gameHobby = cbGame.isChecked,
            shop = cbShop.isChecked
        )

        FileStore.save(requireContext(), Constants.FILE_STUDENT, student)
        Toast.makeText(requireContext(), getString(R.string.info_saved), Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }

    private fun showAvatar(uriString: String?) {
        if (uriString.isNullOrBlank()) {
            ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder)
            return
        }

        try {
            ivAvatar.setImageURI(uriString.toUri())
        } catch (_: SecurityException) {
            selectedAvatarUri = null
            ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder)
        }
    }
}
