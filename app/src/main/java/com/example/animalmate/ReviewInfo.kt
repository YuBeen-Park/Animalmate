package com.example.animalmate

data class ReviewInfo (var ID : String, var content : String, var rating : Float){
    constructor():this("", "nocontent", 0.0f)
}