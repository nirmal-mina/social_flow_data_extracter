package com.pipe.social_flow_data_extracter

import org.schabi.newpipe.extractor.exceptions.ParsingException
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeChannelLinkHandlerFactory
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubePlaylistLinkHandlerFactory
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeStreamLinkHandlerFactory

object YoutubeLinkHandler {

    @JvmStatic
    fun getIdFromStreamUrl(url: String?): String? {
        var parsedUrl: String? = null
        val streamLinkHandler = YoutubeStreamLinkHandlerFactory.getInstance()
        try {
            parsedUrl = streamLinkHandler.fromUrl(url).id
        } catch (_: ParsingException) {}
        return parsedUrl
    }

    @JvmStatic
    fun getIdFromPlaylistUrl(url: String?): String? {
        var parsedUrl: String? = null
        val playlistLinkHandler = YoutubePlaylistLinkHandlerFactory.getInstance()
        try {
            parsedUrl = playlistLinkHandler.fromUrl(url).id
        } catch (_: ParsingException) {}
        return parsedUrl
    }

    @JvmStatic
    fun getIdFromChannelUrl(url: String?): String? {
        var parsedUrl: String? = null
        val channelLinkHandler = YoutubeChannelLinkHandlerFactory.getInstance()
        try {
            parsedUrl = channelLinkHandler.fromUrl(url).id
        } catch (_: ParsingException) {}
        return parsedUrl
    }
}
