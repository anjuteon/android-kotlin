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

@JsonDeserialize(using=MyDeserializer::class)
data class OpenWeatherAPUJSONResponse(val temp: Double, val id: Int)

class MyDeserializer: StdDeserializer<OpenWeatherAPUJSONResponse>(
    OpenWeatherAPUJSONResponse::class.java
) {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?)
            : OpenWeatherAPUJSONResponse {
        val node = p?.codec?.readTree<JsonNode>(p)

        val weather = node?.get("weather") //weather 먼저 접근
        val firstWeather = weather?.elements()?.next() //날씨가 배열이라
        val id = firstWeather?.get("id")?.asInt()

        val main = node?.get("main") //main 접근
        val temp = main?.get("temp")?.asDouble()

        return OpenWeatherAPUJSONResponse(
            temp!!,
            id!!
        )

    }

    class WeatherPageFragment : Fragment() {
        lateinit var weatherImage: ImageView
        lateinit var statusText: TextView
        lateinit var temperatureText: TextView
        var APP_ID = "061815f04d4503272cd600b7efc706e5"


        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.weather_page_fragment, container, false)

            statusText = view.findViewById<TextView>(R.id.weather_status_text)
            temperatureText = view.findViewById<TextView>(R.id.weather_temp_text)
            weatherImage = view.findViewById<ImageView>(R.id.weather_icon)

            /*weather_status.text=arguments?.getString("status")
        weather_temp.text=arguments?.getDouble("temperature").toString()
        weather_icon.setImageResource(R.drawable.sun)*/

            return view
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val lat = arguments?.getDouble("lat")
            val lon = arguments?.getDouble("lon")
            val url = "https://api.openweathermap.org/data/2.5/weather?units-metric&appid=${APP_ID}&lat=${lat}&lon=${lon}"

            APICall(object : APICall.APICallback {
                override fun onComplete(result: String) {
                    //Log.d("mytag", result)

                    var mapper = jacksonObjectMapper()
                    var data = mapper?.readValue<OpenWeatherAPUJSONResponse>(result)

                    temperatureText.text = data.temp.toString()

                    var id=data.id.toString()
                    if(id != null) {
                        statusText.text = when {
                            id.startsWith("2") -> {
                                weatherImage.setImageResource(R.drawable.flash)
                                "천둥, 번개"
                            }
                            id.startsWith("3") -> {
                                weatherImage.setImageResource(R.drawable.rain)
                                "이슬비"
                            }
                            id.startsWith("5") -> {
                                weatherImage.setImageResource(R.drawable.rain)
                                "비"
                            }
                            id.startsWith("6") -> {
                                weatherImage.setImageResource(R.drawable.snow)
                                "눈"
                            }
                            id.startsWith("7") -> {
                                weatherImage.setImageResource(R.drawable.cloudy)
                                "흐림"
                            }
                            id.equals("800") -> {
                                weatherImage.setImageResource(R.drawable.sun)
                                "화창"
                            }
                            id.startsWith("8") -> {
                                weatherImage.setImageResource(R.drawable.cloud)
                                "구름 낌"
                            }
                            else -> "알 수 없음"
                        }
                    }
                }
            }).execute(URL(url))

        }


        companion object {
            fun newInstance(lat: Double, lon: Double)
                    : WeatherPageFragment {
                val fragment = WeatherPageFragment()

                val args = Bundle()
                args.putDouble("lat", lat)
                args.putDouble("lon", lon)
                fragment.arguments = args

                return fragment
            }
        }
    }
}