package com.example.leafnovel.ui.main.view

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.leafnovel.R
import com.example.leafnovel.data.api.NovelApi
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.data.model.ChapterContent
import com.example.leafnovel.ui.base.BookContentViewModelFactory
import com.example.leafnovel.ui.main.adapter.BookChAdapter
import com.example.leafnovel.ui.main.viewmodel.BookContentViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_book_content.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList


class BookContentActivity : AppCompatActivity(), BookChAdapter.OnItemClickListener {
    companion object {
        const val TAG = "BookContentActivity MVVM測試"
    }

    val preference: SharedPreferences by lazy { getSharedPreferences("UiSetting", Context.MODE_PRIVATE) }
    val adapter = BookChAdapter()

    //    小說章節的index(基底用)
    var nowChapterIndex = 0

    //    目前閱讀的小說章節index
    var nowChapterReadIndex = 0

    private var lastViewHeight = 0
//    除小說高度的值
    var under = 2000

    var contentViewHeightList = mutableListOf<Int>()

    private lateinit var viewModel: BookContentViewModel

    private lateinit var bookChId: String
    private lateinit var chTitle: String
    private lateinit var chUrl: String
    lateinit var bookId: String
    lateinit var bookTitle: String
    private var bookIndex: Int = 0

    var allChapters: ArrayList<BookChapter>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_content)

        setData()
        setReceiver()
        setActbar()
        setUI()
        setObserver()
        setUiListener()
        initBottomNavigationView()
    }

    private fun setReceiver() {
        viewModel.initSystemTime()
    }

    private fun setData() {
        bookChId = intent.getStringExtra("BOOK_CH_ID") ?: ""
        chTitle = intent.getStringExtra("BOOK_CH_TITLE") ?: ""
        chUrl = intent.getStringExtra("BOOK_CH_URL") ?: ""
        bookId = intent.getStringExtra("BOOK_ID") ?: ""
        bookTitle = intent.getStringExtra("BOOK_TITLE") ?: ""
        bookIndex = intent.getIntExtra("BOOK_INDEX", 0)

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
            BookContentViewModelFactory(applicationContext, tempBookChapter, allChapters!!, bookTitle)
        ).get(BookContentViewModel::class.java)

//        nowChNum = viewModel.tempChapterIndex.value ?: 0
        NowLookChapter.text = chTitle
        nowChapterIndex = bookIndex
        nowChapterReadIndex = bookIndex
