package com.us4ever.viderapro

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.UIKit.UIPasteboard

actual fun getPlatformDateString(seconds: Long): String {
    val date = NSDate(seconds.toDouble())
    val formatter = NSDateFormatter()
    formatter.dateFormat = "yyyy-MM-dd"
    return formatter.stringFromDate(date)
}

actual fun copyToClipboard(text: String?) {
    if (text != null) {
        UIPasteboard.generalPasteboard.string = text
    }
}
