package com.kyhgroupd.ponggroupd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kyhgroupd.ponggroupd.databinding.ActivityGameOverBinding

class GameOverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameOverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameOverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMainMenu.setOnClickListener{
            Intent(this, MainActivity :: class.java)
                .apply { startActivity(this) }
        }

        binding.btnPlayAgain.setOnClickListener{
            Intent(this, BreakoutActivity :: class.java)
                .apply { startActivity(this) }
        }
    }

    //Override built-in back button to return to main activity
    override fun onBackPressed() {
        Intent(this, MainActivity::class.java)
            .apply { startActivity(this) }
    }
}