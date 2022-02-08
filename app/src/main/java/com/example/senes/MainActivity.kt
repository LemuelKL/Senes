package com.example.senes
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    companion object {
        const val WA_PKG_NAME: String = "com.whatsapp.w4b"; // or without 'w4b'
        const val TARGET_MIME_TYPE: String = "vnd.android.cursor.item/vnd.$WA_PKG_NAME.video.call";
    }
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button: Button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val projection: Array<String> = arrayOf(
                ContactsContract.Data._ID,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.DATA1, // Expects "85212345678@s.whatsapp.net"
            )
            val resolver:ContentResolver = contentResolver;
            val cursor = resolver.query(
                ContactsContract.Data.CONTENT_URI,
                null, "DATA1 = ?", arrayOf("${"85212345678"}@s.whatsapp.net"),
                ContactsContract.Contacts.DISPLAY_NAME);

            while (cursor?.moveToNext() == true) {
                val entryId: Long =
                    cursor.getLong(cursor.getColumnIndex(ContactsContract.Data._ID)); // NOT contact_id !!!
                val displayName: String =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                val mimeType: String =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
                if (mimeType != TARGET_MIME_TYPE) continue;
                val phoneNo: String =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA1));
                println("$entryId | $displayName | $phoneNo")
//                for (i in 0 until cursor.columnCount) {
//                    println("${cursor.columnNames[i]} | ${cursor.getString(i)}")
//                }
                videoCall(entryId.toString())
            }
        }
    }

    private fun videoCall(id: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(
            Uri.parse("content://com.android.contacts/data/$id"),
            TARGET_MIME_TYPE
        )
        intent.setPackage(WA_PKG_NAME)
        startActivity(intent)
    }
}

