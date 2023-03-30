package com.example.medicui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medicui.ui.theme.MedicUITheme
import java.time.LocalDateTime
import org.json.JSONObject
import java.time.Instant
import java.time.Duration

//global array to store the list items, each item will be a dictionary
var rows = mutableListOf<MutableMap<String, String>>()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get a reference to the AssetManager
        val assetsManager = applicationContext.assets

        // Open the JSON file and read its contents
        val json = try {
            assetsManager.open("input.json").bufferedReader().use { it.readText() }
        }
        catch (e: Exception) {
            e.printStackTrace()
            ""
        }

        // Parse the JSON data using a JSON parsing library of your choice
        // For example, using org.json
        val jsonObject = JSONObject(json)

        //iterate over the JSON object and get the values
        val medications = jsonObject.getJSONArray("medication")
        //iterate over the medications array
        for (i in 0 until medications.length()) {
            //get the medication object
            val medication = medications.getJSONObject(i)
            //get the medication name
            val name = medication.getString("name")
            //get the medication dosage
            val dosage = medication.getString("dosage")
            //get the medication frequency
            val frequency = medication.getString("frequency")
            //parse the start_time to a LocalDateTime object
            var startTime = Instant.parse(medication.getString("start_time"))

            //get the medication time which is an integer
            val times = medication.getInt("times")
            //iterate times times
            for (j in 0 until times) {
                //create a dictionary to store the values
                val row = mutableMapOf<String, String>()
                //add the values to the dictionary
                row["name"] = name
                row["dosage"] = dosage
                startTime = startTime.plus(Duration.parse(frequency))
                row["time"] = startTime.toString()

                //add the dictionary to the array
                rows.add(row)
            }
        }

        //sort the array by the time
        rows.sortBy { it["time"] }
        //print the array to debug
        println(rows)


        setContent {
            MedicUITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MedApp()
                }
            }
        }
    }
}

@Composable
fun MedApp() {
    // State variable for email
    var email by remember { mutableStateOf("") }
    // State list for medications
    val medications = remember { mutableStateListOf<String>() }

    // Scaffold for the app layout
    Scaffold(
        topBar = {
            // App bar with title
            TopAppBar(title = { Text("Medication App") })
        },
        content = { padding ->
            // Content with padding and scrollable
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Text field for email with label and placeholder
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("Enter your email") }
                )
                // For each medication in the list, display a text field with label and placeholder
                medications.forEachIndexed { index, medication ->
                    OutlinedTextField(
                        value = medication,
                        onValueChange = { medications[index] = it },
                        label = { Text("Medication ${index + 1}") },
                        placeholder = { Text("Enter your medication data") }
                    )
                }
                // Button to add a new medication to the list
                Button(onClick = {
                    medications.add("")
                }) {
                    Text("Add Medication")
                }
                // Button to submit the data
                Button(onClick = {
//                    // Get the Python instance
//                    val python = Python.getInstance()
//                    // Get the Python module that contains the function you want to call
//                    val module = python.getModule("your_python_module")
//                    // Call the function with the email and medications as arguments
//                    module.callAttr("your_python_function", email.text, medications.map { it.text })
                }) {
                    Text("Submit")
                }
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MedicUITheme {
        MedApp()
    }
}