import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.res.AssetManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "calendar.db"
        private const val DATABASE_VERSION = 1
    }

    init {
        if (!context.getDatabasePath(DATABASE_NAME).exists()) {
            copyDatabaseFromAssets(context)
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    private fun copyDatabaseFromAssets(context: Context) {
        val assetManager: AssetManager = context.assets
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            inputStream = assetManager.open(DATABASE_NAME)
            val outputFile = context.getDatabasePath(DATABASE_NAME)
            outputFile.parentFile?.mkdirs()
            outputStream = FileOutputStream(outputFile)

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            outputStream?.flush()
            outputStream?.close()
        }
    }
}
