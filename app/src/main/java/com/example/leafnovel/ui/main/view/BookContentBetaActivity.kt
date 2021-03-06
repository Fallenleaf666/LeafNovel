package com.example.leafnovel.ui.main.view

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.leafnovel.R
import com.example.leafnovel.data.api.NovelApi
import com.example.leafnovel.data.model.AccurateReadProgress
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.data.model.ChapterContentBeta
import com.example.leafnovel.data.model.LastReadProgress
import com.example.leafnovel.ui.base.BookContentViewModelFactory
import com.example.leafnovel.ui.main.adapter.BookChapterInContentAdapter
import com.example.leafnovel.ui.main.adapter.ChapterContentAdapter
import com.example.leafnovel.ui.main.viewmodel.BookContentBetaViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_book_beta_content.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList
import kotlin.math.abs


//class BookContentBetaActivity : AppCompatActivity(), BookChapterAdapter.OnItemClickListener{
class BookContentBetaActivity : AppCompatActivity(), BookChapterInContentAdapter.OnItemClickListener{
    companion object {
        const val TAG = "BookContentBetaActivity5"
    }

    val preference: SharedPreferences by lazy { getSharedPreferences("UiSetting", Context.MODE_PRIVATE) }

    var isNotLoading = true
    //是否自動讀取下一章
    var isLoadingMore = true
    //是否隱藏標題
    var isHiddenTitle = false

    //目錄適配器
//    val adapter = BookChapterAdapter()
    val adapter = BookChapterInContentAdapter()

    //內文適配器
    val chapterContentAdapter = ChapterContentAdapter(this)

    //目前閱讀的小說章節在adapter裡的index
    var firstVisibleIndex = 0

    //當前閱讀進度
    var readProgress = 0

    //適配器中 items每章高度暫存
    val tempHeight: MutableMap<Int, Int> = mutableMapOf()

    //目前滑動總距離
    var totalScrollY = 0

    //每頁小說高度(預設)
    var pageHeight = 2000

    //分隔線高度
    var decorationHeight = 0
    var recyclerViewHeight = 0

    var accurateReadProgress = AccurateReadProgress(0,0)

    var isGetPageHeight = false

    var isFirstChapter = true

    var contentViewHeightList = mutableListOf<Int>()

    private lateinit var viewModel: BookContentBetaViewModel

    private var bookChId: Int = 0
    private lateinit var chTitle: String
    private lateinit var chUrl: String
    lateinit var bookId: String
    lateinit var bookTitle: String

