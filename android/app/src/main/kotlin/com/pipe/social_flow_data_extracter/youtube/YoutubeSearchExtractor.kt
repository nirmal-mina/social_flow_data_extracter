package com.pipe.social_flow_data_extracter

import java.util.*
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.ListExtractor
import org.schabi.newpipe.extractor.search.SearchExtractor

object YoutubeSearchExtractor {

    private var extractor: SearchExtractor? = null
    private var itemsPage: org.schabi.newpipe.extractor.ListExtractor.InfoItemsPage<InfoItem>? =
            null

    @JvmStatic
    @Throws(Exception::class)
    fun searchYoutube(
            query: String?,
            filters: List<String>?
    ): Map<String, Map<Int, Map<String, String>>> {
        extractor =
                org.schabi.newpipe.extractor.ServiceList.YouTube.getSearchExtractor(
                        query,
                        filters,
                        ""
                )
        extractor!!.fetchPage()
        itemsPage = extractor!!.initialPage
        val items: List<InfoItem> = itemsPage!!.items
        return FetchData.fetchInfoItems(items)
    }

    @JvmStatic
    @Throws(Exception::class)
    fun getNextPage(): Map<String, Map<Int, Map<String, String>>> {
        return if (itemsPage!!.hasNextPage()) {
            itemsPage = extractor!!.getPage(itemsPage!!.nextPage)
            val items: List<InfoItem> = itemsPage!!.items
            FetchData.fetchInfoItems(items)
        } else {
            HashMap()
        }
    }
}
