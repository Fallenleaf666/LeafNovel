package com.example.leafnovel.ui.main.view.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.leafnovel.R
import com.example.leafnovel.customToast
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
        BookDetailShimmerLayout.startShimmer()
//        val storedBookInfo = viewModel?.bookInformation?.value
//        Book_titleView.text = storedBookInfo?.bookname
//        Book_authorView.text = storedBookInfo?.bookauthor
    }

    private fun setObserver() {
        viewModel?.bookOtherInformation?.observe(viewLifecycleOwner,{bookInfo->
//            NewChapterText.text = bookInfo["newChapter"]
            val storedBookInfo = viewModel?.bookInformation?.value
            Book_titleView.text = storedBookInfo?.bookname
            Book_authorView.text = storedBookInfo?.bookauthor

            bookInfo["newChapter"]?.let{
                NewChapterText.text = viewModel?.checkIsStringOutOfBound(it)
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

            BookDetailShimmerLayout.stopShimmer()
            BookDetailShimmerLayout.setShimmer(null)
            Book_titleView.background = null
            Book_authorView.background = null
            UpdateTimeText.background = null
            NewChapterText.background = null
            Book_stateText.background = null
            Book_DescripeView.background = null
            Book_imgView.background = null
        })

        viewModel?.bookLastReadInfo?.observe(viewLifecycleOwner,{
                LastReadInfo->
            LastReadInfo?.let {
                LastReadText.text = it.chapterTitle
            }
        })


        viewModel?.bookFavorite?.observe(viewLifecycleOwner,{
                bookFavorite->
            val isDbHasData = bookFavorite != null
            FavoriteBT.tag = if(isDbHasData)"已收藏" else "收藏"
            context?.let {
                FavoriteBT.setImageDrawable(if(isDbHasData)ContextCompat.getDrawable(it,R.drawable.ic_bookmarkno)
                else ContextCompat.getDrawable(it,R.drawable.ic_bookmark))
            }

            viewModel?.isBookStored?.value = bookFavorite != null

            Log.d(TAG,"收藏狀態:${bookFavorite?.bookid}")
        })
    }

    @SuppressWarnings("deprecation")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if(Build.VERSION.SDK_INT < 23){
            parentActivity = activity as BookDetailActivity
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Activity){
            parentActivity = context as BookDetailActivity
        }
    }

    private fun setUiListener() {
        FavoriteBT.setOnClickListener {
            if(FavoriteBT.tag=="已收藏"){
                viewModel?.removeFavoriteBook()
            }else{
                launchAlertDialog()
            }
            //            viewModel?.isBookStored?.value?.let {
//                viewModel?.isBookStored?.value = !it
//            }
//            TODO 如果cancel就放到預設
//            launchAlertDialog()
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
                    putExtra("BOOK_LAST_READ_HEIGHT", lastReadInfo.readHeight)
                    putExtra("BOOK_LAST_READ_CHAPTER_HEIGHT", lastReadInfo.chapterHeight)
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
//            val folderList = viewModel?.getBookFolders()?.await()?.toMutableList()
            val folderList = viewModel?.getBookFolders()?.toMutableList()
//            folderList?.add(StoredBookFolder("未分類", 0, -5))
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
//                                    viewModel?.storedBook(it[singleIndex].folderid)
//                                    viewModel?.storedBook()
                                    viewModel?.addFavoriteBook(it[singleIndex].folderid)
                                }
                                withContext(Dispatchers.Main) {
                                    customToast(activity,"已將書本放入${folderNameList[singleIndex]}").show()
                                    dialog.dismiss()
                                }
                            }
                            //取消時放入預設分類
                        }.setOnCancelListener{dialog->
                            CoroutineScope(Dispatchers.IO).launch {
                                folderList?.let {
                                    viewModel?.addFavoriteBook(folderList.last().folderid)
                                }
                                withContext(Dispatchers.Main) {
                                    customToast(activity,"已將書本放入${folderNameList.last()}").show()
                                    dialog.dismiss()
                                }
                            }
                        }.show()
                }
            }
        }
    }
    companion object{
        const val TAG = "BookIntroduceFragment"
    }
}
