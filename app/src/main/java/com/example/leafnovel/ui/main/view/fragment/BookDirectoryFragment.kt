package com.example.leafnovel.ui.main.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leafnovel.R
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.data.model.BookDownloadInfo
import com.example.leafnovel.data.model.ChapterDownloadInfo
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.ui.main.adapter.BookChAdapter
import com.example.leafnovel.ui.main.view.BookContentActivity
import com.example.leafnovel.ui.main.view.BookDetailActivity
import com.example.leafnovel.ui.main.viewmodel.BookDetailViewModel
import kotlinx.android.synthetic.main.fragment_book_directory.*

class BookDirectoryFragment : Fragment(), BookChAdapter.OnItemClickListener {
    private var viewModel : BookDetailViewModel? = null
    var parentActivity : BookDetailActivity? = null
    val adapter = BookChAdapter()
    var bookId :String =""
    var bookName :String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_book_directory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = parentActivity?.getActivityViewModel()
        setUi()
        setObserver()
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
            }
            true
        }
        popupMenu.show()
    }

}