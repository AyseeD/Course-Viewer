package com.app.course_viewer.model

//data class for course
class Course(
    //required values for a course
    val code: String,
    val name:String,
    val credits: Int,
    val semester: String,
    //I added this to make the app look more realistic
    val description: String
)