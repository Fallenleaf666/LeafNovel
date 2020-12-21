package com.example.leafnovel.ui.main.view.fragment

import android.app.Activity
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.selection.SelectionTracker
//import androidx.fragment.app.activityViewModels
//import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.leafnovel.R
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.ui.main.view.BookDetailActivity
import com.example.leafnovel.ui.main.viewmodel.BookDetailViewModel
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.fragment_book_introduce.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
            NewChapterText.text = bookInfo["newChapter"]
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
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        parentActivity = activity as BookDetailActivity
    }

    private fun setUiListener() {
        StoreBT.setOnClickListener{
            viewModel?.storedBook()
        }
}

    override fun onDetach() {
        super.onDetach()
        parentActivity = null
    }
}