import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.example.proto.Data
import com.google.protobuf.util.JsonFormat

//create jetpack dataStore


object MedicineSerializer : Serializer<Medicine> {
    override val defaultValue: Medicine = Medicine.getDefaultInstance()

    override fun readFrom(input: InputStream): Medicine {
        try {
            return Medicine.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override fun writeTo(t: Data, output: OutputStream) {
        val jsonString = JsonFormat.printer().print(t)
        output.write(jsonString.toByteArray())
    }
}

val Context.settingsDataStore: DataStore<Settings> by dataStore(
    fileName = "settings.pb",
    serializer = SettingsSerializer
)