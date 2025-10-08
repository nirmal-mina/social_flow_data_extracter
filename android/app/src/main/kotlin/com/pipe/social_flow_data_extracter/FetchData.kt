package com.pipe.social_flow_data_extracter

import android.os.Build
import com.google.gson.Gson
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors
import org.schabi.newpipe.extractor.Image
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.channel.ChannelInfoItem
import org.schabi.newpipe.extractor.exceptions.ParsingException
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem
import org.schabi.newpipe.extractor.stream.AudioStream
import org.schabi.newpipe.extractor.stream.StreamExtractor
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import org.schabi.newpipe.extractor.stream.StreamSegment
import org.schabi.newpipe.extractor.stream.VideoStream

object FetchData {

    @JvmStatic
    fun fetchVideoInfo(extractor: StreamExtractor): Map<String, String> {
        val videoInformationMap: MutableMap<String, String> = HashMap()
        try {
            videoInformationMap.put("id", extractor.id ?: "")
        } catch (_: ParsingException) {}
        try {
            videoInformationMap.put("url", extractor.url ?: "")
        } catch (_: ParsingException) {}
        try {
            videoInformationMap.put("name", extractor.name ?: "")
        } catch (_: ParsingException) {}
        try {
            videoInformationMap.put("uploaderName", extractor.uploaderName ?: "")
        } catch (_: ParsingException) {}
        try {
            videoInformationMap.put("uploaderUrl", extractor.uploaderUrl ?: "")
        } catch (_: ParsingException) {}
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                videoInformationMap.put(
                        "uploaderAvatars",
                        Gson().toJson(
                                        extractor
                                                .uploaderAvatars
                                                .stream()
                                                .map(Image::getUrl)
                                                .collect(Collectors.toList())
                                )
                )
            }
        } catch (_: ParsingException) {}
        try {
            videoInformationMap.put("uploadDate", extractor.textualUploadDate ?: "")
        } catch (_: ParsingException) {}
        try {
            videoInformationMap.put("description", extractor.description?.content ?: "")
        } catch (_: ParsingException) {}
        try {
            videoInformationMap.put("length", extractor.length.toString())
        } catch (_: ParsingException) {}
        try {
            videoInformationMap.put("viewCount", extractor.viewCount.toString())
        } catch (_: ParsingException) {}
        try {
            videoInformationMap.put("likeCount", extractor.likeCount.toString())
        } catch (_: ParsingException) {}
        try {
            videoInformationMap.put("dislikeCount", extractor.dislikeCount.toString())
        } catch (_: ParsingException) {}
        try {
            videoInformationMap.put("category", extractor.category ?: "")
        } catch (_: ParsingException) {}
        try {
            videoInformationMap.put("ageLimit", extractor.ageLimit.toString())
        } catch (_: ParsingException) {}
        try {
            videoInformationMap.put("tags", extractor.tags.toString())
        } catch (_: ParsingException) {}
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                videoInformationMap.put(
                        "thumbnails",
                        Gson().toJson(
                                        extractor
                                                .thumbnails
                                                .stream()
                                                .map(Image::getUrl)
                                                .collect(Collectors.toList())
                                )
                )
            }
        } catch (_: ParsingException) {}
        return videoInformationMap
    }

    @JvmStatic
    fun fetchAudioStreamInfo(stream: AudioStream): Map<String, String> {
        val streamMap: MutableMap<String, String> = HashMap()
        streamMap["torrentUrl"] = stream.content ?: ""
        streamMap["url"] = stream.content ?: ""
        streamMap["averageBitrate"] = stream.averageBitrate.toString()
        stream.format?.let {
            streamMap["formatName"] = it.name ?: ""
            streamMap["formatSuffix"] = it.suffix ?: ""
            streamMap["formatMimeType"] = it.mimeType ?: ""
        }
        return streamMap
    }

    @JvmStatic
    fun fetchVideoStreamInfo(stream: VideoStream): Map<String, String> {
        val streamMap: MutableMap<String, String> = HashMap()
        streamMap["torrentUrl"] = stream.content ?: ""
        streamMap["url"] = stream.content ?: ""
        streamMap["resolution"] = stream.resolution ?: ""
        stream.format?.let {
            streamMap["formatName"] = it.name ?: ""
            streamMap["formatSuffix"] = it.suffix ?: ""
            streamMap["formatMimeType"] = it.mimeType ?: ""
        }
        return streamMap
    }

    @JvmStatic
    fun fetchPlaylistInfoItem(item: PlaylistInfoItem): Map<String, String> {
        val itemMap: MutableMap<String, String> = HashMap()
        itemMap["name"] = item.name ?: ""
        itemMap["uploaderName"] = item.uploaderName ?: ""
        itemMap["url"] = item.url ?: ""
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
        return itemMap
    }

    @JvmStatic
    fun fetchStreamInfoItem(item: StreamInfoItem): Map<String, String> {
        val itemMap: MutableMap<String, String> = HashMap()
        itemMap["name"] = item.name ?: ""
        itemMap["uploaderName"] = item.uploaderName ?: ""
        itemMap["uploaderUrl"] = item.uploaderUrl ?: ""
        itemMap["uploadDate"] = item.textualUploadDate ?: ""
        try {
            item.uploadDate?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    itemMap["date"] = it.offsetDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
                } else {
                    itemMap["date"] = ""
                }
            }
                    ?: run { itemMap["date"] = "" }
        } catch (_: NullPointerException) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            itemMap["uploaderAvatars"] =
                    Gson().toJson(
                                    item.uploaderAvatars
                                            .stream()
                                            .map(Image::getUrl)
                                            .collect(Collectors.toList())
                            )
        }
        itemMap["duration"] = item.duration.toString()
        itemMap["viewCount"] = item.viewCount.toString()
        itemMap["url"] = item.url ?: ""
        itemMap["id"] = YoutubeLinkHandler.getIdFromStreamUrl(item.url) ?: ""
        return itemMap
    }

    @JvmStatic
    fun fetchStreamSegment(segment: StreamSegment): Map<String, String> {
        val itemMap: MutableMap<String, String> = HashMap()
        itemMap["url"] = segment.url ?: ""
        itemMap["title"] = segment.title ?: ""
        itemMap["previewUrl"] = segment.previewUrl ?: ""
        itemMap["startTimeSeconds"] = segment.startTimeSeconds.toString()
        return itemMap
    }

    @JvmStatic
    fun fetchInfoItems(items: List<InfoItem>): Map<String, Map<Int, Map<String, String>>> {
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
                streamResultsMap[i] = fetchStreamInfoItem(item)
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
                playlistResultsMap[i] = fetchPlaylistInfoItem(item)
            }
        }
        resultsList["playlists"] = playlistResultsMap
        return resultsList
    }
}
