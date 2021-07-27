package com.azhar.webviewtopdf

import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintJob
import android.print.PrintManager
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var printFabPressed = false
    lateinit var strFileName: String
    lateinit var strDateTime: String
    lateinit var printJob: PrintJob
    lateinit var printManager: PrintManager
    lateinit var printDocumentAdapter: PrintDocumentAdapter
    lateinit var webViewPrint: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        assert(supportActionBar != null)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //set web client
        webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                webViewPrint = webView
            }
        })

        //set link url
        webView.loadUrl("https://www.google.co.id")
        fabPrint.setOnClickListener {
            if (webViewPrint != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getPrintPage(webViewPrint)
                } else {
                    Toast.makeText(this@MainActivity, "Oops, Perangkatmu tidak support!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@MainActivity, "Oops! Halaman tidak dimuat sepenuhnya", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //set name file & show print menu
    private fun getPrintPage(webView: WebView) {
        printFabPressed = true
        printManager = this.getSystemService(PRINT_SERVICE) as PrintManager
        strDateTime = SimpleDateFormat("yyyy-MM-dd").format(Date())
        strFileName = "File Dokumen $strDateTime"
        printDocumentAdapter = webView.createPrintDocumentAdapter(strFileName)

        assert(printManager != null)
        printJob = printManager.print(strFileName, printDocumentAdapter, PrintAttributes.Builder().build())
    }

    override fun onResume() {
        super.onResume()
        if (printJob != null && printFabPressed) {
            if (printJob.isCompleted) {
                Toast.makeText(this, "Sukses menyimpan File PDF", Toast.LENGTH_SHORT).show()
            } else if (printJob.isStarted) {
                Toast.makeText(this, "Memulai...", Toast.LENGTH_SHORT).show()
            } else if (printJob.isBlocked) {
                Toast.makeText(this, "Oops, File PDF diblokir", Toast.LENGTH_SHORT).show()
            } else if (printJob.isCancelled) {
                Toast.makeText(this, "Cetak PDF dibatalkan", Toast.LENGTH_SHORT).show()
            } else if (printJob.isFailed) {
                Toast.makeText(this, "Oops! Gagal membuat File PDF", Toast.LENGTH_SHORT).show()
            } else if (printJob.isQueued) {
                Toast.makeText(this, "Tunggu sebentar...", Toast.LENGTH_SHORT).show()
            }
            printFabPressed = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.exit -> {
                finish()
                true
            }
            R.id.stop -> {
                webView.stopLoading()
                true
            }
            R.id.back -> {
                webView.goBack()
                true
            }
            R.id.next -> {
                webView.goForward()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}