//        nowChapterReadEndIndex = bookIndex
//        viewModel.tempChapterReadEndIndex.postValue(nowChapterReadIndex)
        viewModel.tempChapterReadIndex.postValue(nowChapterReadIndex)
        viewModel.tempChapterIndex.postValue(nowChapterIndex)
        adapter.lastPositionChange(nowChapterIndex)
    }

    private fun setObserver() {
        viewModel.tempChapterIndex.observe(this, { tempChapterIndex ->
            nowChapterIndex = tempChapterIndex
//            adapter.lastPositionChange(nowChapterIndex)
        })

        viewModel.tempChapterReadIndex.observe(this, { chapterReadIndex ->
            nowChapterReadIndex = chapterReadIndex
            adapter.lastPositionChange(nowChapterReadIndex)
        })

//        viewModel.tempChapterIndex.observe(this, Observer { tempChapterEndIndex ->
//            nowChapterReadEndIndex = tempChapterEndIndex
//        })

        viewModel.chapterContent.observe(this, { chapterContent ->
            ChTitle.text = chapterContent.chTitle
            ChContent.text = chapterContent.chContent
            scrollView2.fullScroll(NestedScrollView.FOCUS_UP)
            LoadMoreProgressBar.visibility = View.INVISIBLE
        })

//        viewModel.loadNextChapter().observe(this@BookContentActivity,loadChapterObserver)
        viewModel.loadChapterContent.observe(this@BookContentActivity, loadChapterObserver)

        viewModel.systemTime.observe(this, { systemTime ->
            SystemTimeView.text = systemTime
        })
        viewModel.batteryIsCharging.observe(this, { isCharging ->
            novelBatteryView.isCharging = isCharging
        })
        viewModel.batteryLevel.observe(this, { batteryLevel ->
            novelBatteryView.batteryLevel = batteryLevel
        })



    }

    private val loadChapterObserver = object : Observer<ChapterContent> {
        override fun onChanged(chapterContent: ChapterContent?) {
//            viewModel.loadNextChapter().removeObserver(this)
//            viewModel.loadChapterContent.value = null
            if (chapterContent != null) {
                val mConstraintLayout =
                    layoutInflater.inflate(
                        R.layout.row_chapter_content, ChapterListView,
                        false
                    ) as ConstraintLayout
                Log.d(TAG, "-----------------------收到回傳時-----------------------------")
                Log.d(TAG, "chapterContent.chTitle ${chapterContent.chTitle}")
                Log.d(TAG, "-----------------------收到回傳時END------------------------------")
                val mTitle = mConstraintLayout.getViewById(R.id.row_chapter_title) as TextView
                val mContent = mConstraintLayout.getViewById(R.id.row_chapter_content) as TextView
                val fontSize = preference.getFloat(getString(R.string.novel_fontsize), 16F)

                mTitle.textSize = fontSize
                mContent.textSize = fontSize + 4
                mTitle.text = chapterContent.chTitle
                mContent.text = chapterContent.chContent
                if (checkUiModeNight()) {
                    mTitle.setTextColor(ContextCompat.getColor(applicationContext, R.color.fontNight))
                    mContent.setTextColor(ContextCompat.getColor(applicationContext, R.color.fontNight))
                } else {
                    mTitle.setTextColor(ContextCompat.getColor(applicationContext, R.color.fontMorning))
                    mContent.setTextColor(ContextCompat.getColor(applicationContext, R.color.fontMorning))
                }

                ChapterListView.addView(mConstraintLayout)
//                            adapter.lastPositionChange(nowChapterIndex)
                SwipToRefreshView.isRefreshing = false
            }
        }
    }

    //    TODO 更新頁面
    val refreshListener = SwipeRefreshLayout.OnRefreshListener {
//        myList.shuffle()
//        adapter.notifyDataSetChanged()
        Handler().postDelayed({ SwipToRefreshView.isRefreshing = false }, 2000)
    }

    //記得改寫成mvvm
    override fun onItemClick(bookCh: BookChapter, position: Int) {
        Toast.makeText(this, "Item ${bookCh.chtitle} clicked", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            val chapterContents = NovelApi.RequestChTextBETA(bookCh.chUrl, bookCh.chtitle, bookTitle)
            allChapters?.let {
                if (bookCh.chUrl == it[position].chUrl) {
//                nowChapterIndex = position
//                nowChapterReadIndex = position
                    viewModel.tempChapterIndex.postValue(position)
                    viewModel.tempChapterReadIndex.postValue(position)

                } else {
                    for (i in it.indices) {
                        if (it[i].chUrl == bookCh.chUrl) {
//                        nowChapterIndex = i
//                        nowChapterReadIndex = i
                            viewModel.tempChapterIndex.postValue(i)
                            viewModel.tempChapterReadIndex.postValue(i)
                            break
                        }
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
        if (checkUiModeNight()) {
            BackgroundView.setBackgroundResource(R.color.bgNight)
            ChTitle.setTextColor(ContextCompat.getColor(this, R.color.fontNight))
            ChContent.setTextColor(ContextCompat.getColor(this, R.color.fontNight))
        } else {
            val bgResId = preference.getInt(getString(R.string.novel_background), R.color.bgMorning)
            BackgroundView.setBackgroundResource(bgResId)
            ChTitle.setTextColor(ContextCompat.getColor(this, R.color.fontMorning))
            ChContent.setTextColor(ContextCompat.getColor(this, R.color.fontMorning))
        }

        viewModel.initBatteryLevel()
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
            LoadMoreProgressBar.visibility = View.VISIBLE
            viewModel.goLastChapter()
        }
        NextPageBT.setOnClickListener {
            LoadMoreProgressBar.visibility = View.VISIBLE
            viewModel.goNextChapter()
        }

        FontSizeSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var tempFontSizeValue = 16F
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //                max is 25 but min is 10
                tempFontSizeValue = progress.toFloat() + 10
                val allTextViewList = getAllTextViewInChapterList()
                for (i in allTextViewList.chapterTitleTextViewList) {
                    i.textSize = tempFontSizeValue
                }
                for (i in allTextViewList.chapterContextTextViewList) {
                    i.textSize = (tempFontSizeValue + 4)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                updateContentViewHeightList()
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
                var totalLenght: Int
                var nowLenght: Int
                if (contentViewHeightList.size > 1) {
                    for (i in contentViewHeightList.size downTo 2) {
                        if (i == 2) {
                            nowLenght = scrollY / under
                            totalLenght = contentViewHeightList[1] / under
                            NowLookProgressiew.text = "${nowLenght + 1}/${totalLenght + 1}"
//                            nowChReadNum = 0
                            if (nowChapterReadIndex != nowChapterIndex) {
                                nowChapterReadIndex = nowChapterIndex
                                viewModel.tempChapterReadIndex.postValue(nowChapterIndex)
                                NowLookChapter.text = viewModel.allChapterList.value?.get(nowChapterIndex)?.chtitle
                            }
                        } else {
                            if (scrollY in contentViewHeightList[i - 2]..contentViewHeightList[i - 1]) {
                                nowLenght = (scrollY - contentViewHeightList[i - 2]) / under
                                totalLenght = (contentViewHeightList[i - 1] - contentViewHeightList[i - 2]) / under
                                NowLookProgressiew.text = "${nowLenght + 1}/${totalLenght + 1}"
                                if (nowChapterReadIndex != nowChapterIndex - i + 2) {
                                    nowChapterReadIndex = nowChapterIndex - i + 2
                                    viewModel.tempChapterReadIndex.postValue(nowChapterIndex - i + 2)
                                    NowLookChapter.text =
                                        viewModel.allChapterList.value?.get(nowChapterIndex - i + 2)?.chtitle
                                }
                                break
                            }
                        }
                    }
                }
//                滑到底部
                v?.let {
                    if (scrollY == it.getChildAt(0).measuredHeight - it.measuredHeight) {
//                        loadNextChapter()
                        Log.d(TAG, "-----------------------滑到底部時-----------------------------")
                        Log.d(TAG, "viewModel 的現在章節Index ${viewModel.tempChapterReadIndex.value}")
                        Log.d(TAG, "-------------------------滑到底部時END---------------------------------")
//                        viewModel.loadNextChapter().observe(this@BookContentActivity,loadChapterObserver)
                        viewModel.loadNextChapter()
                    }
                }
            }
        })
        SwipToRefreshView.setOnRefreshListener(refreshListener)
    }

    private fun updateContentViewHeightList() {
        var tempValue = 0
        for (i in 1..ChapterListView.childCount) {
            val childView = ChapterListView.getChildAt(i - 1) as ConstraintLayout
//            println(" $i 個舊值${contentViewHeightList[i]}")
            tempValue += childView.height
            contentViewHeightList[i] = tempValue
//            println(" $i 個 新值${contentViewHeightList[i]}")
//            todo更新進度
        }
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
        if (checkUiModeNight()) {
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
        NowLookChapter.text = chTitle
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
            if (checkUiModeNight()) {
                "夜間模式"
            } else {
                "日間模式"
            }
    }

    private val bottomNavigationViewListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.dayNightModeItem -> {
//                return true -> night , false -> morning
                if (checkUiModeNight()) {
                    val bgResId = preference.getInt(getString(R.string.novel_background), R.color.bgMorning)
                    BackgroundView.setBackgroundResource(bgResId)
                    val allTextViewList = getAllTextViewInChapterList()
                    for (i in allTextViewList.chapterTitleTextViewList) {
                        i.setTextColor(ContextCompat.getColor(this, R.color.fontMorning))
                    }
                    for (i in allTextViewList.chapterContextTextViewList) {
                        i.setTextColor(ContextCompat.getColor(this, R.color.fontMorning))
                    }
                    item.title = "日間模式"
                    with(preference.edit()) {
                        putBoolean(getString(R.string.novel_uimode), false).apply()
                    }
                } else {
                    BackgroundView.setBackgroundResource(R.color.bgNight)
                    val allTextViewList = getAllTextViewInChapterList()
                    for (i in allTextViewList.chapterTitleTextViewList) {
                        i.setTextColor(ContextCompat.getColor(this, R.color.fontNight))
                    }
                    for (i in allTextViewList.chapterContextTextViewList) {
                        i.setTextColor(ContextCompat.getColor(this, R.color.fontNight))
                    }
//                    ChTitle.setTextColor(ContextCompat.getColor(this, R.color.fontNight))
//                    ChContent.setTextColor(ContextCompat.getColor(this, R.color.fontNight))
                    item.title = "夜間模式"
                    with(preference.edit()) {
                        putBoolean(getString(R.string.novel_uimode), true).apply()
                    }
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

    fun checkUiModeNight(): Boolean {
        return preference.getBoolean(getString(R.string.novel_uimode), false)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val observer: ViewTreeObserver = ChapterListView.viewTreeObserver
        observer.addOnGlobalLayoutListener(onGlobalLayoutListener)
//        observer.addOnGlobalLayoutListener {
//            if (ChapterListView.height != lastViewHeight) {
//                contentViewHeightList.add(ChapterListView.height)
//                if(contentViewHeightList.size == 2){NowLookProgressiew.text = "1/${(contentViewHeightList[1]/under)+1}"}
//                lastViewHeight = ChapterListView.height
//                println("總高度測量 高度 : " + lastViewHeight)
//                println("控件數量 高度 : " + ChapterListView.childCount)
//            }
//        }
    }

    private val onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        if (ChapterListView.height != lastViewHeight) {
            contentViewHeightList.add(ChapterListView.height)
            if (contentViewHeightList.size == 2) {
                NowLookProgressiew.text = "1/${(contentViewHeightList[1] / under) + 1}"
            }
            lastViewHeight = ChapterListView.height
            println("總高度測量 高度 : " + lastViewHeight)
            println("控件數量 高度 : " + ChapterListView.childCount)
        }
    }

//    private fun refreshChapter() {
//        Handler().postDelayed({SwipToRefreshView.isRefreshing = false},2000)
//    }

//    取得所有畫面上TEXTVIEW子控件的集合
    fun getAllTextViewInChapterList(): TextViewInChapterList {
        val chapterTitleTextViewList = mutableListOf<TextView>()
        val chapterContextTextViewList = mutableListOf<TextView>()
        for (i in ChapterListView.childCount downTo 1) {
            val parentView = ChapterListView.getChildAt(i - 1) as ConstraintLayout
//            title
            chapterTitleTextViewList.add(parentView.getChildAt(0) as TextView)
//            context
            chapterContextTextViewList.add(parentView.getChildAt(1) as TextView)
        }
        return TextViewInChapterList(chapterTitleTextViewList, chapterContextTextViewList)
    }

    inner class TextViewInChapterList(
        _chapterTitleTextViewList: MutableList<TextView>,
        _chapterContextTextViewList: MutableList<TextView>
    ) {
        val chapterTitleTextViewList = _chapterTitleTextViewList
        val chapterContextTextViewList = _chapterContextTextViewList
    }

    override fun onDestroy() {
        super.onDestroy()
        ChapterListView.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
    }


}