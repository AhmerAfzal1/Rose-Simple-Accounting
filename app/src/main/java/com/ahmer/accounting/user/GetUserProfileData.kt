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
import com.google.android.material.card.MaterialCardView

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
        val adapter = UserDataAdapter(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}

class UserDataAdapter(context: Context) : RecyclerView.Adapter<UserDataHolder>() {

    private val mContext = context
    private val mMyDatabaseHelper = MyDatabaseHelper(mContext)
    private val mUserProfileList = mMyDatabaseHelper.getUserProfileData()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserDataHolder {
        val inflater = LayoutInflater.from(parent.context)
                .inflate(R.layout.user_profile_data_container, parent, false)
        return UserDataHolder(inflater)
    }

    override fun onBindViewHolder(holder: UserDataHolder, position: Int) {
        holder.bindItems(mUserProfileList[position])
        holder.cardView.setOnClickListener {
            showDialogMoreInfo(position)
        }
        holder.tvEditButton.setOnClickListener {
            val intent = Intent(mContext, EditUserProfileData::class.java).apply {
                putExtra("mID", mUserProfileList[position].id)
                putExtra("mName", mUserProfileList[position].name)
                putExtra("mGender", mUserProfileList[position].gender)
                putExtra("mAddress", mUserProfileList[position].address)
                putExtra("mCity", mUserProfileList[position].city)
                putExtra("mPhone1", mUserProfileList[position].phone1)
                putExtra("mPhone2", mUserProfileList[position].phone2)
                putExtra("mEmail", mUserProfileList[position].email)
                putExtra("mComments", mUserProfileList[position].comment)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N ||
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                ) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mUserProfileList.size
    }

    private fun showDialogMoreInfo(position: Int) {
        try {
            val dialog = Dialog(mContext)
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
            Log.v(LOG_TAG, "Dialog ID: ${mUserProfileList[position].id}")
            Log.v(LOG_TAG, "Dialog Name: ${mUserProfileList[position].name}")
            Log.v(LOG_TAG, "Dialog Gender: ${mUserProfileList[position].gender}")
            Log.v(LOG_TAG, "Dialog Address: ${mUserProfileList[position].address}")
            Log.v(LOG_TAG, "Dialog Phone1: ${mUserProfileList[position].phone1}")
            Log.v(LOG_TAG, "Dialog Phone2: ${mUserProfileList[position].phone2}")
            Log.v(LOG_TAG, "Dialog Email: ${mUserProfileList[position].email}")
            Log.v(LOG_TAG, "Dialog Comments: ${mUserProfileList[position].comment}")
            Log.v(LOG_TAG, "Dialog Created: ${mUserProfileList[position].created}")
            Log.v(LOG_TAG, "Dialog Modified: ${mUserProfileList[position].modified}")
            getID.text = mUserProfileList[position].id.toString()
            getName.text = mUserProfileList[position].name
            getGender.text = mUserProfileList[position].gender
            getAddress.text = mUserProfileList[position].address
            getCity.text = mUserProfileList[position].city
            getPhone1.text = mUserProfileList[position].phone1
            getPhone2.text = mUserProfileList[position].phone2
            getEmail.text = mUserProfileList[position].email
            getComments.text = mUserProfileList[position].comment
            getCreated.text = mUserProfileList[position].created
            getModified.text = mUserProfileList[position].modified
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

    val cardView: MaterialCardView = itemView.findViewById(R.id.cardView)
    val tvEditButton: TextView = itemView.findViewById(R.id.tvBtnEdit)

    fun bindItems(userProfile: UserProfile) {
        val userID = itemView.findViewById<TextView>(R.id.tvGetUserID)
        val userName = itemView.findViewById<TextView>(R.id.tvGetUserName)
        userID.text = userProfile.id.toString()
        userName.text = userProfile.name
    }
}