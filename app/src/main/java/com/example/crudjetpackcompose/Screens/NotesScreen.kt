package com.example.crudjetpackcompose.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.crudjetpackcompose.Models.Notes
import com.example.crudjetpackcompose.ui.theme.colorBlack
import com.example.crudjetpackcompose.ui.theme.colorGrey
import com.example.crudjetpackcompose.ui.theme.colorLightGrey
import com.example.crudjetpackcompose.ui.theme.colorRed
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.firestore.CollectionReference
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.navigation.NavHostController
import com.example.crudjetpackcompose.Navigation.NotesNavigationItem


@Composable
fun NotesScreen(navHostController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    val notesDBRef = db.collection("notes")
    val notesList = remember { mutableStateListOf<Notes>() }
    val dataValue = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        notesDBRef.addSnapshotListener { value, error ->
            if (error == null){
                val data = value?.toObjects(Notes::class.java)
                notesList.clear()
                notesList.addAll(data!!)
                dataValue.value = true
            } else {
                dataValue.value = false
            }
        }
    }

    Scaffold(floatingActionButton = {
        FloatingActionButton(
            contentColor = Color.Black,
            containerColor = Color.White,
            shape = RoundedCornerShape(corner = CornerSize(100.dp)),
            onClick = {
                navHostController.navigate(NotesNavigationItem.InsertNoteScreen.route+"/defaultId")
            }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "addNotes")
        }
        }) {
        innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = colorBlack)
        ) {

            Column(modifier = Modifier.padding(15.dp)) {

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "My Notes Wall",
                    style = TextStyle(fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(15.dp))

                if (dataValue.value){
                    LazyColumn {
                        items(notesList) { notes ->
                            ListItems(notes, notesDBRef, navHostController)
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize()){
                        CircularProgressIndicator(modifier = Modifier.size(25.dp).align(Alignment.Center))
                    }
                }
            }
        }
    }
}



@Composable
fun ListItems(notes: Notes, notesDBRef: CollectionReference, navHostController: NavHostController) {

    val context = LocalContext.current

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .clip(shape = RoundedCornerShape(corner = CornerSize(20.dp)))
        .background(color = colorGrey)

    )
    {
        DropdownMenu(modifier = Modifier.background(color = Color.White),
            properties = PopupProperties(clippingEnabled = true),
            offset = DpOffset(x = (-40).dp, y = 0.dp),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }) {
            DropdownMenuItem(text = { Text(text = "Update", style = TextStyle(color = colorGrey))},
                onClick = {
                    navHostController.navigate(NotesNavigationItem.InsertNoteScreen.route+"/${notes.id}")
                    expanded = false
                })

            val showDialog = remember { mutableStateOf(false) }

            DropdownMenuItem(
                text = { Text(text = "Delete", style = TextStyle(color = colorGrey)) },
                onClick = {
                    showDialog.value = true
                }
            )
            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { Text("Confirm Delete") },
                    text = { Text("Are you sure you want to delete this note?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                notesDBRef.document(notes.id).delete()
                                showDialog.value = false
                            }
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDialog.value = false }
                        ) {
                            Text("No")
                        }
                    }
                )
            }


        }

        Icon(imageVector = Icons.Default.MoreVert,
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).clickable{
                expanded = true
            })

        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = notes.title, style = TextStyle(color = Color.White))
            Text(text = notes.description, style = TextStyle(color = colorLightGrey))
        }
    }
}