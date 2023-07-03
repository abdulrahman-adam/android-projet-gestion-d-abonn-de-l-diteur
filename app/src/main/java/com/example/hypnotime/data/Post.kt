package com.example.hypnotime.data

data class Post(
    var titre : String,
    var description: String,
    var image:ByteArray,
    var jaime:Int = 0
) {
    var id : Int = -1;
    constructor(id:Int, titre: String, description: String, image: ByteArray,jaime:Int) :this(titre, description, image,jaime) {
        this.id = id;
    }
}
