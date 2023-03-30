
class SettingsActivity : AppCompatActivity() {
    
    private lateinit var notificationSwitch: Switch
    private lateinit var breakfastTimePicker: TimePicker
    private lateinit var lunchTimePicker: TimePicker
    private lateinit var dinnerTimePicker: TimePicker
    private lateinit var darkThemeSwitch: Switch
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        notificationSwitch = findViewById(R.id.notification_switch)
        breakfastTimePicker = findViewById(R.id.breakfast_time_picker)
        lunchTimePicker = findViewById(R.id.lunch_time_picker)
        dinnerTimePicker = findViewById(R.id.dinner_time_picker)
        darkThemeSwitch = findViewById(R.id.dark_theme_switch)
        
        // Load settings from SharedPreferences
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        notificationSwitch.isChecked = sharedPrefs.getBoolean("notification", true)
        val breakfastTime = sharedPrefs.getString("breakfast_time", "08:00")
        val lunchTime = sharedPrefs.getString("lunch_time", "12:00")
        val dinnerTime = sharedPrefs.getString("dinner_time", "18:00")
        breakfastTimePicker.hour = breakfastTime?.substringBefore(":")?.toIntOrNull() ?: 8
        breakfastTimePicker.minute = breakfastTime?.substringAfter(":")?.toIntOrNull() ?: 0
        lunchTimePicker.hour = lunchTime?.substringBefore(":")?.toIntOrNull() ?: 12
        lunchTimePicker.minute = lunchTime?.substringAfter(":")?.toIntOrNull() ?: 0
        dinnerTimePicker.hour = dinnerTime?.substringBefore(":")?.toIntOrNull() ?: 18
        dinnerTimePicker.minute = dinnerTime?.substringAfter(":")?.toIntOrNull() ?: 0
        darkThemeSwitch.isChecked = sharedPrefs.getBoolean("dark_theme", false)
        
        // Save settings when user changes them
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("notification", isChecked).apply()
        }
        breakfastTimePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            sharedPrefs.edit().putString("breakfast_time", "$hourOfDay:$minute").apply()
        }
        lunchTimePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            sharedPrefs.edit().putString("lunch_time", "$hourOfDay:$minute").apply()
        }
        dinnerTimePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            sharedPrefs.edit().putString("dinner_time", "$hourOfDay:$minute").apply()
        }
        darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("dark_theme", isChecked).apply()
            recreate()
        }
    }
}