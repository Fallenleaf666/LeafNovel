package com.example.leafnovel.ui.main.view.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.view.ActionMode
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.leafnovel.R
import com.example.leafnovel.customToast
import com.example.leafnovel.data.DownloadNovelService
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.data.model.BookDownloadInfo
import com.example.leafnovel.receiver.DownloadResultReceiver
import com.example.leafnovel.ui.main.adapter.BookChapterAdapter
import com.example.leafnovel.ui.main.adapter.multiselection.BookChapterItemDetailLookup
import com.example.leafnovel.ui.main.adapter.multiselection.BookChapterItemKeyProvider
import com.example.leafnovel.ui.main.view.BookContentBetaActivity
import com.example.leafnovel.ui.main.view.BookDetailActivity
import com.example.leafnovel.ui.main.viewmodel.BookDetailViewModel
import kotlinx.android.synthetic.main.fragment_book_directory.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class BookDirectoryFragment : Fragment(), BookChapterAdapter.OnItemClickListener ,ActionMode.Callback{
    private var viewModel : BookDetailViewModel? = null
    var parentActivity : BookDetailActivity? = null
    var bookId :String =""
    var bookName :String = ""
    var mutiSelectBT :Button? = null
    var orderSwitchBT :Switch? = null
    var isFirstTransfer = true

    private var selectedBookChapterItems:MutableList<BookChapter> = mutableListOf()
    private var actionMode:ActionMode?=null
    val adapter = BookChapterAdapter()
    private var tracker: SelectionTracker<BookChapter>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_book_directory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = parentActivity?.getActivityViewModel()
        setUi()
        setUiListener()
        setObserver()
    }

    private fun setUiListener() {
        mutiSelectBT?.setOnClickListener{
//            Toast.makeText(parentActivity, "開始下載", Toast.LENGTH_SHORT).show()
            customToast(parentActivity, "開始下載").show()
        }
        orderSwitchBT?.setOnCheckedChangeListener{
                _,isChecked->
            adapter.reversedItems(isChecked)
        }
        BookDirectoryRecycler.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
//                if(dy>0){
//                    if(OrderSwitch.visibility == View.VISIBLE)
//                        OrderSwitch.visibility = View.GONE
//                }
//                else if(dy<0){
//                    if(OrderSwitch.visibility == View.GONE)
//                        OrderSwitch.visibility = View.VISIBLE
////                    if(OrderSwitch.visibility == View.GONE){
////                    OrderSwitch.animate().alpha(1f).setDuration(500).setListener(object :AnimatorListenerAdapter(){
////                        override fun onAnimationEnd(animation: Animator?) {
////                            super.onAnimationEnd(animation)
////                            OrderSwitch.visibility = View.VISIBLE
////                        }
////                    })
////                    }
//                }
            }
        })
    }

    private fun setObserver() {
        viewModel?.bookChapterList?.observe(viewLifecycleOwner,{ bookChResults->
            adapter.setItems(bookChResults, this)
            Handler().postDelayed({
                viewModel?.bookLastReadInfo?.value?.let {
                    la->
                    orderSwitchBT?.isChecked?.let {
                        var position = if(it)adapter.itemCount - la.chapterIndex -1 else la.chapterIndex
                        if(position + 5 < adapter.itemCount) position += 5
                        BookDirectoryRecycler.scrollToPosition(position)
                    }
                }
            },300)
        })
//        追蹤儲存章節的變化
//        var lastTime = System.currentTimeMillis()
//        var nowTime :Long
        var time = 0
        viewModel?.chaptersIndexSaved?.observe(viewLifecycleOwner,{ indexes->
            indexes?.let {
                if(isFirstTransfer){
//                    nowTime = System.currentTimeMillis()
//                    if(nowTime -lastTime >= 1000){
                        adapter.setSavedIndex(indexes)
                    isFirstTransfer = false
//                    }
//                    lastTime = nowTime
                }else{
                    adapter.setSingleSavedIndex(indexes.last())
                    Handler().postDelayed({

                    },3000)
                    time += 1
                    Log.d("aaaa","setSavedIndex${ time}篇")
                }
            }
        })
        viewModel?.bookInformation?.observe(viewLifecycleOwner,{ bookInfo->
            bookId = bookInfo.bookid
            bookName = bookInfo.bookname
        })
        viewModel?.bookLastReadInfo?.observe(viewLifecycleOwner,{ bookLastReadInfo->
            bookLastReadInfo?.let {
                adapter.lastPositionChange(bookLastReadInfo.chapterIndex)
            }
            Handler().postDelayed({
                viewModel?.bookLastReadInfo?.value?.let {
                        la->
                    orderSwitchBT?.isChecked?.let {
                        var position = if(it)adapter.itemCount - la.chapterIndex -1 else la.chapterIndex
                        if(position + 5 < adapter.itemCount) position += 5
                        BookDirectoryRecycler.scrollToPosition(position)
                    }
                }
            },300)
        })

    }

    private fun setUi() {
        BookDirectoryRecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = this@BookDirectoryFragment.adapter
        }

        mutiSelectBT = (parentActivity as AppCompatActivity).findViewById<Button>(R.id.MutiSelectBT)
        orderSwitchBT = (parentActivity as AppCompatActivity).findViewById<Switch>(R.id.OrderSwitch)
