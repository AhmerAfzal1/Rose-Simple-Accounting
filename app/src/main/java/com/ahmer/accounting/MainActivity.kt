package com.ahmer.accounting

import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.adapter.GetAllUsersAdapter
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.user.AddUserProfileData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {
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
        val myDatabaseHelper = MyDatabaseHelper(this)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.isSmoothScrollbarEnabled = true
        linearLayoutManager.isAutoMeasureEnabled
        rvMain.recycledViewPool.clear()
        rvMain.setHasFixedSize(true)
        rvMain.isNestedScrollingEnabled = false
        rvMain.layoutManager = linearLayoutManager
        rvMain.adapter = GetAllUsersAdapter(this, myDatabaseHelper.getAllUserProfileData())

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

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        TODO("Not yet implemented")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        TODO("Not yet implemented")
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        TODO("Not yet implemented")
    }
}