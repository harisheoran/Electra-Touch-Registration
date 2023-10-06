package com.sparrowbit.electratouchregistration

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.sparrowbit.electratouchregistration.model.User
import com.sparrowbit.electratouchregistration.ui.theme.ElectraTouchRegistrationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElectraTouchRegistrationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainUI(context = LocalContext.current)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUI(modifier: Modifier = Modifier, context: Context) {
    val nameText = remember {
        mutableStateOf(TextFieldValue())
    }
    val ageText = remember {
        mutableStateOf(TextFieldValue())
    }
    val idText = remember {
        mutableStateOf(TextFieldValue())
    }
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var gender by remember {
        mutableStateOf("")
    }
    var buttonEnable by remember {
        mutableStateOf(true)
    }

    fun reset() {
        nameText.value
        ageText
    }

    Column(
        modifier = Modifier
            .padding(top = 32.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {

            TextField(
                value = nameText.value,
                label = { Text(text = "Name") },
                placeholder = { Text(text = "Enter Name") },
                onValueChange = { enteredValue ->
                    nameText.value = enteredValue
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
            )
        }
        Row(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
            TextField(
                value = ageText.value,
                label = { Text(text = "Age") },
                placeholder = { Text(text = "Enter Age") },
                onValueChange = { enteredValue ->
                    ageText.value = enteredValue
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Search
                ),
            )
        }
        Row(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
            TextField(
                value = idText.value,
                label = { Text(text = "ID") },
                placeholder = { Text(text = "Enter fingerprint ID") },
                onValueChange = { enteredValue ->
                    idText.value = enteredValue
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Search
                ),
            )
        }
        Row(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = {
                    isExpanded = it
                }
            ) {
                TextField(
                    value = gender,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                    },
                    placeholder = {
                        Text(text = "Select Gender")
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = {
                        isExpanded = false
                    }) {
                    DropdownMenuItem(
                        text = { Text(text = "Male") },
                        onClick = {
                            gender = "Male"
                            isExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = "Female") },
                        onClick = {
                            gender = "Female"
                            isExpanded = false
                        }
                    )
                }
            }
        }
        Row(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
            Button(
                enabled = buttonEnable,
                onClick = {
                    if (buttonEnable) {
                        if (TextUtils.isEmpty(nameText.value.text.toString())) {
                            Toast.makeText(context, "Please enter user name", Toast.LENGTH_SHORT)
                                .show()
                        } else if (TextUtils.isEmpty(ageText.value.text.toString())) {
                            Toast.makeText(context, "Please enter user age", Toast.LENGTH_SHORT)
                                .show()
                        } else if (TextUtils.isEmpty(idText.value.text.toString())) {
                            Toast.makeText(
                                context,
                                "Please enter fingerprint ID",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else if (TextUtils.isEmpty(gender.toString())) {
                            Toast.makeText(context, "Please select gender", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            buttonEnable = false
                            addDataToFirebase(
                                name = nameText.value.text,
                                age = ageText.value.text.toInt(),
                                gender = gender,
                                fingerID = idText.value.text.toInt(),
                                context = context,
                                onComplete = {
                                    buttonEnable = true
                                    nameText.value = TextFieldValue("")
                                    ageText.value = TextFieldValue()
                                    idText.value = TextFieldValue()
                                    gender = ""
                                }

                            )


                        }
                    }
                }, modifier = Modifier
            ) {
                Text(text = if (buttonEnable) "Save Data" else "Saving Data")
            }
        }
    }
}

fun addDataToFirebase(
    name: String,
    age: Int,
    gender: String,
    fingerID: Int,
    context: Context,
    onComplete: () -> Unit
) {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val userCollection: CollectionReference = db.collection("users")
    val users = User(name = name, age = age, gender = gender, fingerID = fingerID)


    userCollection.add(users).addOnSuccessListener {
        Toast.makeText(
            context,
            "User Data has been successfully added to database",
            Toast.LENGTH_SHORT
        ).show()
        onComplete()
        //return@addOnSuccessListener
    }.addOnFailureListener {
        Toast.makeText(context, "Fail to add user data to database", Toast.LENGTH_SHORT).show()
        //  return@addOnFailureListener
        onComplete
    }

}


/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropDownMenu() {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    var gender by remember {
        mutableStateOf("")
    }


}
 */