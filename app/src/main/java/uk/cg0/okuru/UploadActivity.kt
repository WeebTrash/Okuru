package uk.cg0.okuru

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import kotlinx.android.synthetic.main.activity_upload.*
import android.text.method.ScrollingMovementMethod
import android.provider.MediaStore
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.BlobDataPart
import android.os.AsyncTask
import java.net.MalformedURLException
import android.view.View
import org.json.JSONObject
import android.provider.OpenableColumns


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
        /*paste_button.setOnClickListener {
            val myClipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val myClip: ClipData = ClipData.newPlainText("upload link", upload.text)
            myClipboard.setPrimaryClip(myClip)
            Toast.makeText(this, "Link copied", Toast.LENGTH_LONG).show()
        }
        */
    }

    private fun handleSendImage(intent: Intent) {
        //TODO: move to a seperate thread
        /*val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
         */
        upload.text = intent.clipData?.getItemAt(0)?.uri.toString()
        upload.movementMethod = ScrollingMovementMethod()
        val inputStream = contentResolver.openInputStream(intent.clipData?.getItemAt(0)?.uri!!)
        var size:Long=0
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
                filename_text.text = cursor.getString(nameIndex)
                size=cursor.getLong(sizeIndex)
                filesize_text.text =size.toString()
        }

        try {
            val (_, response, _) = Fuel.upload("endpoint")
                .add(BlobDataPart(inputStream!!, name = "files[]", filename = filename_text.text.toString()))
                .header("token", "token")
                .requestProgress{ readBytes,_ ->
                    if(readBytes<size){
                        val progress=(readBytes/size)*100
                        upload.text="Upload progress: $progress%"
                    }
                }
                .response()
            if (response.statusCode == 200) {
                    upload.text = JSONObject(String(response.data)).getJSONArray("files").getJSONObject(0).getString("url")
                    runOnUiThread {
                        imageView.setImageURI(intent.clipData?.getItemAt(0)?.uri)
                        paste_button.visibility = View.VISIBLE
                    }
            } else {
                val code=response.statusCode.toString()
                val message=response.responseMessage
                upload.text = "$code: $message"
            }
        } catch (mue: MalformedURLException) {
            upload.text = "Invalid upload URL"
        }
    }
}



