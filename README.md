# LeafNovel 

鑿於平時就有看小說的習慣，加上閱讀時總被突然出現的廣告所干擾，因此嘗試使用Kotlin編寫這款app，落葉小說是一款提供讀者可以方便閱讀小說的app，支援多執行續下載，可快速將小說章節下載到本地端，方便離線閱讀，日夜模式，字體大小編輯，紀錄上回閱讀進度，提供小書分類資料夾，方便對小說個人化分類。

未來預計新增書友評論區，會員書櫃雲端紀錄，增加小說來源，追蹤作者新書。

---

隨著Kotlin成為Google認定的Android主流開發語言後，便想說嘗試使用它來開發一個SideProject，從而初步瞭解Kotlin這個語言，並嘗試套用MVVM的架構去進行開發。



---

## app畫面預覽



|  | |||
| -------- | -------- |-------- |-------- |
|||||

---

## 功能大覽

- 個人書櫃
  - 移動分類
  - 刪除分類 
- 閱讀設定
  - 用戶搜尋
- 小說搜尋
  - 編輯個人資訊
  - 追隨/被追隨查看
- 閱讀設定
  - 自動讀取下一章節
  - 隱藏標題
  - 刪除緩存
- 收藏最愛
  - 收藏小說
  - 移除收藏
---
### 使用庫及開發工具
- ****Jsoup****
- ****Coroutines****
- ****Room****
- ****intellij idea****

---

## 功能介紹及操作畫面
| 功能 | 介紹 | 操作畫面 |其他|
| -------- | -------- | -------- |--------|
|個人書櫃|使用者可根據個人喜好將書本收藏放入個人書櫃，以便下次閱讀。|<img src="" width="500"/>||
|小說查詢|可以在搜尋欄位中輸入書名或作者，查詢關聯小說。 <br> 這裡我使用了FB的shimmer庫，可以在載入小說列表時，呈現類似載入中的閃爍動畫|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/searchBook.gif" width="500"/>||
|新增分類資料夾|讓使用者依據個人閱讀習慣方便分類，以便下次使用時可以快速找到。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/createClassFolder.gif" width="500"/>||
|編輯分類資料夾|更改資料夾名稱。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/editClassFolder.gif" width="500"/>||
|刪除分類資料夾|將不必要的資料夾刪除，內部的書會移至未分類。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/deleteClassFolder.gif" width="500"/>||
|小說閱讀狀態欄位|顯示目前閱讀章節名稱.閱讀進度，電池狀態，當前時間。|<img src="" width="500"/>||
|自動讀取下一章節|若該選項開啟時，用戶將小說章節閱讀完成時，會自動讀入下一章節。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/autoLoadNextChapter.gif" width="500"/>||
|移除收藏|將小說從書櫃中移除。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/deleteFavoriteBook.gif" width="500"/>||
|隱藏標題|若該選項開啟時，用戶將不會在閱讀內文時，看到該章節的標題。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/hideTitle.gif" width="500"/>||
|目錄跳轉|用戶在閱讀時，點擊螢幕顯示功能頁面，而後點擊目錄，便會出現側邊章節列表，點擊便可進行章節跳轉。|<img src="https://github.com/Fallenleaf666/LeafNovel/blob/master/screenshot/directoryChangeChapter.gif" width="500"/>||

---


## 製作遇到的困難

-  


## :tada: 感想


