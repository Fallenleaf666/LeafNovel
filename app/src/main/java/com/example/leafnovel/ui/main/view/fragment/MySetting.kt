package com.example.leafnovel.ui.main.view.fragment

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.leafnovel.R
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.data.database.StoredBookDB
import com.example.leafnovel.data.repository.Repository
import kotlinx.android.synthetic.main.fragment_my_setting.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    }

    override fun onDestroyView() {
        Log.d(TAG,"onDestroyView")
        super.onDestroyView()
        NightReadModeSwitch.setOnCheckedChangeListener(null)
    }
}