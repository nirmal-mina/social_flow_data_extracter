package com.pipe.social_flow_data_extracter

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.webkit.CookieManager
import androidx.annotation.NonNull
import com.pipe.social_flow_data_extracter.downloader.DownloaderImpl
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.net.URLDecoder
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization

@Suppress("UNCHECKED_CAST")
@SuppressLint("ApplySharedPref")
class NewpipeextractorDartPlugin : FlutterPlugin, MethodChannel.MethodCallHandler {

    private var channel: MethodChannel? = null
    private var context: Context? = null

    private val searchExtractor = YoutubeSearchExtractor
    private val musicExtractor = YoutubeMusicExtractor
    private val channelExtractor = YoutubeChannelExtractorImpl

    private val PREFS_COOKIES_KEY = "prefs_cookies_key"

    override fun onAttachedToEngine(
            @NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding
    ) {
        context = flutterPluginBinding.applicationContext
        NewPipe.init(
                DownloaderImpl.getInstance(),
                Localization.fromLocale(Locale.getDefault()),
                ContentCountry(Locale.getDefault().country)
        )
        val preferences: SharedPreferences =
                context?.getSharedPreferences("newpipe_prefs", Context.MODE_PRIVATE)
                        ?: kotlin.run {
                            val appCtx = flutterPluginBinding.applicationContext
                            appCtx.getSharedPreferences("newpipe_prefs", Context.MODE_PRIVATE)
                        }
        val cookie = preferences.getString(PREFS_COOKIES_KEY, null)
        if (cookie != "") {
            DownloaderImpl.getInstance().setCookie(cookie!!)
        }
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "newpipeextractor_dart")
        channel?.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        val method = call.method
        val info: Array<MutableMap<String, Any?>> = arrayOf(HashMap())
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        val listMaps: Array<List<Any>> = arrayOf(ArrayList())

