package com.dynamix.formbuilder.image

import android.net.Uri
import java.io.InputStream

/**
 * Created by user on 12/11/20
 */
data class DynamixAttachment(var name: String, var inputStream: InputStream, var uri: Uri) {
    var tag: String? = null
}