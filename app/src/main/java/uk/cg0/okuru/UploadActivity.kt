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
        imageView.setImageURI(intent.clipData?.getItemAt(0)?.uri)
        upload.movementMethod = ScrollingMovementMethod()
        val inputStream = contentResolver.openInputStream(intent.clipData?.getItemAt(0)?.uri!!)
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        val metaCursor = contentResolver.query(
            intent.clipData?.getItemAt(0)?.uri!!,
            projection,
            null,
            null,
            null
        )
        var filename = ""
        if (metaCursor != null) {
            try {
                if (metaCursor!!.moveToFirst()) {
                    filename = metaCursor!!.getString(0)
                    filename_text.text = filename
                }
            } finally {
                metaCursor!!.close()
            }
        }

        try {
            val (_, response, _) = Fuel.upload("endpoint")
                .add(BlobDataPart(inputStream!!, name = "files[]", filename = filename))
                .header("token", "token")
                .response()
            if (response.statusCode == 200) {
                    upload.text =
                    JSONObject(String(response.data)).getJSONArray("files").getJSONObject(0).getString("url")
                    runOnUiThread {paste_button.visibility = View.VISIBLE}
            } else {
                upload.text = String.format(
                    "%s: %s",
                    response.statusCode.toString(),
                    response.responseMessage
                )
            }
        } catch (mue: MalformedURLException) {
            upload.text = "Invalid upload URL"
        }
    }
}