        executor.execute {
            try {
                when (method) {
                    "getChannel" -> {
                        val channelUrl: String? = call.argument("channelUrl")
                        try {
                            info[0] =
                                    channelExtractor.getChannel(channelUrl!!) as
                                            MutableMap<String, Any?>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "getIdFromStreamUrl" -> {
                        val streamUrl: String? = call.argument("streamUrl")
                        val id = YoutubeLinkHandler.getIdFromStreamUrl(streamUrl)
                        info[0]["id"] = id
                    }
                    "getIdFromPlaylistUrl" -> {
                        val playlistUrl: String? = call.argument("playlistUrl")
                        val id = YoutubeLinkHandler.getIdFromPlaylistUrl(playlistUrl)
                        info[0]["id"] = id
                    }
                    "getIdFromChannelUrl" -> {
                        val channelUrl: String? = call.argument("channelUrl")
                        val id = YoutubeLinkHandler.getIdFromChannelUrl(channelUrl)
                        info[0]["id"] = id
                    }
                    "getComments" -> {
                        val videoUrl: String? = call.argument("videoUrl")
                        try {
                            val res = YoutubeCommentsExtractorImpl.getComments(videoUrl!!)
                            info[0] = res as MutableMap<String, Any?>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "getVideoInfoAndStreams" -> {
                        val videoUrl: String? = call.argument("videoUrl")
                        try {
                            listMaps[0] = StreamExtractorImpl.getStream(videoUrl!!)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "getVideoInformation" -> {
                        val videoUrl: String? = call.argument("videoUrl")
                        try {
                            info[0] =
                                    StreamExtractorImpl.getInfo(videoUrl!!) as
                                            MutableMap<String, Any?>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "getAllVideoStreams" -> {
                        val videoUrl: String? = call.argument("videoUrl")
                        try {
                            listMaps[0] = StreamExtractorImpl.getMediaStreams(videoUrl!!)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "getVideoOnlyStreams" -> {
                        val videoUrl: String? = call.argument("videoUrl")
                        try {
                            info[0] =
                                    StreamExtractorImpl.getVideoOnlyStreams(videoUrl!!) as
                                            MutableMap<String, Any?>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "getAudioOnlyStreams" -> {
                        val videoUrl: String? = call.argument("videoUrl")
                        try {
                            info[0] =
                                    StreamExtractorImpl.getAudioOnlyStreams(videoUrl!!) as
                                            MutableMap<String, Any?>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "getVideoStreams" -> {
                        val videoUrl: String? = call.argument("videoUrl")
                        try {
                            info[0] =
                                    StreamExtractorImpl.getMuxedStreams(videoUrl!!) as
                                            MutableMap<String, Any?>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "getVideoSegments" -> {
                        val videoUrl: String? = call.argument("videoUrl")
                        try {
                            info[0] =
                                    StreamExtractorImpl.getStreamSegments(videoUrl!!) as
                                            MutableMap<String, Any?>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "searchYoutube" -> {
                        val query: String? = call.argument("query")
                        val filters: List<String>? = call.argument("filters")
                        try {
                            info[0] =
                                    searchExtractor.searchYoutube(query!!, filters!!) as
                                            MutableMap<String, Any?>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "getNextPage" -> {
                        try {
                            info[0] = searchExtractor.getNextPage() as MutableMap<String, Any?>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "searchYoutubeMusic" -> {
                        val query: String? = call.argument("query")
                        val filters: List<String>? = call.argument("filters")
                        try {
                            info[0] =
                                    musicExtractor.searchYoutube(query!!, filters!!) as
                                            MutableMap<String, Any?>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "getNextMusicPage" -> {
                        try {
                            info[0] = musicExtractor.getNextPage() as MutableMap<String, Any?>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "getPlaylistDetails" -> {
                        val playlistUrl: String? = call.argument("playlistUrl")
                        try {
                            info[0] =
                                    YoutubePlaylistExtractorImpl.getPlaylistDetails(
                                            playlistUrl!!
                                    ) as
                                            MutableMap<String, Any?>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "getPlaylistStreams" -> {
                        val playlistUrl: String? = call.argument("playlistUrl")
                        try {
                            info[0] =
                                    YoutubePlaylistExtractorImpl.getPlaylistStreams(
                                            playlistUrl!!
                                    ) as
                                            MutableMap<String, Any?>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "getChannelUploads" -> {
                        val channelUrl: String? = call.argument("channelUrl")
                        try {
                            info[0] =
                                    channelExtractor.getChannelUploads(channelUrl!!) as
                                            MutableMap<String, Any?>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "getChannelNextPage" -> {
                        try {
                            info[0] =
                                    channelExtractor.getChannelNextPage() as
                                            MutableMap<String, Any?>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "getRelatedStreams" -> {
                        val videoUrl: String? = call.argument("videoUrl")
                        try {
                            info[0] =
                                    StreamExtractorImpl.getRelatedStreams(videoUrl!!) as
                                            MutableMap<String, Any?>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "getTrendingStreams" -> {
                        try {
                            info[0] =
                                    YoutubeTrendingExtractorImpl.getTrendingPage() as
                                            MutableMap<String, Any?>
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "setCookie" -> {
                        val cookie: String? = call.argument("cookie")
                        if (cookie != null) {
                            DownloaderImpl.getInstance().setCookie(cookie)
                            val preferences =
                                    context?.getSharedPreferences(
                                            "newpipe_prefs",
                                            Context.MODE_PRIVATE
                                    )
                                            ?: context!!.getSharedPreferences(
                                                    "newpipe_prefs",
                                                    Context.MODE_PRIVATE
                                            )
                            preferences.edit().putString(PREFS_COOKIES_KEY, cookie).commit()
                            info[0]["status"] = "success"
                        } else {
                            info[0]["status"] = "failed"
                        }
                    }
                    "getCookieByUrl" -> {
                        val url: String? = call.argument("url")
                        try {
                            val cookie = CookieManager.getInstance().getCookie(url)
                            info[0]["cookie"] = cookie
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                    "decodeCookie" -> {
                        val cookie: String? = call.argument("cookie")
                        try {
                            info[0]["cookie"] = URLDecoder.decode(cookie, "UTF-8")
                        } catch (e: Exception) {
                            e.printStackTrace()
                            info[0]["error"] = e.message
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                info[0]["error"] = e.message
            }

            handler.post {
                if (useList(method)) {
                    result.success(listMaps[0])
                } else {
                    result.success(info[0])
                }
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
    }

    private fun useList(methodName: String): Boolean {
        val functions: MutableList<String> = ArrayList()
        functions.add("getVideoInfoAndStreams")
        functions.add("getAllVideoStreams")
        return functions.contains(methodName)
    }
}
