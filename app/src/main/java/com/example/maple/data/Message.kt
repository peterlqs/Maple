package com.example.maple.data

class Message {
    var message: String? = null
    var user: String? = null

    constructor() {}

    constructor(message: String?, user: String?) {
        this.message = message
        this.user = user
    }

}