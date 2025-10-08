package com.pipe.social_flow_data_extracter

import android.os.Build
import com.google.gson.Gson
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors
import org.schabi.newpipe.extractor.Image
import org.schabi.newpipe.extractor.playlist.PlaylistExtractor
import org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper
import org.schabi.newpipe.extractor.stream.StreamInfoItem

object YoutubePlaylistExtractorImpl {

    private var extractor: PlaylistExtractor? = null

    @JvmStatic
    @Throws(Exception::class)
    fun getPlaylistDetails(url: String?): Map<String, String> {
        YoutubeParsingHelper.resetClientVersion()
        YoutubeParsingHelper.setNumberGenerator(Random(1))
        extractor = org.schabi.newpipe.extractor.ServiceList.YouTube.getPlaylistExtractor(url)
        extractor!!.fetchPage()
        val playlistDetails: MutableMap<String, String> = HashMap()
        playlistDetails["name"] = extractor!!.name
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            playlistDetails["thumbnails"] =
                    Gson().toJson(
                                    extractor!!
                                            .thumbnails
                                            .stream()
                                            .map(Image::getUrl)
                                            .collect(Collectors.toList())
                            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            playlistDetails["banners"] =
                    Gson().toJson(
                                    extractor!!
                                            .banners
                                            .stream()
                                            .map(Image::getUrl)
                                            .collect(Collectors.toList())
                            )
        }
        try {
            playlistDetails["uploaderName"] = extractor!!.uploaderName
        } catch (e: Exception) {
            playlistDetails["uploaderName"] = "Unknown"
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                playlistDetails["uploaderAvatars"] =
                        Gson().toJson(
                                        extractor!!
                                                .uploaderAvatars
                                                .stream()
                                                .map(Image::getUrl)
                                                .collect(Collectors.toList())
                                )
            }
        } catch (e: Exception) {
            playlistDetails["uploaderAvatars"] = ""
        }
        try {
            playlistDetails["uploaderUrl"] = extractor!!.uploaderUrl
        } catch (e: Exception) {
            playlistDetails["uploaderUrl"] = ""
        }
        playlistDetails["streamCount"] = extractor!!.streamCount.toString()
        playlistDetails["id"] = extractor!!.id
        playlistDetails["url"] = extractor!!.url
        return playlistDetails
    }

    @JvmStatic
    @Throws(Exception::class)
    fun getPlaylistStreams(url: String?): Map<Int, Map<String, String>> {
        extractor = org.schabi.newpipe.extractor.ServiceList.YouTube.getPlaylistExtractor(url)
        extractor!!.fetchPage()
        val items: List<StreamInfoItem> = extractor!!.initialPage.items
        return _fetchResultsFromItems(items)
    }

    private fun _fetchResultsFromItems(items: List<StreamInfoItem>): Map<Int, Map<String, String>> {
        val playlistResults: MutableMap<Int, Map<String, String>> = HashMap()
        for (i in items.indices) {
            val itemMap: MutableMap<String, String> = HashMap()
            val item = items[i]
            itemMap["name"] = item.name ?: ""
            itemMap["uploaderName"] = item.uploaderName ?: ""
            itemMap["uploaderUrl"] = item.uploaderUrl ?: ""
            itemMap["uploadDate"] = item.textualUploadDate ?: ""
            try {
                val upload = item.uploadDate
                if (upload != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        itemMap["date"] =
                                upload.offsetDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
                    } else {
                        itemMap["date"] = ""
                    }
                } else {
                    itemMap["date"] = ""
                }
            } catch (ignore: Exception) {
                itemMap["date"] = ""
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                itemMap["thumbnails"] =
                        Gson().toJson(
                                        item.thumbnails
                                                .stream()
                                                .map(Image::getUrl)
                                                .collect(Collectors.toList())
                                )
            }
            itemMap["duration"] = item.duration.toString()
            itemMap["viewCount"] = item.viewCount.toString()
            itemMap["url"] = item.url ?: ""
            itemMap["id"] = YoutubeLinkHandler.getIdFromStreamUrl(item.url) ?: ""
            playlistResults[i] = itemMap
        }
        return playlistResults
    }
}
