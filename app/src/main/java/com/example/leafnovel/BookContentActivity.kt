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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_book_content.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BookContentActivity : AppCompatActivity(), BookChAdapter.OnItemClickListener {
    //    lateinit var bottomNavigationView : BottomNavigationView
//    companion object{}
    val preference by lazy { getSharedPreferences("UiSetting", Context.MODE_PRIVATE) }
    val adapter = BookChAdapter()
    var nowChNum = 0

    lateinit var bookChId: String
    lateinit var chTitle: String
    lateinit var chUrl: String
    lateinit var bookId: String
    lateinit var bookTitle: String

    var allChapters: ArrayList<BookChapter>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_content)

        bookChId = intent.getStringExtra("BOOK_CH_ID")
        chTitle = intent.getStringExtra("BOOK_CH_TITLE")
        chUrl = intent.getStringExtra("BOOK_CH_URL")
        bookId = intent.getStringExtra("BOOK_ID")
        bookTitle = intent.getStringExtra("BOOK_TITLE")

        setActbar()
        setUI()
        setUiListener()
        initBottomNavigationView()
        getTransData()


        ChTitle.text = chTitle
        ChContent.text = ""
        CoroutineScope(Dispatchers.IO).launch {
            val chapterContents = NovelApi.RequestChTextBETA(chUrl, bookTitle)
            launch(Dispatchers.Main) {
                ChContent.text = chapterContents
            }
        }
    }

    private fun getTransData() {
        allChapters = intent.getParcelableArrayListExtra<BookChapter>("NOVEL_CHAPTERS")
        allChapters?.let {
            adapter.setItems(it, this@BookContentActivity)
        } ?: CoroutineScope(Dispatchers.IO).launch {
            val bookChResults = NovelApi.RequestChList(bookId)
//            Log.d(TAG,"onCreate:${bookResults.size}")
            launch(Dispatchers.Main) {
                adapter.setItems(bookChResults, this@BookContentActivity)
            }
        }
        allChapters?.let {
            for (i in it.indices) {
                if (it[i].chUrl == chUrl) {
                    nowChNum = i
                    break
                }
            }
        }
        adapter.lastPositionChange(nowChNum)
    }

    override fun onItemClick(bookCh: BookChapter) {
        Toast.makeText(this, "Item ${bookCh.chtitle} clicked", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.IO).launch {
                val chapterContents = NovelApi.RequestChTextBETA(bookCh.chUrl, bookCh.chtitle)
                allChapters?.let {
                    for (i in it.indices) {
                        if (it[i].chUrl == bookCh.chUrl) {
                            nowChNum = i
                            break
                        }
                    }
                }
                launch(Dispatchers.Main) {
                    ChTitle.text = bookCh.chtitle
                    ChContent.text = chapterContents
                    scrollView2.fullScroll(NestedScrollView.FOCUS_UP)
                    DrawerLayout.closeDrawer(GravityCompat.START)
                    FunctionMenu.visibility = View.INVISIBLE
                }
            }

    }

    private fun setUI() {
        val fontSize = preference.getFloat(getString(R.string.novel_fontsize), 16F)
        ChTitle.textSize = fontSize
        ChContent.textSize = fontSize + 4
        windowBrightness = preference.getFloat(getString(R.string.window_brightness), getSysWindowBrightness())
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
        val bgResId = preference.getInt(getString(R.string.novel_background), R.color.bgcolor2)
        BackgroundView.setBackgroundResource(bgResId)
    }

    private fun setUiListener() {

        ChapterListView.setOnClickListener {
            FunctionMenu.visibility = View.VISIBLE
        }
        ImageView.setOnClickListener {
            when (StyleSettingView.visibility) {
                View.VISIBLE -> StyleSettingView.visibility = View.GONE
            }
            FunctionMenu.visibility = View.INVISIBLE
        }
        LastPageBT.setOnClickListener {
            var tempChUrl = ""
            var tempBookTitle = ""
            var tempChapterContents = ""
            var hasLast = false
//            之後新增檢查正序倒序
            CoroutineScope(Dispatchers.IO).launch {
                allChapters?.let {
                    if (nowChNum != it.size) {
                        tempChUrl = it[nowChNum + 1].chUrl
                        tempBookTitle = it[nowChNum + 1].chtitle
                        tempChapterContents = NovelApi.RequestChTextBETA(tempChUrl, tempBookTitle)
                        nowChNum++
                        hasLast = true
                    } else {
                        hasLast = false
                    }
                }
                launch(Dispatchers.Main) {
                    if (hasLast) {
                        ChTitle.text = tempBookTitle
                        ChContent.text = tempChapterContents
                        scrollView2.fullScroll(NestedScrollView.FOCUS_UP)
                        Toast.makeText(applicationContext, "上一章", Toast.LENGTH_SHORT).show()
                        adapter.lastPositionChange(nowChNum)
                    } else {
                        Toast.makeText(applicationContext, "已經沒有上一章囉", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        NextPageBT.setOnClickListener {
            var tempChUrl = ""
            var tempBookTitle = ""
            var tempChapterContents = ""
            var hasNext = false
//            之後新增檢查正序倒序
            CoroutineScope(Dispatchers.IO).launch {
                allChapters?.let {
                    if (nowChNum != 0) {
                        tempChUrl = it[nowChNum - 1].chUrl
                        tempBookTitle = it[nowChNum - 1].chtitle
                        tempChapterContents = NovelApi.RequestChTextBETA(tempChUrl, tempBookTitle)
                        nowChNum--
                        hasNext = true
                    } else {
                        hasNext = false
                    }
                }
                launch(Dispatchers.Main) {
                    if (hasNext) {
                        Toast.makeText(applicationContext, "下一章", Toast.LENGTH_SHORT).show()
                        ChTitle.text = tempBookTitle
                        ChContent.text = tempChapterContents
                        scrollView2.fullScroll(NestedScrollView.FOCUS_UP)
                        adapter.lastPositionChange(nowChNum)
                    } else {
                        Toast.makeText(applicationContext, "已經沒有下一章囉", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        FontSizeSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var tempFontSizeValue = 16F
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //                max is 25 but min is 10
                tempFontSizeValue = progress.toFloat() + 10
                ChTitle.textSize = tempFontSizeValue
                ChContent.textSize = (tempFontSizeValue + 4)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                with(preference.edit()) {
                    putFloat(getString(R.string.novel_fontsize), tempFontSizeValue).apply()
                }
            }
        })

        LightSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var tempLightValue = preference.getFloat(getString(R.string.window_brightness), 0F)

            //            var tempLightValue = 0F
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tempLightValue = progress / 100F
                windowBrightness = tempLightValue
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //                set app window_brightness , it also can put in onDestroy
                with(preference.edit()) {
                    putFloat(getString(R.string.window_brightness), tempLightValue).apply()
                }
            }
        })
        DeletePrefBT.setOnClickListener {
            preference.edit().clear().apply()
        }

        scrollView2.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
//                下滑
//                if(scrollY > oldScrollY){ }
//                上滑
//                if(oldScrollY > scrollY){}
//                頂部
//                if(scrollY == 0){}

//                滑到底部
                v?.let {
                    if (scrollY == it.getChildAt(0).measuredHeight - it.measuredHeight) {
                        loadNextChapter()
                    }
                }
            }
        })


    }

    fun bgColorSelected(view:View){
    view.let {
        when(it.id){
            R.id.Bg_ColorBT1 -> setBackgroundRes(R.drawable.bg_paper5)
            R.id.Bg_ColorBT3 -> setBackgroundRes(R.drawable.bg_paper2)
            R.id.Bg_ColorBT4 -> setBackgroundRes(R.color.bgcolor1)
            R.id.Bg_ColorBT5 -> setBackgroundRes(R.color.bgcolor2)
            R.id.Bg_ColorBT6 -> setBackgroundRes(R.drawable.bg_paper1)
            R.id.Bg_ColorBT7 -> setBackgroundRes(R.drawable.bg_paper3)
            else -> setBackgroundRes(R.color.bgcolor1)
        }
    }
}
    private fun setBackgroundRes(resId :Int){
        BackgroundView.setBackgroundResource(resId)
        with(preference.edit()) {
            putInt(getString(R.string.novel_background),resId).apply()
        }
    }

    private fun loadNextChapter() {
        var tempChUrl = ""
        var tempBookTitle = ""
        var tempChapterContents = ""
        var hasNext = false
//            之後新增檢查正序倒序
        CoroutineScope(Dispatchers.IO).launch {
            allChapters?.let {
                if (nowChNum != 0) {
                    tempChUrl = it[nowChNum - 1].chUrl
                    tempBookTitle = it[nowChNum - 1].chtitle
                    tempChapterContents = NovelApi.RequestChTextBETA(tempChUrl, tempBookTitle)
                    nowChNum--
                    hasNext = true
                } else {
                    hasNext = false
                }
            }
            launch(Dispatchers.Main) {
                if (hasNext) {
                    Toast.makeText(applicationContext, "下一章", Toast.LENGTH_SHORT).show()
                    val mConstraintLayout =
                        layoutInflater.inflate(R.layout.row_chapter_content, ChapterListView, false) as ConstraintLayout
                    val mTitle = mConstraintLayout.getViewById(R.id.row_chapter_title) as TextView
                    val mContent = mConstraintLayout.getViewById(R.id.row_chapter_content) as TextView
                    val fontSize = preference.getFloat(getString(R.string.novel_fontsize), 16F)

                    mTitle.textSize = fontSize
                    mContent.textSize = fontSize + 4
                    mTitle.text = tempBookTitle
                    mContent.text = tempChapterContents
                    ChapterListView.addView(mConstraintLayout)
                    adapter.lastPositionChange(nowChNum)
                } else {
                    Toast.makeText(applicationContext, "已經沒有下一章囉", Toast.LENGTH_SHORT).show()
                }
            }
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
        when (item.itemId) {
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

    private fun getSysWindowBrightness(): Float {
        var nowWindowBrightness = 0
        try {
//  get system windowBrightness it usually in 0 ~ 255 , need adjust to 0 ~ 100 in seekbar
            nowWindowBrightness =
                Settings.System.getInt(applicationContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return nowWindowBrightness / 255F
    }
}