package com.quotes.widget

import android.content.Context
import android.content.SharedPreferences
import android.text.Layout

class SharedPref (context: Context) {
    private var shared:SharedPreferences = context.getSharedPreferences("SettingsActivity",Context.MODE_PRIVATE)

    val textSelection: String? get() = shared.getString("textSelection","Small")
    val quoteFontSelect :String? get() = shared.getString("quoteFontSelection","dancing-script")
    val style : Int get() = shared.getInt("quoteStylePos",0)
    val align : String? get () = shared.getString("quoteAlignSelection","Right")

    // Author settings
    val authorSelection :String? get() = shared.getString("authorSelection","Small")
    val authorFontSelection :String? get() = shared.getString("authorFontSelection","amatica")
    val authorTextPosition :Int get() = shared.getInt("authorTextPos",0)
    val authorAlignment :String? get() = shared.getString("authorAlignSelection","Center")
    val color :Int get() = shared.getInt("color",-328966)
    val colorPos :Int get() = shared.getInt("colorPos",0)
    val colorAlphaPos:Int get()  = shared.getInt("colorAlphaPos",0)
    
    val quotePosition = if (align.equals("Right")) Layout.Alignment.ALIGN_OPPOSITE else if (align.equals("Left")) Layout.Alignment.ALIGN_NORMAL else Layout.Alignment.ALIGN_CENTER
    val authorPosition = if (authorAlignment.equals("Right")) Layout.Alignment.ALIGN_OPPOSITE else if (authorAlignment.equals("Left")) Layout.Alignment.ALIGN_NORMAL else Layout.Alignment.ALIGN_CENTER

    val font : String  get() = shared.getString("quoteFontSelection","dancing-script") ?: "NULL"

    val lang :String get() = shared.getString("quoteLang","English") ?: "NULL"

}