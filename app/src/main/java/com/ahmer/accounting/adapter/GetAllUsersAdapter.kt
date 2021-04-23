package com.ahmer.accounting.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.provider.BaseColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.R
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.model.UserProfile
import com.ahmer.accounting.transactions.UserTransactionsReport
import com.ahmer.accounting.user.EditUserProfileData
import com.google.android.material.card.MaterialCardView
import com.google.firebase.crashlytics.FirebaseCrashlytics

class GetAllUsersAdapter(context: Context, cursor: Cursor) :
    RecyclerView.Adapter<GetAllUsersAdapter.UsersViewHolder>() {

    private val mContext = context
    private val mCursor = cursor

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_profile_data_container, parent, false)
        return UsersViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        mCursor.moveToPosition(position)
        val userProfile = UserProfile().apply {
            id = mCursor.getInt(mCursor.getColumnIndexOrThrow(BaseColumns._ID))
            name =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.NAME))
            gender =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.GENDER))
            address =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.ADDRESS))
            city =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.CITY))
            phone1 =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.PHONE1))
            phone2 =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.PHONE2))
            email =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.EMAIL))
            comment =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.COMMENTS))
            created =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.CREATED_ON))
            modified =
                mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.LAST_MODIFIED))

            val stringBuilder = StringBuilder()
            stringBuilder.append("GetUserProfileData ${BaseColumns._ID}: ")
                .append(mCursor.getInt(mCursor.getColumnIndexOrThrow(BaseColumns._ID)))
            stringBuilder.append("\nGetUserProfileData ${Constants.UserColumn.NAME}: ")
                .append(mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.NAME)))
            stringBuilder.append("\nGetUserProfileData ${Constants.UserColumn.GENDER}: ")
                .append(mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.GENDER)))
            stringBuilder.append("\nGetUserProfileData ${Constants.UserColumn.ADDRESS}: ")
                .append(mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.ADDRESS)))
            stringBuilder.append("\nGetUserProfileData ${Constants.UserColumn.CITY}: ")
                .append(mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.CITY)))
            stringBuilder.append("\nGetUserProfileData ${Constants.UserColumn.PHONE1}: ")
                .append(mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.PHONE1)))
            stringBuilder.append("\nGetUserProfileData ${Constants.UserColumn.PHONE2}: ")
                .append(mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.PHONE2)))
            stringBuilder.append("\nGetUserProfileData ${Constants.UserColumn.EMAIL}: ")
                .append(mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.EMAIL)))
            stringBuilder.append("\nGetUserProfileData ${Constants.UserColumn.COMMENTS}: ")
                .append(mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.COMMENTS)))
            stringBuilder.append("\nGetUserProfileData ${Constants.UserColumn.CREATED_ON}: ")
                .append(mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.CREATED_ON)))
            stringBuilder.append("\nGetUserProfileData ${Constants.UserColumn.LAST_MODIFIED}: ")
                .append(mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.UserColumn.LAST_MODIFIED)))
            Log.v(Constants.LOG_TAG, stringBuilder.toString())
        }
        holder.bindItems(userProfile)
        holder.cvMain.setOnClickListener {
            val intent = Intent(mContext, UserTransactionsReport::class.java).apply {
                putExtra("mPosUserID", userProfile.id)
                putExtra("mPosUserName", userProfile.name)
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
            showDialogMoreInfo(userProfile)
        }
        holder.ivEditButton.setOnClickListener {
            val intent = Intent(mContext, EditUserProfileData::class.java).apply {
                putExtra("mID", userProfile.id)
                putExtra("mName", userProfile.name)
                putExtra("mGender", userProfile.gender)
                putExtra("mAddress", userProfile.address)
                putExtra("mCity", userProfile.city)
                putExtra("mPhone1", userProfile.phone1)
                putExtra("mPhone2", userProfile.phone2)
                putExtra("mEmail", userProfile.email)
                putExtra("mComments", userProfile.comment)
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
        return if (mCursor.isClosed) {
            0
        } else {
            mCursor.count
        }
    }

    private fun showDialogMoreInfo(userProfile: UserProfile) {
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
            Log.v(Constants.LOG_TAG, "Dialog ID: ${userProfile.id}")
            Log.v(Constants.LOG_TAG, "Dialog Name: ${userProfile.name}")
            Log.v(Constants.LOG_TAG, "Dialog Gender: ${userProfile.gender}")
            Log.v(Constants.LOG_TAG, "Dialog Address: ${userProfile.address}")
            Log.v(Constants.LOG_TAG, "Dialog Phone1: ${userProfile.phone1}")
            Log.v(Constants.LOG_TAG, "Dialog Phone2: ${userProfile.phone2}")
            Log.v(Constants.LOG_TAG, "Dialog Email: ${userProfile.email}")
            Log.v(Constants.LOG_TAG, "Dialog Comments: ${userProfile.comment}")
            Log.v(Constants.LOG_TAG, "Dialog Created: ${userProfile.created}")
            Log.v(Constants.LOG_TAG, "Dialog Modified: ${userProfile.modified}")
            getID.text = userProfile.id.toString()
            getName.text = userProfile.name
            getGender.text = userProfile.gender
            getAddress.text = userProfile.address
            getCity.text = userProfile.city
            getPhone1.text = userProfile.phone1
            getPhone2.text = userProfile.phone2
            getEmail.text = userProfile.email
            getComments.text = userProfile.comment
            getCreated.text = userProfile.created
            getModified.text = userProfile.modified
            btnOk.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
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
}