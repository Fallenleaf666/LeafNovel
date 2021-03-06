package com.example.leafnovel.ui.main.view.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.leafnovel.R
import com.example.leafnovel.bean.*
import com.example.leafnovel.checkNetConnect
import com.example.leafnovel.customToast
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.data.model.StoredBookFolder
import com.example.leafnovel.ui.base.MyBooksViewModelFactory
import com.example.leafnovel.ui.main.view.BookDetailActivity
import com.example.leafnovel.ui.main.viewmodel.MyBooksViewModel
import kotlinx.android.synthetic.main.fragment_my_books.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MyBooksBeta : Fragment(), MyBookAdapter.OnItemClickListener, MyBookAdapter.OnFolderItemClickListener{
    companion object {
        val newInstance: MyBooksBeta by lazy {
            MyBooksBeta()
        }
//        var isCreate = true
    }

    lateinit var mLinearLayoutManager: LinearLayoutManager
    val hasMore = false
    var currentChapter = 0
    var isLoading = false
    var lastVisibleItem = 0
    private var lastClickBookItem: Item? = null

    private lateinit var mAdapter: MyBookAdapter
    private lateinit var viewModel: MyBooksViewModel
//    val storedBookAdapter = context?.let { StoredBookAdapter(it) }?:StoredBookAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_my_books, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//            storedBookAdapter = activity?.applicationContext?.let { StoredBookAdapter(it) } ?: StoredBookAdapter()
        initUI()
        initUiLister()
//        loadingMore()

    }

