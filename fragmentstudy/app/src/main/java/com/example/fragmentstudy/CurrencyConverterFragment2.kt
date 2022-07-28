package com.example.fragmentstudy

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class CurrencyConverterFragment2 : Fragment() //필요한 정보 전달
{
    val currencyExchangeMap=mapOf( //나중에 수정할거면 mutableMapOf
        "USD" to 1.0,
        "EUR" to 0.9,
        "JPY" to 110.0,
        "KRW" to 1150.0
    )

    fun calculateCurrency(amount: Double, from: String, to: String): Double{
        var USDAmount=if(from!="USD") {
            (amount / currencyExchangeMap[from]!!)
        }
        else{
            amount
        }
        return currencyExchangeMap[to]!!*USDAmount
    }

    lateinit var fromCurrency: String
    lateinit var toCurrency: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(
            R.layout.currency_converter_fragment2,
            container,
            false
        )

        val calculateBtn=view.findViewById<Button>(R.id.calculate)
        val amount = view.findViewById<EditText>(R.id.amount)
        val result=view.findViewById<TextView>(R.id.result)
        val exchangeType=view.findViewById<TextView>(R.id.exchange_type)

        fromCurrency=arguments?.getString("from", "USD")!!
        toCurrency=arguments?.getString("to", "USD")!!

        exchangeType.text="${fromCurrency} => ${toCurrency} 변환"

       calculateBtn.setOnClickListener{
            result.text=calculateCurrency(
                amount.text.toString().toDouble(),
                fromCurrency,
                toCurrency
            ).toString()
        }

        return view
    }

    companion object{
        fun newInstance(from: String, to: String):CurrencyConverterFragment2{
            val fragment=CurrencyConverterFragment2()

            val args=Bundle()
            args.putString("from", from)
            args.putString("to", to)
            fragment.arguments=args

            return fragment
        }
    }
}