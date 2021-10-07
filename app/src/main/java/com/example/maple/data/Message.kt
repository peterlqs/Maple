package com.example.maple.data

// This is like a data class but for some reason the guy made it like this
class Message {
    var message: String? = null
    var user: String? = null

    constructor() {}

    //Setter I suppose
    constructor(message: String?, user: String?) {
        this.message = message
        this.user = user
    }

}