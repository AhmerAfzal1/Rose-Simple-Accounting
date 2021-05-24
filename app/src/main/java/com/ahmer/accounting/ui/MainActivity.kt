package com.ahmer.accounting.ui

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.R
import com.ahmer.accounting.adapter.UsersAdapter
import com.ahmer.accounting.databinding.ActivityMainBinding
import com.ahmer.accounting.helper.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.ahmer.utils.constants.PermissionConstants
import io.ahmer.utils.utilcode.*
import java.io.File

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: UsersAdapter
    private lateinit var mResultLauncherCreateDB: ActivityResultLauncher<Intent>
    private lateinit var mResultLauncherRestoreDB: ActivityResultLauncher<Intent>
    private lateinit var myDatabaseHelper: MyDatabaseHelper
    private lateinit var mTvTotalAllDebit: TextView
    private lateinit var mTvTotalAllCredit: TextView
    private lateinit var mTvTotalAllBalances: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.executePendingBindings()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBinding.appBar.toolbarMain.overflowIcon?.setTint(Color.WHITE)
        }
        setSupportActionBar(mBinding.appBar.toolbarMain)

        val drawerToggle = ActionBarDrawerToggle(
            this,
            mBinding.drawerLayout,
            mBinding.appBar.toolbarMain,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerToggle.drawerArrowDrawable.color = Color.WHITE
        mBinding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        mBinding.navView.setNavigationItemSelectedListener(this)
        val headerNavView = mBinding.navView.getHeaderView(0)
        mTvTotalAllDebit = headerNavView.findViewById(R.id.tvAllTotalDebit)
        mTvTotalAllCredit = headerNavView.findViewById(R.id.tvAllTotalCredit)
        mTvTotalAllBalances = headerNavView.findViewById(R.id.tvAllTotalBalance)

        mBinding.appBar.mActivity = this
        myDatabaseHelper = MyDatabaseHelper()

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.isSmoothScrollbarEnabled = true
        linearLayoutManager.isAutoMeasureEnabled
        mBinding.appBar.contentMain.rvMain.recycledViewPool.clear()
        mBinding.appBar.contentMain.rvMain.clearOnScrollListeners()
        mBinding.appBar.contentMain.rvMain.setHasFixedSize(true)
        mBinding.appBar.contentMain.rvMain.isNestedScrollingEnabled = false
        mBinding.appBar.contentMain.rvMain.layoutManager = linearLayoutManager
        mBinding.appBar.contentMain.rvMain.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && mBinding.appBar.fabAddNewUser.visibility == View.VISIBLE) {
                    mBinding.appBar.fabAddNewUser.hide()
                } else if (dy < 0 && mBinding.appBar.fabAddNewUser.visibility != View.VISIBLE) {
                    mBinding.appBar.fabAddNewUser.show()
                }
            }
        })

        LoaderManager.getInstance(this).initLoader(1, null, this)

        mResultLauncherCreateDB =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    myDatabaseHelper.backupOrRestore(uri, true)
                }
            }

        mResultLauncherRestoreDB =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    myDatabaseHelper.backupOrRestore(uri, false)
                }
            }

        if (NetworkUtils.isConnected()) {
            MyAds.loadInterstitialAd(this)
        }
    }

    fun fabRun(view: View) {
        val intent = Intent(view.context, AddUser::class.java).apply {
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isCheckable = false
        when (item.itemId) {
            R.id.nav_backup -> {
                if (PermissionUtils.isGranted(PermissionConstants.STORAGE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val fileName =
                            "backup_${HelperFunctions.getDateTime("ddMMyyHHmmss", false)}.abf"
                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/vnd.sqlite3"
                            putExtra(Intent.EXTRA_TITLE, fileName)
                        }
                        mResultLauncherCreateDB.launch(intent)
                    } else {
                        val src = File(getDatabasePath(Constants.DATABASE_NAME).absolutePath)
                        val dst = File(
                            "${Environment.getExternalStorageDirectory()}/${getString(R.string.app_name)}",
                            "Backup.abf"
                        )
                        if (dst.exists()) {
                            dst.delete()
                        }
                        val isBackup = FileUtils.copy(src, dst)
                        if (isBackup) {
                            ToastUtils.showShort(getString(R.string.backup_complete))
                        }
                    }
                }
            }
            R.id.nav_restore -> {
                if (PermissionUtils.isGranted(PermissionConstants.STORAGE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "*/*"
                            Intent.createChooser(intent, getString(R.string.choose_backup))
                        }
                        mResultLauncherRestoreDB.launch(intent)
                    } else {
                        val src = File(
                            "${Environment.getExternalStorageDirectory()}/${getString(R.string.app_name)}",
                            "Backup.abf"
                        )
                        if (src.exists()) {
                            myDatabaseHelper.backupOrRestore(Uri.fromFile(src), false)
                        } else {
                            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "*/*"
                                Intent.createChooser(intent, getString(R.string.choose_backup))
                            }
                            mResultLauncherRestoreDB.launch(intent)
                        }
                    }
                }
            }
            R.id.nav_settings -> {
                val intent = Intent(applicationContext, Settings::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
                startActivity(intent)
            }
            R.id.nav_exit -> {
                AppUtils.exitApp()
            }
        }
        mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun searchUser(name: String) {
        val cursor = myDatabaseHelper.searchUsersName(name)
        mAdapter = UsersAdapter(applicationContext, cursor)
        mBinding.appBar.contentMain.mAdapter = mAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_main, menu)
        val searchItem = menu.findItem(R.id.menu_search_user_name)
        val searchView: SearchView = searchItem.actionView as SearchView
        val editText: EditText = searchView.findViewById(R.id.search_src_text)
        editText.setTextColor(Color.WHITE)
        editText.setHintTextColor(Color.GRAY)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchUser(query!!)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchUser(newText!!)
                return false
            }
        })
        return true
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
        mBinding.appBar.contentMain.mVisibility = mAdapter.itemCount <= 0
        mBinding.appBar.contentMain.mAdapter = mAdapter
        val mAllCredit = myDatabaseHelper.getSumForColumns(0, "Credit", true)
        val mAllDebit = myDatabaseHelper.getSumForColumns(0, "Debit", true)
        val mAllBalance = mAllCredit - mAllDebit
        mTvTotalAllDebit.text = HelperFunctions.getRoundedValue(mAllDebit)
        mTvTotalAllCredit.text = HelperFunctions.getRoundedValue(mAllCredit)
        mTvTotalAllBalances.text = HelperFunctions.getRoundedValue(mAllBalance)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Keep empty
    }

    override fun onDestroy() {
        super.onDestroy()
        myDatabaseHelper.close()
    }
}