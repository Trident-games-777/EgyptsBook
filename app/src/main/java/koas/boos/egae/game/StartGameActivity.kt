package koas.boos.egae.game

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import koas.boos.egae.R

class StartGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_game)

        findViewById<Button>(R.id.btStartGame).setOnClickListener {
            startActivity(Intent(this@StartGameActivity, GameActivity::class.java))
        }
    }
}