//        trace multiselect BookChapter to Download
        tracker =SelectionTracker.Builder<BookChapter>(
            "bookChapterDownloadSelection",
            BookDirectoryRecycler,
            BookChapterItemKeyProvider(adapter),
            BookChapterItemDetailLookup(BookDirectoryRecycler),
            StorageStrategy.createParcelableStorage(BookChapter::class.java)
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()).build()
        adapter.tracker = tracker

        tracker?.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                tracker?.let {
                    selectedBookChapterItems = it.selection.toMutableList()
                    if (selectedBookChapterItems.isEmpty()) {
                        actionMode?.finish()

                    } else {
                        if (actionMode == null) actionMode =
                            parentActivity?.startSupportActionMode(this@BookDirectoryFragment)
                        actionMode?.title = "已選取${selectedBookChapterItems.size}篇章節"
                    }
                }
            }
        }
        )
//        OrderSwitch.setOnCheckedChangeListener{
//        orderSwitchBT.setOnCheckedChangeListener{
//                _,isChecked->
//                adapter.reversedItems(isChecked)
//        }
    }

    override fun onActionItemClicked(p0: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_multi_selected_download -> {
                if (bookId != "" && bookName != "") {
                    tracker?.let {
                        val bookTransInfo = BookDownloadInfo(bookName, bookId, it.selection.toList())
                        viewModel?.downloadChapter(bookTransInfo)
                        context?.registerReceiver(downloadReceiver, IntentFilter("DOWNLOAD_CHAPTER_RESULT_KEY"))
                    }
                }
                actionMode?.finish()
            }
        }
        return true
    }

    private var downloadReceiver = object :DownloadResultReceiver(){
        override fun onReceive(p0: Context?, intent: Intent?) {
            val action :String? = intent?.action
            action?.let {
                when(it){
                    DOWNLOAD_CHAPTER_RESULT_KEY -> {
                        val messenger = intent.getSerializableExtra(DOWNLOAD_RESULT)
                        if(messenger == DownloadNovelService.DownloadResultType.SUCCESS){
                            p0?.let {p0->
                                viewModel?.chaptersIndexSaved?.value?.let {list->
                                    adapter.setSavedIndex(list)
                                    context?.unregisterReceiver(this)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.let {
            val inflater:MenuInflater = it.menuInflater
            inflater.inflate(R.menu.bookchapter_multiselected_menu,menu)
            return true
        }
        return false
    }

    override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
        return true
    }

    override fun onDestroyActionMode(p0: ActionMode?) {
        adapter.tracker?.clearSelection()
        adapter.notifyDataSetChanged()
        actionMode = null
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        parentActivity = activity as BookDetailActivity
    }
    override fun onDetach() {
        super.onDetach()
        parentActivity = null
    }

    override fun onItemClick(bookCh: BookChapter,position:Int) {
//        val intent = Intent(activity, BookContentActivity::class.java).apply {
        if (actionMode == null) {
            val intent = Intent(activity, BookContentBetaActivity::class.java).apply {
                putExtra("BOOK_INDEX", bookCh.chIndex)
                putExtra("BOOK_ID", viewModel?.bookInformation?.value?.bookid)
                putExtra("BOOK_CH_ID", bookCh.chIndex)
                putExtra("BOOK_CH_URL", bookCh.chUrl)
                putExtra("BOOK_CH_TITLE", bookCh.chtitle)
                putExtra("BOOK_TITLE", viewModel?.bookInformation?.value?.bookname)
                putParcelableArrayListExtra("NOVEL_CHAPTERS", viewModel?.bookChapterList?.value)
            }
            this.startActivity(intent)
        }
    }

    override fun onMoreClick(bookCh: BookChapter, position: Int,view:View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.chapter_more_menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.chapterDownload -> {
                    if (bookId != "" && bookName != "") {
                        val chapterDownloadInfo = BookChapter(bookCh.chIndex, bookCh.chtitle, bookCh.chUrl)
                        val bookTransInfo = BookDownloadInfo(bookName, bookId, listOf(chapterDownloadInfo))
                        viewModel?.downloadChapter(bookTransInfo)
                    }
//                    if(bookId!="" && bookName!=""){
//                        tracker?.let {
//                            val bookTransInfo = BookDownloadInfo(bookName,bookId, it.selection.toList())
//                            viewModel?.downLoadChapter(bookTransInfo)
//                        }
//                    }
                }
                R.id.selectSpecialChapters -> {
                    launchAlertDialogSelectChaptersRange(position)
                }
                R.id.selectAllChapters -> {
                    val chList = adapter.getSpecialItems(adapter.itemCount - 1, 0)
                    for (i in chList) {
                        adapter.tracker?.select(i)
                    }
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun launchAlertDialogSelectChaptersRange(position: Int) {
        context?.let { mContext ->
            val dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialogview_select_chapters_range, null)
            val builder = AlertDialog.Builder(mContext).apply {
                setView(dialogView)
            }
            val addBt = dialogView.findViewById<Button>(R.id.DialogSelectChapterCompleteBT)
            val start = dialogView.findViewById<TextView>(R.id.DialogSelectChapterStartIndexView)
            val end = dialogView.findViewById<TextView>(R.id.DialogSelectChapterEndIndexView)

            val listSize = adapter.itemCount

            val dialog = builder.create().apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            start.text = "1"
            end.hint = "$listSize"
            end.text = "${position + 1}"

            addBt.setOnClickListener {
                try {
                    when {
                        start.text.isEmpty() -> {
                            start.error = "請填入範圍"
                        }
                        end.text.isEmpty() -> {
                            end.error = "請填入範圍"
                        }
                        start.text.toString().toInt() !in 1..listSize -> {
                            start.error = "無效範圍"
                        }
                        end.text.toString().toInt() !in 1..listSize -> {
                            end.error = "無效範圍"
                        }
                        else -> {
                                val chList = adapter.getSpecialItems(end.text.toString().toInt() - 1, start.text.toString().toInt() - 1)
                                for (i in chList) {
                                    adapter.tracker?.select(i)
                                }
                                dialog.cancel()
                        }
                    }
                } catch (e: NumberFormatException) {
                    customToast(activity, "請勿輸入數字以外符號！").show()
                }
            }
            dialog.show()
        }
    }

    override fun onStop() {
        super.onStop()
            context?.unregisterReceiver(downloadReceiver)
    }
}