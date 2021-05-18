package com.ahmer.accounting.adapter

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.R
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.helper.MyDialogs
import com.ahmer.accounting.model.UserProfile
import com.ahmer.accounting.ui.AddTransactions
import com.ahmer.accounting.ui.EditUser
import com.google.android.material.card.MaterialCardView

class UsersAdapter(context: Context, cursor: Cursor) :
    RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

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
            id = mCursor.getLong(mCursor.getColumnIndexOrThrow(BaseColumns._ID))
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

            /*
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
            */
        }
        holder.bindItems(userProfile)
        holder.cvMain.setOnClickListener {
            val intent = Intent(mContext, AddTransactions::class.java).apply {
                putExtra("mPosUserID", userProfile.id)
                putExtra("mPosUserName", userProfile.name)
                putExtra("mPosUserPhone", userProfile.phone1)
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
            MyDialogs.showUserProfileInfo(mContext, userProfile)
        }
        holder.ivEditButton.setOnClickListener {
            val intent = Intent(mContext, EditUser::class.java).apply {
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
        holder.ivDeleteButton.setOnClickListener {
            MyDialogs.confirmUserDelete(mContext, userProfile.id, userProfile.name)
        }
    }

    override fun getItemCount(): Int {
        return if (mCursor.isClosed) {
            0
        } else {
            mCursor.count
        }
    }

    class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cvMain: MaterialCardView = itemView.findViewById(R.id.cardView)
        val ivEditButton: ImageView = itemView.findViewById(R.id.ivBtnEdit)
        val ivInfoButton: ImageView = itemView.findViewById(R.id.ivBtnInfo)
        val ivDeleteButton: ImageView = itemView.findViewById(R.id.ivBtnDelete)

        fun bindItems(userProfile: UserProfile) {
            val mUserName = itemView.findViewById<TextView>(R.id.tvGetUserName)
            mUserName.text = userProfile.name
        }
    }
}