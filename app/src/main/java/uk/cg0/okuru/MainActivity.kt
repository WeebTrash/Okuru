package uk.cg0.okuru

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.widget.ListView as ListView


class MainActivity : AppCompatActivity() {
    private var images = arrayOf(ImageInfo("https://example.com","sample1.png"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //viewlist on click shows more info about the image
        var lv = findViewById<ListView>(R.id.viewList)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,images)
        lv.adapter= adapter
        lv.onItemClickListener = AdapterView.OnItemClickListener{
            _, _, i, _ ->
            val image = images[i]
            val imageInfoIntent = Intent(applicationContext, ImageItem::class.java)
            imageInfoIntent.putExtra("imageUrl",image.imageUrl)
            imageInfoIntent.putExtra("imageName",image.imageName)
            this.startActivity(imageInfoIntent)
        }
        //viewlist, on hold of item link gets copied
        lv.onItemLongClickListener=AdapterView.OnItemLongClickListener{
            _, _, i, _ ->
            val myClipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val myClip: ClipData = ClipData.newPlainText("upload link", images[i].toString())
            myClipboard.setPrimaryClip(myClip)
            Toast.makeText(this, "Link copied", Toast.LENGTH_LONG).show()
            true
        }
    }
}
