package com.example.zapis_version3.ui.notifications

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.zapis_version3.R

class VstalVocheredActivity : AppCompatActivity() {

    private lateinit var countdownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vstal_vochered)

        val textViewMestoUslugaAdres: TextView = findViewById(R.id.textViewMestoUslugaAdres)
        val textViewMestoVochered: TextView = findViewById(R.id.textViewMestoVochered)
        val buttonExit: Button = findViewById(R.id.buttonExit)
        val textViewTimer: TextView = findViewById(R.id.textViewTimer)

        val sharedPref = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        val mesto = sharedPref.getString("place", "Место не выбрано")
        val usluga = sharedPref.getString("uslug", "Услуга не выбрана")
        val adres = sharedPref.getString("adres", "Адрес не выбран")

        textViewMestoUslugaAdres.text = "Место: $mesto\nУслуга: $usluga\nАдрес: $adres"
        textViewMestoVochered.text = "Место в очереди: 1"

        val startTime = 180000
        countdownTimer = object : CountDownTimer(startTime.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                textViewTimer.text = "До автоматического выхода из очереди: $minutes:${String.format("%02d", seconds)}"
            }

            override fun onFinish() {
                finish()
            }
        }.start()

        buttonExit.setOnClickListener {
            countdownTimer.cancel()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer.cancel()
    }
}

