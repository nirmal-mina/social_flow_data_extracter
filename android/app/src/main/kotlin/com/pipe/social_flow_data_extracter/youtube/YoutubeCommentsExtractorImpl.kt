package com.pipe.social_flow_data_extracter

import android.os.Build
import com.google.gson.Gson
import java.util.*
import java.util.stream.Collectors
import org.schabi.newpipe.extractor.Image
import org.schabi.newpipe.extractor.ListExtractor
import org.schabi.newpipe.extractor.comments.CommentsInfoItem
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeCommentsExtractor

object YoutubeCommentsExtractorImpl {

    @JvmStatic
    @Throws(Exception::class)
    fun getComments(url: String?): Map<Int, Map<String, String>> {
        val extractor =
                org.schabi.newpipe.extractor.ServiceList.YouTube.getCommentsExtractor(url) as
                        YoutubeCommentsExtractor
        extractor.fetchPage()
        val commentsMap: MutableMap<Int, Map<String, String>> = HashMap()
        val commentsInfo: ListExtractor.InfoItemsPage<CommentsInfoItem> = extractor.initialPage
        val comments: List<CommentsInfoItem> = commentsInfo.items
        for (i in comments.indices) {
            val comment = comments[i]
            val commentMap: MutableMap<String, String> = HashMap()
            commentMap["commentId"] = comment.commentId
            commentMap["author"] = comment.uploaderName
            commentMap["commentText"] = comment.commentText.content
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                commentMap["uploaderAvatars"] =
                        Gson().toJson(
                                        comment.uploaderAvatars
                                                .stream()
                                                .map(Image::getUrl)
                                                .collect(Collectors.toList())
                                )
            }
            commentMap["uploadDate"] = comment.textualUploadDate
            commentMap["uploaderUrl"] = comment.uploaderUrl
            commentMap["likeCount"] = comment.likeCount.toString()
            commentMap["pinned"] = comment.isPinned.toString()
            commentMap["hearted"] = comment.isHeartedByUploader.toString()
            commentsMap[i] = commentMap
        }
        return commentsMap
    }
}
