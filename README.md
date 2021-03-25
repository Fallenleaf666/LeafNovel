# LeafNovel 

鑿於平時就有看小說的習慣，加上閱讀時總被突然出現的廣告所干擾，因此嘗試使用Kotlin編寫這款app，落葉小說是一款提供讀者可以方便閱讀小說的app，支援多執行續下載，可快速將小說章節下載到本地端，方便離線閱讀，日夜模式，字體大小編輯，紀錄上回閱讀進度，提供小書分類資料夾，方便對小說個人化分類。

未來預計新增書友評論區，會員書櫃雲端紀錄，增加小說來源，追蹤作者新書。

---

隨著Kotlin成為Google認定的Android主流開發語言後，便想說嘗試使用它來開發一個SideProject，從而初步瞭解Kotlin這個語言，並嘗試套用MVVM的架構去進行開發。



---

## app畫面預覽
|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/picture/myBook.png" width="300"/>|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/picture/search.png" width="300"/>|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/picture/bookIntroduce.png" width="300"/>|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/picture/bookDcitionary.png" width="300"/>|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/picture/bookDownload.png" width="300"/>|
|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/picture/readView.png" width="300"/>|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/picture/readInterface.png" width="300"/>|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/picture/readISettingnterface.png" width="300"/>||<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/picture/setting.png" width="300"/>|

---

## 功能大覽

- 個人書櫃
  - 下拉刷新小說最新章節
  - 移除/移動收藏
  - 建立/刪除分類 
- 小說搜尋
  - 書名/作者查詢
- 小說主頁
  - 書籍資訊
  - 上次閱讀進度
  - 收藏/移除小說
  - 章節目錄
- 閱讀功能介面
  - 日夜模式
  - 改變字體/螢幕亮度/閱讀背景
  - 閱讀進度/電量/當前章節狀態欄
  - 章節跳轉
- 閱讀設定
  - 隱藏內文標題
  - 自動讀取下一章節
  - 隱藏內文標題
- 下載
  - 下載小說章節
  - 刪除下載章節
- 問題回報
---
### 開發工具
- ****intellij idea****


### 使用庫
- ****Jsoup****
- ****Coroutines****
- ****Room****

---

## 功能介紹及操作畫面
| 功能 | 介紹 | 操作畫面 |其他|
| -------- | -------- | -------- |--------|
|小說查詢|可於搜尋欄位中輸入書名或作者，查詢關聯小說。 <br> 這裡使用了FB的shimmer庫，可以在載入小說列表時，呈現類似載入中的閃爍動畫|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/searchBook.gif" width="500"/>||
|新增收藏|將小說放入個人書櫃中。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/addFavoriteBook.gif" width="500"/>||
|移除收藏|將小說從書櫃中移除。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/deleteFavoriteBook.gif" width="500"/>||
|小說刷新|書櫃下拉可更新當前小說最新章節資訊。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/refreshUpdate.gif" width="500"/>||
|新增分類資料夾|讓使用者依據個人閱讀習慣方便分類，以便下次使用時可以快速找到。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/createClassFolder.gif" width="500"/>||
|編輯分類資料夾|更改資料夾名稱。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/editClassFolder.gif" width="500"/>||
|刪除分類資料夾|將不必要的資料夾刪除，內部的書會移至未分類。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/deleteClassFolder.gif" width="500"/>||
|取消收藏|將小說從書櫃中移除。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/cancelFavoriteBook.gif" width="500"/>||
|小說閱讀狀態欄位|顯示目前閱讀章節名稱.閱讀進度，電池狀態，當前時間。|<img src="" width="500"/>||
|跳至下個章節|閱讀時點擊畫面進入設定模式，點擊畫面兩側的箭頭符號，便可以進行章節的上/下回跳轉。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/changeToNextChapter.gif" width="500"/>||
|日夜模式|可切換日夜主題配色，因應不同環境。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/dayNightChange.gif" width="500"/>||
|閱讀設定|可調整字體大小及螢幕亮度。<br>註：因操作影片為畫面截圖，故無法顯示亮度變化|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/fontstyleChange.gif" width="500"/>||
|背景更換|用戶可以自由選擇閱讀時的背景顏色，增加閱讀體驗。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/backgroundChange.gif" width="500"/>||
|目錄跳轉|用戶在閱讀時，點擊螢幕顯示功能頁面，而後點擊目錄，便會出現側邊章節列表，點擊便可進行章節跳轉。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/directoryChangeChapter.gif" width="500"/>||
|自動讀取下一章節|若該選項開啟時，用戶將小說章節閱讀完成時，會自動讀入下一章節。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/autoLoadNextChapter.gif" width="500"/>||
|隱藏標題|若該選項開啟時，用戶將不會在閱讀內文時，看到該章節的標題。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/hideTitle.gif" width="500"/>||
|單章節下載|可下載指定小說章節。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/downloadSingleChapter.gif" width="500"/>||
|範圍章節下載|可下載特定範圍的小說章節。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/downloadRangeChapter.gif" width="500"/>||
|全選下載|可下載當前小說全部章節。<br>使用多執行續進行下載，大幅增加下載速度。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/downloadAllChapter.gif" width="500"/>||
|問題回報|可反應使用app時遇到的問題至開發者信箱，並自動附上手機型號以便排錯。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/sendResponseMessage.gif" width="500"/>||
|上次閱讀|自動記錄上次的閱讀進度，下次即可直接從該進度開始閱讀。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/recordLastRead.gif" width="500"/>||
|分類擴展|可擴展收縮分類收藏，隱藏部分藏書。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/extendFolder.gif" width="500"/>||


---


## 製作遇到的困難

-  


## :tada: 感想


