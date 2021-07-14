package com.quotes.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat


open class ViewsUtils {

    companion object {

         fun selectFont(font:String) : Int{

            when (font) {
                "amatica" -> return R.font.amatica
                "dancing-script" -> return R.font.dancing_script
                "oswald"  -> return R.font.oswald
                "teko" -> return R.font.teko
                "berkshire-swash" -> return R.font.berkshire_swash
                "walt-disney"-> return R.font.disney_font
                "josefin-sans" -> return R.font.josefin_sans
                "josefin-slab" -> return R.font.josefin_slab
                "roboto" -> return R.font.roboto
            }
            return 0
        }

        fun setQuotePreview(view: TextView, size: String?, style: Int?, font: String?, gravity: String,color: Int,context: Context) {
            val resource = ResourcesCompat.getFont(context, selectFont(font!!))

            when (size) {
                "Small" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setTextAppearance(android.R.style.TextAppearance_Small).also {
                        view.typeface = Typeface.create(resource,style!!)
                        quoteAlignment1(view,gravity,color)
                    }
                }
                else {
                    view.textSize = 41F
                    view.typeface = Typeface.create(resource,style!!)
                    quoteAlignment1(view,gravity,color)
                }

                "Medium" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setTextAppearance(android.R.style.TextAppearance_Medium).also {
                        view.typeface = Typeface.create(resource,style!!)
                        quoteAlignment1(view,gravity,color)
                    }
                }
                else {
                    view.textSize = 51F
                    view.typeface = Typeface.create(resource,style!!)
                    quoteAlignment1(view,gravity,color)
                }

                "Large" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setTextAppearance(android.R.style.TextAppearance_Large).also {
                        view.typeface = Typeface.create(resource,style!!)
                        quoteAlignment1(view,gravity,color)
                    }
                }
                else {
                    view.textSize = 61F
                    view.typeface = Typeface.create(resource,style!!)
                    quoteAlignment1(view,gravity,color)
                }
            }
        }

        fun setAuthorPreview(view: TextView, size: String?, style: Int?, font: String?, color: Int,context: Context) {
            val resource = ResourcesCompat.getFont(context, selectFont(font!!))

            when (size) {

                "Small" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setTextAppearance(android.R.style.TextAppearance_Small).also {
                        view.typeface = Typeface.create(resource, style!!)
                        view.setTextColor(color)
                    }
                }
                else {
                        view.textSize = 41F
                        view.typeface = Typeface.create(resource, style!!)
                        view.setTextColor(color)
                }

                "Medium" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setTextAppearance(android.R.style.TextAppearance_Medium).also {
                        view.typeface = Typeface.create(resource, style!!)
                        view.setTextColor(color)
                    }
                }
                else {
                    view.textSize = 51F
                    view.typeface = Typeface.create(resource, style!!)
                    view.setTextColor(color)
                }

                "Large" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setTextAppearance(android.R.style.TextAppearance_Large).also {
                        view.typeface = Typeface.create(resource, style!!)
                        view.setTextColor(color)
                    }
                }
                else {
                    view.textSize = 61F
                    view.typeface = Typeface.create(resource, style!!)
                    view.setTextColor(color)
                }
            }
        }

        fun authorAlignment(view: TextView, gravity: String,color: Int) {
            when (gravity) {
                "Right" -> setLayoutParams(view, 8388613,color)
                "Center" -> setLayoutParams(view, 17,color)
                "Left" -> setLayoutParams(view, 8388611,color)
                else -> Log.d("ViewsUtils", "No gravity FOUND.")
            }
        }

        fun quoteAlignment(view: TextView, gravity1: String) {
            when (gravity1) {
                "Right" -> view.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                "Center" -> view.textAlignment = View.TEXT_ALIGNMENT_CENTER
                "Left" -> view.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                else -> Log.d("ViewsUtils", "No gravity FOUND.")
            }
        }

        fun quoteAlignment1(view: TextView, gravity1: String, color: Int) {
            when (gravity1) {
                "Right" -> setLayoutParams(view, 8388613, color)
                "Center" -> setLayoutParams(view, 17, color)
                "Left" -> setLayoutParams(view, 8388611, color)
                else -> Log.d("ViewsUtils", "No gravity FOUND.")
            }
        }

        private fun setLayoutParams(
            view: TextView,
            position: Int,
            color: Int
        ): LinearLayout.LayoutParams {

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
                .apply {
                    gravity = position
                    topMargin = 10
                    marginEnd = 10
                    marginStart = 10
                }
            view.layoutParams = params
            view.setTextColor(color)
            return params
        }

        fun createQuoteBitmap(
            context: Context,
            quote: String,
            color: Int,
            typeface: Int,
            style: Int,
            alignment: Layout.Alignment,
            size: Float
        ): Bitmap {

            val resources = context.resources
            val displayWidth = resources.displayMetrics.widthPixels
            val resource = ResourcesCompat.getFont(context,typeface)

            val textPaint = TextPaint(65).apply {
                isAntiAlias = true
                this.style = Paint.Style.FILL
                this.color = color
                textSize = size
                this.typeface = Typeface.create(resource, style)
            }

            val build = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                StaticLayout.Builder.obtain(quote, 0, quote.length, textPaint, displayWidth)
                    .setAlignment(alignment).setLineSpacing(0.0f, 1.2f)
                    .build() else StaticLayout(
                quote, textPaint, displayWidth, alignment, 1.2f,
                0.0f, false
            )

            val createBitmap =
                Bitmap.createBitmap(displayWidth, build.height, Bitmap.Config.ARGB_8888)
            createBitmap.eraseColor(0)
            val canvas = Canvas(createBitmap)
            canvas.save()
            canvas.translate(0.0F, 0.0F)
            build.draw(canvas)
            canvas.restore()
            return createBitmap
        }

        fun createAuthorBitmap(
            context: Context,
            author: String,
            color: Int,
            typeface: Int,
            style: Int,
            alignment: Layout.Alignment,
            size: Float
        ): Bitmap {

            val resources = context.resources
            val displayWidth = resources.displayMetrics.widthPixels
            val resource = ResourcesCompat.getFont(context,typeface)

            val textPaint = TextPaint(65).apply {
                isAntiAlias = true
                this.style = Paint.Style.FILL
                this.color = color
                textSize = size
                this.typeface = Typeface.create(resource, style)
            }

            val build = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                StaticLayout.Builder.obtain(author, 0, author.length, textPaint, displayWidth)
                    .setAlignment(alignment).setLineSpacing(0.0f, 1.2f)
                    .build() else StaticLayout(
                author, textPaint, displayWidth, alignment, 1.2f,
                0.0f, false
            )

            val createBitmap =
                Bitmap.createBitmap(displayWidth, build.height, Bitmap.Config.ARGB_8888)
            createBitmap.eraseColor(0)
            val canvas = Canvas(createBitmap)
            canvas.save()
            canvas.translate(0.0F, 0.0F)
            build.draw(canvas)
            canvas.restore()
            return createBitmap
        }

        fun setLanguage(select:String,quote: TextView,author:TextView,context: Context) {
            when (select) {
                "Hindi" -> setHindiPreview(quote,author,context)
                "English" -> setEnglishPreview(quote,author,context)
            }
        }

        private fun setEnglishPreview(quote: TextView, author: TextView,context: Context) {
            quote.text = context.getString(R.string.sample_quote)
            author.text = context.getString(R.string.quote_by)
        }

        private fun setHindiPreview(quote: TextView, author: TextView,context: Context) {
            quote.text = context.getString(R.string.hindi_quote)
            author.text = context.getString(R.string.hindi_quote_by)
        }
    }
}