package com.example.leafnovel.ui.main.view

import com.example.leafnovel.data.api.NovelApi
import android.app.Activity
import android.content.Context
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
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leafnovel.R
import com.example.leafnovel.ui.main.adapter.BookChAdapter
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.ui.main.viewmodel.BookContentViewModel
import com.example.leafnovel.ui.base.BookContentViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_book_content.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BookContentActivity : AppCompatActivity(), BookChAdapter.OnItemClickListener {
//    companion object{}
    val preference by lazy { getSharedPreferences("UiSetting", Context.MODE_PRIVATE) }
    val adapter = BookChAdapter()
    var nowChNum = 0


    private lateinit var viewModel: BookContentViewModel

    lateinit var bookChId: String
    lateinit var chTitle: String
    lateinit var chUrl: String
    lateinit var bookId: String
    lateinit var bookTitle: String

    var allChapters: ArrayList<BookChapter>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_content)

        setData()
        setActbar()
        setUI()
        setObserver()
        setUiListener()
        initBottomNavigationView()
    }

    private fun setData() {
        bookChId = intent.getStringExtra("BOOK_CH_ID") ?: ""
        chTitle = intent.getStringExtra("BOOK_CH_TITLE") ?: ""
        chUrl = intent.getStringExtra("BOOK_CH_URL") ?: ""
        bookId = intent.getStringExtra("BOOK_ID") ?: ""
        bookTitle = intent.getStringExtra("BOOK_TITLE") ?: ""

        allChapters = intent.getParcelableArrayListExtra<BookChapter>("NOVEL_CHAPTERS")
        allChapters?.let {
            adapter.setItems(it, this@BookContentActivity)
        } ?: CoroutineScope(Dispatchers.IO).launch {
            val bookChResults = NovelApi.RequestChList(bookId)
            launch(Dispatchers.Main) {
                adapter.setItems(bookChResults, this@BookContentActivity)
            }
        }
        val tempBookChapter = BookChapter(chTitle, bookChId, chUrl)
        viewModel = ViewModelProvider(
            this,
            BookContentViewModelFactory(applicationContext, tempBookChapter, allChapters!!,bookTitle)
        ).get(BookContentViewModel::class.java)

        nowChNum = viewModel.tempChapterIndex.value ?: 0
        adapter.lastPositionChange(nowChNum)
    }

    private fun setObserver() {
        viewModel.tempChapterIndex.observe(this, Observer { tempChapterIndex ->
            nowChNum = tempChapterIndex
            adapter.lastPositionChange(nowChNum)
        })

        viewModel.chapterContent.observe(this, Observer { chapterContent ->
            ChTitle.text = chapterContent.chTitle
            ChContent.text = chapterContent.chContent
            scrollView2.fullScroll(NestedScrollView.FOCUS_UP)
        })

    }


    override fun onItemClick(bookCh: BookChapter) {
        Toast.makeText(this, "Item ${bookCh.chtitle} clicked", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            val chapterContents = NovelApi.RequestChTextBETA(bookCh.chUrl, bookCh.chtitle,bookTitle)
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
        if(checkUiModeNight()){
            BackgroundView.setBackgroundResource(R.color.bgNight)
            ChTitle.setTextColor(ContextCompat.getColor(this, R.color.fontNight))
            ChContent.setTextColor(ContextCompat.getColor(this, R.color.fontNight))
        }else{
            val bgResId = preference.getInt(getString(R.string.novel_background), R.color.bgMorning)
            BackgroundView.setBackgroundResource(bgResId)
            ChTitle.setTextColor(ContextCompat.getColor(this, R.color.fontMorning))
            ChContent.setTextColor(ContextCompat.getColor(this, R.color.fontMorning))
        }

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

        LastPageBT.setOnClickListener { viewModel.goLastChapter() }
        NextPageBT.setOnClickListener { viewModel.goNextChapter() }

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
        DeletePrefBT.setOnClickListener { preference.edit().clear().apply() }

        scrollView2.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
//                滑到底部
                v?.let {
                    if (scrollY == it.getChildAt(0).measuredHeight - it.measuredHeight) {
//                        loadNextChapter()
                        viewModel.loadNextChapter().observe(this@BookContentActivity, Observer { chapterContent ->

                            val mConstraintLayout =
                                layoutInflater.inflate(R.layout.row_chapter_content, ChapterListView, false) as ConstraintLayout
                            val mTitle = mConstraintLayout.getViewById(R.id.row_chapter_title) as TextView
                            val mContent = mConstraintLayout.getViewById(R.id.row_chapter_content) as TextView
                            val fontSize = preference.getFloat(getString(R.string.novel_fontsize), 16F)

                            mTitle.textSize = fontSize
                            mContent.textSize = fontSize + 4
                            mTitle.text = chapterContent.chTitle
                            mContent.text = chapterContent.chContent
                            ChapterListView.addView(mConstraintLayout)
                            adapter.lastPositionChange(nowChNum)
                        })
                    }
                }
            }
        })
    }

    fun bgColorSelected(view: View) {
        view.let {
            when (it.id) {
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

    private fun setBackgroundRes(resId: Int) {
        BackgroundView.setBackgroundResource(resId)
        with(preference.edit()) {
            putInt(getString(R.string.novel_background), resId).apply()
        }
        if(checkUiModeNight()){
            ChTitle.setTextColor(ContextCompat.getColor(this, R.color.fontMorning))
            ChContent.setTextColor(ContextCompat.getColor(this, R.color.fontMorning))
            with(preference.edit()) {
                putBoolean(getString(R.string.novel_uimode), false).apply()
            }
            bottomNavigation.menu.getItem(0).title = "日間模式"
        }

    }

    private fun setActbar() {
        setSupportActionBar(ToolBar)
        supportActionBar?.apply {
            title = bookTitle
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
        bottomNavigation.menu.getItem(0).title =
            if(checkUiModeNight()){"夜間模式"} else{"日間模式"}
    }

    private val bottomNavigationViewListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.dayNightModeItem -> {
//                return true -> night , false -> morning
                if(checkUiModeNight()){
                    val bgResId = preference.getInt(getString(R.string.novel_background), R.color.bgMorning)
                    BackgroundView.setBackgroundResource(bgResId)
                    ChTitle.setTextColor(ContextCompat.getColor(this, R.color.fontMorning))
                    ChContent.setTextColor(ContextCompat.getColor(this, R.color.fontMorning))
                    item.title = "日間模式"
                    with(preference.edit()) {
                        putBoolean(getString(R.string.novel_uimode), false).apply()
                    }
                }else{
                    BackgroundView.setBackgroundResource(R.color.bgNight)
                    ChTitle.setTextColor(ContextCompat.getColor(this, R.color.fontNight))
                    ChContent.setTextColor(ContextCompat.getColor(this, R.color.fontNight))
                    item.title = "夜間模式"
                    with(preference.edit()) {
                        putBoolean(getString(R.string.novel_uimode), true).apply()
                    }
                }
//                val currentNightMode = checkUiMode()
//                when (currentNightMode) {
//                    Configuration.UI_MODE_NIGHT_NO -> {
//                        delegate.localNightMode = MODE_NIGHT_YES
//                        recreate()
//                    }
//                    Configuration.UI_MODE_NIGHT_YES -> {
//                        delegate.localNightMode = MODE_NIGHT_NO
//                        recreate()
//                    }
//                }
//                when (StyleSettingView.visibility) {
//                    View.VISIBLE -> StyleSettingView.visibility = View.GONE
//                }
//
//                item.title = when (checkUiMode()) {
//                    1 -> "日間模式"
//                    2 -> "夜間模式"
//                    else -> "系統模式"
//                }
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

//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        when (newConfig.uiMode) {
//            Configuration.UI_MODE_NIGHT_NO -> {
//                Toast.makeText(applicationContext, "切換日間", Toast.LENGTH_SHORT).show()
//            }
//            Configuration.UI_MODE_NIGHT_YES -> {
//                Toast.makeText(applicationContext, "切換夜間", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

//    fun checkUiMode(): Int {
//        var currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
//        when (currentNightMode) {
//            Configuration.UI_MODE_NIGHT_NO -> {
//                Configuration.UI_MODE_NIGHT_NO
//                currentNightMode = Configuration.UI_MODE_NIGHT_NO
//            }
//            Configuration.UI_MODE_NIGHT_YES -> {
//                currentNightMode = Configuration.UI_MODE_NIGHT_YES
//            }
//        }
//        return currentNightMode
//    }
fun checkUiModeNight(): Boolean {
    return preference.getBoolean(getString(R.string.novel_uimode),false)
}
}