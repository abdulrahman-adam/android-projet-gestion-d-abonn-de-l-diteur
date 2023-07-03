package com.example.hypnotime

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.hypnotime.data.Post
import com.example.hypnotime.db.FaceBookDatabase
import java.io.ByteArrayOutputStream

class AddPostActivity : AppCompatActivity() {

    lateinit var btnSave:  Button
    lateinit var editTitle:  TextView
    lateinit var editDescription:  TextView
    lateinit var imagePost:  ImageView

    var bitmap: Bitmap? = null;
    lateinit var db : FaceBookDatabase



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        db = FaceBookDatabase(this)

        btnSave = findViewById(R.id.btnSave);
        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        imagePost = findViewById(R.id.imagePost);

        val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { data ->
            val inputStream = data?.let { contentResolver.openInputStream(it) };
            bitmap = BitmapFactory.decodeStream(inputStream);
            imagePost.setImageBitmap(bitmap);
        }
        imagePost.setOnClickListener{
            // On ouvrir la gallery
            galleryLauncher.launch("image/*")
        }

        btnSave.setOnClickListener {
            // On récuperer les differences valeurs
            val titre = editTitle.text.toString();
            val description = editDescription.text.toString();
            if(titre.isEmpty() || description.isEmpty() || bitmap == null) {
                Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
            } else {
                val imagesBlob : ByteArray = getBytes(bitmap!!)
                val post = Post(titre, description,imagesBlob);
                db.addPost(post);

                // clear the formilaire
                editTitle.setText("");
                editDescription.setText("");
                bitmap = null
                finish()
            }

        }
    } // fin de onCreate

    fun getBytes(bitmap: Bitmap) : ByteArray {
        val stream = ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0,stream);
        return stream.toByteArray();
    }
}