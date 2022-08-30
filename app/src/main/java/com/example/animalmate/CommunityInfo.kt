package com.example.animalmate

data class CommunityInfo (var id : String, var title : String, var content : String){
    constructor() : this("", "notitle", "nocontent")
}