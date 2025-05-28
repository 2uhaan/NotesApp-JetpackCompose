package com.example.crudjetpackcompose.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.crudjetpackcompose.ui.theme.colorBlack
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crudjetpackcompose.ui.theme.colorGrey
import com.example.crudjetpackcompose.ui.theme.colorLightGrey
import com.example.crudjetpackcompose.ui.theme.colorRed
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.crudjetpackcompose.Models.Notes


@Composable
fun InsertNotesScreen(navHostController: NavHostController, id: String?) {

    val context = LocalContext.current

    val db = FirebaseFirestore.getInstance()
    val notesDBRef = db.collection("notes")

    val title = remember {
        mutableStateOf("")
    }

    val description = remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        if (id != "defaultId"){
            notesDBRef.document(id.toString()).get().addOnSuccessListener {
                val singleData = it.toObject(Notes::class.java)
                title.value = singleData!!.title
                description.value = singleData.description
            }
        }
    }

    Scaffold(floatingActionButton = {
        FloatingActionButton(

            onClick = {
                if (title.value.isEmpty() && description.value.isEmpty()){
                    Toast.makeText(context,"Enter valid data!", Toast.LENGTH_SHORT).show()
                }else{

                    val myNotesID = if (id != "defaultId"){
                        id.toString()
                    } else {
                        notesDBRef.document().id
                    }

                    val notes = Notes(
                        id = myNotesID,
                        title = title.value,
                        description = description.value,
                    )


                    notesDBRef.document(myNotesID).set(notes).addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(context, "Notes Inserted Successfully", Toast.LENGTH_SHORT).show()

                            navHostController.popBackStack()
                        } else {
                            Toast.makeText(context, "Something went wrong buddy", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            },
            contentColor = Color.Black,
            containerColor = Color.White,
            shape = RoundedCornerShape(corner = CornerSize(100.dp)),
        ) {
            Icon(imageVector = Icons.Default.Done, contentDescription = "addNotes")
        }
    }) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(color = colorBlack))
        {
            Column(modifier = Modifier.padding(15.dp)) {
                Text(
                    text = "Insert Data",
                    style = TextStyle(fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextField(textStyle = TextStyle(color = Color.White), colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorGrey,
                    unfocusedContainerColor = colorGrey,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ), shape = RoundedCornerShape(corner = CornerSize(10.dp)), label = {
                    Text(text = "Enter Title", style = TextStyle(fontSize = 18.sp, color = colorLightGrey))
                }, value = title.value, onValueChange = {
                    title.value = it
                }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(15.dp))

                TextField(textStyle = TextStyle(color = Color.White), colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorGrey,
                    unfocusedContainerColor = colorGrey,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ), shape = RoundedCornerShape(corner = CornerSize(10.dp)), label = {
                    Text(text = "Enter Content", style = TextStyle(fontSize = 18.sp, color = colorLightGrey))
                }, value = description.value, onValueChange = {
                    description.value = it
                }, modifier = Modifier.fillMaxWidth().fillMaxHeight(0.6f))

            }
        }
    }
}