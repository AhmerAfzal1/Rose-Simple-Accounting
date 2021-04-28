package com.ahmer.accounting

import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.adapter.UsersAdapter
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.helper.HelperFunctions
import com.ahmer.accounting.helper.MyCursorLoader
import com.ahmer.accounting.helper.MyDatabaseHelper
import com.ahmer.accounting.user.AddUserProfileData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mAdapter: UsersAdapter
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mLayoutNoUserAccount: LinearLayout
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var myDatabaseHelper: MyDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)

        mDrawerLayout = findViewById(R.id.drawer_layout)
        val drawerToggle = ActionBarDrawerToggle(
            this,
            mDrawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerToggle.drawerArrowDrawable.color = Color.WHITE
        mDrawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        val fabAddNewUser = findViewById<ExtendedFloatingActionButton>(R.id.fabAddNewUser)
        mLayoutNoUserAccount = findViewById(R.id.layoutNoUserAccount)
        mRecyclerView = findViewById(R.id.rvMain)
        mLayoutNoUserAccount = findViewById(R.id.layoutNoUserAccount)
        myDatabaseHelper = MyDatabaseHelper(applicationContext)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.isSmoothScrollbarEnabled = true
        linearLayoutManager.isAutoMeasureEnabled
        mRecyclerView.recycledViewPool.clear()
        mRecyclerView.clearOnScrollListeners()
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && fabAddNewUser.visibility == View.VISIBLE) {
                    fabAddNewUser.hide()
                } else if (dy < 0 && fabAddNewUser.visibility != View.VISIBLE) {
                    fabAddNewUser.show()
                }
            }
        })

        LoaderManager.getInstance(this).initLoader(1, null, this)

        fabAddNewUser.setOnClickListener {
            val intent = Intent(it.context, AddUserProfileData::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N ||
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                ) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            Log.v(Constants.LOG_TAG, "Add record activity opened")
            startActivity(intent)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_backup -> {
                if (HelperFunctions.checkPermission(this)) {
                    HelperFunctions.makeToast(
                        applicationContext,
                        getString(R.string.under_progress)
                    )
                }
            }
            R.id.nav_restore -> {
                if (HelperFunctions.checkPermission(this)) {
                    HelperFunctions.makeToast(
                        applicationContext,
                        getString(R.string.under_progress)
                    )
                }
            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> {
                HelperFunctions.makeToast(applicationContext, getString(R.string.under_progress))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return object : MyCursorLoader(applicationContext) {
            private val mObserver = ForceLoadContentObserver()
            override fun loadInBackground(): Cursor {
                val mCursor = myDatabaseHelper.getAllUserProfileData()
                mCursor.registerContentObserver(mObserver)
                mCursor.setNotificationUri(contentResolver, Constants.UserColumn.USER_TABLE_URI)
                return mCursor
            }
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        mAdapter = UsersAdapter(this, cursor!!)
        if (mAdapter.itemCount > 0) {
            mLayoutNoUserAccount.visibility = View.GONE
            mRecyclerView.visibility = View.VISIBLE
        } else {
            mLayoutNoUserAccount.visibility = View.VISIBLE
            mRecyclerView.visibility = View.GONE
        }
        mRecyclerView.adapter = mAdapter
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Keep empty
    }
}