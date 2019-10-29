package uk.cg0.okuru

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_upload.*


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

    }

    private fun handleSendImage(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {


            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(this)
            val url = "https://fuckingweeb.site"

            // Request a string response from the provided URL.

            val imageRequest = ()
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                Response.Listener<String> { response ->
                    // Display the first 500 characters of the response string.
                    textView.text = "Response is: $response"
                },
                Response.ErrorListener { textView.text = "That didn't work!" })

            // Add the request to the RequestQueue.
            queue.add(stringRequest)

        }
    }



}
