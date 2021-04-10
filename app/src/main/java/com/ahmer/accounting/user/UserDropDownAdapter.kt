package com.ahmer.accounting.user

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.ahmer.accounting.R
import com.ahmer.accounting.model.UserProfile

class UserDropDownAdapter(context: Context, arrayList: ArrayList<UserProfile>) :
    BaseAdapter(), Filterable {

    private val mContext = context
    private var mArrayList: ArrayList<UserProfile> = arrayList

    override fun getCount(): Int {
        return mArrayList.size
    }

    override fun getItem(position: Int): UserProfile {
        return mArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private val scale = context.resources.displayMetrics.density
    private fun init(position: Int, convertView: View?, parent: ViewGroup): View {
        var viewConvert = convertView

        if (viewConvert == null) {
            viewConvert = LayoutInflater.from(mContext)
                .inflate(R.layout.user_profile_data_dropdown, parent, false)
        }

        val tvId = viewConvert?.findViewById<TextView>(R.id.tvId)
        val tvName = viewConvert?.findViewById<TextView>(R.id.tvName)
        tvId?.text = mArrayList[position].id.toString()
        tvName?.text = mArrayList[position].name
        val px = (scale * 5 + 0.5f).toInt()
        tvId?.setPadding(px, px, px, px)
        tvName?.setPadding(px, px, px, px)

        return viewConvert!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return init(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return init(position, convertView, parent)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(prefix: CharSequence?): FilterResults {
                /*
                val results = FilterResults()
                if (prefix == null || prefix.length == 0)
                {
                    val list = mArrayList
                    results.values = list
                    results.count = list.size
                }
                else
                {
                    val prefixString = prefix.toString().toLowerCase()
                    val unfilteredValues = mArrayList
                    val count = unfilteredValues.size
                    val newValues = ArrayList<CustomerProfile>(count)
                    for (i in 0 until count)
                    {
                        val pc = unfilteredValues.get(i)
                        if (pc != null)
                        {
                            if (pc.name != null && pc.name.startsWith(prefixString))
                            {
                                newValues.add(pc)
                            }*//*
                            else if (pc.getEmail() != null && pc.getEmail().startsWith(prefixString))
                            {
                                newValues.add(pc)
                            }*//*
                        }
                    }
                    results.values = newValues
                    results.count = newValues.size
                }
                return results
                */
                return FilterResults()
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                /*
                if (results != null) {
                    mArrayList = results.values as ArrayList<CustomerProfile>
                }
                notifyDataSetChanged()
                */
            }
        }
    }
}