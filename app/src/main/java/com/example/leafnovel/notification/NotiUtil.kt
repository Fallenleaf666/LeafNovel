package com.example.leafnovel.notification

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.SystemClock
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.leafnovel.`interface`.NotificationHelper


@SuppressLint("NewApi")
class NotifyUtil(private val mContext: Context, private val NOTIFICATION_ID: Int) {
    var requestCode = SystemClock.uptimeMillis().toInt()
    private val nm: NotificationManager =
        mContext.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
    private var notification: Notification? = null
    private var cBuilder: NotificationCompat.Builder
    private var nBuilder: NotificationCompat.Builder? = null

    init {
//        android8之前版本的裝置會忽略channelId
        cBuilder = NotificationCompat.Builder(mContext, "channelBeforeAndroid 8")
        if (Build.VERSION.SDK_INT >= 26) {
            val id = "channel_1"
            val description = "Leaf Novel"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(id, description, importance)
            channel.enableLights(true)
            channel.enableVibration(true)
            nm.createNotificationChannel(channel)
            cBuilder = NotificationCompat.Builder(mContext, id)
            cBuilder.setCategory(Notification.CATEGORY_MESSAGE)
        }
    }

    /**
     * 设置在顶部通知栏中的各种信息
     *
     * @param pendingIntent
     * @param smallIcon
     * @param ticker
     */
    private fun setCompatBuilder(
        pendingIntent: PendingIntent?, smallIcon: Int, ticker: String,
        title: String?, content: String?, sound: Boolean, vibrate: Boolean, lights: Boolean
    ) {
//        // 如果当前Activity启动在前台，则不开启新的Activity。
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        // 当设置下面PendingIntent.FLAG_UPDATE_CURRENT这个参数的时候，常常使得点击通知栏没效果，你需要给notification设置一个独一无二的requestCode
//        // 将Intent封装进PendingIntent中，点击通知的消息后，就会启动对应的程序
//        PendingIntent pIntent = PendingIntent.getActivity(mContext,
//                requestCode, intent, FLAG);
        pendingIntent?.let {
            cBuilder.setContentIntent(it) // 该通知要启动的Intent
        }
        cBuilder.setSmallIcon(smallIcon) // 设置顶部状态栏的小图标
        cBuilder.setTicker(ticker) // 在顶部状态栏中的提示信息
        cBuilder.setContentTitle(title) // 设置通知中心的标题
        cBuilder.setContentText(content) // 设置通知中心中的内容
        cBuilder.setWhen(System.currentTimeMillis())

        /*
         * 将AutoCancel设为true后，当你点击通知栏的notification后，它会自动被取消消失,
         * 不设置的话点击消息后也不清除，但可以滑动删除
         */cBuilder.setAutoCancel(true)
        // 将Ongoing设为true 那么notification将不能滑动删除
        // notifyBuilder.setOngoing(true);
        /*
         * 从Android4.1开始，可以通过以下方法，设置notification的优先级，
         * 优先级越高的，通知排的越靠前，优先级低的，不会在手机最顶部的状态栏显示图标
         */cBuilder.setPriority(Notification.PRIORITY_MAX)
        /*
         * Notification.DEFAULT_ALL：铃声、闪光、震动均系统默认。
         * Notification.DEFAULT_SOUND：系统默认铃声。
         * Notification.DEFAULT_VIBRATE：系统默认震动。
         * Notification.DEFAULT_LIGHTS：系统默认闪光。
         * notifyBuilder.setDefaults(Notification.DEFAULT_ALL);
         */
        var defaults = 0
        if (sound) {
            defaults = defaults or Notification.DEFAULT_SOUND
        }
        if (vibrate) {
            defaults = defaults or Notification.DEFAULT_VIBRATE
        }
        if (lights) {
            defaults = defaults or Notification.DEFAULT_LIGHTS
        }
        cBuilder.setDefaults(defaults)
    }

