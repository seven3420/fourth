package cn.edu.sicnu.cs.stu.zhuoyilin.fourth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.core.net.toUri
import com.google.android.material.button.MaterialButton

class MainActivity : BaseActivity() {

    private lateinit var ivAvatar: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvStuid: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvHobby: TextView
    private lateinit var tvMaxScore: TextView
    private lateinit var btnModifyInfo: MaterialButton
    private lateinit var btnGoGame: MaterialButton

    // 个人信息
    private var name = ""
    private var gender = ""
    private var stuid = ""
    private var phone = ""
    private var email = ""
    private var avatarUri: String? = null
    private var travel = false
    private var read = false
    private var gameHobby = false
    private var shop = false
    private var maxScore = 0

    private lateinit var prefs: SharedPreferences

    private val editInfoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let(::applyEditedInfo)
        }
    }

    private val gameLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let(::applyGameResult)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)

        ivAvatar = findViewById(R.id.iv_avatar)
        tvName = findViewById(R.id.tv_name)
        tvGender = findViewById(R.id.tv_gender)
        tvStuid = findViewById(R.id.tv_stuid)
        tvPhone = findViewById(R.id.tv_phone)
        tvEmail = findViewById(R.id.tv_email)
        tvHobby = findViewById(R.id.tv_hobby)
        tvMaxScore = findViewById(R.id.tv_max_score)
        btnModifyInfo = findViewById(R.id.btn_modify_info)
        btnGoGame = findViewById(R.id.btn_go_game)

        loadData()
        refreshUI()

        btnModifyInfo.setOnClickListener {
            val intent = Intent(this, EditInfoActivity::class.java)
            intent.putExtra(Constants.EXTRA_NAME, name)
            intent.putExtra(Constants.EXTRA_GENDER, gender)
            intent.putExtra(Constants.EXTRA_STUID, stuid)
            intent.putExtra(Constants.EXTRA_PHONE, phone)
            intent.putExtra(Constants.EXTRA_EMAIL, email)
            intent.putExtra(Constants.EXTRA_AVATAR_URI, avatarUri)
            intent.putExtra(Constants.EXTRA_TRAVEL, travel)
            intent.putExtra(Constants.EXTRA_READ, read)
            intent.putExtra(Constants.EXTRA_GAME_HOBBY, gameHobby)
            intent.putExtra(Constants.EXTRA_SHOP, shop)
            editInfoLauncher.launch(intent)
        }

        btnGoGame.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra(Constants.EXTRA_OLD_MAX_SCORE, maxScore)
            gameLauncher.launch(intent)
        }
    }

    private fun loadData() {
        name = prefs.getString(Constants.EXTRA_NAME, getString(R.string.default_name)) ?: ""
        gender = prefs.getString(Constants.EXTRA_GENDER, getString(R.string.default_gender)) ?: ""
        stuid = prefs.getString(Constants.EXTRA_STUID, getString(R.string.default_stuid)) ?: ""
        phone = prefs.getString(Constants.EXTRA_PHONE, getString(R.string.default_phone)) ?: ""
        email = prefs.getString(Constants.EXTRA_EMAIL, getString(R.string.default_email)) ?: ""
        avatarUri = prefs.getString(Constants.EXTRA_AVATAR_URI, null)
        travel = prefs.getBoolean(Constants.EXTRA_TRAVEL, false)
        read = prefs.getBoolean(Constants.EXTRA_READ, false)
        gameHobby = prefs.getBoolean(Constants.EXTRA_GAME_HOBBY, false)
        shop = prefs.getBoolean(Constants.EXTRA_SHOP, false)
        maxScore = prefs.getInt(Constants.PREF_MAX_SCORE, 0)
    }

    private fun saveData() {
        prefs.edit {
            putString(Constants.EXTRA_NAME, name)
            putString(Constants.EXTRA_GENDER, gender)
            putString(Constants.EXTRA_STUID, stuid)
            putString(Constants.EXTRA_PHONE, phone)
            putString(Constants.EXTRA_EMAIL, email)
            putString(Constants.EXTRA_AVATAR_URI, avatarUri)
            putBoolean(Constants.EXTRA_TRAVEL, travel)
            putBoolean(Constants.EXTRA_READ, read)
            putBoolean(Constants.EXTRA_GAME_HOBBY, gameHobby)
            putBoolean(Constants.EXTRA_SHOP, shop)
            putInt(Constants.PREF_MAX_SCORE, maxScore)
        }
    }

    private fun refreshUI() {
        showAvatar(avatarUri)
        tvName.text = getString(R.string.name_format, name)
        tvGender.text = getString(R.string.gender_format, gender)
        tvStuid.text = getString(R.string.stu_id_format, stuid)
        tvPhone.text = getString(R.string.phone_format, phone)
        tvEmail.text = getString(R.string.email_format, email)

        val hobbyList = mutableListOf<String>()
        if (travel) hobbyList.add(getString(R.string.hobby_travel))
        if (read) hobbyList.add(getString(R.string.hobby_read))
        if (gameHobby) hobbyList.add(getString(R.string.hobby_game))
        if (shop) hobbyList.add(getString(R.string.hobby_shop))
        tvHobby.text = getString(
            R.string.hobby_format,
            hobbyList.joinToString(getString(R.string.hobby_separator))
        )
        tvMaxScore.text = getString(R.string.high_score_format, maxScore)
        supportActionBar?.title = getString(R.string.main_title_format, name, stuid)
    }

    override fun onResume() {
        super.onResume()
        loadData()
        refreshUI()
    }

    private fun applyEditedInfo(data: Intent) {
        name = data.getStringExtra(Constants.EXTRA_NAME) ?: ""
        gender = data.getStringExtra(Constants.EXTRA_GENDER) ?: ""
        stuid = data.getStringExtra(Constants.EXTRA_STUID) ?: ""
        phone = data.getStringExtra(Constants.EXTRA_PHONE) ?: ""
        email = data.getStringExtra(Constants.EXTRA_EMAIL) ?: ""
        avatarUri = data.getStringExtra(Constants.EXTRA_AVATAR_URI)
        travel = data.getBooleanExtra(Constants.EXTRA_TRAVEL, false)
        read = data.getBooleanExtra(Constants.EXTRA_READ, false)
        gameHobby = data.getBooleanExtra(Constants.EXTRA_GAME_HOBBY, false)
        shop = data.getBooleanExtra(Constants.EXTRA_SHOP, false)
        saveData()
        refreshUI()
    }

    private fun applyGameResult(data: Intent) {
        val currentGameScore = data.getIntExtra(Constants.EXTRA_GAME_SCORE, 0)
        if (currentGameScore > maxScore) {
            maxScore = currentGameScore
            saveData()
        }
        refreshUI()
    }

    private fun showAvatar(uriString: String?) {
        if (uriString.isNullOrBlank()) {
            ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder)
            return
        }

        try {
            ivAvatar.setImageURI(uriString.toUri())
        } catch (_: SecurityException) {
            avatarUri = null
            ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder)
        }
    }
}
