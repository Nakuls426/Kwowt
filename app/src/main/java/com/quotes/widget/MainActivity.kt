package com.quotes.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.RemoteViews
import com.quotes.widget.ViewsUtils.Companion.selectFont
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.random.Random.Default.nextInt


open class MainActivity : AppWidgetProvider() {

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {

        Log.d("MainActivity", "Widget update...")
        appWidgetIds?.forEach {
            appWidgetManager?.updateAppWidget(it, buildUpdate1(context))
            mainActivityUpdateWidget(context, appWidgetManager, it, buildUpdate1(context))
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent?.action == "ReceiveUpdate") {
            Log.d("MainActivity", "ReceiveUpdate")
            val componentName = ComponentName(context!!.packageName, javaClass.name)
            val appWidgetManger = AppWidgetManager.getInstance(context)

            appWidgetManger.getAppWidgetIds(componentName).forEach {
                mainActivityUpdateWidget(context, appWidgetManger, it, buildUpdate1(context))
            }
        }
    }

    override fun onEnabled(context: Context?) {
        val componentName = ComponentName(context!!.packageName, javaClass.name)
        val appWidgetManger = AppWidgetManager.getInstance(context)

        appWidgetManger.getAppWidgetIds(componentName).forEach {
            val buildUpdate = buildUpdate1(context)
            mainActivityUpdateWidget(context, appWidgetManger, it, buildUpdate)
        }
    }

       private fun buildUpdate1(context: Context?): RemoteViews? {
        val setting1 = SharedPref(context!!)
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), if (setting1.lang == "English") "eng-quote.json" else "hin-quote.json")
        val b: InputStream = file.inputStream()

        val content = b.readBytes().toString(Charset.defaultCharset())

        val json = JSONObject(content)
        val quotes = json.getJSONArray("quotes")
        var views: RemoteViews? = null
        val setting = SharedPref(context)

        for (j in 0 until quotes.length()) {
            views = RemoteViews(context.packageName, R.layout.activity_main)

            val rand = nextInt(1, quotes.length())
            val jsonArray = quotes.getJSONObject(rand)
            val quote = jsonArray.getString("quote")
            val author = jsonArray.getString("author")
            //Log.d("MainActivity","From $author and $quote")


            when (setting.textSelection) {

                "Small" -> ViewsUtils.createQuoteBitmap(
                    context, quote, setting.color, selectFont(setting.font), setting.style, setting.quotePosition,
                    41F
                ).apply {
                    views.setImageViewBitmap(R.id.quote_as_bitmap, this)
                }

                "Medium" -> ViewsUtils.createQuoteBitmap(
                    context, quote, setting.color, selectFont(setting.font), setting.style, setting.quotePosition,
                    51F
                ).apply {
                    views.setImageViewBitmap(R.id.quote_as_bitmap, this)
                }

                "Large" -> ViewsUtils.createQuoteBitmap(
                    context, quote, setting.color, selectFont(setting.font), setting.style, setting.quotePosition,
                    61F
                ).apply {
                    views.setImageViewBitmap(R.id.quote_as_bitmap, this)
                }
            }

            when (setting.authorSelection) {

                "Small" -> ViewsUtils.createAuthorBitmap(
                    context, "— $author", setting.color, selectFont(setting.authorFontSelection!!), setting.authorTextPosition, setting.authorPosition,
                    41F
                ).apply {
                    views.setImageViewBitmap(R.id.quote_author, this)
                }

                "Medium" -> ViewsUtils.createAuthorBitmap(
                    context, "— $author", setting.color, selectFont(setting.authorFontSelection!!), setting.authorTextPosition,
                    setting.authorPosition,
                    51F
                ).apply {
                    views.setImageViewBitmap(R.id.quote_author, this)
                }

                "Large" -> ViewsUtils.createAuthorBitmap(
                    context, "— $author", setting.color, selectFont(setting.authorFontSelection!!), setting.authorTextPosition, setting.authorPosition,
                    61F
                ).apply {
                    views.setImageViewBitmap(R.id.quote_author, this)
                }
            }
        }
        return views
    }

    companion object {

        private var UPDATE = "ReceiveUpdate"

        fun mainActivityUpdateWidget(
            context: Context?,
            appWidgetManager: AppWidgetManager?,
            appWidgetIds: Int,
            buildUpdate: RemoteViews?
        ) {

            Intent(context, MainActivity::class.java).apply {
                this.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds)
                PendingIntent.getBroadcast(context, 0, this, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            buildUpdate?.setOnClickPendingIntent(R.id.update_tap, pendingIntent(context, UPDATE))
            appWidgetManager?.updateAppWidget(appWidgetIds, buildUpdate)
        }

        private fun pendingIntent(context: Context?, action: String): PendingIntent {
            Intent(context, MainActivity::class.java)
                .apply {
                    this.action = action
                    return PendingIntent.getBroadcast(context, 0, this, 0)
                }
        }
    }
}