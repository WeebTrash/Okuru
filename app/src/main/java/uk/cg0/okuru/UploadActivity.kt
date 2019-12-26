package uk.cg0.okuru

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import kotlinx.android.synthetic.main.activity_upload.*
import android.text.method.ScrollingMovementMethod
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.BlobDataPart
import android.os.AsyncTask
import java.net.MalformedURLException
import android.view.View
import org.json.JSONObject
import android.provider.OpenableColumns
import android.widget.Toast
import kotlin.math.roundToInt


class UploadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        when {
            intent?.action == Intent.ACTION_SEND -> {
                AsyncTask.execute {
                    handleSendImage(intent)
                }
            }
            else -> {
                // Handle other intents, such as being started from the home screen
            }
        }
        paste_button.setOnClickListener {
            val myClipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val myClip: ClipData = ClipData.newPlainText("upload link", upload.text)
            myClipboard.setPrimaryClip(myClip)
            Toast.makeText(this, "Link copied", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleSendImage(intent: Intent) {
        //TODO: move to a seperate thread
        /*val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
         */
        upload.text = intent.clipData?.getItemAt(0)?.uri.toString()
        upload.movementMethod = ScrollingMovementMethod()
        val inputStream = contentResolver.openInputStream(intent.clipData?.getItemAt(0)?.uri!!)
        var filesize= 0.0F
        var filename= ""
        contentResolver.query(
            intent.clipData?.getItemAt(0)?.uri!!,
            null,
            null,
            null,
            null
        )?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                cursor.moveToFirst()
                filename=cursor.getString(nameIndex)
                filesize=cursor.getLong(sizeIndex).toFloat()
        }

        try {
            val (_, response, _) = Fuel.upload(getString(R.string.URL))
                .add(BlobDataPart(inputStream!!, name = "files[]", filename = filename))
                .header("token", getString(R.string.SAFE_TOKEN))
                .requestProgress{ readBytes,_ ->
                    if(readBytes<filesize){
                        val progress=((readBytes/filesize)*100).toInt()
                        upload.text="Upload progress: $progress%"
                    }
                }
                .response()
            when(response.statusCode) {
                200 -> {
                    upload.text = JSONObject(String(response.data)).getJSONArray("files").getJSONObject(0).getString("url")
                    runOnUiThread {
                        imageView.setImageURI(intent.clipData?.getItemAt(0)?.uri)
                        paste_button.visibility = View.VISIBLE
                    }
                }
                404 -> {
                    upload.text="Upload API can't be reached, sure URL is correct?"
                }
                else -> {
                    val code = response.statusCode.toString()
                    val message = response.responseMessage
                    upload.text = "$code: $message"
                }
            }
        } catch (mue: MalformedURLException) {
            upload.text = "Invalid upload URL"
        }
    }
}