//    private fun loadingMore() {
//        SB_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    updateNewChapter()
//                }
//            }
//        })
//    }

    private fun updateNewChapter() {
        context?.let {
            if(checkNetConnect(it)){
                CoroutineScope(Dispatchers.Main).launch{
                    mAdapter.clear()
                    val groupList = viewModel.updateStoredBookOnlineAsync().await()
                    for (i in groupList.indices) {
                        val folder = groupList[i]
                        mAdapter.addItem(folder)
                    }
                    withContext(Dispatchers.Main) {
                        mAdapter.notifyDataSetChanged()
                    }
                    MyBookRefreshLayout.isRefreshing = false

                }
            }else{
                customToast(it,getString(R.string.please_check_net_connect_state)).show()
                MyBookRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun initUiLister() {
        mAdapter.setListener(this,this)
        MyBookRefreshLayout.setOnRefreshListener(refreshListener)
        AddBookFolderBT.setOnClickListener {
            launchAlertDialog()
        }
    }

    private val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        updateNewChapter()
//      Handler().postDelayed({ MyBookRefreshLayout.isRefreshing = false }, 2000)
    }


    override fun onItemClick(sbBook: Child, view: View) {
        viewModel.lastReadBookItem.value = sbBook
        val intent = Intent(context, BookDetailActivity::class.java).apply {
            putExtra("BOOK_IS_STORED", true)
            putExtra(
                "BOOK_Detail", StoredBook(
                    sbBook.bookName, sbBook.bookAuthor, sbBook.bookSource,
                    sbBook.newChapter, sbBook.lastRead, sbBook.bookUrl, sbBook.isMostLike, -5, sbBook.bookId
                )
            )
        }
//
//        this.startActivity(intent)
        myBookFragmentLauncher.launch(intent)
    }

    override fun onDeleteClick(book:Child, view: View) {
        viewModel.deleteBookById(book.bookId)
        mAdapter.removeChildItem(book)
        setHasFavoriteBookView(mAdapter.checkHasFavoriteBook())
    }

    override fun onPinningClick(sbbook:Child, view: View) {
        launchAlertDialogForMoveItem(sbbook)
    }


    private fun initUI() {
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        mAdapter = MyBookAdapter()
        SB_recycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
            addItemDecoration(decoration)
        }

        viewModel = ViewModelProvider(this, MyBooksViewModelFactory(context!!)).get(MyBooksViewModel::class.java)
//      storedBookAdapter.setViewModel(viewModel)

        CoroutineScope(Dispatchers.IO).launch {
            mAdapter.clear()
            val groupList = viewModel.getFolderWithBook().await()
            for (i in groupList.indices) {
                val folder = groupList[i]
                mAdapter.addItem(folder)
            }

            setHasFavoriteBookView(mAdapter.checkHasFavoriteBook())

            withContext(Dispatchers.Main) {
                mAdapter.notifyDataSetChanged()
            }
        }

        viewModel.lastReadBookItem.observe(viewLifecycleOwner, { BookItem ->
            lastClickBookItem = BookItem
        })

//        viewModel.allsbBooks.observe(viewLifecycleOwner, { storedBooks ->
//            CoroutineScope(Dispatchers.IO).launch {
//                mAdapter.clear()
//                val groupList = viewModel.getFolderWithBook().await()
//                for (i in groupList.indices) {
//                    val folder = groupList[i]
//                    mAdapter.addItem(folder)
//                }
//                withContext(Dispatchers.Main) {
//                    mAdapter.notifyDataSetChanged()
//                }
//            }
//            mAdapter.setListener(this)
//            mAdapter.addItem()
//            mAdapter.addAll(viewModel.getFolderWithBook().await())
//            val groupNames = arrayOf("未分類")
//            for (i in groupNames.indices) {
//                val folder = Group()
//                folder.id = i
//                folder.isExpendable = i == 0
//                folder.title = groupNames[i]
//                for (j in storedBooks.indices) {
//                    val storedBook = Child()
//                    storedBook.bookUrl = storedBooks[j].bookUrl
//                    storedBook.bookAuthor = storedBooks[j].bookauthor
//                    storedBook.bookName = storedBooks[j].bookname
//                    storedBook.isMostLike = storedBooks[j].ismostlike
//                    storedBook.bookId = storedBooks[j].bookid
//                    storedBook.position = j
//                    storedBook.group = folder
//                    folder.addSubItem(storedBook)
//                }
//                mAdapter.addItem(folder)
//            }
//        })


        mAdapter.setExpandableToggleListener(object : ExpandableItemAdapter.ExpandableToggleListener {
            override fun onExpand(item: Item) {
            }

            override fun onCollapse(item: Item) {
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
            mAdapter?.saveStates(outState)
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
            if (savedInstanceState != null) {
                mAdapter?.restoreStates(savedInstanceState)
            }
    }

    private fun launchAlertDialog() {
        context?.let { mContext ->
            val dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialogview_add_book_folder, null)
            val builder = AlertDialog.Builder(mContext).apply {
                setView(dialogView)
            }
            val addBt = dialogView.findViewById<Button>(R.id.DialogAddBookFolderBT)
            val name = dialogView.findViewById<TextView>(R.id.DialogAddBookFolderEditTextView)

            val dialog = builder.create().apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            addBt.setOnClickListener {
                if (name.text.isEmpty()) {
                    customToast(activity,"請輸入至少1個字元！").show()
                } else {
                    CoroutineScope(Dispatchers.IO).launch{
                        val id = viewModel.addFolder(StoredBookFolder(
                            name.text.toString(),
                            System.currentTimeMillis()
                        ))
                        dialog.cancel()
                        mAdapter.addItemToPosition(Group().apply {
                            this.id = id.await().toInt()
                            title = name.text.toString()
                            isExpendable = false
                        }
                        )
                        mAdapter.notifyItemInserted(0)
                    }
                }
            }
            dialog.show()
        }
    }

    private fun launchAlertDialogEditFolderName(folder:Group,position:Int) {
        context?.let { mContext ->
            val dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialogview_add_book_folder, null)
            val builder = AlertDialog.Builder(mContext).apply {
                setView(dialogView)
            }
            val addBt = dialogView.findViewById<Button>(R.id.DialogAddBookFolderBT)
            val name = dialogView.findViewById<TextView>(R.id.DialogAddBookFolderEditTextView)
            val title = dialogView.findViewById<TextView>(R.id.DialogUpdateFolderTitle)
            addBt.text = "編輯"
            title.text = "編輯分類名稱"
            name.text = folder.title

            val dialog = builder.create().apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            addBt.setOnClickListener {
                if (name.text.isEmpty()) {
                    customToast(activity,"請輸入至少1個字元！").show()
                } else {
                    CoroutineScope(Dispatchers.IO).launch{
                        val id = viewModel.updateFolder(name.text.toString(),folder.id.toLong())
                        folder.title = name.text.toString()
                        mAdapter.notifyItemChanged(position,folder)
                        dialog.cancel()
                    }
                }
            }
            dialog.show()
        }
    }

    override fun onMoreClick(bookFolder: Group, view: View, position:Int) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.mybook_folder_more_menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menuBookFolderDelete -> {
//                    TODO 鎖起來
                    CoroutineScope(Dispatchers.IO).launch {
                        val folder = mAdapter.getItem(0xfa01,bookFolder.id)
                        viewModel.deleteBookFolderAndUpdate(bookFolder.id.toLong())
                        mAdapter.removeItem(bookFolder)

                        withContext(Dispatchers.Main){
                            mAdapter.notifyDataSetChanged()
                        }
                    }
                }
                R.id.menuBookFolderEdit -> {
                    launchAlertDialogEditFolderName(bookFolder,position)
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun launchAlertDialogForMoveItem(sbbook:Child) {
        CoroutineScope(Dispatchers.IO).launch {
            val folderList = viewModel.getBookFolders().await().toMutableList()
//            folderList.add(StoredBookFolder("未分類", 0, -5))
            val folderNameList = arrayListOf<String>()
            folderList.let {
                for (i in it) {
                    folderNameList.add(i.foldername)
                }
            }
            withContext(Dispatchers.Main) {
                var singleIndex = 0
                context?.let { mContext ->
                    AlertDialog.Builder(mContext)
                        .setTitle("選擇移動分類")
                        .setSingleChoiceItems(folderNameList.toTypedArray(), singleIndex) { _, clickIndex ->
                            singleIndex = clickIndex
                        }
                        .setPositiveButton("移動") { dialog, _ ->
                            CoroutineScope(Dispatchers.IO).launch {
                                folderList.let {
//                                    viewModel.storedBook(it[singleIndex].folderid)
                                    viewModel.moveBook(sbbook.bookId,it[singleIndex].folderid)
                                    mAdapter.moveChildItem(sbbook,it[singleIndex].folderid)
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

    private val myBookFragmentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            //當收藏狀態變化時
            if(activityResult.resultCode == Activity.RESULT_OK){
                val isBookStored = activityResult.data?.getBooleanExtra("ISBOOKSTORED", true)
                val bookId = activityResult.data?.getStringExtra("BOOKID")
                val lastReadChapter = activityResult.data?.getStringExtra("LASTREADCHAPTER")
                isBookStored?.let {
                    if (!it) {
                        removeBookItem(bookId)
                    }
                }
                lastReadChapter?.let{
                    updateBookItem(bookId,it)
                }
            }
        }

    private fun removeBookItem(bookId: String?) {
        bookId?.let { id ->
            viewModel.lastReadBookItem.value?.let {
                if (id == it.bookId) {
                    mAdapter.removeChildItem(it)
                    setHasFavoriteBookView(mAdapter.checkHasFavoriteBook())
                }
            }
        }
    }

    private fun updateBookItem(bookId: String?,lastReadChapter:String) {
        bookId?.let { id ->
            viewModel.lastReadBookItem.value?.let {
                val tempBook = it
                tempBook.lastRead = lastReadChapter
                if (id == it.bookId) {
                    mAdapter.updateChildItem(tempBook)
                }
            }
        }
    }

    override fun onPause() {
        viewModel.updateBookFolderState(mAdapter.findAllGroupItemPosition())
        super.onPause()
    }

    private fun setHasFavoriteBookView(hasBook:Boolean){
        if(!hasBook){
            NoFavoriteBookView.visibility = View.VISIBLE
        }else{
            NoFavoriteBookView.visibility = View.INVISIBLE
        }
    }
}