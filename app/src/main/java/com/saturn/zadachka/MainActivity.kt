package com.saturn.zadachka

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.onesignal.OneSignal
import org.json.JSONException
import java.lang.Exception
import java.math.BigInteger
import java.time.LocalTime

class MainActivity : AppCompatActivity() {
    lateinit var tv : TextView
    lateinit var lv : ListView
    lateinit var spisok :ArrayList<String>
    lateinit var myH : String
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lv = findViewById(R.id.lv)
        val onesig = getString(R.string.onesignal)
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this)
        OneSignal.setAppId(onesig)

        OneSignal.setNotificationOpenedHandler { result ->
            result.notification.additionalData?.let { additionalData ->
                val addData = result.notification.additionalData
                val keys: Iterator<String> = addData.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    if (additionalData.has("toHash")) {
                        myH = additionalData.getString("toHash")
                        spisok = ArrayList()
                            spisok.add(
                                "${
                                    getTime().toString().trim()
                                }  toHash: $myH, hash value: ${sha_gen(myH)}"
                            )

                        lv.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, spisok)
                    }
                    else {
                        try {
                            OneSignal.sendTag(key, addData.get(key).toString())
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

    }



    private fun sha_gen(to_sha_256 : String): String {
        var inputData = to_sha_256.toByteArray()
        var outputdata : ByteArray = byteArrayOf(0)
        try {
           outputdata = sha.encryptSha(inputData, "SHA-256")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val  shaData = BigInteger(1, outputdata)
        return shaData.toString(16)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTime(): LocalTime? {
        val time = LocalTime.now()
        return time
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val savelistelem = spisok

        outState.putStringArrayList("saved", spisok)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val listItemRestore = savedInstanceState.getStringArrayList("saved")
        if (listItemRestore != null) {
            spisok = listItemRestore
            spisok.add(
                "${
                    getTime().toString().trim()
                }  toHash: $myH, hash value: ${sha_gen(myH)}"
            )
        }
    }


}