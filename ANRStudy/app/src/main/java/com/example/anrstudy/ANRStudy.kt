package com.example.anrstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text
import java.lang.Math.sqrt
import kotlin.concurrent.thread
import kotlin.random.Random

class ANRStudy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var result=findViewById<TextView>(R.id.result)

        findViewById<Button>(R.id.btn).setOnClickListener{
            Toast.makeText(this,
            "Clicked!",
                Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.anr).setOnClickListener{
            Thread(Runnable{
                var sum=0.0
                for (i in 1 .. 60) {
                    sum+=sqrt(Random.nextDouble())
                    //Log.d("mytag", sqrt(Random.nextDouble()).toString())
                    Thread.sleep(1000)
                }
                Log.d("mytag", sum.toString())
                runOnUiThread {
                    result.text = sum.toString()
                }
            }).start()
        }

    }
}