    /**
     * 设置builder的信息，在用大文本时会用到这个
     *
     * @param pendingIntent
     * @param smallIcon
     * @param ticker
     */
    private fun setBuilder(
        pendingIntent: PendingIntent,
        smallIcon: Int,
        ticker: String,
        sound: Boolean,
        vibrate: Boolean,
        lights: Boolean
    ) {
        nBuilder = NotificationCompat.Builder(mContext)
        // 如果当前Activity启动在前台，则不开启新的Activity。
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pIntent = PendingIntent.getActivity(mContext,
//                requestCode, intent, FLAG);
        nBuilder!!.setContentIntent(pendingIntent)
        nBuilder!!.setSmallIcon(smallIcon)
        nBuilder!!.setTicker(ticker)
        nBuilder!!.setWhen(System.currentTimeMillis())
        nBuilder!!.setPriority(Notification.PRIORITY_MAX)
        var defaults = 0
        if (sound) {
            defaults = defaults or Notification.DEFAULT_SOUND
        }
        if (vibrate) {
            defaults = defaults or Notification.DEFAULT_VIBRATE
        }
        if (lights) {
            defaults = defaults or Notification.DEFAULT_LIGHTS
        }
        nBuilder!!.setDefaults(defaults)
    }

    /**
     * 普通的通知
     *
     *
     * 1. 侧滑即消失，下拉通知菜单则在通知菜单显示
     *
     * @param pendingIntent
     * @param smallIcon
     * @param ticker
     * @param title
     * @param content
     */
    fun notifyNormalSingline(
        pendingIntent: PendingIntent, smallIcon: Int,
        ticker: String, title: String?, content: String?, sound: Boolean, vibrate: Boolean, lights: Boolean
    ) {
        setCompatBuilder(pendingIntent, smallIcon, ticker, title, content, sound, vibrate, lights)
        sent()
    }

    /**
     * 进行多项设置的通知(在小米上似乎不能设置大图标，系统默认大图标为应用图标)
     *
     * @param pendingIntent
     * @param smallIcon
     * @param ticker
     * @param title
     * @param content
     */
//    fun notifyMailBox(
//        pendingIntent: PendingIntent, smallIcon: Int, largeIcon: Int, messageList: ArrayList<String?>,
//        ticker: String, title: String, content: String?, sound: Boolean, vibrate: Boolean, lights: Boolean
//    ) {
//        setCompatBuilder(pendingIntent, smallIcon, ticker, title, content, sound, vibrate, lights)
//
//        // 将Ongoing设为true 那么notification将不能滑动删除
//        //cBuilder.setOngoing(true);
//        /**
//         * // 删除时
//         * Intent deleteIntent = new Intent(mContext, DeleteService.class);
//         * int deleteCode = (int) SystemClock.uptimeMillis();
//         * // 删除时开启一个服务
//         * PendingIntent deletePendingIntent = PendingIntent.getService(mContext,
//         * deleteCode, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//         * cBuilder.setDeleteIntent(deletePendingIntent);
//         */
//        val bitmap = BitmapFactory.decodeResource(mContext.resources, largeIcon)
//        cBuilder.setLargeIcon(bitmap)
//        cBuilder.setDefaults(Notification.DEFAULT_ALL) // 设置使用默认的声音
//        //cBuilder.setVibrate(new long[]{0, 100, 200, 300});// 设置自定义的振动
//        cBuilder.setAutoCancel(true)
//        // builder.setSound(Uri.parse("file:///sdcard/click.mp3"));
//
//        // 设置通知样式为收件箱样式,在通知中心中两指往外拉动，就能出线更多内容，但是很少见
//        //cBuilder.setNumber(messageList.size());
//        val inboxStyle = Notification.InboxStyle()
//        for (msg in messageList) {
//            inboxStyle.addLine(msg)
//        }
//        inboxStyle.setSummaryText("[" + messageList.size + "条]" + title)
//        cBuilder.style = inboxStyle
//        sent()
//    }

    /**
     * 自定義視圖
     *
     * @param remoteViews
     * @param pendingIntent
     * @param smallIcon
     * @param ticker
     */
    fun notifyCustomView(
        remoteViews: RemoteViews?, pendingIntent: PendingIntent,
        smallIcon: Int, ticker: String, sound: Boolean, vibrate: Boolean, lights: Boolean
    ) {
        setCompatBuilder(pendingIntent, smallIcon, ticker, null, null, sound, vibrate, lights)
        notification = cBuilder.apply { setCustomContentView(remoteViews) }.build()
        nm.notify(NOTIFICATION_ID, notification)
    }

    /**
     * 可以容纳多行提示文本的通知信息 (因为在高版本的系统中才支持，所以要进行判断)
     *
     * @param pendingIntent
     * @param smallIcon
     * @param ticker
     * @param title
     * @param content
     */
    fun notifyNormalMultiline(
        pendingIntent: PendingIntent, smallIcon: Int, ticker: String,
        title: String?, content: String?, sound: Boolean, vibrate: Boolean, lights: Boolean
    ) {
        val sdk = Build.VERSION.SDK_INT
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            notifyNormalSingline(pendingIntent, smallIcon, ticker, title, content, sound, vibrate, lights)
            Toast.makeText(mContext, "手機版本低於Android 4.1.2，不支持多行通知！！", Toast.LENGTH_SHORT).show()
        } else {
            setCompatBuilder(pendingIntent, smallIcon, ticker, title, content, sound, vibrate, lights)
            notification = cBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(content)).build()
            nm.notify(NOTIFICATION_ID, notification)
        }
    }

    //  含進度條的通知
    fun notifyProgress(
        pendingIntent: PendingIntent?, smallIcon: Int,
        ticker: String, title: String?, content: String?, sound: Boolean, vibrate: Boolean, lights: Boolean,
        max:Int
    ) {
        setCompatBuilder(pendingIntent, smallIcon, ticker, title, content, false, false, false)
                // 參數：1.最大進度， 2.當前進度， 3.是否精確顯示進度
                cBuilder.setProgress(max, 0, false)
                cBuilder.setOngoing(true)
                sent()
    }

    fun updateNotifyProgress(nowProgress:Int,max :Int,title:String) {
        // 參數：1.最大進度， 2.當前進度， 3.是否精確顯示進度
        cBuilder.setContentText("$nowProgress/$max $title")
        cBuilder.setProgress(max, nowProgress, false)
        cBuilder.setOngoing(true)
        sent()
    }
    //下載完成後發送
    fun downloadCompleteNotify() {
        cBuilder.setContentText("下載完成").setProgress(0, 0, false)
        cBuilder.setOngoing(false)
        cBuilder.setAutoCancel(true)
        sent()
    }

    /**
     * 含大圖的通知
     *
     * @param pendingIntent
     * @param smallIcon
     * @param ticker
     * @param title
     * @param bigPic
     */
    fun notifyBigPic(
        pendingIntent: PendingIntent, smallIcon: Int, ticker: String,
        title: String?, content: String?, bigPic: Int, sound: Boolean, vibrate: Boolean, lights: Boolean
    ) {
        setCompatBuilder(pendingIntent, smallIcon, ticker, title, null, sound, vibrate, lights)
        val picStyle = NotificationCompat.BigPictureStyle()
        val options = BitmapFactory.Options()
        options.inScaled = true
        options.inSampleSize = 2
        val bitmap = BitmapFactory.decodeResource(mContext.resources, bigPic, options)
        picStyle.bigPicture(bitmap)
        picStyle.bigLargeIcon(bitmap)
        cBuilder.setContentText(content)
        cBuilder.setStyle(picStyle)
        sent()
    }

    /**
     * 里面有两个按钮的通知
     *
     * @param smallIcon
     * @param leftBtnIcon
     * @param leftText
     * @param leftPendIntent
     * @param rightBtnIcon
     * @param rightText
     * @param rightPendIntent
     * @param ticker
     * @param title
     * @param content
     */
    fun notifyButton(
        smallIcon: Int,
        leftBtnIcon: Int,
        leftText: String?,
        leftPendIntent: PendingIntent?,
        rightBtnIcon: Int,
        rightText: String?,
        rightPendIntent: PendingIntent,
        ticker: String,
        title: String?,
        content: String?,
        sound: Boolean,
        vibrate: Boolean,
        lights: Boolean
    ) {
        requestCode = SystemClock.uptimeMillis().toInt()
        setCompatBuilder(rightPendIntent, smallIcon, ticker, title, content, sound, vibrate, lights)
        cBuilder.addAction(
            leftBtnIcon,
            leftText, leftPendIntent
        )
        cBuilder.addAction(
            rightBtnIcon,
            rightText, rightPendIntent
        )
        sent()
    }

    //  發送通知
    private fun sent() {
        notification = cBuilder.build()
        nm.notify(NOTIFICATION_ID, notification)
    }

    //  根據id清除通知
    fun clear() {
        // 取消通知
        nm.cancelAll()
    }

    companion object {
        private const val FLAG = Notification.FLAG_INSISTENT
    }

}