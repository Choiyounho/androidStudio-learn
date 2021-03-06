package com.soten.androidstudio.learn.boostcourse.db

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.soten.androidstudio.learn.R
import java.lang.Exception

class DbActivity : AppCompatActivity() {

    private val editText: EditText by lazy {
        findViewById(R.id.et_db)
    }

    private val dbText: TextView by lazy {
        findViewById(R.id.tv_db)
    }

    private val dbMakeBtn: Button by lazy {
        findViewById(R.id.btn_db_make)
    }

    private val dbAddBtn: Button by lazy {
        findViewById(R.id.btn_db_add)
    }

    private val editText2: EditText by lazy {
        findViewById(R.id.et_db_2)
    }

    private val editText3: EditText by lazy {
        findViewById(R.id.et_db_3)
    }

    private val editText4: EditText by lazy {
        findViewById(R.id.et_db_4)
    }

    private val editText5: EditText by lazy {
        findViewById(R.id.et_db_5)
    }

    private val dbSelectBtn: Button by lazy {
        findViewById(R.id.btn_db_select)
    }

    private lateinit var database: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_db)

        val btn = findViewById<Button>(R.id.btn_db_open)

        btn.setOnClickListener {
            val databaseName = editText.text.toString()
            openDatabase(databaseName)
        }

        dbMakeBtn.setOnClickListener {
            val tableName = editText2.text.toString()
            createTable(tableName)
        }

        dbAddBtn.setOnClickListener {
            val name = editText3.text.toString().trim()
            val ageStr = editText4.text.toString().trim()
            val number = editText5.text.toString().trim()

            var age = -1
            try {
                age = ageStr.toInt()
            } catch (e: Exception) {
            }

            insertData(name, age, number)
        }

        dbSelectBtn.setOnClickListener {
            val tableName = editText2.text.toString()

            selectData(tableName)
        }
    }

    private fun openDatabase(databaseName: String) {
        if (databaseName == "") {
            Toast.makeText(applicationContext, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        printing("openDatabase() 호출")

        val helper = DatabaseHelper(this, databaseName, null, 3)

        database = helper.writableDatabase
    }

    private fun createTable(tableName: String) {
        if (tableName == "") {
            Toast.makeText(applicationContext, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        if (!database.isOpen) {
            printing("데이터 베이스를 먼저 오픈해주세요요")
        }

        printing("createTable() 호출 됨")

        database.let {
            val sql =
                "create table $tableName (_id integer PRIMARY KEY autoincrement, name text, age integer, mobile text)"
            database.execSQL(sql)
            printing("테이블 생성 됨")
        }
    }

    private fun insertData(name: String, age: Int, mobile: String) {
        if (!database.isOpen) {
            printing("데이터 베이스를 먼저 오픈해주세요요")
        }

        printing("insertData() 호출됨")

        database.let {
            val sql = "insert into customer(name, age, mobile) values(?, ?, ?)"
            val params = arrayOf(name, age, mobile)

            database.execSQL(sql, params)
            printing("data 추가함")
        }
    }

    private fun selectData(tableName: String) {
        printing("selectData() 호출")

        database.let {
            val sql = "select name, age, mobile from $tableName"
            val cursor = database.rawQuery(sql, null)
            printing("조회된 데이터 개수 : ${cursor.count}")

            for (i in 0 until cursor.count) {
                cursor.moveToNext()
                val name = cursor.getString(0)
                val age = cursor.getInt(1)
                val mobile = cursor.getString(2)

                printing("# $i -> $name, $age, $mobile")
            }
            cursor.close()
        }
    }

    private fun printing(data: String) {
        dbText.append("$data \n")
    }

    inner class DatabaseHelper(
        context: Context?,
        name: String?,
        factory: SQLiteDatabase.CursorFactory?,
        version: Int
    ) : SQLiteOpenHelper(context, name, factory, version) {

        override fun onCreate(db: SQLiteDatabase?) {
            printing("createTable() 호출 됨")

            val tableName = "customer"
            val sql =
                "create table if not exists $tableName (_id integer PRIMARY KEY autoincrement, name text, age integer, mobile text)"
            db?.execSQL(sql)
            printing("테이블 생성 됨")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            printing("onUpgrade() 호출 됨 : $oldVersion, $newVersion")

            if (newVersion > 1) {
                val tableName = "customer"
                db?.execSQL("drop table if exists $tableName")
                printing("테이블 삭제함")


                db.let {
                    val sql =
                        "create table $tableName (_id integer PRIMARY KEY autoincrement, name text, age integer, mobile text)"
                    db?.execSQL(sql)
                    printing("테이블 생성 됨")
                }
            }
        }
    }

}