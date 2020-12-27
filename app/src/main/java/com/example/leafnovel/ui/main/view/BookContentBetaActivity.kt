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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.leafnovel.R
import com.example.leafnovel.data.api.NovelApi
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.data.model.ChapterContentBeta
import com.example.leafnovel.data.model.LastReadProgress
import com.example.leafnovel.ui.base.BookContentViewModelFactory
import com.example.leafnovel.ui.main.adapter.BookChapterAdapter
import com.example.leafnovel.ui.main.adapter.ChapterContentAdapter
import com.example.leafnovel.ui.main.viewmodel.BookContentBetaViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_book_beta_content.*
import kotlinx.android.synthetic.main.activity_book_beta_content.BackgroundView
import kotlinx.android.synthetic.main.activity_book_beta_content.BookChRecyclerView
import kotlinx.android.synthetic.main.activity_book_beta_content.DrawerLayout
import kotlinx.android.synthetic.main.activity_book_beta_content.FontSizeSeekBar
import kotlinx.android.synthetic.main.activity_book_beta_content.FunctionMenu
import kotlinx.android.synthetic.main.activity_book_beta_content.ImageView
import kotlinx.android.synthetic.main.activity_book_beta_content.LastPageBT
import kotlinx.android.synthetic.main.activity_book_beta_content.LightSeekBar
import kotlinx.android.synthetic.main.activity_book_beta_content.LoadMoreProgressBar
import kotlinx.android.synthetic.main.activity_book_beta_content.NextPageBT
import kotlinx.android.synthetic.main.activity_book_beta_content.NowLookChapter
import kotlinx.android.synthetic.main.activity_book_beta_content.StyleSettingView
import kotlinx.android.synthetic.main.activity_book_beta_content.SwipToRefreshView
import kotlinx.android.synthetic.main.activity_book_beta_content.ToolBar
import kotlinx.android.synthetic.main.activity_book_beta_content.bottomNavigation
import kotlinx.android.synthetic.main.activity_book_content.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList


class BookContentBetaActivity : AppCompatActivity(), BookChapterAdapter.OnItemClickListener,
    ChapterContentAdapter.OnItemClickListener {
    companion object {
        const val TAG = "BookContentBetaActivity5"
    }

    val preference: SharedPreferences by lazy { getSharedPreferences("UiSetting", Context.MODE_PRIVATE) }

    //    目錄
    val adapter = BookChapterAdapter()

    //    內文
    val chapterContentAdapter = ChapterContentAdapter(this)

    //    目前閱讀的小說章節index
    var nowChapterReadIndex = 0

    //    除小說高度的值
    var under = 2000

    var contentViewHeightList = mutableListOf<Int>()

    private lateinit var viewModel: BookContentBetaViewModel

    private var bookChId: Int = 0
    private lateinit var chTitle: String
    private lateinit var chUrl: String
    lateinit var bookId: String
    lateinit var bookTitle: String
    private var bookIndex: Int = 0

    var allChapters: ArrayList<BookChapter>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_beta_content)

        setData()
        setActbar()
        setUI()
        setObserver()
        setUiListener()
        initBottomNavigationView()
    }

    private fun setData() {
        bookChId = intent.getIntExtra("BOOK_CH_ID", 0)
        chTitle = intent.getStringExtra("BOOK_CH_TITLE") ?: ""
        chUrl = intent.getStringExtra("BOOK_CH_URL") ?: ""
        bookId = intent.getStringExtra("BOOK_ID") ?: ""
        bookTitle = intent.getStringExtra("BOOK_TITLE") ?: ""
        bookIndex = intent.getIntExtra("BOOK_INDEX", 0)

//        填入目錄內容
        allChapters = intent.getParcelableArrayListExtra<BookChapter>("NOVEL_CHAPTERS")
        allChapters?.let {
            adapter.setItems(it, this@BookContentBetaActivity)
        } ?: CoroutineScope(Dispatchers.IO).launch {
            val bookChResults = NovelApi.requestChapterList(bookId)
            launch(Dispatchers.Main) {
                adapter.setItems(bookChResults, this@BookContentBetaActivity)
            }
        }


        val tempBookChapter = BookChapter(bookChId, chTitle, chUrl)
        viewModel = ViewModelProvider(
            this,
            BookContentViewModelFactory(applicationContext, tempBookChapter, allChapters!!, bookTitle, bookId)
        ).get(BookContentBetaViewModel::class.java)

//        nowChNum = viewModel.tempChapterIndex.value ?: 0
        NowLookChapter.text = chTitle
        nowChapterReadIndex = bookIndex

//        adapter.lastPositionChange(nowChapterIndex)
    }

    private fun setObserver() {
        viewModel.chapterContent.observe(this, { chapterContent ->
            chapterContentAdapter.setItems(chapterContent, this)
            ChapterContentRecycleView.scrollToPosition(0)
            LoadMoreProgressBar.visibility = View.INVISIBLE
            FunctionMenu.visibility = View.INVISIBLE
            viewModel.nowLookAtIndex.postValue(chapterContent.chIndex)
        })
        viewModel.loadChapterContent.observe(this, { chapterContent ->
            chapterContentAdapter.addItem(chapterContent, this)
            LoadMoreProgressBar.visibility = View.INVISIBLE
        })

    }

    //    TODO 更新頁面
    val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        chapterContentAdapter.refresh()
