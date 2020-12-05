package com.example.leafnovel

import NovelApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_book_content.*
import kotlinx.android.synthetic.main.activity_book_directory.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BookContentActivity : AppCompatActivity(),BookChAdapter.OnItemClickListener {
//    lateinit var bottomNavigationView : BottomNavigationView
//    companion object{}
    val preference by lazy {getSharedPreferences("UiSetting",Context.MODE_PRIVATE)}
    val adapter = BookChAdapter()

    lateinit var bookChId :String
    lateinit var chTitle :String
    lateinit var chUrl :String
    lateinit var bookId :String
    lateinit var bookTitle :String

    var allChapters : ArrayList<BookChapter>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_content)

        SetActbar()
        SetUI()
        SetUiListener()
        initBottomNavigationView()
        GetTransData()
//        initBottomNavigationView()
        bookChId =  intent.getStringExtra("BOOK_CH_ID")
        chTitle =  intent.getStringExtra("BOOK_CH_TITLE")
        chUrl =  intent.getStringExtra("BOOK_CH_URL")
        bookId =  intent.getStringExtra("BOOK_ID")
        bookTitle =  intent.getStringExtra("BOOK_TITLE")


        val book_chTitleView = findViewById<TextView>(R.id.ChTitle).apply { text = chTitle }
        val book_chContentView = findViewById<TextView>(R.id.ChContent).apply { text = "" }
        CoroutineScope(Dispatchers.IO).launch {
//            val chapterContents = NovelApi.RequestChText(chUrl)
            val chapterContents = NovelApi.RequestChTextBETA(chUrl, bookTitle)
//            Log.d(TAG,"onCreate:${bookResults.size}")
            launch(Dispatchers.Main) {
//                for(line in chapterContents){
//                    book_chContentView.setText(book_chContentView.text.toString() + line.chapterLineContent +"\n\n")
                    book_chContentView.setText(chapterContents)
//                }
            }
        }
//請求資源
    }

    private fun GetTransData() {
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
    }

    override fun onItemClick(bookCh: BookChapter) {
        Toast.makeText(this, "Item ${bookCh.chtitle} clicked", Toast.LENGTH_SHORT).show()
        val intent = Intent(this,BookContentActivity::class.java).apply {
            putExtra("BOOK_CH_ID",bookCh.chId)
            putExtra("BOOK_CH_URL",bookCh.chUrl)
            putExtra("BOOK_CH_TITLE",bookCh.chtitle)
        }
    }

    private fun SetUI() {
        val fontSize = preference.getFloat(getString(R.string.novel_fontsize),16F)
        ChTitle.setTextSize(fontSize)
        ChContent.setTextSize(fontSize + 4)
        windowBrightness = preference.getFloat(getString(R.string.window_brightness),GetSysWindowBrightness())
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

    private fun SetUiListener() {
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
//            CoroutineScope(Dispatchers.IO).launch {
//                val tempChUrl =  intent.getStringExtra("BOOK_CH_URL")
//                val tempBookTitle =  intent.getStringExtra("BOOK_TITLE")
//                val tempChapterContents = NovelApi.RequestChTextBETA(tempChUrl, tempBookTitle)
//                launch(Dispatchers.Main) {
//                    ChTitle.setText(tempBookTitle)
//                    ChContent.setText(tempChapterContents)
//                }
//            }
            Toast.makeText(applicationContext, "上一章", Toast.LENGTH_SHORT).show()
        }
        NextPageBT.setOnClickListener{
            Toast.makeText(applicationContext, "下一節", Toast.LENGTH_SHORT).show()
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
    private fun SetActbar() {
        setSupportActionBar(ToolBar)
        getSupportActionBar()?.apply {
//            setTitle("")
            setDisplayHomeAsUpEnabled(true);
            setHomeButtonEnabled(true);
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

    fun GetSysWindowBrightness():Float{
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