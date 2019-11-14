package uk.cg0.okuru

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_image_history_item.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.os.StrictMode


class ImageItem : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_history_item)

        val url=intent.getStringExtra("imageUrl")

        filename.text=intent.getStringExtra("imageName")
        imageUrl.text=url

        bigPicture.setImageBitmap(doInBackground(url))
    }
    private fun doInBackground(vararg urls: String): Bitmap? {
        /**
        TODO: move to a seperate thread
        TODO: add image caching
         */
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val urldisplay = urls[0]
        var bmp: Bitmap? = null
            val `in` = java.net.URL(urldisplay).openStream()
            bmp = BitmapFactory.decodeStream(`in`)
        return bmp
    }
}