    private var allChapters: ArrayList<BookChapter>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_beta_content)

        setData()
        setActionBar()
        setUI()
        setObserver()
        setReceiver()
        setUiListener()
        initBottomNavigationView()
    }

    private fun scrollToLastReadPosition() {
        //如果是從上次閱讀進入，則滑動至上次位置
        Handler().post {
            val lastReadHeight = intent.getIntExtra("BOOK_LAST_READ_HEIGHT", 0)
            val lastReadChapterHeight = intent.getIntExtra("BOOK_LAST_READ_CHAPTER_HEIGHT", 0)
            if (lastReadHeight != 0 && lastReadChapterHeight != 0) {
                val layoutManager = (ChapterContentRecycleView.layoutManager as LinearLayoutManager)
                val firstChapterHeight = layoutManager.findViewByPosition(0)?.height ?: 0
                if (firstChapterHeight != 0 && firstChapterHeight == lastReadChapterHeight) {
//                    ChapterContentRecycleView.smoothScrollBy(0, lastReadHeight)
                    ChapterContentRecycleView.scrollBy(0, lastReadHeight)
                }else if (firstChapterHeight != 0 && firstChapterHeight != lastReadChapterHeight){
//                    ChapterContentRecycleView.smoothScrollBy(0, (firstChapterHeight * (lastReadHeight.toFloat()/lastReadChapterHeight)).toInt())
                    ChapterContentRecycleView.scrollBy(0, (firstChapterHeight * (lastReadHeight.toFloat()/lastReadChapterHeight)).toInt())
                }
            }
        }
    }

    private fun setData() {
        bookChId = intent.getIntExtra("BOOK_CH_ID", 0)
        chTitle = intent.getStringExtra("BOOK_CH_TITLE") ?: ""
        chUrl = intent.getStringExtra("BOOK_CH_URL") ?: ""
        bookId = intent.getStringExtra("BOOK_ID") ?: ""
        bookTitle = intent.getStringExtra("BOOK_TITLE") ?: ""

        //填入目錄內容
        allChapters = intent.getParcelableArrayListExtra<BookChapter>("NOVEL_CHAPTERS")
        allChapters?.let {
            adapter.setItems(it, this@BookContentBetaActivity)
        } ?: CoroutineScope(Dispatchers.IO).launch {
            val bookChResults = NovelApi.requestChapterList(bookId)
            withContext(Dispatchers.Main) {
                adapter.setItems(bookChResults, this@BookContentBetaActivity)
            }
        }
        val tempBookChapter = BookChapter(bookChId, chTitle, chUrl)
        viewModel = ViewModelProvider(
            this, BookContentViewModelFactory(applicationContext, tempBookChapter, allChapters!!, bookTitle, bookId)
        ).get(BookContentBetaViewModel::class.java)

        //取得裝置顯示RecycleView之高度
        Handler().post{
            pageHeight = ChapterContentRecycleView.height
            Log.d(TAG,"HOLDER$pageHeight")
        }
        isLoadingMore = preference.getBoolean(getString(R.string.novel_loadmore),true)
        isHiddenTitle = preference.getBoolean(getString(R.string.novel_title_hide),false)

    }

    private fun setObserver() {
        viewModel.chapterContent.observe(this, { chapterContent ->
            chapterContentAdapter.setItems(chapterContent)
            ChapterContentRecycleView.scrollToPosition(0)
            tempHeight.clear()
            totalScrollY = 0
            viewModel.nowLookAtIndex.postValue(chapterContent.chIndex)
            LoadMoreProgressBar.visibility = View.INVISIBLE
            FunctionMenu.visibility = View.INVISIBLE
            NowLookChapter.text = chapterContentAdapter.getChapterTitleByPosition(0)
            NowLookProgressiew.text = "${1}/${
                ((getItemHeight(chapterContent, chapterContentAdapter.getFontSize()) + decorationHeight)/pageHeight)+1
            }"
            SwipToRefreshView.isRefreshing = false

            //如果剛進頁面時是上次閱讀的章節，則拖拉至該進度
            if(bookChId == chapterContent.chIndex && isFirstChapter){
                scrollToLastReadPosition()
                isFirstChapter = false
            }
//            if(DrawerLayout.isDrawerOpen(GravityCompat.START)){
//                DrawerLayout.closeDrawer(GravityCompat.START)
//            }
        })
        viewModel.loadChapterContent.observe(this, { chapterContent ->
            chapterContentAdapter.addItem(chapterContent)
            LoadMoreProgressBar.visibility = View.INVISIBLE
        })

        viewModel.systemTime.observe(this, { systemTime ->
            SystemTimeView.text = systemTime
        })
        viewModel.batteryIsCharging.observe(this, { isCharging ->
            novelBatteryView.isCharging = isCharging
        })
        viewModel.batteryLevel.observe(this, { batteryLevel ->
            novelBatteryView.batteryLevel = batteryLevel
        })

        viewModel.isLoadMore.observe(this, { isLoadMore ->
            LoadMoreProgressBar.visibility = if(isLoadMore)View.VISIBLE else View.INVISIBLE
        })

        viewModel.isRefresh.observe(this, { isRefreshing ->
            if(!isRefreshing){
                SwipToRefreshView.isRefreshing = false
            }
        })

    }

    private val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        viewModel.refresh()
