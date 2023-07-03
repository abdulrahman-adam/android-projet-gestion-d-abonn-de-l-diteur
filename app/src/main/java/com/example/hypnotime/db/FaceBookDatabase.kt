package com.example.hypnotime.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.hypnotime.data.Post
import com.example.hypnotime.data.User

class FaceBookDatabase(mContext : Context) : SQLiteOpenHelper(
    mContext,
    DB_NAME,
    null,
    DB_VERSION
){

    // On création le tableau de db
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableUser = """
            CREATE TABLE $USERS_TABLE_NAME(
                 $USER_ID integer PRIMARY KEY,
                 $NAME varchar(50),
                 $EMAIL varchar(50),
                 $PASSWORD varchar(20)
            )
        """.trimIndent()

        val createTablePosts = """
            CREATE TABLE $POSTS_TABLE_NAME(
                 $POST_ID integer PRIMARY KEY,
                 $TITLE varchar(50),
                 $DESCRIPTION text,
                 $IMAGE blob,
                 $LIKES Integer
            )
        """.trimIndent()

        db?.execSQL(createTableUser)
        db?.execSQL(createTablePosts)
    }
    // On supprision des anciens tables et la recréation de nouveaux
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $USERS_TABLE_NAME");
        db?.execSQL("DROP TABLE IF EXISTS $POSTS_TABLE_NAME");
        onCreate(db);
    }

    // Cette function créer des utilisateurs
    fun addUser(user: User):Boolean {
        // Inserer un nouveau utilisateur dans la base de donées

        val values = ContentValues();
        values.put(NAME, user.name)
        values.put(EMAIL, user.email)
        values.put(PASSWORD, user.password)


        val db = this.writableDatabase;
        // insert into users(nom, email, password) values(user.nom, user.email, user.password)
        val result = db.insert(USERS_TABLE_NAME, null, values).toInt();
        db.close();
        return result != -1;
    }

    fun findUser(email: String, password: String): User? {
        var user : User? = null;
        val db = this.readableDatabase
        val selectionArgs = arrayOf(email, password);
        // On executer la requête
        val cursor = db.query(USERS_TABLE_NAME,null, "$EMAIL = ? AND $PASSWORD = ?" ,selectionArgs, null, null, null);
        if(cursor != null) {
            if(cursor.moveToFirst()) {

                val id = cursor.getInt(0);
                val name = cursor.getString(1);
                val email = cursor.getString(2);
                val user = User(id, name, email, "")
                return user;
            }
        }
        db.close();
        return user;
    }

    fun addPost(post: Post): Boolean {
        val db = writableDatabase;
        val values = ContentValues();
        values.put(TITLE, post.titre);
        values.put(DESCRIPTION, post.description);
        values.put(IMAGE, post.image);
        values.put(LIKES, 0);
        val result = db.insert(POSTS_TABLE_NAME,null,values)
        db.close()
        return result != -1L
    }

    fun findPosts(): ArrayList<Post> {
        val posts = ArrayList<Post>();
        val db = readableDatabase
        val selectQuery = "SELECT * FROM $POSTS_TABLE_NAME";
        val cursor = db.rawQuery(selectQuery, null);
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(POST_ID))
                    val titre = cursor.getString(cursor.getColumnIndexOrThrow(TITLE))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION))
                    val image = cursor.getBlob(cursor.getColumnIndexOrThrow(IMAGE))
                    val likes = cursor.getInt(cursor.getColumnIndexOrThrow(LIKES))

                    val post = Post(id, titre, description, image,likes);
                    posts.add(post);
                } while(cursor.moveToNext())
            }
        }
        db.close();
        return posts;
    }

    fun deletePost(id: Int): Boolean{
        val db = writableDatabase
        val rawDeleted = db.delete(POSTS_TABLE_NAME, "id=?", arrayOf(id.toString()))
        return rawDeleted >0
    }

    fun incrementLike(post: Post) {

        val db = this.writableDatabase;
        val newLikesCount = post.jaime+1;
        val values = ContentValues()
        values.put(POST_ID,post.id);
        values.put(LIKES, newLikesCount)
        db.update(POSTS_TABLE_NAME, values, "id=?", arrayOf("${post.id}"));
        db.close();
    }

    companion object {
        private val DB_NAME = "facebook_db"
        private val DB_VERSION = 3

        // La table users
        private val USERS_TABLE_NAME = "users"
        private val USER_ID = "id"
        private val NAME = "name"
        private val EMAIL = "email"
        private val PASSWORD = "password"

        // La table posts
        private val POSTS_TABLE_NAME = "posts"
        private val POST_ID = "id"
        private val TITLE = "title"
        private val DESCRIPTION = "description"
        private val IMAGE = "image"
        private val LIKES = "jaime"
    }

}