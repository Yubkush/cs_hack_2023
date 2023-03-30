package com.example.medicui

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
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
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.delay


//global array to store the list items, each item will be a dictionary
var rows = mutableListOf<MutableMap<String, String>>()

var rows1 = mutableListOf<MutableMap<String, String>>()
var jsonObject1 = JSONObject()

//create 

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
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

        jsonObject1 = JSONObject(json1)

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
        //sort rows by time
        rows.sortBy { it["time"] }

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
                                    id = "Side Effects",
                                    title = "Side Effects",
                                    contentDescription = "Go to side effects screen",
                                    icon = Icons.Default.Warning
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
    val context = LocalContext.current

    // Create a notification channel and set its importance level
    val channel = NotificationChannel(
        "channel_id",
        "channel_name",
        NotificationManager.IMPORTANCE_DEFAULT
    )
    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.createNotificationChannel(channel)

    // Create a notification builder
    val builder = NotificationCompat.Builder(context, "channel_id")
        .setContentTitle("MedTrack")
        .setContentText("It's time to take your medication!")
        .setSmallIcon(R.drawable.ic_launcher_foreground)

    // Set the notification's tap action
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
    builder.setContentIntent(pendingIntent)

    // Send the notification after 5 seconds
    Handler(Looper.getMainLooper()).postDelayed({
        notificationManager.notify(1, builder.build())
    }, 5000)
    LaunchedEffect(Unit) {
        delay(5000) // wait for 5 seconds
        notificationManager.notify(1, builder.build()) // send the notification
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        // Create a header composable for each date group
        @Composable
        fun DateHeader(date: String) {
            Text(
                text = date,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = MaterialTheme.typography.h6
            )
        }

        // Create a list of medication items grouped by date
        val groupedRows = rows.groupBy { it["time"]?.substringBefore("T") }

        groupedRows.forEach { (date, rowsByDate) ->
            // Add the header composable as the first item for each date group
            item {
                DateHeader(date!!)
            }
            // Create a row for each medication item in the date group
            items(rowsByDate.size) { index ->
                val row = rowsByDate[index]
                val name = row["name"]
                val dosage = row["dosage"]
                val iso_time_string = row["time"]!!
                val split = iso_time_string.split("T")
                var hours = split[1].substring(0, split[1].length - 4)
                val formattedDateTime =  hours

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var isChecked by remember { mutableStateOf(false) }
                    Checkbox(
                        checked = false,
                        onCheckedChange = { isChecked = it },
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(24.dp)
                    )
                    //make the name and dosage in the same column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = name.toString(),
                            style = MaterialTheme.typography.subtitle1
                        )
                        Text(
                            text = dosage.toString(),
                            style = MaterialTheme.typography.subtitle2
                        )
                    }
                    Text(
                        text = formattedDateTime,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f)
                    )
                    IconButton(
                        onClick = {
                            // val medications_info = jsonObject1.getJSONArray("medication_info")
                            // //open a popup window with the information for the medication in the row
                            // for (i in 0 until medications_info.length()) {
                            //     //get the medication object
                            //     val medication_info = medications_info.getJSONObject(i)
                            //     //get the medication name
                            //     val medication_name = medication_info.getString("name")
                            //     if (medication_name == name) {
                            //         //get the medication general_info
                            //         val general_info = medication_info.getString("general_info")
                            //         //get the medication when_to_take
                            //         val when_to_take = medication_info.getString("when_to_take")
                            //         //get the medication side_effects
                            //         val side_effects = medication_info.getString("side_effects")
                            //         //show popup
                            //         AlertDialog(
                            //             onDismissRequest = { /*TODO*/ },
                            //             title = { Text("Medication Info") },
                            //             text = { Text("General Info: $general_info \n\nWhen to take: $when_to_take \n\nSide Effects: $side_effects") },
                            //             confirmButton = {
                            //                 Button(onClick = { /*TODO*/ }) {
                            //                     Text("OK")
                            //                 }
                            //             }
                            //         )
                            //         break
                            //     }
                            // }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info"
                        )
                    }
                }
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