//        myList.shuffle()
//        adapter.notifyDataSetChanged()
        Handler().postDelayed({ SwipToRefreshView.isRefreshing = false }, 2000)
    }

    //目錄章節點擊
    override fun onItemClick(bookCh: BookChapter, position: Int) {
        viewModel.goSpecialChapter(bookCh)
    }

    override fun onMoreClick(bookCh: BookChapter, position: Int, view: View) {
//        TODO("Not yet implemented")
    }

    private fun setUI() {
//內文
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ChapterContentRecycleView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = chapterContentAdapter
            addItemDecoration(decoration)
        }
//目錄
        BookChRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@BookContentBetaActivity)
            adapter = this@BookContentBetaActivity.adapter
        }
//取得字體大小
        val fontSize = preference.getFloat(getString(R.string.novel_fontsize), 16F)
        chapterContentAdapter.setFontSize(fontSize)
//取得當前日夜模式
        chapterContentAdapter.setUiByDayNightMode(checkUiModeNight())
        if (checkUiModeNight()) {
            BackgroundView.setBackgroundResource(R.color.bgNight)
        } else {
            val bgResId = preference.getInt(getString(R.string.novel_background), R.color.bgMorning)
            BackgroundView.setBackgroundResource(bgResId)
        }
//取得螢幕亮度
        windowBrightness = preference.getFloat(getString(R.string.window_brightness), getSysWindowBrightness())
//        if windowBrightness not default it will in 0 ~ 1
        LightSeekBar.progress = if (windowBrightness in 0.0..1.0) (windowBrightness * 100).toInt() else 0
        FontSizeSeekBar.progress = (fontSize - 10).toInt()
//lock navigationView avoid hand slide
        DrawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun setUiListener() {
        var view: View? = null
        var index = 0

        ChapterContentRecycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) { super.onScrollStateChanged(recyclerView, newState) }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!ChapterContentRecycleView.canScrollVertically(1)) {
                    viewModel.loadNextChapter()
                    LoadMoreProgressBar.visibility = View.VISIBLE
                }

                if (view != recyclerView.findChildViewUnder(dx.toFloat(), dy.toFloat())) {
                    view = recyclerView.findChildViewUnder(dx.toFloat(), dy.toFloat())
                    view?.let { index = recyclerView.getChildAdapterPosition(it) }
                    NowLookChapter.text = chapterContentAdapter.getChapterTitleByPosition(index)
                }
                Log.d(TAG,"Extent${recyclerView.computeVerticalScrollExtent()}")
                Log.d(TAG,"Offset${recyclerView.computeVerticalScrollOffset()}")
                Log.d(TAG,"Range${recyclerView.computeVerticalScrollRange()}")
                Log.d(TAG,"Rercent${recyclerView.computeVerticalScrollOffset()*100/(recyclerView.computeVerticalScrollRange()-recyclerView.computeVerticalScrollExtent())}")
                view?.let {
                    Log.d(TAG,"子物件編號${recyclerView.getChildAdapterPosition(it)}")
                    Log.d(TAG,"子物件高度${it.height}")
                }

            }
        })
        ChapterContentRecycleView.setOnClickListener {
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
                chapterContentAdapter.setFontSize(tempFontSizeValue)
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

        SwipToRefreshView.setOnRefreshListener(refreshListener)
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
            chapterContentAdapter.setUiByDayNightMode(checkUiModeNight())
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
                chapterContentAdapter.setUiByDayNightMode(!checkUiModeNight())
                if (checkUiModeNight()) {
                    val bgResId = preference.getInt(getString(R.string.novel_background), R.color.bgMorning)
                    BackgroundView.setBackgroundResource(bgResId)
                    item.title = "日間模式"
                    with(preference.edit()) {
                        putBoolean(getString(R.string.novel_uimode), false).apply()
                    }
                } else {
                    BackgroundView.setBackgroundResource(R.color.bgNight)
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

//    private fun refreshChapter() {
//        Handler().postDelayed({SwipToRefreshView.isRefreshing = false},2000)
//    }

    override fun onPause() {
        super.onPause()
        allChapters?.let {
            viewModel.saveReadProgress(
                LastReadProgress(
                    bookId, it[nowChapterReadIndex].chtitle, nowChapterReadIndex,
                    it[nowChapterReadIndex].chUrl, 0f
                )
            )
        }
    }

    override fun onScreenClick(chapterContent: ChapterContentBeta, view: View) {
        FunctionMenu.visibility = View.VISIBLE
    }
}