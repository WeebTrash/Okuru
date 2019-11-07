package uk.cg0.okuru

import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import kotlinx.android.synthetic.main.activity_upload.*
import android.text.method.ScrollingMovementMethod
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.BlobDataPart
import org.json.JSONObject
import android.content.ClipData
import android.widget.Toast

class UploadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        when {
            intent?.action == Intent.ACTION_SEND -> {
                handleSendImage(intent)
            }
            else -> {
                // Handle other intents, such as being started from the home screen
            }
        }
        paste_button.setOnClickListener(View.OnClickListener {
            val myClipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val myClip: ClipData = ClipData.newPlainText("text", upload.text)
            myClipboard.setPrimaryClip(myClip)
            Toast.makeText(this, "Link copied", Toast.LENGTH_LONG).show()
        })

    }

    private fun handleSendImage(intent: Intent) {
        //TODO: move to a seperate thread
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        upload.text = intent.clipData?.getItemAt(0)?.uri.toString()
        imageView.setImageURI(intent.clipData?.getItemAt(0)?.uri)
        upload.movementMethod = ScrollingMovementMethod()
        val inputStream = contentResolver.openInputStream(intent.clipData?.getItemAt(0)?.uri!!)
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        val metaCursor = contentResolver.query(intent.clipData?.getItemAt(0)?.uri!!, projection, null, null, null)
        var filename=""

        if (metaCursor != null) {
            try {
                if (metaCursor!!.moveToFirst()) {
                    filename = metaCursor!!.getString(0)
                }
            } finally {
                metaCursor!!.close()
            }
        }
        filename_text.text=filename
        val (_,response, _)= Fuel.upload("endpoint")
            .add(BlobDataPart(inputStream!!, name="files[]",filename = filename))
            .header("token", "token")
            .response()
        upload.text =JSONObject(String(response.data)).getJSONArray("files").getJSONObject(0).getString("url").replace("fucking", "uwu")
    }
}



