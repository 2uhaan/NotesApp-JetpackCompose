package com.example.crudjetpackcompose.Navigation


sealed class NotesNavigationItem(val route: String) {

    object SplashScreen : NotesNavigationItem(route = "splash")
    object HomeScreen : NotesNavigationItem(route = "home")
    object InsertNoteScreen : NotesNavigationItem(route = "insert")
}