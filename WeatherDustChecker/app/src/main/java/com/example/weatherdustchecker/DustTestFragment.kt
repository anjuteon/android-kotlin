package com.example.weatherdustchecker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.net.URL

@JsonDeserialize(using=MyDeserializer2::class)
data class OpenWeatherwaqiJSON(val pm10: Int, val pm25: Int)

class MyDeserializer2: StdDeserializer<OpenWeatherwaqiJSON>(
    OpenWeatherwaqiJSON::class.java
) {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?)
            : OpenWeatherwaqiJSON {

        fun checkCategory(aqi: Int):String{
            return when(aqi){
                in(1..100)->"좋음"
                in(100..200)->"보통"
                in(200..300)->"나쁨"
                else->"매우 나쁨"
            }
        }
        val node = p?.codec?.readTree<JsonNode>(p)

        val pm10=node?.get("data")?.get("iaqi")?.get("pm10")?.get("v")?.asInt()
        val pm25=node?.get("data")?.get("iaqi")?.get("pm25")?.get("v")?.asInt()

        return OpenWeatherwaqiJSON(
            pm10!!, pm25!!
        )

    }

    class DustTestFragment : Fragment() {
        lateinit var dustText: TextView
        lateinit var chodustText: TextView
        lateinit var dustImage: ImageView
        lateinit var dust: TextView
        lateinit var chodust: TextView

        var MyToken = "2c35a88bf3c69f1ff0324738c37d5ede46ec744f"

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.dust_test_fragment, container, false)

            dustText = view.findViewById<TextView>(R.id.dust_text)
            chodustText = view.findViewById<TextView>(R.id.chodust_text)
            dustImage= view.findViewById<ImageView>(R.id.dust_icon)
            dust=view.findViewById<TextView>(R.id.dust_status_text)
            chodust=view.findViewById<TextView>(R.id.chodust_status_text)

            return view
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val lat = arguments?.getDouble("lat")
            val lon = arguments?.getDouble("lon")
            val url = "http://api.waqi.info/feed/geo:${lat};${lon}/?token=${MyToken}"

            APICall(object : APICall.APICallback {
                override fun onComplete(result: String) {
                    //Log.d("mytag", result)

                    var mapper = jacksonObjectMapper()
                    var data = mapper?.readValue<OpenWeatherwaqiJSON>(result)

                    /*dustText.text = data.pm10.toString()
                    chodustText.text = data.pm25.toString()*/
                    dustText.text
                    chodustText.text
                    dust.text=data.pm10.toString()
                    chodust.text=data.pm25.toString()

                    val pm10=data.pm10
                    val pm25=data.pm25

                    //pm10 //초미세먼지
                    if(pm25<100) {
                        dustImage.setImageResource(R.drawable.good)
                        chodustText.text=("좋음 (초미세먼지)")
                    }
                    else if(100>=pm25&&pm25<200) {
                        dustImage.setImageResource(R.drawable.normal)
                        chodustText.text=("보통 (초미세먼지)")
                    }
                    else if(pm25<=200&&pm25<300) {
                        dustImage.setImageResource(R.drawable.bad)
                        chodustText.text=("나쁨 (초미세먼지)")
                    }
                    else if(pm25>=300) {
                        dustImage.setImageResource(R.drawable.very_bad)
                        dustText.text=("매우 나쁨 (미세먼지)")
                    }

                    //pm25 //미세먼지
                    if(pm10<100) {
                        dustText.text=("좋음 (미세먼지)")
                    }
                    else if(100>=pm10&&pm10<200) {
                        dustText.text=("보통 (미세먼지)")
                    }
                    else if(pm10<=200&&pm10<300) {
                        dustText.text=("나쁨 (미세먼지)")
                    }
                    else if(pm10>=300) {
                        dustText.text=("매우 나쁨 (미세먼지)")
                    }
                }
            }).execute(URL(url))

        }


        companion object {
            fun newInstance(lat: Double, lon: Double)
                    : DustTestFragment {
                val fragment = DustTestFragment()

                val args = Bundle()
                args.putDouble("lat", lat)
                args.putDouble("lon", lon)
                fragment.arguments = args

                return fragment
            }
        }
    }
}