package yiftach.carmon.coffeealarm.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import yiftach.carmon.coffeealarm.R
import yiftach.carmon.coffeealarm.databinding.ActivityRingBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

}