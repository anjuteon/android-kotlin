package com.example.weatherdustchecker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager

class MainActivity : AppCompatActivity() {
    private lateinit var mPager: ViewPager
    private var lat: Double=0.0
    private var lon: Double=0.0

    lateinit var locationManager: LocationManager
    lateinit var locationListener: LocationListener
    val PERMISSION_REQUEST_CODE =1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationManager=getSystemService(LOCATION_SERVICE)as LocationManager
        locationListener=LocationListener{
            lat=it.latitude
            lon=it.longitude
            Log.d("mytag", lat.toString())
            Log.d("mytag", lon.toString())
            locationManager.removeUpdates(locationListener)

            val pagerAdapter=MyPagerAdapter(supportFragmentManager, lat, lon)
            mPager.adapter=pagerAdapter
        }

        /*locationManager.requestLocationUpdates(
            locationManager.NETWORK_PROVIDER,
            0,
            0f,
            locationListener
        )*/

        //권환 확인
        if(ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED&&
                ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        ==PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, //에뮬레이터에선 GPS_PROVIDER
                0,
                0f,
                locationListener)
        }
        else{
            ActivityCompat.requestPermissions(this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, //arrayOf 배열에 추가
                Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_CODE)

        }

        supportActionBar?.hide()

        mPager=findViewById(R.id.pager)


        mPager.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if(position==0){
                    Toast.makeText(applicationContext,
                    "날씨 페이지입니다.",
                    Toast.LENGTH_SHORT).show()
                }
                else if(position==1){
                    Toast.makeText(applicationContext,
                    "미세먼지 페이지입니다.",
                    Toast.LENGTH_SHORT).show()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })

        /*val transaction=supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container,
            //MyDeserializer.WeatherPageFragment.newInstance(37.58, 126.98),
            MyDeserializer2.DustTestFragment.newInstance(37.58, 126.98))
        transaction.commit()*/

    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode==PERMISSION_REQUEST_CODE){
            var allPermissionsGranted=true
            for(result in grantResults){
                allPermissionsGranted=(result==PackageManager.PERMISSION_GRANTED)
                if(!allPermissionsGranted) break
            }
            if(allPermissionsGranted){
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    locationListener)
            }
            else {
                Toast.makeText(applicationContext,
                "위치 정보 제공 동의가 필요합니다.",
                Toast.LENGTH_SHORT).show()
            }
        }
    }

    //앞에 inner 붙이면 오류안남
    class MyPagerAdapter(fm: FragmentManager, val lat: Double, val lon: Double):FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Fragment {
            return when(position){
                0-> MyDeserializer.WeatherPageFragment.newInstance(lat, lon)
                1-> MyDeserializer2.DustTestFragment.newInstance(lat, lon)
                else->throw Exception("페이지가 존재하지 않음")
            }
        }
    }
}