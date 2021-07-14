package com.quotes.widget

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nakul.widget.ColorSeekBar
import java.io.File
import java.io.FileOutputStream

class SettingsActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener,View.OnClickListener {

    private lateinit var sharedPreferences:SharedPreferences

    private lateinit var textSize:Spinner
    private lateinit var authorAlignment:Spinner
    private lateinit var authorSizeSpin:Spinner
    private lateinit var quoteStyle:Spinner
    private lateinit var authorStyle:Spinner
    private lateinit var changeQuoteFont:Spinner
    private lateinit var changeAuthorFont:Spinner
    private lateinit var changeQuoteAlignment:Spinner
    private lateinit var setQuoteLang:Spinner

    private lateinit var quotePreview:TextView
    private lateinit var authorPreview:TextView

    var alertDialog:AlertDialog? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        textSize = findViewById(R.id.text_size_spin)
        authorSizeSpin = findViewById(R.id.author_size_spin)
        setQuoteLang = findViewById(R.id.set_quote_language)
        quoteStyle = findViewById(R.id.quote_style_spin)
        authorStyle = findViewById(R.id.author_text_style)

        authorAlignment = findViewById(R.id.author_alignment_spin)
        changeQuoteAlignment = findViewById(R.id.quote_alignment_spin)
        changeQuoteFont = findViewById(R.id.quote_font)
        changeAuthorFont = findViewById(R.id.author_font)

        findViewById<Button>(R.id.choose_color).setOnClickListener(this)
        findViewById<Button>(R.id.save_button).setOnClickListener(this)

        quotePreview = findViewById(R.id.quote_preview_text)
        authorPreview = findViewById(R.id.quote_author_text)
        quotePreview.setTextColor(sharedPreferences.getInt("color",-54887))

        initAdapters()
        jsonRequest(Const.ENGLISH_QUOTE_API)
        jsonRequest(Const.HINDI_QUOTE_API)

        findViewById<Toolbar>(R.id.setting_toolbar).apply {
            title = getString(R.string.app_name)
            setSupportActionBar(this)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("SettingsActivity","OnPause Called")
        jsonRequest(Const.ENGLISH_QUOTE_API)
        jsonRequest(Const.HINDI_QUOTE_API)
    }

