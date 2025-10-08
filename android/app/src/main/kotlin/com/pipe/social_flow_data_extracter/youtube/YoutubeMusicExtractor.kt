package com.pipe.social_flow_data_extracter

import android.os.Build
import com.google.gson.Gson
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Collections.singletonList
import java.util.stream.Collectors
import org.schabi.newpipe.extractor.Image
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.ListExtractor
import org.schabi.newpipe.extractor.channel.ChannelInfoItem
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem
import org.schabi.newpipe.extractor.search.SearchExtractor
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory
import org.schabi.newpipe.extractor.stream.StreamInfoItem

object YoutubeMusicExtractor {

    private var extractor: SearchExtractor? = null
    private var itemsPage: org.schabi.newpipe.extractor.ListExtractor.InfoItemsPage<InfoItem>? =
            null

    @JvmStatic
    @Throws(Exception::class)
    fun searchYoutube(
            query: String?,
            filters: List<String>?
    ): Map<String, Map<Int, Map<String, String>>> {
        val contentFilter: MutableList<String> =
                ArrayList(singletonList(YoutubeSearchQueryHandlerFactory.MUSIC_SONGS))
        if (filters != null) contentFilter.addAll(filters)
        extractor =
                org.schabi.newpipe.extractor.ServiceList.YouTube.getSearchExtractor(
                        query,
                        contentFilter,
                        ""
                )
        extractor!!.fetchPage()
        itemsPage = extractor!!.initialPage
        val items: List<InfoItem> = itemsPage!!.items
        return _fetchResultsFromItems(items)
    }

    @JvmStatic
    @Throws(Exception::class)
    fun getNextPage(): Map<String, Map<Int, Map<String, String>>> {
        if (itemsPage!!.hasNextPage()) {
            itemsPage = extractor!!.getPage(itemsPage!!.nextPage)
            val items: List<InfoItem> = itemsPage!!.items
            return _fetchResultsFromItems(items)
        } else {
            return HashMap()
        }
    }

    private fun _fetchResultsFromItems(
            items: List<InfoItem>
    ): Map<String, Map<Int, Map<String, String>>> {
        val streamsList: MutableList<StreamInfoItem> = ArrayList()
        val playlistsList: MutableList<PlaylistInfoItem> = ArrayList()
        val channelsList: MutableList<ChannelInfoItem> = ArrayList()
        val resultsList: MutableMap<String, Map<Int, Map<String, String>>> = HashMap()
        for (i in items.indices) {
            when (items[i].infoType) {
                org.schabi.newpipe.extractor.InfoItem.InfoType.STREAM ->
                        streamsList.add(items[i] as StreamInfoItem)
                org.schabi.newpipe.extractor.InfoItem.InfoType.CHANNEL ->
                        channelsList.add(items[i] as ChannelInfoItem)
                org.schabi.newpipe.extractor.InfoItem.InfoType.PLAYLIST ->
                        playlistsList.add(items[i] as PlaylistInfoItem)
                else -> {}
            }
        }

        val streamResultsMap: MutableMap<Int, Map<String, String>> = HashMap()
        if (streamsList.isNotEmpty()) {
            for (i in streamsList.indices) {
                val item = streamsList[i]
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
                streamResultsMap[i] = itemMap
            }
        }
        resultsList["streams"] = streamResultsMap

        val channelResultsMap: MutableMap<Int, Map<String, String>> = HashMap()
        if (channelsList.isNotEmpty()) {
            for (i in channelsList.indices) {
                val item = channelsList[i]
                val itemMap: MutableMap<String, String> = HashMap()
                itemMap["name"] = item.name
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    itemMap["thumbnails"] =
                            Gson().toJson(
                                            item.thumbnails
                                                    .stream()
                                                    .map(Image::getUrl)
                                                    .collect(Collectors.toList())
                                    )
                }
                itemMap["url"] = item.url
                itemMap["id"] = YoutubeLinkHandler.getIdFromChannelUrl(item.url) ?: ""
                itemMap["description"] = item.description
                itemMap["streamCount"] = item.streamCount.toString()
                itemMap["subscriberCount"] = item.subscriberCount.toString()
                channelResultsMap[i] = itemMap
            }
        }
        resultsList["channels"] = channelResultsMap

        val playlistResultsMap: MutableMap<Int, Map<String, String>> = HashMap()
        if (playlistsList.isNotEmpty()) {
            for (i in playlistsList.indices) {
                val item = playlistsList[i]
                val itemMap: MutableMap<String, String> = HashMap()
                itemMap["name"] = item.name
                itemMap["uploaderName"] = item.uploaderName
                itemMap["url"] = item.url
                itemMap["id"] = YoutubeLinkHandler.getIdFromPlaylistUrl(item.url) ?: ""
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    itemMap["thumbnails"] =
                            Gson().toJson(
                                            item.thumbnails
                                                    .stream()
                                                    .map(Image::getUrl)
                                                    .collect(Collectors.toList())
                                    )
                }
                itemMap["streamCount"] = item.streamCount.toString()
                playlistResultsMap[i] = itemMap
            }
        }
        resultsList["playlists"] = playlistResultsMap
        return resultsList
    }
}
