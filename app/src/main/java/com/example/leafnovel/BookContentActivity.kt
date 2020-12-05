package com.example.leafnovel

import NovelApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_book_content.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BookContentActivity : AppCompatActivity(),BookChAdapter.OnItemClickListener {
//    lateinit var bottomNavigationView : BottomNavigationView
//    companion object{}
    val preference by lazy {getSharedPreferences("UiSetting",Context.MODE_PRIVATE)}
    val adapter = BookChAdapter()
    var nowChNum = 0

    lateinit var bookChId :String
    lateinit var chTitle :String
    lateinit var chUrl :String
    lateinit var bookId :String
    lateinit var bookTitle :String

    var allChapters : ArrayList<BookChapter>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_content)

        bookChId =  intent.getStringExtra("BOOK_CH_ID")
        chTitle =  intent.getStringExtra("BOOK_CH_TITLE")
        chUrl =  intent.getStringExtra("BOOK_CH_URL")
        bookId =  intent.getStringExtra("BOOK_ID")
        bookTitle =  intent.getStringExtra("BOOK_TITLE")

        setActbar()
        setUI()
        setUiListener()
        initBottomNavigationView()
        getTransData()
//        initBottomNavigationView()


//        val book_chTitleView = findViewById<TextView>(R.id.ChTitle).apply { text = chTitle }
//        val book_chContentView = findViewById<TextView>(R.id.ChContent).apply { text = "" }
        ChTitle.text = chTitle
        ChContent.text = ""
        CoroutineScope(Dispatchers.IO).launch {
//            val chapterContents = NovelApi.RequestChText(chUrl)
            val chapterContents = NovelApi.RequestChTextBETA(chUrl, bookTitle)
//            Log.d(TAG,"onCreate:${bookResults.size}")
            launch(Dispatchers.Main) {
//                for(line in chapterContents){
//                    book_chContentView.setText(book_chContentView.text.toString() + line.chapterLineContent +"\n\n")
                ChContent.text = chapterContents
//                }
            }
        }
