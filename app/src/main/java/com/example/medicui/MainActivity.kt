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
                row["time"] = startTime.toString()
                startTime = startTime.plus(Duration.parse(frequency))

                //add the dictionary to the array
                rows.add(row)
            }
        }

        //sort the array by the time
        rows.sortBy { it["time"] }


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
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Medications", style = MaterialTheme.typography.h5)
        Text(text = "Today", style = MaterialTheme.typography.h6)
        for (row in rows) {
            //q: what is the !! operator?
            //a: https://kotlinlang.org/docs/reference/null-safety.html#the--operator
            
            MedCard(
                name = row["name"]!!,
                dosage = row["dosage"]!!,
                //format time to a readable format from the ISO 8601 format
                time = Instant.parse(row["time"]!!).toString(),
                expanded = expanded
            )
        }
        Button(onClick = { expanded = !expanded }) {
            Text("Toggle")
        }
    }
}

@Composable
fun MedCard(name: String, dosage: String, time: String, expanded: Boolean) {
    Card(
        modifier = Modifier.padding(4.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = name, style = MaterialTheme.typography.h6)
            Text(text = dosage, style = MaterialTheme.typography.body2)
            Text(text = time, style = MaterialTheme.typography.body2)
            if (expanded) {
                Text(text = "Expanded", style = MaterialTheme.typography.body2)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MedicUITheme {
        MedApp()
    }
}

