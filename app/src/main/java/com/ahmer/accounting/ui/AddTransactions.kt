package com.ahmer.accounting.ui

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmer.accounting.R
import com.ahmer.accounting.adapter.TransactionsAdapter
import com.ahmer.accounting.adapter.TransactionsBalanceAdapter
import com.ahmer.accounting.databinding.TransAddBinding
import com.ahmer.accounting.dialog.TransactionsAdd
import com.ahmer.accounting.helper.*
import com.ahmer.accounting.model.TransactionsBalance
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.ahmer.utils.utilcode.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class AddTransactions : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>,
    ActionMode.Callback {

    private lateinit var mAdapterBalance: TransactionsBalanceAdapter
    private lateinit var mAdapterTrans: TransactionsAdapter
    private lateinit var mBinding: TransAddBinding
    private lateinit var mResultLauncherGeneratePdf: ActivityResultLauncher<Intent>
    private lateinit var myDatabaseHelper: MyDatabaseHelper
    private var mActionMode: ActionMode? = null
    private var mIsMultiSelect = false
    private var mIsSelectAll = false
    private var mSelectedIds = ArrayList<Int>()
    private var mUserId: Long = 0
    private var mUserName: String? = null
    private var mUserPhone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.trans_add)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBinding.toolbarRvTrans.overflowIcon?.setTint(Color.WHITE)
        }
        setSupportActionBar(mBinding.toolbarRvTrans)
        mBinding.rvTransactionsActivity = this

        mUserId = intent.getLongExtra("mPosUserID", -1)
        mUserName = intent.getStringExtra("mPosUserName")
        mUserPhone = intent.getStringExtra("mPosUserPhone")

        myDatabaseHelper = MyDatabaseHelper()

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.isSmoothScrollbarEnabled = true
        linearLayoutManager.isAutoMeasureEnabled
        mBinding.rvGetAllRecords.recycledViewPool.clear()
        mBinding.rvGetAllRecords.clearOnScrollListeners()
        mBinding.rvGetAllRecords.setHasFixedSize(true)
        mBinding.rvGetAllRecords.isNestedScrollingEnabled = false
        mBinding.rvGetAllRecords.layoutManager = linearLayoutManager
        mBinding.rvGetAllRecords.addOnItemTouchListener(object :
            RecyclerItemClickListener(applicationContext,
                mBinding.rvGetAllRecords, object : OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        if (mIsMultiSelect) {
                            //if multiple selection is enabled then select item on single click else perform normal click on item.
                            multiSelect(position)
                        }
                    }

                    override fun onItemLongClick(view: View, position: Int) {
                        if (!mIsMultiSelect) {
                            mSelectedIds = ArrayList()
                            mIsMultiSelect = true
                            mActionMode = startActionMode(this@AddTransactions)
                        }
                        multiSelect(position)
                    }

                }) {})

        val gridLayoutManager = GridLayoutManager(this, 3)
        gridLayoutManager.isAutoMeasureEnabled
        mBinding.rvTotalBalances.recycledViewPool.clear()
        mBinding.rvTotalBalances.clearOnScrollListeners()
        mBinding.rvTotalBalances.setHasFixedSize(true)
        mBinding.rvTotalBalances.isNestedScrollingEnabled = false
        mBinding.rvTotalBalances.layoutManager = gridLayoutManager

        LoaderManager.getInstance(this).initLoader(1, null, this)

        mResultLauncherGeneratePdf =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    if (uri != null && mUserName != null) {
                        var isGenerated = false
                        val job: Job = CoroutineScope(Dispatchers.IO).launch {
                            isGenerated =
                                GeneratePdf.createPdf(
                                    applicationContext,
                                    uri,
                                    mUserId,
                                    mUserName!!
                                )
                        }
                        job.invokeOnCompletion {
                            CoroutineScope(Dispatchers.Main).launch {
                                if (isGenerated) {
                                    ToastUtils.showShort(getString(R.string.pdf_generated))
                                }
                            }
                        }
                    }
                }
            }

        MyAds.loadInterstitialAd(this)
    }

    fun getUserName(): String {
        return mUserName!!
    }

    fun getUserPhone(): String {
        return mUserPhone!!
    }

    private fun searchTransactionDescription(keyword: String) {
        val cursor = myDatabaseHelper.searchTransDesc(mUserId, keyword)
        mAdapterTrans = TransactionsAdapter(this, cursor)
        mBinding.rvTransactionsAdapter = mAdapterTrans
    }

    private fun multiSelect(position: Int) {
        if (position < mAdapterTrans.itemCount) {
            if (mActionMode != null) {
                if (mSelectedIds.contains(position)) {
                    mSelectedIds.remove(position)
                    mAdapterTrans.removeSelectedIds(position)
                } else {
                    mSelectedIds.add(position)
                    mAdapterTrans.addSelectedIds(mSelectedIds)
                }
                if (mSelectedIds.size > 0) {
                    //Show title selected item count on action mode.
                    mActionMode!!.title = "${mSelectedIds.size} selected"
                } else {
                    mActionMode!!.title = "" //Remove item count from action mode.
                    mActionMode!!.finish() //Hide action mode.
                }
                mAdapterTrans.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_trans, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_trans -> {
                val dialog = TransactionsAdd(this, mUserId)
                val dialogWindow: Window? = dialog.window
                dialogWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
                dialogWindow.setLayout(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            R.id.menu_search_trans_desc -> {
                val searchView: SearchView = item.actionView as SearchView
                val editText: EditText = searchView.findViewById(R.id.search_src_text)
                editText.setTextColor(Color.WHITE)
                editText.setHintTextColor(Color.GRAY)
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchTransactionDescription(query!!)
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        searchTransactionDescription(newText!!)
                        return false
                    }
                })
            }
            R.id.menu_export_to_pdf -> {
                if (mAdapterTrans.itemCount > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val fileName =
                            HelperFunctions.getDateTime("ddMMyyHHmmss", false) + ".pdf"
                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_TITLE, fileName)
                        }
                        mResultLauncherGeneratePdf.launch(intent)
                    } else {
                        val mPath = Environment.getExternalStorageDirectory().absolutePath
                        val mDirPath = File("$mPath/${getString(R.string.app_name)}")
                        if (!mDirPath.exists()) {
                            mDirPath.mkdirs()
                        }
                        val mFileName =
                            File(
                                mDirPath,
                                HelperFunctions.getDateTime("ddMMyyHHmmss", false) + ".pdf"
                            )
                        if (mUserName != null) {
                            var isGenerated = false
                            val job: Job = CoroutineScope(Dispatchers.IO).launch {
                                isGenerated =
                                    GeneratePdf.createPdf(
                                        applicationContext,
                                        Uri.fromFile(mFileName),
                                        mUserId,
                                        mUserName!!
                                    )
                            }
                            job.invokeOnCompletion {
                                CoroutineScope(Dispatchers.Main).launch {
                                    if (isGenerated) {
                                        ToastUtils.showShort(getString(R.string.pdf_generated))
                                    }
                                }
                            }
                        }
                    }
                } else {
                    ToastUtils.showLong(getString(R.string.pdf_not_generated))
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return object : MyCursorLoader(applicationContext) {
            private val mObserver = ForceLoadContentObserver()
            override fun loadInBackground(): Cursor {
                val mCursor = myDatabaseHelper.getAllTransactionsByUserId(mUserId)
                mCursor.registerContentObserver(mObserver)
                mCursor.setNotificationUri(
                    contentResolver, Constants.TranColumn.TRANSACTION_TABLE_URI
                )
                return mCursor
            }
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        val mUserCredit = myDatabaseHelper.getSumForColumns(mUserId, "Credit")
        val mUserDebit = myDatabaseHelper.getSumForColumns(mUserId, "Debit")
        val mUserBalance = mUserCredit - mUserDebit
        val mTotalBalance = HelperFunctions.getRoundedValue(mUserBalance.toString())
        mAdapterTrans = TransactionsAdapter(this, cursor!!)
        if (mAdapterTrans.itemCount > 0) {
            mBinding.ivWarning.visibility = View.GONE
            mBinding.tvNoTransHeading.visibility = View.GONE
            mBinding.tvAddNewTrans.visibility = View.GONE
            mBinding.rvGetAllRecords.visibility = View.VISIBLE
            mBinding.rvTotalBalances.visibility = View.VISIBLE
        } else {
            mBinding.ivWarning.visibility = View.VISIBLE
            mBinding.tvNoTransHeading.visibility = View.VISIBLE
            mBinding.tvAddNewTrans.visibility = View.VISIBLE
            mBinding.rvGetAllRecords.visibility = View.GONE
            mBinding.rvTotalBalances.visibility = View.GONE
        }
        val mListBalance = ArrayList<TransactionsBalance>()
        val debit = TransactionsBalance()
        debit.accountType = "Debit"
        debit.cvBgColor = HelperFunctions.convertColorIntToHexString(R.color.colorRedLight)
        debit.tvColor = HelperFunctions.convertColorIntToHexString(R.color.colorRedDark)
        debit.totalAmount = HelperFunctions.getRoundedValue(mUserDebit.toString())
        val credit = TransactionsBalance()
        credit.accountType = "Credit"
        credit.cvBgColor = HelperFunctions.convertColorIntToHexString(R.color.colorGreenLight)
        credit.tvColor = HelperFunctions.convertColorIntToHexString(R.color.colorGreenDark)
        credit.totalAmount = HelperFunctions.getRoundedValue(mUserCredit.toString())
        val balance = TransactionsBalance()
        balance.accountType = "Balance"
        if (mTotalBalance > "0") {
            balance.cvBgColor = HelperFunctions.convertColorIntToHexString(R.color.colorGreenLight)
            balance.tvColor = HelperFunctions.convertColorIntToHexString(R.color.colorGreenDark)
        } else {
            balance.cvBgColor = HelperFunctions.convertColorIntToHexString(R.color.colorRedLight)
            balance.tvColor = HelperFunctions.convertColorIntToHexString(R.color.colorRedDark)
        }
        balance.totalAmount = mTotalBalance
        mListBalance.add(debit)
        mListBalance.add(credit)
        mListBalance.add(balance)
        mAdapterBalance = TransactionsBalanceAdapter(mListBalance)
        mBinding.rvTransactionsAdapter = mAdapterTrans
        mBinding.rvTransactionsBalAdapter = mAdapterBalance
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Keep empty
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        val inflater: MenuInflater? = mode?.menuInflater
        inflater?.inflate(R.menu.activity_trans_delete, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_delete_trans -> {
                val alertBuilder = MaterialAlertDialogBuilder(this)
                alertBuilder.setTitle(getString(R.string.confirmation))
                alertBuilder.setIcon(R.drawable.ic_baseline_delete_forever)
                alertBuilder.setMessage(
                    getString(R.string.trans_bulk_delete_warning_msg, mSelectedIds.size)
                )
                alertBuilder.setCancelable(false)
                alertBuilder.setPositiveButton(getString(R.string.delete)) { dialog, which ->
                    var isDeletedSuccessfully = false
                    for (pos in mSelectedIds) {
                        val trans = mAdapterTrans.getTransList()
                        isDeletedSuccessfully =
                            myDatabaseHelper.deleteTransactions(trans[pos].transId)
                    }
                    if (isDeletedSuccessfully) {
                        ToastUtils.showShort(getString(R.string.trans_deleted))
                    }
                    dialog.dismiss()
                    mode?.finish()
                }
                alertBuilder.setNegativeButton(getString(android.R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                    mode?.finish()
                }
                val dialog = alertBuilder.create()
                dialog.show()
                dialog.findViewById<ImageView?>(android.R.id.icon)?.setColorFilter(Color.BLACK)
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(getColor(R.color.black))
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(getColor(R.color.black))
                        dialog.findViewById<ImageView?>(android.R.id.icon)
                            ?.setColorFilter(getColor(R.color.black))
                    } else {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(resources.getColor(R.color.black))
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(resources.getColor(R.color.black))
                        dialog.findViewById<ImageView?>(android.R.id.icon)
                            ?.setColorFilter(resources.getColor(R.color.black))
                    }
                }
            }
            R.id.menu_select_all_trans -> {
                if (!mIsSelectAll) {
                    Log.v(Constants.LOG_TAG, "Select All")
                    mIsSelectAll = true
                    mIsMultiSelect = true
                    mSelectedIds = ArrayList()
                    mAdapterTrans.addSelectedIds(ArrayList())
                    val tempList = ArrayList<Int>()
                    for (i in 0 until mAdapterTrans.itemCount) {
                        tempList.add(i)
                        mSelectedIds.add(i)
                    }
                    mAdapterTrans.addSelectedIds(tempList)
                    mode?.title = "${mSelectedIds.size} selected"
                } else {
                    Log.v(Constants.LOG_TAG, "DeSelect All")
                    mIsSelectAll = false
                    mIsMultiSelect = false
                    mSelectedIds = ArrayList()
                    mAdapterTrans.addSelectedIds(ArrayList())
                    mode?.title = ""
                    mode?.finish()
                }
            }
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        mode?.finish()
        mActionMode = null
        mIsMultiSelect = false
        mIsSelectAll = false
        mSelectedIds = ArrayList()
        mAdapterTrans.addSelectedIds(ArrayList())
    }

    override fun onDestroy() {
        super.onDestroy()
        myDatabaseHelper.close()
    }
}
