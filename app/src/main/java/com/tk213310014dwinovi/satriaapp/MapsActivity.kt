package com.tk213310014dwinovi.satriaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MapsActivity : AppCompatActivity() {

    private val mqttBroker = "tcp://free.mqtt.iyoti.id:1883"
    private val mqttTopicWaterCategory = "kali/water_category"
    private val mqttTopicWaterCategory2 = "sungai/water_category"
    private lateinit var textViewKategoriAir: TextView
    private lateinit var textViewKategoriAir2: TextView
    private lateinit var mqttClient: MqttClient
    private lateinit var mqttClient2: MqttClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        textViewKategoriAir = findViewById(R.id.textKategoriAirValue1)
        textViewKategoriAir2 = findViewById(R.id.textKategoriAirValue2)

        //KALI --> SUNGAI CODE
        try {
            mqttClient = MqttClient(mqttBroker, MqttClient.generateClientId(), MemoryPersistence())
            mqttClient.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    if (reconnect) {
                        // Re-subscribe to the topics after reconnecting
                        mqttClient.subscribe(mqttTopicWaterCategory)
                    }
                }

                override fun connectionLost(cause: Throwable?) {}

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    runOnUiThread {
                        when (topic) {
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
            mqttClient.subscribe(mqttTopicWaterCategory)

        } catch (e: MqttException) {
            e.printStackTrace()
        }

        //SUNGAI --> SUNGAI OPAK
        try {
            mqttClient2 = MqttClient(mqttBroker, MqttClient.generateClientId(), MemoryPersistence())
            mqttClient2.setCallback(object : MqttCallbackExtended {

                override fun connectionLost(cause: Throwable?) {}

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    runOnUiThread {

                        when (topic){
                            mqttTopicWaterCategory2 -> {
                                val waterCategory2 = String(message?.payload ?: ByteArray(0))
                                textViewKategoriAir2.text = waterCategory2
                            }
                        }
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {}

                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    if (reconnect) {
                        mqttClient2.subscribe(mqttTopicWaterCategory2)
                    }
                }
            })

            val options = MqttConnectOptions()
            options.isAutomaticReconnect = true

            mqttClient2.connect(options)
            mqttClient2.subscribe(mqttTopicWaterCategory2)

        } catch (e: MqttException) {
            e.printStackTrace()
        }

        val buttonDetail1: Button = findViewById(R.id.btnDetail1)
        val buttonDetail2: Button = findViewById(R.id.btnDetail2)

        buttonDetail1.setOnClickListener{
            val intent = Intent(this@MapsActivity, StatusActivityA::class.java)
            startActivity(intent)
        }

        buttonDetail2.setOnClickListener{
            val intent = Intent(this@MapsActivity, StatusActivityB::class.java)
            startActivity(intent)
        }

        val logoBack: ImageView = findViewById(R.id.logoBack)
        logoBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.drawable.slide_right)
    }

    private fun overridePendingTransition(slideRight: Int) {

    }
}