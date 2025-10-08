package com.pipe.social_flow_data_extracter

import java.util.*
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.InfoItemExtractor
import org.schabi.newpipe.extractor.InfoItemsCollector
import org.schabi.newpipe.extractor.stream.AudioStream
import org.schabi.newpipe.extractor.stream.StreamExtractor
import org.schabi.newpipe.extractor.stream.StreamSegment
import org.schabi.newpipe.extractor.stream.VideoStream

object StreamExtractorImpl {

    @JvmStatic
    fun getInfo(url: String?): Map<String, String> {
        val extractor: StreamExtractor =
                org.schabi.newpipe.extractor.ServiceList.YouTube.getStreamExtractor(url)
        extractor.fetchPage()
        return FetchData.fetchVideoInfo(extractor)
    }

    @JvmStatic
    fun getStream(url: String?): List<Map<*, *>> {
        val extractor: StreamExtractor =
                org.schabi.newpipe.extractor.ServiceList.YouTube.getStreamExtractor(url)
        extractor.fetchPage()
        val listMaps: MutableList<Map<*, *>> = ArrayList()

        listMaps.add(FetchData.fetchVideoInfo(extractor))

        val audioOnlyStreamsMap: MutableMap<Int, Map<String, String>> = HashMap()
        val audioStreams: List<AudioStream> = extractor.audioStreams
        for (i in audioStreams.indices) {
            val audioStream = audioStreams[i]
            audioOnlyStreamsMap[i] = FetchData.fetchAudioStreamInfo(audioStream)
        }
        listMaps.add(audioOnlyStreamsMap)

        val videoOnlyStreamsMap: MutableMap<Int, Map<String, String>> = HashMap()
        val videoOnlyStreams: List<VideoStream> = extractor.videoOnlyStreams
        for (i in videoOnlyStreams.indices) {
            val videoOnlyStream = videoOnlyStreams[i]
            videoOnlyStreamsMap[i] = FetchData.fetchVideoStreamInfo(videoOnlyStream)
        }
        listMaps.add(videoOnlyStreamsMap)

        val videoStreamsMap: MutableMap<Int, Map<String, String>> = HashMap()
        val videoStreams: List<VideoStream> = extractor.videoStreams
        for (i in videoStreams.indices) {
            val videoStream = videoStreams[i]
            videoStreamsMap[i] = FetchData.fetchVideoStreamInfo(videoStream)
        }
        listMaps.add(videoStreamsMap)

        listMaps.add(fetchStreamSegments(extractor.streamSegments))

        return listMaps
    }

    @JvmStatic
    fun getMediaStreams(url: String?): List<Map<*, *>> {
        val extractor: StreamExtractor =
                org.schabi.newpipe.extractor.ServiceList.YouTube.getStreamExtractor(url)
        extractor.fetchPage()
        val listMaps: MutableList<Map<*, *>> = ArrayList()

        val audioOnlyStreamsMap: MutableMap<Int, Map<String, String>> = HashMap()
        val audioStreams: List<AudioStream> = extractor.audioStreams
        for (i in audioStreams.indices) {
            val audioStream = audioStreams[i]
            audioOnlyStreamsMap[i] = FetchData.fetchAudioStreamInfo(audioStream)
        }
        listMaps.add(audioOnlyStreamsMap)

        val videoOnlyStreamsMap: MutableMap<Int, Map<String, String>> = HashMap()
        val videoOnlyStreams: List<VideoStream> = extractor.videoOnlyStreams
        for (i in videoOnlyStreams.indices) {
            val videoOnlyStream = videoOnlyStreams[i]
            videoOnlyStreamsMap[i] = FetchData.fetchVideoStreamInfo(videoOnlyStream)
        }
        listMaps.add(videoOnlyStreamsMap)

        val videoStreamsMap: MutableMap<Int, Map<String, String>> = HashMap()
        val videoStreams: List<VideoStream> = extractor.videoStreams
        for (i in videoStreams.indices) {
            val videoStream = videoStreams[i]
            videoStreamsMap[i] = FetchData.fetchVideoStreamInfo(videoStream)
        }
        listMaps.add(videoStreamsMap)

        listMaps.add(fetchStreamSegments(extractor.streamSegments))

        return listMaps
    }

    @JvmStatic
    fun getVideoOnlyStreams(url: String?): Map<Int, Map<String, String>> {
        val extractor: StreamExtractor =
                org.schabi.newpipe.extractor.ServiceList.YouTube.getStreamExtractor(url)
        extractor.fetchPage()
        val videoOnlyStreamsMap: MutableMap<Int, Map<String, String>> = HashMap()
        val videoOnlyStreams: List<VideoStream> = extractor.videoOnlyStreams
        for (i in videoOnlyStreams.indices) {
            val videoOnlyStream = videoOnlyStreams[i]
            videoOnlyStreamsMap[i] = FetchData.fetchVideoStreamInfo(videoOnlyStream)
        }
        return videoOnlyStreamsMap
    }

    @JvmStatic
    fun getAudioOnlyStreams(url: String?): Map<Int, Map<String, String>> {
        val extractor: StreamExtractor =
                org.schabi.newpipe.extractor.ServiceList.YouTube.getStreamExtractor(url)
        extractor.fetchPage()
        val audioOnlyStreamsMap: MutableMap<Int, Map<String, String>> = HashMap()
        val audioStreams: List<AudioStream> = extractor.audioStreams
        for (i in audioStreams.indices) {
            val audioStream = audioStreams[i]
            audioOnlyStreamsMap[i] = FetchData.fetchAudioStreamInfo(audioStream)
        }
        return audioOnlyStreamsMap
    }

    @JvmStatic
    fun getMuxedStreams(url: String?): Map<Int, Map<String, String>> {
        val extractor: StreamExtractor =
                org.schabi.newpipe.extractor.ServiceList.YouTube.getStreamExtractor(url)
        extractor.fetchPage()
        val videoStreamsMap: MutableMap<Int, Map<String, String>> = HashMap()
        val videoStreams: List<VideoStream> = extractor.videoStreams
        for (i in videoStreams.indices) {
            val videoStream = videoStreams[i]
            videoStreamsMap[i] = FetchData.fetchVideoStreamInfo(videoStream)
        }
        return videoStreamsMap
    }

    @JvmStatic
    fun getRelatedStreams(url: String?): Map<String, Map<Int, Map<String, String>>> {
        val extractor: StreamExtractor =
                org.schabi.newpipe.extractor.ServiceList.YouTube.getStreamExtractor(url)
        extractor.fetchPage()
        val collector: InfoItemsCollector<out InfoItem, out InfoItemExtractor>? =
                extractor.relatedItems
        val items: List<InfoItem> = collector?.items as? List<InfoItem> ?: ArrayList()
        return FetchData.fetchInfoItems(items)
    }

    @JvmStatic
    fun getStreamSegments(url: String?): Map<Int, Map<String, String>> {
        val extractor: StreamExtractor =
                org.schabi.newpipe.extractor.ServiceList.YouTube.getStreamExtractor(url)
        extractor.fetchPage()
        return fetchStreamSegments(extractor.streamSegments)
    }

    @JvmStatic
    fun fetchStreamSegments(segments: List<StreamSegment>): Map<Int, Map<String, String>> {
        val itemsMap: MutableMap<Int, Map<String, String>> = HashMap()
        for (i in segments.indices) {
            val segment = segments[i]
            itemsMap[i] = FetchData.fetchStreamSegment(segment)
        }
        return itemsMap
    }
}