    private fun jsonRequest(api:String) {
        val englishQuoteRequest = JsonObjectRequest(0,api,null, {
            val saveFile = if (api == Const.ENGLISH_QUOTE_API) "eng-quote.json" else "hin-quote.json"
            val file1 = File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), saveFile)
            val fos = FileOutputStream(file1)
            val byte:ByteArray = it.toString().toByteArray()
            fos.write(byte)
            fos.close()

        }, { it.message })
        Volley.newRequestQueue(this).add(englishQuoteRequest)
    }

    override fun onStart() {
        super.onStart()
        Log.d("SettingsActivity","onStart")
        textSize.setSelection(sharedPreferences.getInt("textPos",0))
        authorSizeSpin.setSelection(sharedPreferences.getInt("authorPos",0))
        quoteStyle.setSelection(sharedPreferences.getInt("quoteStylePos",0))
        authorStyle.setSelection(sharedPreferences.getInt("authorTextPos",0))
        changeQuoteFont.setSelection(sharedPreferences.getInt("quoteFontPos",0))
        changeAuthorFont.setSelection(sharedPreferences.getInt("authorFontPos",0))
        authorAlignment.setSelection(sharedPreferences.getInt("authorAlignPos",0))
        changeQuoteAlignment.setSelection(sharedPreferences.getInt("quoteAlignPos",0))
        setQuoteLang.setSelection(sharedPreferences.getInt("langPosition",0))
        findViewById<Button>(R.id.choose_color).setTextColor(sharedPreferences.getInt("color",-328966))
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

       val sharedPrefEditor  = this@SettingsActivity.getPreferences(Context.MODE_PRIVATE)
       val setting = SharedPref(context = this@SettingsActivity)

       when (parent?.id) {
           R.id.text_size_spin -> with(sharedPrefEditor.edit()) {
               putInt("textPos", position)
               putString("textSelection", parent.getItemAtPosition(position).toString()).apply()

               ViewsUtils.setQuotePreview(quotePreview,setting.textSelection,setting.style,setting.quoteFontSelect, setting.align!!,setting.color,this@SettingsActivity)
               ViewsUtils.quoteAlignment1(quotePreview,setting.align ?: "NULL",setting.color)

           }

           R.id.author_size_spin -> with(sharedPrefEditor.edit()) {
                putInt("authorPos",position)
                putString("authorSelection", parent.getItemAtPosition(position).toString()).apply()

                ViewsUtils.setAuthorPreview(authorPreview,setting.authorSelection,setting.authorTextPosition,setting.authorFontSelection,setting.color,this@SettingsActivity)

           }

           R.id.quote_style_spin -> with(sharedPrefEditor.edit()) {
               putInt("quoteStylePos",position)
               putString("quoteStyleSelection", parent.getItemAtPosition(position).toString()).apply()

               ViewsUtils.setQuotePreview(quotePreview,setting.textSelection,setting.style,setting.quoteFontSelect, setting.align!!,setting.color,this@SettingsActivity)
           }

            R.id.author_text_style -> with(sharedPrefEditor.edit()) {
                putInt("authorTextPos", position)
                putString("authorStyleSelection", parent.getItemAtPosition(position).toString()).apply()

                ViewsUtils.setAuthorPreview(authorPreview,setting.authorSelection,setting.authorTextPosition,setting.authorFontSelection,setting.color,this@SettingsActivity)

            }

           R.id.quote_font -> with(sharedPrefEditor.edit()) {
               putInt("quoteFontPos", position)
               putString("quoteFontSelection", parent.getItemAtPosition(position).toString()).apply()

               ViewsUtils.setQuotePreview(quotePreview,setting.textSelection,setting.style,setting.quoteFontSelect, setting.align!!,setting.color,this@SettingsActivity)
           }

            R.id.author_font -> with(sharedPrefEditor.edit()) {
                putInt("authorFontPos", position)
                putString("authorFontSelection", parent.getItemAtPosition(position).toString()).apply()

                ViewsUtils.setAuthorPreview(authorPreview,setting.authorSelection,setting.authorTextPosition,setting.authorFontSelection,setting.color,this@SettingsActivity)

            }

            R.id.author_alignment_spin -> with(sharedPrefEditor.edit()) {
                putInt("authorAlignPos", position)
                putString("authorAlignSelection", parent.getItemAtPosition(position).toString()).apply()

                ViewsUtils.authorAlignment(authorPreview,setting.authorAlignment!!,setting.color)
            }
          
           R.id.quote_alignment_spin -> with (sharedPrefEditor.edit()) {
               putInt("quoteAlignPos", position)
               putString("quoteAlignSelection", parent.getItemAtPosition(position).toString()).apply()

               val quoteGravity = parent.getItemAtPosition(position).toString()
               ViewsUtils.quoteAlignment1(quotePreview,quoteGravity,setting.color)
               ViewsUtils.quoteAlignment(quotePreview,quoteGravity)
           }

           R.id.set_quote_language -> with(sharedPrefEditor.edit()) {
               putString("quoteLang",parent.getItemAtPosition(position).toString())
               putInt("langPosition",position).apply()
               val lang = parent.getItemAtPosition(position).toString()
               ViewsUtils.setLanguage(lang,quotePreview,authorPreview,this@SettingsActivity)
           }
       }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) { TODO("Not yet implemented") }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.choose_color -> colorDialog()
            R.id.save_button -> applySettings()
        }
    }

    private fun applySettings() {
        val build = MainActivity()
        build.onEnabled(this)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.about_section -> {
                Intent(this,AboutActivity::class.java).apply {
                    startActivity(this)
                }
                true
            }
            else ->super.onOptionsItemSelected(item)
        }
    }

    private fun initAdapters() {
        ArrayAdapter.createFromResource(this,R.array.textSizes,android.R.layout.simple_spinner_item)
            .also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                textSize.adapter = it
                textSize.onItemSelectedListener = this
            }

        ArrayAdapter.createFromResource(this,R.array.textSizes,android.R.layout.simple_spinner_item)
            .also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                authorSizeSpin.adapter = it
                authorSizeSpin.onItemSelectedListener = this
            }

        ArrayAdapter.createFromResource(this,R.array.textStyle,android.R.layout.simple_spinner_item)
            .also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                quoteStyle.adapter = it
                quoteStyle.onItemSelectedListener = this
            }

        ArrayAdapter.createFromResource(this,R.array.textStyle,android.R.layout.simple_spinner_item)
            .also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                authorStyle.adapter = it
                authorStyle.onItemSelectedListener = this
            }

        ArrayAdapter.createFromResource(this,R.array.font,android.R.layout.simple_spinner_item)
            .also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                changeQuoteFont.adapter = it
                changeQuoteFont.onItemSelectedListener = this
            }
        ArrayAdapter.createFromResource(this,R.array.font,android.R.layout.simple_spinner_item)
            .also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                changeAuthorFont.adapter = it
                changeAuthorFont.onItemSelectedListener = this
            }
        ArrayAdapter.createFromResource(this,R.array.alignmentPosition,android.R.layout.simple_spinner_item)
            .also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                authorAlignment.adapter = it
                authorAlignment.onItemSelectedListener = this
            }

        ArrayAdapter.createFromResource(this,R.array.alignmentPosition,android.R.layout.simple_spinner_item)
            .also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                changeQuoteAlignment.adapter = it
                changeQuoteAlignment.onItemSelectedListener = this
            }

        ArrayAdapter.createFromResource(this,R.array.lang,android.R.layout.simple_spinner_item)
            .also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                setQuoteLang.adapter = it
                setQuoteLang.onItemSelectedListener = this
            }
    }

    private fun colorDialog() {
        val sharedPref = SharedPref(this)
        val view = layoutInflater.inflate(R.layout.seekbar_layout, null, false)
        val colorSeekBar = view.findViewById<ColorSeekBar>(R.id.color_seek_bar)

        AlertDialog.Builder(this).apply {
            setView(view)
            setCancelable(true)
            alertDialog = this.create()
            alertDialog!!.show()
            view.findViewById<Button>(R.id.cancel).setOnClickListener { alertDialog?.dismiss() }
            colorSeekBar.setPosition(sharedPref.colorPos,sharedPref.colorAlphaPos)
            colorSeekBar.setOnColorChangeListener(object : ColorSeekBar.OnColorChangeListener {
                override fun onColorChangeListener(
                    colorBarPosition: Int,
                    alphaBarPosition: Int,
                    color: Int
                ) {
                    view.findViewById<TextView>(R.id.text_seekbar).setTextColor(colorSeekBar.getColor())
                    view.findViewById<TextView>(R.id.seek_bar_author).setTextColor(colorSeekBar.getColor())
                    view.findViewById<View>(R.id.color_preview).setBackgroundColor(colorSeekBar.getColor())

                    view.findViewById<Button>(R.id.save).setOnClickListener {
                        with(sharedPreferences.edit()) {
                            putInt("colorPos",colorBarPosition)
                            putInt("color",color)
                            putInt("colorAlphaPos",alphaBarPosition)
                            apply()
                        }

                        quotePreview.setTextColor(sharedPref.color)
                        authorPreview.setTextColor(sharedPref.color)
                        findViewById<Button>(R.id.choose_color).setTextColor(sharedPref.color)
                        alertDialog?.dismiss()
                    }
                }
            })
        }
    }
}