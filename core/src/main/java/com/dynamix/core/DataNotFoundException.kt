package com.dynamix.core

/**
 * Created by En Dra on 7/15/2019.
 */
class DataNotFoundException : Exception {

    constructor() : super() {}

    constructor(message: String?) : super(message) {}
}