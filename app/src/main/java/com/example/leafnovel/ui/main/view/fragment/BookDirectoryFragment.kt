package com.example.leafnovel.ui.main.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.view.ActionMode
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.leafnovel.R
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.data.model.BookDownloadInfo
import com.example.leafnovel.ui.main.adapter.BookChapterAdapter
import com.example.leafnovel.ui.main.adapter.multiselection.BookChapterItemDetailLookup
import com.example.leafnovel.ui.main.adapter.multiselection.BookChapterItemKeyProvider
import com.example.leafnovel.ui.main.view.BookContentBetaActivity
import com.example.leafnovel.ui.main.view.BookDetailActivity
import com.example.leafnovel.ui.main.viewmodel.BookDetailViewModel
import kotlinx.android.synthetic.main.fragment_book_directory.*

class BookDirectoryFragment : Fragment(), BookChapterAdapter.OnItemClickListener ,ActionMode.Callback{
    private var viewModel : BookDetailViewModel? = null
    var parentActivity : BookDetailActivity? = null
    var bookId :String =""
    var bookName :String = ""
    var mutiSelectBT :Button? = null
    var orderSwitchBT :Switch? = null

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
            Toast.makeText(parentActivity, "開始下載", Toast.LENGTH_SHORT).show()
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
        })
//        追蹤儲存章節的變化
        viewModel?.chaptersIndexSaved?.observe(viewLifecycleOwner,{ indexes->
            adapter.setSavedIndex(indexes)
        })
        viewModel?.bookInformation?.observe(viewLifecycleOwner,{ bookInfo->
            bookId = bookInfo.bookid
            bookName = bookInfo.bookname
        })
        viewModel?.bookLastReadInfo?.observe(viewLifecycleOwner,{ bookLastReadInfo->
            bookLastReadInfo?.let { adapter.lastPositionChange(bookLastReadInfo.chapterIndex) }
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

        tracker?.addObserver(object :SelectionTracker.SelectionObserver<Long>(){
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                tracker?.let {
                    selectedBookChapterItems = it.selection.toMutableList()
                    if(selectedBookChapterItems.isEmpty()){
                        actionMode?.finish()
                }else{
                        if(actionMode == null) actionMode = parentActivity?.startSupportActionMode(this@BookDirectoryFragment)
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
        when(item?.itemId){
            R.id.action_multi_selected_download->{
//                Toast.makeText(context,selectedBookChapterItems.toString(),Toast.LENGTH_LONG).show()
//                if(actionMode == null) actionMode = parentActivity?.startSupportActionMode(this@BookDirectoryFragment)
                if(bookId!="" && bookName!=""){
                    tracker?.let {
                        val bookTransInfo = BookDownloadInfo(bookName,bookId, it.selection.toList())
                        viewModel?.downLoadChapter(bookTransInfo)
                    }
                }
                actionMode?.finish()
            }
            R.id.action_multi_selected_Onedownload->{
                if(bookId!="" && bookName!=""){
                    tracker?.let {
                        val bookTransInfo = BookDownloadInfo(bookName,bookId, it.selection.toList())
                        viewModel?.downLoadOneThreadChapter(bookTransInfo)
                    }
                }
                actionMode?.finish()
            }
        }
        return true
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

    override fun onMoreClick(bookCh: BookChapter, position: Int,view:View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.chapter_more_menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
//                R.id.chapterDownload -> {
//                    if(bookId!="" && bookName!=""){
//                        val chapterDownloadInfo = BookChapter(bookCh.chIndex, bookCh.chtitle, bookCh.chUrl)
//                    val bookTransInfo = BookDownloadInfo(bookName,bookId, listOf(chapterDownloadInfo))
//                        viewModel?.downLoadChapter(bookTransInfo)
//                    }
//                }
                R.id.chapterDownload -> {
                    if(bookId!="" && bookName!=""){
                        tracker?.let {
                            val bookTransInfo = BookDownloadInfo(bookName,bookId, it.selection.toList())
                            viewModel?.downLoadChapter(bookTransInfo)
                        }
                    }
                }
                R.id.chapterOneThreadDownload -> {
                    if(bookId!="" && bookName!=""){
                        tracker?.let {
                            val bookTransInfo = BookDownloadInfo(bookName,bookId, it.selection.toList())
                            viewModel?.downLoadOneThreadChapter((bookTransInfo))
                        }
                    }
                }
                R.id.chapterMoreDownload -> {
                    val chList = adapter.getSpecialItems(adapter.itemCount-1,0)
                    for(i in chList){
                        adapter.tracker?.select(i)
                    }
                }
            }
            true
        }
        popupMenu.show()
    }

}