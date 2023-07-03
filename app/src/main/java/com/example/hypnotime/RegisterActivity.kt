package com.example.hypnotime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.hypnotime.data.User
import com.example.hypnotime.db.FaceBookDatabase


class RegisterActivity : AppCompatActivity() {

    lateinit var db: FaceBookDatabase;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Créer un compte"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

db = FaceBookDatabase(this);
        val editName = findViewById<EditText>(R.id.editName);
        val editEmail = findViewById<EditText>(R.id.editEmail);
        val editPassword = findViewById<EditText>(R.id.editPassword);
        val editConfirmPassword= findViewById<EditText>(R.id.editConfirmPassword);
        val btnSave = findViewById<Button>(R.id.btnSave);
        val tvError = findViewById<TextView>(R.id.tvError);

        btnSave.setOnClickListener{
            tvError.visibility = View.INVISIBLE;
            tvError.text = "";

            val name = editName.text.toString();
            val email = editEmail.text.toString();
            val password = editPassword.text.toString();
            val confirmPassword = editConfirmPassword.text.toString();

            // On check if null
            if(name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                tvError.text = getString(R.string.error_empty_fields);
                tvError.visibility = View.VISIBLE;
            }

            else {
                // check if password === confirmPassword
                if(password != confirmPassword) {
                    tvError.text = getString(R.string.passwords_different);
                    tvError.visibility = View.VISIBLE;
                }
                else {
                    val user = User(name,email, password);
                    val isInserted = db.addUser(user);
                    if(isInserted) {
                        print(getString(R.string.success_register));
                        Intent(this, HomeActivity::class.java).also {
                            it.putExtra("email", email);
                            startActivity(it);
                        }
                        finish();
                    }
                }
            }
        }
    }
}