//請求資源
    }

    private fun getTransData() {
        allChapters = intent.getParcelableArrayListExtra<BookChapter>("NOVEL_CHAPTERS")
        allChapters?.let {
            adapter.setItems(it, this@BookContentActivity)
        }?: CoroutineScope(Dispatchers.IO).launch {
            val bookChResults = NovelApi.RequestChList(bookId)
//            Log.d(TAG,"onCreate:${bookResults.size}")
            launch(Dispatchers.Main) {
                adapter.setItems(bookChResults, this@BookContentActivity)
            }
        }
        allChapters?.let {
            for(i in it.indices){
                if(it[i].chUrl == chUrl){
                    nowChNum = i
                    break
                }
            }
        }
    }

    override fun onItemClick(bookCh: BookChapter) {
        Toast.makeText(this, "Item ${bookCh.chtitle} clicked", Toast.LENGTH_SHORT).show()
        val intent = Intent(this,BookContentActivity::class.java).apply {
            putExtra("BOOK_CH_ID",bookCh.chId)
            putExtra("BOOK_CH_URL",bookCh.chUrl)
            putExtra("BOOK_CH_TITLE",bookCh.chtitle)
        }
    }

    private fun setUI() {
        val fontSize = preference.getFloat(getString(R.string.novel_fontsize),16F)
        ChTitle.textSize = fontSize
        ChContent.textSize = fontSize + 4
        windowBrightness = preference.getFloat(getString(R.string.window_brightness),getSysWindowBrightness())
//        if windowBrightness not default it will in 0 ~ 1
        LightSeekBar.progress = if (windowBrightness in 0.0..1.0) (windowBrightness * 100).toInt() else 0
        FontSizeSeekBar.progress = (fontSize - 10).toInt()
//lock navigationView avoid hand slide
        DrawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//BookDirectory UI
        BookChRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@BookContentActivity)
            adapter = this@BookContentActivity.adapter
        }
    }

    private fun setUiListener() {
        ControlView.setOnClickListener{
            FunctionMenu.visibility = View.VISIBLE
        }
        ImageView.setOnClickListener{
            when(StyleSettingView.visibility){
                View.VISIBLE -> StyleSettingView.visibility = View.GONE
            }
            FunctionMenu.visibility = View.INVISIBLE
        }
        LastPageBT.setOnClickListener {
            var tempChUrl =""
            var tempBookTitle = ""
            var tempChapterContents = ""
            var hasLast = false
//            之後新增檢查正序倒序
            CoroutineScope(Dispatchers.IO).launch {
                allChapters?.let {
                if(nowChNum != it.size){
                    tempChUrl =  it[nowChNum+1].chUrl
                    tempBookTitle =  it[nowChNum+1].chtitle
                    tempChapterContents = NovelApi.RequestChTextBETA(tempChUrl, tempBookTitle)
                    nowChNum++
                    hasLast = true
                }else{
                    hasLast = false
                }
                }
                launch(Dispatchers.Main) {
                    if(hasLast){
                        ChTitle.text = tempBookTitle
                        ChContent.text = tempChapterContents
                        Toast.makeText(applicationContext, "上一章", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(applicationContext, "已經沒有上一章囉", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        NextPageBT.setOnClickListener{
            var tempChUrl =""
            var tempBookTitle = ""
            var tempChapterContents = ""
            var hasNext = false
//            之後新增檢查正序倒序
            CoroutineScope(Dispatchers.IO).launch {
                allChapters?.let {
                    if(nowChNum != 0){
                        tempChUrl =  it[nowChNum - 1].chUrl
                        tempBookTitle =  it[nowChNum - 1].chtitle
                        tempChapterContents = NovelApi.RequestChTextBETA(tempChUrl, tempBookTitle)
                        nowChNum--
                        hasNext = true
                    }else{
                        hasNext = false
                    }
                }
                launch(Dispatchers.Main) {
                    if(hasNext){
                        Toast.makeText(applicationContext, "下一章", Toast.LENGTH_SHORT).show()
                        ChTitle.text = tempBookTitle
                        ChContent.text = tempChapterContents
                    }else{
                        Toast.makeText(applicationContext, "已經沒有下一章囉", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        FontSizeSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            var tempFontSizeValue = 16F
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //                max is 25 but min is 10
                tempFontSizeValue = progress.toFloat() +10
                ChTitle.textSize = tempFontSizeValue
                ChContent.textSize = (tempFontSizeValue + 4)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                with(preference.edit()){
                    putFloat(getString(R.string.novel_fontsize),tempFontSizeValue).apply()
                }
            }
        })

        LightSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            var tempLightValue = preference.getFloat(getString(R.string.window_brightness),0F)
//            var tempLightValue = 0F
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tempLightValue = progress / 100F
                windowBrightness = tempLightValue
        }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //                set app window_brightness , it also can put in onDestroy
                with(preference.edit()){
                    putFloat(getString(R.string.window_brightness),tempLightValue).apply()
                }
            }
        })
        DeletePrefBT.setOnClickListener{
            preference.edit().clear().apply()
        }
    }
    private fun setActbar() {
        setSupportActionBar(ToolBar)
        supportActionBar?.apply {
//            setTitle("")
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initBottomNavigationView() {
        bottomNavigation.setOnNavigationItemSelectedListener(bottomNavigationViewListener)
    }

    private val bottomNavigationViewListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.dayNightModeItem -> {
                when (StyleSettingView.visibility) {
                    View.VISIBLE -> StyleSettingView.visibility = View.GONE
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.directoryItem -> {
                when (StyleSettingView.visibility) {
                    View.VISIBLE -> StyleSettingView.visibility = View.GONE
                }
                DrawerLayout.openDrawer(GravityCompat.START)
                return@OnNavigationItemSelectedListener true
            }
            R.id.fontSettingItem -> {
                when (StyleSettingView.visibility) {
                    View.GONE -> StyleSettingView.visibility = View.VISIBLE
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

//screen brightness
    var Activity.windowBrightness
        get() = window.attributes.screenBrightness
        set(brightness) {
//            less than 0 or big then 1.0 see as sys default brightness (-1)
            window.attributes = window.attributes.apply {
                screenBrightness = if (brightness > 1.0 || brightness < 0) -1.0F else brightness
            }
        }

    private fun getSysWindowBrightness():Float{
        var nowWindowBrightness = 0
        try{
//  get system windowBrightness it usually in 0 ~ 255 , need adjust to 0 ~ 100 in seekbar
            nowWindowBrightness = Settings.System.getInt(applicationContext.contentResolver,Settings.System.SCREEN_BRIGHTNESS)
        }catch (e:Exception){
            e.printStackTrace()
        }
    return nowWindowBrightness/255F
    }
}