//        Handler().postDelayed({
//            if(SwipToRefreshView.isRefreshing){
//                SwipToRefreshView.isRefreshing = false
//            }
//        }, 5000)
    }

    //目錄章節點擊
    override fun onItemClick(bookCh: BookChapter, position: Int) {
        viewModel.goSpecialChapter(bookCh)
        DrawerLayout.closeDrawer(GravityCompat.START)
    }

//    override fun onMoreClick(bookCh: BookChapter, position: Int, view: View) {
//    //TODO("Not yet implemented")
//    }

    private fun setUI() {
        //內文
        NowLookChapter.text = chTitle
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.chapter_item_decoration)?.let {
            decoration.setDrawable(it)
            decorationHeight = it.intrinsicHeight
        }
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

        //將下載狀態及更多功能隱藏
//        adapter.setMoreVisible(false)
        //縮小字體
//        adapter.setFontSizeSmall(true)

        //取得字體大小，預設為16sp
        val fontSize = preference.getFloat(getString(R.string.novel_fontsize), 16F)
        chapterContentAdapter.setFontSize(fontSize)
        //是否在內文隱藏標題，預設為顯示
        chapterContentAdapter.setTitleHidden(preference.getBoolean(getString(R.string.novel_title_hide),false))
        //取得當前日夜模式
        chapterContentAdapter.setUiByDayNightMode(checkUiModeNight())
        if (checkUiModeNight()) {
        //BackgroundView.setBackgroundResource(R.color.bgNight)
            BackgroundView.setBackgroundResource(R.drawable.bgNight)
        } else {
            //val bgResId = preference.getInt(getString(R.string.novel_background), R.color.bgMorning)
            val bgResId = preference.getInt(getString(R.string.novel_background), R.drawable.bgMorning)
            BackgroundView.setBackgroundResource(bgResId)
        }
        //取得螢幕亮度
        windowBrightness = preference.getFloat(getString(R.string.window_brightness), getSysWindowBrightness())
        //if windowBrightness not default it will in 0 ~ 1
        LightSeekBar.progress = if (windowBrightness in 0.0..1.0) (windowBrightness * 100).toInt() else 0
        FontSizeSeekBar.progress = (fontSize - 10).toInt()
        //lock navigationView avoid hand slide
        DrawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

    }

    private fun setUiListener() {
        ChapterContentRecycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            lateinit var layoutManager: LinearLayoutManager
            var nowLength = 0
            var totalLength = 0
            var tempValue = 0

            init {
                totalScrollY = 0
            }


            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalScrollY += dy
                if (!ChapterContentRecycleView.canScrollVertically(1) && isLoadingMore) {
//                    if(checkNetConnect(this@BookContentBetaActivity)){
                        viewModel.loadNextChapter()
                        isNotLoading = false
                        LoadMoreProgressBar.visibility = View.VISIBLE
//                    }else{
//                        Toast.makeText(this@BookContentBetaActivity,R.string.please_check_net_connect_state,Toast.LENGTH_SHORT).show()
//                    }
                }
                layoutManager = (recyclerView.layoutManager as LinearLayoutManager)
                //如果當前觀看的的索引與adapter的索引不同
                if (firstVisibleIndex != layoutManager.findFirstVisibleItemPosition()) {
                    firstVisibleIndex = layoutManager.findFirstVisibleItemPosition()
                    //更新觀看chId
                    viewModel.nowLookAtIndex.postValue(chapterContentAdapter.getChapterIdByPosition(firstVisibleIndex))
                }
                NowLookChapter.text = chapterContentAdapter.getChapterTitleByPosition(firstVisibleIndex)

                if (!tempHeight.containsKey(firstVisibleIndex) || tempHeight[firstVisibleIndex] == 0) {
                    tempHeight[firstVisibleIndex] = layoutManager.findViewByPosition(firstVisibleIndex)?.height ?: 0
                }
                totalLength = ((tempHeight[firstVisibleIndex] ?: 0) + decorationHeight)/pageHeight
                //Log.d(TAG, "totalLength${totalLength}")
                if (firstVisibleIndex == 0) {
                    nowLength = totalScrollY
                    readProgress = nowLength
                    accurateReadProgress.apply {
                        chapterHeight = tempHeight[firstVisibleIndex] ?: 0
                        readHeight = nowLength
                    }
                    NowLookProgressiew.text = "${nowLength/pageHeight + 1}/${totalLength + 1}"
                } else {
                    tempValue = 0
                    for (i in 0 until firstVisibleIndex) {
                        //ChapterContentRecycleView.decoration
                        tempValue += tempHeight[i] ?: 0
                        //分隔線高度
                        tempValue += decorationHeight
                        //Log.d(TAG, "tempValue${tempValue}")
                    }
                    nowLength = totalScrollY - tempValue
                    readProgress = nowLength / totalLength
                    accurateReadProgress.apply {
                        chapterHeight = tempHeight[firstVisibleIndex] ?: 0
                        readHeight = nowLength
                    }
                    NowLookProgressiew.text = "${nowLength/pageHeight + 1}/${totalLength + 1}"
                }
            }
        })

