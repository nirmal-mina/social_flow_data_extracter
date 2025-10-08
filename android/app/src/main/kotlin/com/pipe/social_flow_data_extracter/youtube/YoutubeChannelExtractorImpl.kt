package com.pipe.social_flow_data_extracter

import android.os.Build
import com.google.gson.Gson
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors
import org.schabi.newpipe.extractor.Image
import org.schabi.newpipe.extractor.feed.FeedExtractor
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeChannelExtractor
import org.schabi.newpipe.extractor.stream.StreamInfoItem

object YoutubeChannelExtractorImpl {

    private var extractor: YoutubeChannelExtractor? = null
    private var feedExtractor: FeedExtractor? = null
    private var currentPage:
            org.schabi.newpipe.extractor.ListExtractor.InfoItemsPage<StreamInfoItem>? =
            null

    @JvmStatic
    @Throws(Exception::class)
    fun getChannel(url: String?): Map<String, String> {
        extractor =
                org.schabi.newpipe.extractor.ServiceList.YouTube.getChannelExtractor(url) as
                        YoutubeChannelExtractor
        extractor!!.fetchPage()
        val channelMap: MutableMap<String, String> = HashMap()
        channelMap["url"] = extractor!!.url ?: ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            channelMap["avatars"] =
                    Gson().toJson(
                                    extractor!!
                                            .avatars
                                            .stream()
                                            .map(Image::getUrl)
                                            .collect(Collectors.toList())
                            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            channelMap["banners"] =
                    Gson().toJson(
                                    extractor!!
                                            .banners
                                            .stream()
                                            .map(Image::getUrl)
                                            .collect(Collectors.toList())
                            )
        }
        channelMap["description"] = extractor!!.description ?: ""
        channelMap["feedUrl"] = extractor!!.feedUrl ?: ""
        channelMap["id"] = extractor!!.id ?: ""
        channelMap["name"] = extractor!!.name ?: ""
        channelMap["subscriberCount"] = extractor!!.subscriberCount.toString()
        return channelMap
    }

    @JvmStatic
    @Throws(Exception::class)
    fun getChannelUploads(url: String?): Map<Int, Map<String, String>> {
        extractor =
                org.schabi.newpipe.extractor.ServiceList.YouTube.getChannelExtractor(url) as
                        YoutubeChannelExtractor
        extractor!!.fetchPage()
        feedExtractor =
                org.schabi.newpipe.extractor.ServiceList.YouTube.getFeedExtractor(extractor!!.url)
        feedExtractor!!.fetchPage()
        currentPage = feedExtractor!!.initialPage
        val items: List<StreamInfoItem> = currentPage!!.items
        return parseData(items)
    }

    @JvmStatic
    @Throws(Exception::class)
    fun getChannelNextPage(): Map<Int, Map<String, String>> {
        if (currentPage!!.hasNextPage()) {
            currentPage = feedExtractor!!.getPage(currentPage!!.nextPage)
            val items: List<StreamInfoItem> = currentPage!!.items
            return parseData(items)
        } else {
            return HashMap()
        }
    }

    @JvmStatic
    fun parseData(items: List<StreamInfoItem>): Map<Int, Map<String, String>> {
        val itemsMap: MutableMap<Int, Map<String, String>> = HashMap()
        for (i in items.indices) {
            val item = items[i]
            val itemMap: MutableMap<String, String> = HashMap()
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
                itemMap["thumbnailUrl"] =
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
            itemsMap[i] = itemMap
        }
        return itemsMap
    }
}
