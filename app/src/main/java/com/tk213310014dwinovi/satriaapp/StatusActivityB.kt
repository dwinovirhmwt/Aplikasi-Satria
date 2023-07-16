package com.tk213310014dwinovi.satriaapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class StatusActivityB : AppCompatActivity() {
    private val mqttBroker = "tcp://free.mqtt.iyoti.id:1883"
    private val mqttTopicWaterLevel = "sungai /tinggiair"
    private val mqttTopicRainCategory = "sungai/rain_category"
    private val mqttTopicWaterCategory = "sungai/water_category"

    private lateinit var progressAir: ProgressBar
    private lateinit var textViewAir: TextView
    private lateinit var textViewKondisiHujan: TextView
    private lateinit var textViewKategoriAir: TextView
    private lateinit var mqttClient: MqttClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_b)

        progressAir = findViewById(R.id.progressAir)
        textViewAir = findViewById(R.id.textAir)
        textViewKondisiHujan = findViewById(R.id.textKondisiHujanValue)
        textViewKategoriAir = findViewById(R.id.textKategoriAirValue1)

        // Set the indeterminate mode for the progress bar
        progressAir.isIndeterminate = true

        // Set the maximum value for the progress bar
        progressAir.max = 20 // Set the desired maximum value here

        try {
            mqttClient = MqttClient(mqttBroker, MqttClient.generateClientId(), MemoryPersistence())
            mqttClient.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    if (reconnect) {
                        // Re-subscribe to the topics after reconnecting
                        mqttClient.subscribe(mqttTopicWaterLevel)
                        mqttClient.subscribe(mqttTopicRainCategory)
                        mqttClient.subscribe(mqttTopicWaterCategory)
                    }
                }

                override fun connectionLost(cause: Throwable?) {}

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    runOnUiThread {
                        when (topic) {
                            mqttTopicWaterLevel -> {
                                val waterLevel = String(message?.payload ?: ByteArray(0)).toIntOrNull()
                                waterLevel?.let { updateProgressAir(it) }
                            }

                            mqttTopicRainCategory -> {
                                val rainCategory = String(message?.payload ?: ByteArray(0))
                                textViewKondisiHujan.text = rainCategory
                            }

                            mqttTopicWaterCategory -> {
                                val waterCategory = String(message?.payload ?: ByteArray(0))
                                textViewKategoriAir.text = waterCategory
                            }
                        }
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {}
            })

            val options = MqttConnectOptions()
            options.isAutomaticReconnect = true

            mqttClient.connect(options)
            mqttClient.subscribe(mqttTopicWaterLevel)
            mqttClient.subscribe(mqttTopicRainCategory)
            mqttClient.subscribe(mqttTopicWaterCategory)

        } catch (e: MqttException) {
            e.printStackTrace()
        }

        val logoBack: ImageView = findViewById(R.id.logoBack)
        logoBack.setOnClickListener {
            Intent(this@StatusActivityB, MapsActivity::class.java)
            onBackPressed()
        }
    }

    private fun updateProgressAir(ketinggianAir: Int) {
        progressAir.isIndeterminate = false
        progressAir.progress = ketinggianAir
        textViewAir.text = "$ketinggianAir Cm"
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttClient.disconnect()
        mqttClient.close()
    }

    fun openLocationLink(view: View) {
        val infoLokasi_b = "https://goo.gl/maps/WoYZRAM3SPcsHKgD8" //link lokasi

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(infoLokasi_b))
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.drawable.slide_right)
    }

    private fun overridePendingTransition(slideRight: Int) {

    }
}