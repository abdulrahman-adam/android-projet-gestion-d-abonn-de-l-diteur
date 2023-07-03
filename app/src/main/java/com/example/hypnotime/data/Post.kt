package com.example.hypnotime.data

data class Post(
    var titre : String,
    var description: String,
    var image:ByteArray
) {
    var id : Int = -1;
    constructor(id:Int, titre: String, description: String, image: ByteArray) :this(titre, description, image) {
        this.id = id;
    }
}
