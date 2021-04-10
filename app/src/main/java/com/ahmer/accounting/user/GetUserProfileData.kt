package com.ahmer.accounting.user

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.R
import com.ahmer.accounting.helper.Constants.Companion.LOG_TAG
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.model.UserProfile
import com.google.android.material.appbar.MaterialToolbar

class GetUserData : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_records)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.title_all_user_record)
        toolbar.setOnClickListener {
            finish()
        }
        val recyclerView = findViewById<RecyclerView>(R.id.rvGetAllRecords)
        val myDatabaseHelper = MyDatabaseHelper(this)
        val adapter = UserDataAdapter(this, myDatabaseHelper.getUserProfileData())
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}

class UserDataAdapter(
    private val context: Context,
    private val userProfileList: ArrayList<UserProfile>
) : RecyclerView.Adapter<UserDataHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserDataHolder {
        val inflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_profile_data_container, parent, false)
        return UserDataHolder(inflater)
    }

    override fun onBindViewHolder(holder: UserDataHolder, position: Int) {
        holder.bindItems(userProfileList[position])
        holder.layoutUserID.setOnClickListener {
            showDialogMoreInfo(position)
        }
        holder.layoutUserName.setOnClickListener {
            showDialogMoreInfo(position)
        }
        holder.layoutEditProfile.setOnClickListener {
            showDialogMoreInfo(position)
        }
        holder.tvEditButton.setOnClickListener {
            val intent = Intent(context, EditUserProfileData::class.java).apply {
                putExtra("mID", userProfileList[position].id)
                putExtra("mName", userProfileList[position].name)
                putExtra("mGender", userProfileList[position].gender)
                putExtra("mAddress", userProfileList[position].address)
                putExtra("mCity", userProfileList[position].city)
                putExtra("mPhone1", userProfileList[position].phone1)
                putExtra("mPhone2", userProfileList[position].phone2)
                putExtra("mEmail", userProfileList[position].email)
                putExtra("mComments", userProfileList[position].comment)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N ||
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                ) {
                    Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userProfileList.size
    }

    private fun showDialogMoreInfo(position: Int) {
        try {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.user_profile_data_dialog)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.setCancelable(false)
            val getID = dialog.findViewById<TextView>(R.id.dialogUserID)
            val getName = dialog.findViewById<TextView>(R.id.dialogUserName)
            val getGender = dialog.findViewById<TextView>(R.id.dialogUserGender)
            val getAddress = dialog.findViewById<TextView>(R.id.dialogUserAddress)
            val getCity = dialog.findViewById<TextView>(R.id.dialogUserCity)
            val getPhone1 = dialog.findViewById<TextView>(R.id.dialogUserPhone1)
            val getPhone2 = dialog.findViewById<TextView>(R.id.dialogUserPhone2)
            val getEmail = dialog.findViewById<TextView>(R.id.dialogUserEmail)
            val getComments = dialog.findViewById<TextView>(R.id.dialogUserComments)
            val getCreated = dialog.findViewById<TextView>(R.id.dialogUserCreated)
            val getModified = dialog.findViewById<TextView>(R.id.dialogUserModified)
            val btnOk = dialog.findViewById<Button>(R.id.btnOk)
            Log.v(LOG_TAG, "Dialog ID: ${userProfileList[position].id}")
            Log.v(LOG_TAG, "Dialog Name: ${userProfileList[position].name}")
            Log.v(LOG_TAG, "Dialog Gender: ${userProfileList[position].gender}")
            Log.v(LOG_TAG, "Dialog Address: ${userProfileList[position].address}")
            Log.v(LOG_TAG, "Dialog Phone1: ${userProfileList[position].phone1}")
            Log.v(LOG_TAG, "Dialog Phone2: ${userProfileList[position].phone2}")
            Log.v(LOG_TAG, "Dialog Email: ${userProfileList[position].email}")
            Log.v(LOG_TAG, "Dialog Comments: ${userProfileList[position].comment}")
            Log.v(LOG_TAG, "Dialog Created: ${userProfileList[position].created}")
            Log.v(LOG_TAG, "Dialog Modified: ${userProfileList[position].modified}")
            getID.text = userProfileList[position].id.toString()
            getName.text = userProfileList[position].name
            getGender.text = userProfileList[position].gender
            getAddress.text = userProfileList[position].address
            getCity.text = userProfileList[position].city
            getPhone1.text = userProfileList[position].phone1
            getPhone2.text = userProfileList[position].phone2
            getEmail.text = userProfileList[position].email
            getComments.text = userProfileList[position].comment
            getCreated.text = userProfileList[position].created
            getModified.text = userProfileList[position].modified
            btnOk.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        } catch (e: Exception) {
            Log.v(LOG_TAG, e.printStackTrace().toString())
        }
    }
}

class UserDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val layoutUserID: LinearLayout = itemView.findViewById(R.id.linearLayoutUserID)
    val layoutUserName: LinearLayout = itemView.findViewById(R.id.linearLayoutUserName)
    val layoutEditProfile: RelativeLayout = itemView.findViewById(R.id.relativeLayoutEditProfile)
    val tvEditButton: TextView = itemView.findViewById(R.id.tvBtnEdit)

    fun bindItems(userProfile: UserProfile) {
        val userID = itemView.findViewById<TextView>(R.id.tvGetUserID)
        val userName = itemView.findViewById<TextView>(R.id.tvGetUserName)
        userID.text = userProfile.id.toString()
        userName.text = userProfile.name
    }
}