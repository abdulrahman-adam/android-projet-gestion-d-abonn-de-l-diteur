package com.example.hypnotime

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.example.hypnotime.data.Post
import com.example.hypnotime.db.FaceBookDatabase

class PostsAdapter(
    var mContext: Context,
    var resource: Int,
    var values : ArrayList<Post>
) : ArrayAdapter<Post>(mContext,resource,values) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // On récupérer les valeurs
        val post = values[position]
        val itemView = LayoutInflater.from(mContext).inflate(resource, parent, false)
        val tvTitre = itemView.findViewById<TextView>(R.id.tvTitre);
        val tvDescription = itemView.findViewById<TextView>(R.id.tvDescription);
        val imagePost = itemView.findViewById<ImageView>(R.id.imagePost);
        val imageShowPopup = itemView.findViewById<ImageView>(R.id.imageShowPopup);
        val tvLikes = itemView.findViewById<TextView>(R.id.tvLikes);
        val tvShare = itemView.findViewById<TextView>(R.id.tvShare);

        tvTitre.text = post.titre;
        tvDescription.text = post.description;

        val bitmap = getBitmap(post.image);
        imagePost.setImageBitmap(bitmap);
        tvLikes.text = "${post.jaime} j'aime"

        val db = FaceBookDatabase(mContext);
        imageShowPopup.setOnClickListener{
            val popupMenu = PopupMenu(mContext, imageShowPopup)
            popupMenu.menuInflater.inflate(R.menu.list_popup_menu,popupMenu.menu);
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.itemShow -> {
                        Intent(mContext, PostDetailActivity::class.java).also {
                            it.putExtra("titre", post.titre);
                            mContext.startActivity(it);
                        }
                    }

                    R.id.itemDelete -> {

                        val resultDelete = db.deletePost(post.id);
                        if(resultDelete) {
                            values.removeAt(position)
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(mContext, "Erreur de suppression", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                true;
            }
            popupMenu.show();
        }

        tvLikes.setOnClickListener {
            // Incrémenter le compteur de j'aime
            db.incrementLike(post);
            val incrementedLikes = post.jaime+1;
            tvLikes.text = "$incrementedLikes j'aime"
        }

        tvShare.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND;
                putExtra(Intent.EXTRA_TEXT, "https://www.facebook.com/posts/${post.id}")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, post.titre)
            mContext.startActivity(shareIntent);
        }
        return itemView;
    }

    fun getBitmap(byteArray: ByteArray) :Bitmap{
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size);
        return bitmap;
    }
}