package com.example.leafnovel.ui.main.view.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
//import androidx.fragment.app.activityViewModels
//import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.leafnovel.R
import com.example.leafnovel.bean.Group
import com.example.leafnovel.customToast
import com.example.leafnovel.data.model.StoredBookFolder
import com.example.leafnovel.ui.main.view.BookContentBetaActivity
import com.example.leafnovel.ui.main.view.BookDetailActivity
import com.example.leafnovel.ui.main.viewmodel.BookDetailViewModel
import kotlinx.android.synthetic.main.fragment_book_introduce.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BookIntroduceFragment : Fragment() {
//    private val viewModel :BookDetailViewModel
    private var viewModel :BookDetailViewModel? = null
    var parentActivity : BookDetailActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_book_introduce, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = parentActivity?.getActivityViewModel()

        setUi()
        setObserver()
        setUiListener()
    }

    private fun setUi() {
        val storedBookInfo = viewModel?.bookInformation?.value
        Book_titleView.text = storedBookInfo?.bookname
        Book_authorView.text = storedBookInfo?.bookauthor
    }

    private fun setObserver() {
        viewModel?.bookOtherInformation?.observe(viewLifecycleOwner,{bookInfo->
//            NewChapterText.text = bookInfo["newChapter"]
            bookInfo["newChapter"]?.let{
                NewChapterText.text = if(it.length<=9)it else it.subSequence(0,9).toString() + "..."
            }
            UpdateTimeText.text = bookInfo["updateTime"]
            Book_DescripeView.text = bookInfo["bookDescripe"]
            val novelState = bookInfo["novelState"]
            novelState?.let {
                Book_stateText.text =
                    if(it.contains("載中")){"連載中"} else{"已完結"}
            }
            Glide.with(requireContext()).load("http:"+ bookInfo["imgUrl"])
                .placeholder(R.drawable.ic_outline_image_search_24)
                .error(R.drawable.ic_baseline_broken_image_24)
                .fallback(R.drawable.ic_baseline_image_24)
                .into(Book_imgView)
            LoadProgressBar.visibility = View.INVISIBLE
        })

        viewModel?.bookLastReadInfo?.observe(viewLifecycleOwner,{
                LastReadInfo->
            LastReadInfo?.let {
                LastReadText.text = it.chapterTitle
            }
        })
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        parentActivity = activity as BookDetailActivity
    }

    private fun setUiListener() {
        StoreBT.setOnClickListener {
            launchAlertDialog()
//            viewModel?.storedBook()
        }
        LastReadText.setOnClickListener {
            val lastReadInfo = viewModel?.bookLastReadInfo?.value
            val bookInfo = viewModel?.bookInformation?.value
            if (lastReadInfo != null && bookInfo != null) {
                val intent = Intent(context, BookContentBetaActivity::class.java).apply {
                    putExtra("BOOK_ID", bookInfo.bookid)
                    putExtra("BOOK_TITLE", bookInfo.bookname)
                    putExtra("BOOK_INDEX", lastReadInfo.chapterIndex)
                    putExtra("BOOK_CH_ID", lastReadInfo.chapterIndex)
                putExtra("BOOK_CH_URL", lastReadInfo.chapterUrl)
                putExtra("BOOK_CH_TITLE", lastReadInfo.chapterTitle)
                putParcelableArrayListExtra("NOVEL_CHAPTERS", viewModel?.bookChapterList?.value)
                }
                this.startActivity(intent)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        parentActivity = null
    }

    private fun launchAlertDialog() {
        CoroutineScope(Dispatchers.IO).launch {
            val folderList = viewModel?.getBookFolders()?.await()?.toMutableList()
            folderList?.add(StoredBookFolder("未分類", 0, -5))
            val folderNameList = arrayListOf<String>()
            folderList?.let {
                for (i in it) {
                    folderNameList.add(i.foldername)
                }
            }
            withContext(Dispatchers.Main) {
                var singleIndex = 0
                context?.let { mContext ->
                    AlertDialog.Builder(mContext)
                        .setTitle("選擇分類")
                        .setSingleChoiceItems(folderNameList.toTypedArray(), singleIndex) { _, clickIndex ->
                            singleIndex = clickIndex
                        }
                        .setPositiveButton("加入") { dialog, _ ->
                            CoroutineScope(Dispatchers.IO).launch {
                                folderList?.let {
                                    viewModel?.storedBook(it[singleIndex].folderid)
                                }
                                withContext(Dispatchers.Main) {
                                    customToast(activity,"已將書本放入${folderNameList[singleIndex]}").show()
                                    dialog.dismiss()
                                }
                            }
                        }.show()
                }
            }
        }
    }
}