//        ContentView.setOnClickListener {
//            FunctionMenu.visibility = View.VISIBLE
//        }

        ChapterContentRecycleView.addOnItemTouchListener(object :RecyclerView.SimpleOnItemTouchListener(){
            var startX = 0f
            var startY = 0f
            var endX = 0f
            var endY = 0f
            var minScrollDistance = ViewConfiguration.get(this@BookContentBetaActivity).scaledTouchSlop
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when(e.action){
                    MotionEvent.ACTION_DOWN ->{
                        startX = e.x
                        startY = e.y
                    }
                    MotionEvent.ACTION_UP ->{
                        endX = e.x
                        endY = e.y
                        if(abs(startX - endX) < minScrollDistance && abs(startY - endY ) < minScrollDistance && rv.scrollState == 0){
                            FunctionMenu.visibility = View.VISIBLE
                            viewModel.nowLookAtIndex.value?.let {
                                BookChRecyclerView.scrollToPosition(if(it <= (adapter.itemCount-1)-5)it+5 else it)
                                adapter.lastPositionChange(it)
                            }
                        }
                    }
                }
                return super.onInterceptTouchEvent(rv, e)
            }
        })

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
                //max is 25 but min is 10
                tempFontSizeValue = progress.toFloat() + 10
                chapterContentAdapter.setFontSize(tempFontSizeValue)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //更新每章高度
                resetTempHeight()
                with(preference.edit()) {
                    putFloat(getString(R.string.novel_fontsize), tempFontSizeValue).apply()
                }
            }
        })

        LightSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var tempLightValue = preference.getFloat(getString(R.string.window_brightness), 0F)

            //var tempLightValue = 0F
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tempLightValue = progress / 100F
                windowBrightness = tempLightValue
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //set app window_brightness , it also can put in onDestroy
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
                R.id.Bg_ColorBT4 -> setBackgroundRes(R.drawable.bg_color1)
                R.id.Bg_ColorBT5 -> setBackgroundRes(R.drawable.bg_color2)
                R.id.Bg_ColorBT6 -> setBackgroundRes(R.drawable.bg_paper1)
                R.id.Bg_ColorBT7 -> setBackgroundRes(R.drawable.bg_paper3)
                else -> setBackgroundRes(R.drawable.bg_color1)
            }
        }
    }

    private fun setBackgroundRes(resId: Int) {
        BackgroundView.setBackgroundResource(resId)
        with(preference.edit()) {
            putInt(getString(R.string.novel_background), resId).apply()
        }
        if (checkUiModeNight()) {
            chapterContentAdapter.setUiByDayNightMode(!checkUiModeNight())
            with(preference.edit()) {
                putBoolean(getString(R.string.novel_uimode), false).apply()
            }
            bottomNavigation.menu.getItem(0).title = "日間模式"
        }
    }

    private fun setActionBar() {
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
                //return true -> night , false -> morning
                chapterContentAdapter.setUiByDayNightMode(!checkUiModeNight())
                if (checkUiModeNight()) {
                    //val bgResId = preference.getInt(getString(R.string.novel_background), R.color.bgMorning)
                    val bgResId = preference.getInt(getString(R.string.novel_background), R.drawable.bgMorning)
                    BackgroundView.setBackgroundResource(bgResId)
                    item.title = "日間模式"
                    with(preference.edit()) {
                        putBoolean(getString(R.string.novel_uimode), false).apply()
                    }
                } else {
                    //BackgroundView.setBackgroundResource(R.color.bgNight)
                    BackgroundView.setBackgroundResource(R.drawable.bgNight)
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
            //less than 0 or big then 1.0 see as sys default brightness (-1)
            window.attributes = window.attributes.apply {
                screenBrightness = if (brightness > 1.0 || brightness < 0) -1.0F else brightness
            }
        }

    private fun getSysWindowBrightness(): Float {
        var nowWindowBrightness = 0
        try {
            //get system windowBrightness it usually in 0 ~ 255 , need adjust to 0 ~ 100 in seekbar
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
//        private fun refreshChapter() {
//        Handler().postDelayed({SwipToRefreshView.isRefreshing = false},2000)
//    }

    override fun onPause() {
        super.onPause()
        //防止一點進去就馬上退出的狀況會發生空指針
        if (firstVisibleIndex < chapterContentAdapter.itemCount) {
            val chapter = chapterContentAdapter.getChapterItemByPosition(firstVisibleIndex)
            allChapters?.let {
                viewModel.saveReadProgress(
//                    LastReadProgress(
//                        bookId, chapter.chTitle, chapter.chIndex,
//                        chapter.chUrl, readProgress.toFloat()
//                    )
                    LastReadProgress(
                        bookId, chapter.chTitle, chapter.chIndex,
                        chapter.chUrl, accurateReadProgress.readHeight, accurateReadProgress.chapterHeight
                    )
                )
            }
        }
    }

    fun resetTempHeight() {
        tempHeight.clear()
        var tempChapterInfo = ChapterContentBeta(0, "", "", "")
        for (i in 0 until chapterContentAdapter.itemCount) {
            tempChapterInfo = chapterContentAdapter.getChapterItemByPosition(i)
            tempHeight[i] = getItemHeight(tempChapterInfo, chapterContentAdapter.getFontSize())
        }
    }

    private fun getItemHeight(chapterContent: ChapterContentBeta, fontSize: Float): Int {
        val tempView =
            layoutInflater.inflate(R.layout.row_chapter_content, ChapterContentRecycleView, false) as LinearLayout
//        val tempViewTitle = (tempView.getViewById(R.id.row_chapter_title) as TextView)
//        val tempViewContent = (tempView.getViewById(R.id.row_chapter_content) as TextView)
        val tempViewTitle = tempView.getChildAt(0) as TextView
        val tempViewContent = tempView.getChildAt(1) as TextView
        if(isHiddenTitle){
            tempView.removeViewAt(0)
        }
        else{
            tempViewTitle.text = chapterContent.chTitle
            tempViewTitle.textSize = fontSize
        }
        tempViewContent.text = chapterContent.chContent
        tempViewContent.textSize = fontSize + 4
        tempView.measure(
            View.MeasureSpec.makeMeasureSpec(ChapterContentRecycleView.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        return tempView.measuredHeight
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
//        if (!isGetPageHeight) {
//            pageHeight = ChapterContentRecycleView.height
//            isGetPageHeight = true
//            Log.d(TAG,"WINDOW pageHeight${pageHeight}")
//        }
    }

    private fun setReceiver() {
        viewModel.initSystemTime()
        viewModel.initBatteryLevel()
    }
}