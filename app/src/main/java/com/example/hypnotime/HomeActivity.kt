package com.example.hypnotime

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.hypnotime.data.Post
import com.example.hypnotime.db.FaceBookDatabase


class HomeActivity : AppCompatActivity() {

    lateinit var  listPosts : ListView;
    var postsArray = ArrayList<Post>();
    lateinit var adapter: PostsAdapter;
    lateinit var db : FaceBookDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Accueil"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        db = FaceBookDatabase(this);
        //val tvHello = findViewById<TextView>(R.id.tvHello);

        // 1: récupérer l'email envoyé par l'activityMain
                val email = intent.getStringExtra("email");
        // 2: afficher l'email dans le tvHello
            //tvHello.text = "Bienvenu : $email";

        // On afficher la list
        listPosts = findViewById(R.id.listPosts);

    }


    override fun onResume() {
        super.onResume()
        postsArray = db.findPosts();
        adapter = PostsAdapter(this, R.layout.item_post, postsArray);
        listPosts.adapter = adapter;
        listPosts.setOnItemClickListener{ adapterView, view, position, id ->
            val clickedPost = postsArray[position]
            Intent(this, PostDetailActivity::class.java).also {
                it.putExtra("titre", clickedPost.titre)
                startActivity(it)
            }
        }
        registerForContextMenu(listPosts);
    }


    // On afficher le menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // On afficher les options
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.itemAdd -> {
                Intent(this, AddPostActivity::class.java).also {
                    startActivity(it);
                }
            }
            R.id.itemConfig -> {
                Toast.makeText(this, "App configuration", Toast.LENGTH_LONG).show();
            }

            R.id.itemLogout -> {

                //On afficher un dialog de confirmation
                ShowLogoutConfirmDialog()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SuspiciousIndentation")
    fun ShowLogoutConfirmDialog() {
        val builder = AlertDialog.Builder(this);
        builder.setTitle("Confirmation!");
        builder.setMessage("vous êtes sure de vouloir quitter l'application");
        builder.setPositiveButton("Oui") { dialogInterface, id ->
            val editor = this.getSharedPreferences("app_state", Context.MODE_PRIVATE).edit()
                editor.remove("is_authenticated")
                editor.apply();
            finish();
        }

        builder.setNegativeButton("Non") { dialogInterface, id ->
            dialogInterface.dismiss();
        }

        builder.setNeutralButton("Annuler"){ dialogInterface, id ->
            dialogInterface.dismiss();
        }


        val alertDialog: AlertDialog = builder.create()
        alertDialog.show();
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menuInflater.inflate(R.menu.list_popup_menu,menu);
        super.onCreateContextMenu(menu, v, menuInfo)
    }




}