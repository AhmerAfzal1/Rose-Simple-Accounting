package com.ahmer.accounting

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
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.model.UserProfile
import com.ahmer.accounting.transactions.UserTransactionsReport
import com.ahmer.accounting.user.AddUserProfileData
import com.ahmer.accounting.user.EditUserProfileData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.app_name)

        var isFabOpened = false
        val fabMain = findViewById<FloatingActionButton>(R.id.fabMain)
        val fabAddNewUser = findViewById<ExtendedFloatingActionButton>(R.id.fabAddNewUser)
        val fabBgView = findViewById<View>(R.id.fabBgView)
        val rvMain = findViewById<RecyclerView>(R.id.rvMain)
        rvMain.setHasFixedSize(true)
        rvMain.layoutManager = LinearLayoutManager(this)
        rvMain.adapter = GetAllUsersAdapter(this)

        fun showFab() {
            isFabOpened = true
            fabAddNewUser.visibility = View.VISIBLE
            fabBgView.visibility = View.VISIBLE

            fabMain.animate().rotationBy(135f)
            fabAddNewUser.extend()
            fabBgView.animate().alpha(1f)
        }

        fun hideFab() {
            isFabOpened = false
            fabMain.animate().rotationBy(0f)
            fabBgView.animate().alpha(0f)

            fabAddNewUser.visibility = View.GONE
            fabAddNewUser.shrink()
            fabBgView.visibility = View.GONE
        }

        fabMain.setOnClickListener {
            if (!isFabOpened) {
                showFab()
            } else {
                hideFab()
            }
        }

        fabAddNewUser.setOnClickListener {
            val intent = Intent(this, AddUserProfileData::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N ||
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                ) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            Log.v(Constants.LOG_TAG, "Add record activity opened")
            hideFab()
            startActivity(intent)
        }
    }
}

class GetAllUsersAdapter(context: Context) : RecyclerView.Adapter<UsersViewHolder>() {

    private val mContext = context
    private val mMyDatabaseHelper = MyDatabaseHelper(mContext)
    private val mUserProfileList = mMyDatabaseHelper.getUserProfileData()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_profile_data_container, parent, false)
        return UsersViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        // Position + 1 due to position start from 0
        val mUserCredit = mMyDatabaseHelper.getSumForColumns(position + 1, true)
        val mUserDebit = mMyDatabaseHelper.getSumForColumns(position + 1, false)
        holder.bindItems(mUserProfileList[position])
        holder.cvMain.setOnClickListener {
            val intent = Intent(mContext, UserTransactionsReport::class.java).apply {
                putExtra("mPosUserID", mUserProfileList[position].id)
                putExtra("mPosUserName", mUserProfileList[position].name)
                putExtra("mPosUserDebit", mUserDebit)
                putExtra("mPosUserCredit", mUserCredit)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N ||
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                ) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            mContext.startActivity(intent)

        }
        holder.ivInfoButton.setOnClickListener {
            showDialogMoreInfo(position)
        }
        holder.ivEditButton.setOnClickListener {
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
            Log.v(Constants.LOG_TAG, "Dialog ID: ${mUserProfileList[position].id}")
            Log.v(Constants.LOG_TAG, "Dialog Name: ${mUserProfileList[position].name}")
            Log.v(Constants.LOG_TAG, "Dialog Gender: ${mUserProfileList[position].gender}")
            Log.v(Constants.LOG_TAG, "Dialog Address: ${mUserProfileList[position].address}")
            Log.v(Constants.LOG_TAG, "Dialog Phone1: ${mUserProfileList[position].phone1}")
            Log.v(Constants.LOG_TAG, "Dialog Phone2: ${mUserProfileList[position].phone2}")
            Log.v(Constants.LOG_TAG, "Dialog Email: ${mUserProfileList[position].email}")
            Log.v(Constants.LOG_TAG, "Dialog Comments: ${mUserProfileList[position].comment}")
            Log.v(Constants.LOG_TAG, "Dialog Created: ${mUserProfileList[position].created}")
            Log.v(Constants.LOG_TAG, "Dialog Modified: ${mUserProfileList[position].modified}")
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
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}

class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val cvMain: MaterialCardView = itemView.findViewById(R.id.cardView)
    val ivEditButton: ImageView = itemView.findViewById(R.id.ivBtnEdit)
    val ivInfoButton: ImageView = itemView.findViewById(R.id.ivBtnInfo)

    fun bindItems(userProfile: UserProfile) {
        val mUserId = itemView.findViewById<TextView>(R.id.tvGetUserID)
        val mUserName = itemView.findViewById<TextView>(R.id.tvGetUserName)
        mUserId.text = userProfile.id.toString()
        mUserName.text = userProfile.name
    }
}