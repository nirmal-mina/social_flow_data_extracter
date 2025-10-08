package com.pipe.social_flow_data_extracter.downloader

import android.text.TextUtils

object CookieUtils {
    @JvmStatic
    fun concatCookies(cookieStrings: Collection<String>): String {
        val cookieSet: MutableSet<String> = HashSet()
        for (cookies in cookieStrings) {
            cookieSet.addAll(splitCookies(cookies))
        }
        return TextUtils.join("; ", cookieSet).trim()
    }

    @JvmStatic
    fun splitCookies(cookies: String): Set<String> {
        return cookies.split(Regex("; *")).map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }
}
