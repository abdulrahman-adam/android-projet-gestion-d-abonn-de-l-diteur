package com.example.hypnotime

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.hypnotime.db.FaceBookDatabase


class MainActivity : AppCompatActivity() {

lateinit var sharedPreferences: SharedPreferences;
lateinit var db: FaceBookDatabase;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = this.getSharedPreferences("app_state", Context.MODE_PRIVATE);
        db = FaceBookDatabase(this);
        val isAuthenticated = sharedPreferences.getBoolean("is_authenticated", false);
        val emailSharedPreferences = sharedPreferences.getString("email", "")
        if(isAuthenticated) {
            Intent(this, MainActivity::class.java).also{
                it.putExtra("email", emailSharedPreferences)
                startActivity(it)
            }
        }
        // On déclaration les variables
        val connect = findViewById<Button>(R.id.connec);
        val email = findViewById<EditText>(R.id.email);
        val password = findViewById<EditText>(R.id.password);
        val error = findViewById<TextView>(R.id.error);
        val tvRegister = findViewById<TextView>(R.id.tvRegister);

        connect.setOnClickListener {view: View ->
            error.visibility = View.GONE;
            Toast.makeText(this, "vous avez connecté sur le button connect", Toast.LENGTH_LONG).show();

            // On récupérer le email et password
            val txtEmail = email.text.toString();
            val txtPassword = password.text.toString();

            if(txtEmail.trim().isEmpty() || txtPassword.trim().isEmpty()){
                error.text = "vous devez remplir tout les champs"
                error.visibility = View.VISIBLE;
            } else {

                val user = db.findUser(txtEmail, txtPassword)
                if(user != null ) {
                    email.setText("");
                    password.setText("");

                    // Enregister dans sharedPreferences le boolean isAuthentificated
                    val editor = sharedPreferences.edit();
                    editor.putBoolean("is_authenticated",true);
                    editor.putString("email", txtEmail)
                    editor.apply();

                    Intent(this, HomeActivity::class.java).also{
                        it.putExtra("email", txtEmail);
                        startActivity(it);
                    }
                } else {
                    error.text = "Email ou Password n'est pas correct"
                    error.visibility = View.VISIBLE;
                }
                //println("email: $txtEmail - password: $txtPassword");
            }




            Toast.makeText(this, "$txtEmail", Toast.LENGTH_LONG).show();
            //println("$txtEmail");
        }

        tvRegister.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also{
                startActivity(it);
            }
        }
    }
}