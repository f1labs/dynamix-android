package com.dynamix.core.utils

import java.io.File

interface JavaScriptInterfaceProvider {

    fun getBase64FromBlobData(base64Data: String)

    fun convertBase64StringToPdfAndStoreIt(base64PDf: String)

    val file: File

    fun getBase64StringFromBlobUrl(blobUrl: String): String
}