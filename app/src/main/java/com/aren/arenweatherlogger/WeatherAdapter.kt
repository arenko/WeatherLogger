package com.aren.arenweatherlogger

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*


class WeatherAdapter(private val mValues: List<WeatherModel>, val baseAdapterInterface: BaseAdapterInterface) :
    RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    val dateFormatMonthDayShort = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_weather, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherAdapter.ViewHolder, position: Int) {
        if (mValues.size > 0) {
            holder.weatherModel = mValues[position]

            holder.tv_temperature.setText(holder.weatherModel?.main?.temp!!.toString() + " °C")
            holder.tv_city.setText(holder.weatherModel?.name)

            var date: String = dateFormatMonthDayShort.format(Calendar.getInstance().time)
            holder.tv_date.setText(date)

            holder.mView.setOnClickListener { baseAdapterInterface.onAdapterItemSelectListener(holder.weatherModel!!) }
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        internal val tv_temperature: TextView = mView.findViewById(R.id.tv_temperature)
        internal val tv_date: TextView = mView.findViewById(R.id.tv_date)
        internal val tv_city: TextView = mView.findViewById(R.id.tv_city)
        var weatherModel: WeatherModel? = null
    }
}
