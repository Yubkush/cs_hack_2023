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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medicui.ui.theme.MedicUITheme
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.Instant
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// val channel = NotificationChannel(
//     "channel_id",
//     "channel_name",
//     NotificationManager.IMPORTANCE_DEFAULT
// ).apply {
//     description = "channel_description"
// }
// val notificationManager: NotificationManager =
//     getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
// notificationManager.createNotificationChannel(channel)

//global array to store the list items, each item will be a dictionary
var rows = mutableListOf<MutableMap<String, String>>()

var rows1 = mutableListOf<MutableMap<String, String>>()

//create 

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

        val json1 = try {
            assetsManager.open("info.json").bufferedReader().use { it.readText() }
        }
        catch (e: Exception) {
            e.printStackTrace()
            ""
        }

        // Parse the JSON data using a JSON parsing library of your choice
        // For example, using org.json
        val jsonObject = JSONObject(json)

        val jsonObject1 = JSONObject(json1)

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

        //iterate over the JSON object and get the values
        val medications_info = jsonObject1.getJSONArray("medication_info")
        //iterate over the medications array
        for (i in 0 until medications_info.length()) {
            //get the medication object
            val medication_info = medications_info.getJSONObject(i)
            //get the medication name
            val name = medication_info.getString("name")
            //get the medication general_info
            val general_info = medication_info.getString("general_info")
            //get the medication when_to_take
            val when_to_take = medication_info.getString("when_to_take")
            //get the medication side_effects
            val side_effects = medication_info.getString("side_effects")
        }
        setContent {
            MedicUITheme {
                // A surface container using the 'background' color from the theme

                val scaffoldState = rememberScaffoldState()
                val scope = rememberCoroutineScope()
                Scaffold(
                    scaffoldState = scaffoldState,
                    topBar = {
                        AppBar(
                            onNavigationIconClick = {
                                scope.launch {
                                    scaffoldState.drawerState.open()
                                }
                            }
                        )
                    },
                    drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
                    drawerContent = {
                        DrawerHeader()
                        DrawerBody(
                            items = listOf(
                                MenuItem(
                                    id = "home",
                                    title = "Home",
                                    contentDescription = "Go to home screen",
                                    icon = Icons.Default.Home
                                ),
                                MenuItem(
                                    id = "settings",
                                    title = "Settings",
                                    contentDescription = "Go to settings screen",
                                    icon = Icons.Default.Settings
                                ),
                                MenuItem(
                                    id = "help",
                                    title = "Help",
                                    contentDescription = "Get help",
                                    icon = Icons.Default.Info
                                ),
                            ),
                            onItemClick = {
                                println("Clicked on ${it.title}")
                            }
                        )
                    }
                ) {
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
            //format time from the ISO 8601 format string to a human readable format
            val iso_time_string = row["time"]!!
            //parse my_string to a YYYY-MM-DD HH:MM format
            //split the string to get the date and time
            val split = iso_time_string.split("T")
            //split the date to get the year, month and day
            val date = split[0]
            //remove the last 4 characters from split[1]
            var hours = split[1].substring(0, split[1].length - 4)
            
            MedCard(
                name = row["name"]!!,
                dosage = row["dosage"]!!,
                time = date + " " + hours,

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

