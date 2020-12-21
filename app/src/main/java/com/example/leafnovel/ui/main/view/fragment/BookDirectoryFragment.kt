package com.example.leafnovel.ui.main.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.view.ActionMode
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leafnovel.R
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.data.model.BookDownloadInfo
import com.example.leafnovel.data.model.ChapterDownloadInfo
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.ui.main.adapter.BookChAdapter
import com.example.leafnovel.ui.main.adapter.multiselection.BookChapterItemDetailLookup
import com.example.leafnovel.ui.main.adapter.multiselection.BookChapterItemKeyProvider
import com.example.leafnovel.ui.main.view.BookContentActivity
import com.example.leafnovel.ui.main.view.BookDetailActivity
import com.example.leafnovel.ui.main.viewmodel.BookDetailViewModel
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.fragment_book_directory.*

class BookDirectoryFragment : Fragment(), BookChAdapter.OnItemClickListener ,ActionMode.Callback{
    private var viewModel : BookDetailViewModel? = null
    var parentActivity : BookDetailActivity? = null
    var bookId :String =""
    var bookName :String = ""
    var mutiSelectBT :Button? = null

    private var selectedBookChapterItems:MutableList<BookChapter> = mutableListOf()
    private var actionMode:ActionMode?=null
    val adapter = BookChAdapter()
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
    }

    private fun setObserver() {
        viewModel?.bookChapterList?.observe(viewLifecycleOwner,{ bookChResults->
            adapter.setItems(bookChResults, this)
        })
        viewModel?.chaptersIndexSaved?.observe(viewLifecycleOwner,{ indexes->
            adapter.setSavedIndex(indexes)
        })
        viewModel?.bookInformation?.observe(viewLifecycleOwner,{ bookInfo->
            bookId = bookInfo.bookid
            bookName = bookInfo.bookname
        })
    }

    private fun setUi() {
        BookDirectoryRecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = this@BookDirectoryFragment.adapter
        }
        mutiSelectBT = (parentActivity as AppCompatActivity).findViewById<Button>(R.id.MutiSelectBT)
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
                        actionMode?.title = "${selectedBookChapterItems.size}"
                    }
                }
            }
        }
        )

    }

    override fun onActionItemClicked(p0: ActionMode?, item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_multi_selected_download->{
//                Toast.makeText(context,selectedBookChapterItems.toString(),Toast.LENGTH_LONG).show()
                if(actionMode == null) actionMode = parentActivity?.startSupportActionMode(this@BookDirectoryFragment)
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
        val intent = Intent(activity, BookContentActivity::class.java).apply {
            putExtra("BOOK_INDEX", position)
            putExtra("BOOK_ID", viewModel?.bookInformation?.value?.bookid)
            putExtra("BOOK_CH_ID", bookCh.chId)
            putExtra("BOOK_CH_URL", bookCh.chUrl)
            putExtra("BOOK_CH_TITLE", bookCh.chtitle)
            putExtra("BOOK_TITLE", viewModel?.bookInformation?.value?.bookname)
            putParcelableArrayListExtra("NOVEL_CHAPTERS", viewModel?.bookChapterList?.value)
        }
        this.startActivity(intent)
    }

    override fun onMoreClick(bookCh: BookChapter, position: Int,view:View) {
//        TODO 判斷正序/反序
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.chapter_more_menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.chapterDownload -> {
                    if(bookId!="" && bookName!=""){
                        val chapterDownloadInfo = ChapterDownloadInfo(bookCh.chtitle,bookCh.chId,bookCh.chUrl,position)
                    val bookTransInfo = BookDownloadInfo(bookName,bookId, listOf(chapterDownloadInfo))
                        viewModel?.downLoadChapter(bookTransInfo)
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