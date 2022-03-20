package com.dynamix.core.event

enum class DynamixActionConstants(val type: String) {
    TERMS_AND_CONDITION("termsAndCondition"),
    IMAGE_UPLOAD("imageUpload"),
    BOOKING("booking"),
    CONFIRMATION("confirmation"),
    AUTHENTICATION("authentication"),
    INTERMEDIATE_EVENT("intermediateEvent"),
    TRANSACTION("transaction"),
    LOAD_FORM("loadForm")
}