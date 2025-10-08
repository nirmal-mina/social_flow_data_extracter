package com.pipe.social_flow_data_extracter.downloader

import android.util.Log
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.Nonnull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException

class DownloaderImpl private constructor(builder: OkHttpClient.Builder) : Downloader() {

    companion object {
        private const val USER_AGENT =
                "Mozilla/5.0 (Windows NT 10.0; rv:78.0) Gecko/20100101 Firefox/78.0"
        const val YOUTUBE_RESTRICTED_MODE_COOKIE_KEY = "youtube_restricted_mode_key"
        const val RECAPTCHA_COOKIES_KEY = "recaptcha_cookies"
        const val YOUTUBE_DOMAIN = "youtube.com"

        private var instance: DownloaderImpl? = null

        @JvmStatic
        fun init(builder: OkHttpClient.Builder?): DownloaderImpl {
            instance = DownloaderImpl(builder ?: OkHttpClient.Builder())
            return instance!!
        }

        @JvmStatic
        fun getInstance(): DownloaderImpl {
            if (instance == null) init(null)
            return instance!!
        }
    }

    private val cookies: MutableMap<String, String> = HashMap()
    private var client: OkHttpClient = builder.readTimeout(30, TimeUnit.SECONDS).build()

    fun getCookies(url: String): String {
        val resultCookies: MutableList<String> = ArrayList()
        if (url.contains(YOUTUBE_DOMAIN)) {
            val youtubeCookie = getCookie(YOUTUBE_RESTRICTED_MODE_COOKIE_KEY)
            if (youtubeCookie != null) resultCookies.add(youtubeCookie)
        }
        val recaptchaCookie = getCookie(RECAPTCHA_COOKIES_KEY)
        if (recaptchaCookie != null) resultCookies.add(recaptchaCookie)
        return CookieUtils.concatCookies(resultCookies)
    }

    fun getCookie(key: String): String? = cookies[key]

    fun setCookie(cookie: String) {
        cookies[RECAPTCHA_COOKIES_KEY] = cookie
    }

    fun removeCookie(key: String) {
        cookies.remove(key)
    }

    @Throws(IOException::class, ReCaptchaException::class)
    override fun execute(@Nonnull request: Request): Response {
        val httpMethod = request.httpMethod()
        val url = request.url()
        val headers = request.headers()
        val dataToSend = request.dataToSend()

        var requestBody: RequestBody? = null
        if (dataToSend != null) {
            // dataToSend may be a String or a ByteArray depending on caller. Handle both.
            requestBody =
                    when (dataToSend) {
                        is String ->
                                (dataToSend as String)
                                        .toByteArray(Charsets.UTF_8)
                                        .toRequestBody(null)
                        is ByteArray -> (dataToSend as ByteArray).toRequestBody(null)
                        else -> null
                    }
        }

        val requestBuilder =
                okhttp3.Request.Builder()
                        .method(httpMethod, requestBody)
                        .url(url)
                        .addHeader("User-Agent", USER_AGENT)

        val cookies = getCookies(url)
        Log.d("COOKIE", cookies)
        if (cookies.isNotEmpty()) {
            requestBuilder.addHeader("Cookie", cookies)
        }

        for ((headerName, headerValueList) in headers.entries) {
            if (headerValueList.size > 1) {
                requestBuilder.removeHeader(headerName)
                for (headerValue in headerValueList) requestBuilder.addHeader(
                        headerName,
                        headerValue
                )
            } else if (headerValueList.size == 1) {
                requestBuilder.header(headerName, headerValueList[0])
            }
        }

        val response = client.newCall(requestBuilder.build()).execute()

        if (response.code == 429) {
            response.close()
            throw ReCaptchaException("reCaptcha Challenge requested: $url", url)
        }

        val body: ResponseBody? = response.body
        var responseBodyToReturn: String? = null
        if (body != null) responseBodyToReturn = body.string()

        val latestUrl = response.request.url.toString()
        return Response(
                response.code,
                response.message,
                response.headers.toMultimap(),
                responseBodyToReturn,
                latestUrl
        )
    }

    @Throws(IOException::class)
    fun getContentLength(url: String): Long {
        try {
            val response = head(url)
            return java.lang.Long.parseLong(response.getHeader("Content-Length"))
        } catch (e: NumberFormatException) {
            throw IOException("Invalid content length", e)
        } catch (e: ReCaptchaException) {
            throw IOException(e)
        }
    }
}
