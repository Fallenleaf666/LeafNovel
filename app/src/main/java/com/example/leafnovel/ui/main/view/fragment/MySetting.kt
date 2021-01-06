package com.example.leafnovel.ui.main.view.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.leafnovel.HardWareInformationUtil
import com.example.leafnovel.R
import com.example.leafnovel.checkNetConnect
import com.example.leafnovel.data.database.StoredBookDB
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.data.repository.Repository
import kotlinx.android.synthetic.main.fragment_my_setting.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.mail.internet.MimeMessage


class MySetting : Fragment() {
    companion object {
        val newInstance: MySetting by lazy {
            MySetting()
        }
        const val TAG = "MySetting"
    }

    var preference: SharedPreferences? = null
    var repository: Repository? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        initData()
        return inflater.inflate(R.layout.fragment_my_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        setUiListener()
    }

    private fun initUi() {
        preference?.let {
//          預設自動讀取
            AutoLoadNextSwitch.isChecked = it.getBoolean(getString(R.string.novel_loadmore), true)
//          預設為日間 if nightMode return true
            NightReadModeSwitch.isChecked = it.getBoolean(getString(R.string.novel_uimode), false)
//          預設不隱藏標題
            ChapterTitleHideSwitch.isChecked = it.getBoolean(getString(R.string.novel_title_hide), false)
        }
    }

    private fun initData() {
        context?.let { mContext ->
            repository = StoredBookDB.getInstance(mContext)?.storedbookDao()?.let { dao -> Repository(dao) }
        }
        preference = activity?.getSharedPreferences("UiSetting", Context.MODE_PRIVATE)
    }

    fun setUiListener() {
//        preference?.registerOnSharedPreferenceChangeListener(preferenceListener)
        //      刪除本地儲存
        DeleteNovelChapterView.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                repository?.deleteAllChapter()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "已刪除本地儲存", Toast.LENGTH_SHORT).show()
                }
            }
        }
//      自動讀取下一章節
        AutoLoadNextSwitch.setOnCheckedChangeListener { _, isChecked ->
            CoroutineScope(Dispatchers.IO).launch {
                preference?.edit()?.putBoolean(getString(R.string.novel_loadmore), isChecked)?.apply()
                withContext(Dispatchers.Main) {
                    val notify = if (isChecked) "已開啟自動載入" else "已經關閉自動載入"
                    Toast.makeText(context, notify, Toast.LENGTH_SHORT).show()
                }
            }
        }
//      切換夜間模式
        NightReadModeSwitch.setOnCheckedChangeListener { _, isChecked ->
//            if (!isUpdateUiModeChange) {
                CoroutineScope(Dispatchers.IO).launch {
                    preference?.edit()?.putBoolean(getString(R.string.novel_uimode), isChecked)?.apply()
                    withContext(Dispatchers.Main) {
                        val notify = if (isChecked) "已開啟夜間模式" else "已經關閉夜間模式"
                        Toast.makeText(context, notify, Toast.LENGTH_SHORT).show()
                    }
                }
//            }else{
//                isUpdateUiModeChange = false
//            }
        }
        ChapterTitleHideSwitch.setOnCheckedChangeListener { _, isChecked ->
                CoroutineScope(Dispatchers.IO).launch {
                    preference?.edit()?.putBoolean(getString(R.string.novel_title_hide), isChecked)?.apply()
                    withContext(Dispatchers.Main) {
                        val notify = if (isChecked) "隱藏標題顯示" else "取消標題隱藏"
                        Toast.makeText(context, notify, Toast.LENGTH_SHORT).show()
                    }
                }
        }
//      TODO 改變閱讀字體大小
        TexxtSizeSettingView.setOnClickListener {

        }

        DeleteDbBT.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                repository?.deleteAll()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "已刪除所有收藏", Toast.LENGTH_SHORT).show()
                }
            }
        }
        AddBT.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val storedBook = StoredBook(
                    "易筋經", "達摩", "葉子書城", "第一章 洒家不吃素啦", "",
                    "http://www", false, "120000"
                )
                repository?.insert(storedBook)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "已將書本加入書櫃", Toast.LENGTH_SHORT).show()
                }
            }
        }

        ContactUsView.setOnClickListener {
            launchAlertDialog()
        }
    }

    private fun launchAlertDialog() {
        context?.let { mContext ->
            val dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialogview_send_email2developer, null)
            val builder = AlertDialog.Builder(mContext).apply {
                setView(dialogView)
            }
            val sendBt = dialogView.findViewById<Button>(R.id.SendToDeveloperBT)
            val sendBody = dialogView.findViewById<TextView>(R.id.SendBodyTextView)
            val problemSpinner = dialogView.findViewById<Spinner>(R.id.SendEmailProblemSpinner)

            val dialog = builder.create().apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//              window?.attributes?.horizontalMargin = 80f
                //android.R.id.message
            }
            val arrayAdapter = ArrayAdapter.createFromResource(
                mContext,
                R.array.email_problem_type,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                problemSpinner.adapter = adapter
            }
            var problemType = arrayAdapter.getItem(0).toString()
            problemSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                    problemType = parent?.getItemAtPosition(pos).toString()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            sendBt.setOnClickListener {
                if (checkNetConnect(mContext)) {
                    sendEmail(sendBody.text.toString(), problemType)
                } else {
                    Toast.makeText(context, getString(R.string.ALERT_WEBCONNECT_STATE), Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()
        }
    }

    private fun sendEmail(sendBody: String, problemType: String) {
        val intent = Intent(Intent.ACTION_SEND)
        val tos = arrayOf(getString(R.string.developerEmail))
        val phoneInfo = context?.let { HardWareInformationUtil.getHardWareInformationUtil(it) }
        var mailTitle = "${getString(R.string.app_chines_name)} 用戶回報"
        mailTitle += " 型號 : ${phoneInfo?.model ?: "未取得"} SDK版本 : ${phoneInfo?.sdkVersion ?: "未取得"}"
//      寄給開發人
        intent.putExtra(Intent.EXTRA_EMAIL, tos)
        intent.putExtra(Intent.EXTRA_TEXT, sendBody)
        intent.putExtra(Intent.EXTRA_SUBJECT, mailTitle)
//      附件
//      intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/Chrysanthemum.jpg"))
//      intent.type = "image/*"
//      intent.type = "text/plain"
        intent.type = "message/rfc882"
        Intent.createChooser(intent, "選擇寄件軟體")
        startActivity(intent)
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        NightReadModeSwitch.setOnCheckedChangeListener(null)
    }
}