package com.dimsuz.yamm.presentation.common.web_view

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.dimsuz.yamm.BuildConfig
import com.dimsuz.yamm.R
import com.dimsuz.yamm.presentation.baseui.BaseController
import com.dimsuz.yamm.presentation.baseui.util.isVisible
import kotlinx.android.synthetic.main.web_view_controller.*

private const val EXTRA_WEB_VIEW_URL = BuildConfig.APPLICATION_ID + ".web_view_url"

open class WebViewController(args: Bundle) : BaseController(args) {
  companion object {
    fun create(link: String): WebViewController {
      return WebViewController(createArgs(link))
    }

    fun createArgs(link: String): Bundle {
      val bundle = Bundle(1)
      bundle.putString(EXTRA_WEB_VIEW_URL, link)
      return bundle
    }
  }

  private val link get() = args.getString(EXTRA_WEB_VIEW_URL)

  override fun getViewLayout(): Int {
    return R.layout.web_view_controller
  }

  override fun initializeView(rootView: View) {
    webView.configureDefault()
    webView.webViewClient = WebClient(progressBar, this::handleWebViewError, this::onPageFinished, this::onInterceptUrlRedirect)

    webView.loadUrl(link)
  }

  /**
   * Override this function to handle various web view errors.
   * Default implementation shows an alert dialog which pops back stack after user clicks OK
   */
  open protected fun handleWebViewError(errorCode: Int, errorDescription: String, failingUrl: String?) {
    if (view == null) return
    AlertDialog.Builder(view!!.context)
      .setMessage(errorDescription)
      .setPositiveButton(R.string.action_close, { _, _ -> router.popCurrentController() })
      .show()
  }

  /**
   * Override this function to handle url redirection in web view.
   * Return `true` to signal that redirect was intercepted and should not be performed by web view, otherwise return `false` to proceed as usual.
   */
  open protected fun onInterceptUrlRedirect(url: String?): Boolean {
    return false
  }

  open protected fun onPageFinished(url: String?) {

  }

  override fun onDestroyView(view: View) {
    super.onDestroyView(view)
    (webView.parent as ViewGroup).removeView(webView)
    webView.removeAllViews()
    webView.destroy()
  }
}

@SuppressWarnings("SetJavaScriptEnabled")
private fun WebView.configureDefault() {
  val settings = this.settings
  settings.javaScriptEnabled = true
  settings.domStorageEnabled = true
  settings.builtInZoomControls = true
  settings.displayZoomControls = false
  settings.loadWithOverviewMode = true
  settings.useWideViewPort = true
}

private class WebClient(val progressBar: View,
                        private val errorHandler: (Int, String, String?) -> Unit,
                        private val pageFinishedHandler: ((String?) -> Unit)?,
                        private val redirectHandler: ((String?) -> Boolean)?) : WebViewClient() {
  override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
    progressBar.isVisible = true
    view.isVisible = false
  }

  override fun onPageFinished(view: WebView, url: String?) {
    progressBar.isVisible = false
    view.isVisible = true
    pageFinishedHandler?.invoke(url)
  }

  @Suppress("OverridingDeprecatedMember")
  override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String?) {
    progressBar.isVisible = false
    view.isVisible = true
    errorHandler.invoke(errorCode, getWebViewErrorDescription(progressBar.context, errorCode), failingUrl)
  }

  @Suppress("OverridingDeprecatedMember", "DEPRECATION")
  override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
    return redirectHandler?.invoke(url) ?: super.shouldOverrideUrlLoading(view, url)
  }
}

private fun getWebViewErrorDescription(context: Context, errorCode: Int): String {
  val resourceId = when(errorCode) {
    WebViewClient.ERROR_HOST_LOOKUP -> R.string.error_no_connection_title
    WebViewClient.ERROR_TIMEOUT -> R.string.error_network_timeout_title
    else -> R.string.unknown_error_title
  }
  return context.getString(resourceId)
}
