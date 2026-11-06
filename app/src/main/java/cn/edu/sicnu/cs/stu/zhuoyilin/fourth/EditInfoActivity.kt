package cn.edu.sicnu.cs.stu.zhuoyilin.fourth

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.core.net.toUri
import com.google.android.material.button.MaterialButton

class EditInfoActivity : BaseActivity() {

    private lateinit var ivAvatar: ImageView
    private lateinit var btnPickAvatar: ImageButton
    private lateinit var etName: EditText
    private lateinit var etStuid: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var rbMale: RadioButton
    private lateinit var rbFemale: RadioButton
    private lateinit var cbTravel: CheckBox
    private lateinit var cbRead: CheckBox
    private lateinit var cbGame: CheckBox
    private lateinit var cbShop: CheckBox
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton

    private lateinit var prefs: SharedPreferences
    private var selectedAvatarUri: String? = null

    private val avatarPicker = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri ?: return@registerForActivityResult
        try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: SecurityException) {
            // Some document providers grant access only for the current session.
        }
        selectedAvatarUri = uri.toString()
        ivAvatar.setImageURI(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_info)

        supportActionBar?.title = getString(R.string.edit_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)

        ivAvatar = findViewById(R.id.iv_edit_avatar)
        btnPickAvatar = findViewById(R.id.btn_pick_avatar)
        etName = findViewById(R.id.et_name)
        etStuid = findViewById(R.id.et_stuid)
        etPhone = findViewById(R.id.et_phone)
        etEmail = findViewById(R.id.et_email)
        rgGender = findViewById(R.id.rg_gender)
        rbMale = findViewById(R.id.rb_male)
        rbFemale = findViewById(R.id.rb_female)
        cbTravel = findViewById(R.id.cb_travel)
        cbRead = findViewById(R.id.cb_read)
        cbGame = findViewById(R.id.cb_game)
        cbShop = findViewById(R.id.cb_shop)
        btnSave = findViewById(R.id.btn_save)
        btnCancel = findViewById(R.id.btn_cancel)

        populateFields()

        btnSave.setOnClickListener {
            val genderStr = if (rbMale.isChecked) {
                getString(R.string.male)
            } else {
                getString(R.string.female)
            }
            val intent = Intent()
            intent.putExtra(Constants.EXTRA_NAME, etName.text.toString())
            intent.putExtra(Constants.EXTRA_STUID, etStuid.text.toString())
            intent.putExtra(Constants.EXTRA_PHONE, etPhone.text.toString())
            intent.putExtra(Constants.EXTRA_EMAIL, etEmail.text.toString())
            intent.putExtra(Constants.EXTRA_AVATAR_URI, selectedAvatarUri)
            intent.putExtra(Constants.EXTRA_GENDER, genderStr)
            intent.putExtra(Constants.EXTRA_TRAVEL, cbTravel.isChecked)
            intent.putExtra(Constants.EXTRA_READ, cbRead.isChecked)
            intent.putExtra(Constants.EXTRA_GAME_HOBBY, cbGame.isChecked)
            intent.putExtra(Constants.EXTRA_SHOP, cbShop.isChecked)

            // 同时保存到 SharedPreferences
            prefs.edit {
                putString(Constants.EXTRA_NAME, etName.text.toString())
                putString(Constants.EXTRA_STUID, etStuid.text.toString())
                putString(Constants.EXTRA_PHONE, etPhone.text.toString())
                putString(Constants.EXTRA_EMAIL, etEmail.text.toString())
                putString(Constants.EXTRA_AVATAR_URI, selectedAvatarUri)
                putString(Constants.EXTRA_GENDER, genderStr)
                putBoolean(Constants.EXTRA_TRAVEL, cbTravel.isChecked)
                putBoolean(Constants.EXTRA_READ, cbRead.isChecked)
                putBoolean(Constants.EXTRA_GAME_HOBBY, cbGame.isChecked)
                putBoolean(Constants.EXTRA_SHOP, cbShop.isChecked)
            }

            setResult(RESULT_OK, intent)
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnPickAvatar.setOnClickListener {
            avatarPicker.launch(arrayOf(getString(R.string.mime_type_image)))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun populateFields() {
        val extras = intent.extras
        etName.setText(
            extras?.getString(Constants.EXTRA_NAME)
                ?: prefs.getString(Constants.EXTRA_NAME, getString(R.string.default_name))
        )
        etStuid.setText(
            extras?.getString(Constants.EXTRA_STUID)
                ?: prefs.getString(Constants.EXTRA_STUID, getString(R.string.default_stuid))
        )
        etPhone.setText(
            extras?.getString(Constants.EXTRA_PHONE)
                ?: prefs.getString(Constants.EXTRA_PHONE, getString(R.string.default_phone))
        )
        etEmail.setText(
            extras?.getString(Constants.EXTRA_EMAIL)
                ?: prefs.getString(Constants.EXTRA_EMAIL, getString(R.string.default_email))
        )

        val gender = extras?.getString(Constants.EXTRA_GENDER)
            ?: prefs.getString(Constants.EXTRA_GENDER, getString(R.string.default_gender))
        when (gender) {
            getString(R.string.female) -> rbFemale.isChecked = true
            else -> rbMale.isChecked = true
        }

        cbTravel.isChecked =
            extras?.getBoolean(Constants.EXTRA_TRAVEL)
                ?: prefs.getBoolean(Constants.EXTRA_TRAVEL, false)
        cbRead.isChecked =
            extras?.getBoolean(Constants.EXTRA_READ)
                ?: prefs.getBoolean(Constants.EXTRA_READ, false)
        cbGame.isChecked =
            extras?.getBoolean(Constants.EXTRA_GAME_HOBBY)
                ?: prefs.getBoolean(Constants.EXTRA_GAME_HOBBY, false)
        cbShop.isChecked =
            extras?.getBoolean(Constants.EXTRA_SHOP)
                ?: prefs.getBoolean(Constants.EXTRA_SHOP, false)

        selectedAvatarUri = extras?.getString(Constants.EXTRA_AVATAR_URI)
            ?: prefs.getString(Constants.EXTRA_AVATAR_URI, null)
        showAvatar(selectedAvatarUri)
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
