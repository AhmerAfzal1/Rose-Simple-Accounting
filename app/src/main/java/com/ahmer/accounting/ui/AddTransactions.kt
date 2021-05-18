package com.ahmer.accounting.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
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
import androidx.core.content.ContextCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmer.accounting.R
import com.ahmer.accounting.adapter.TransactionsAdapter
import com.ahmer.accounting.helper.*
import com.ahmer.accounting.model.Transactions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.ahmer.utils.utilcode.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddTransactions : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>,
    ActionMode.Callback {

    private lateinit var mAdapter: TransactionsAdapter
    private lateinit var mCvTotalBal: MaterialCardView
    private lateinit var mLayoutNoTransaction: LinearLayout
    private lateinit var mLayoutSubTotal: LinearLayout
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTvTotalBal: TextView
    private lateinit var mTvTotalBalHeading: TextView
    private lateinit var mTvTotalCre: TextView
    private lateinit var mTvTotalDeb: TextView
    private lateinit var myDatabaseHelper: MyDatabaseHelper
    private var mActionMode: ActionMode? = null
    private var mIsMultiSelect = false
    private var mSelectedIds = ArrayList<Int>()
    private var mIsSelectAll = false
    private var mUserId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transactions_rv)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.title_all_transaction_record)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.overflowIcon?.setTint(Color.WHITE)
        }
        toolbar.setOnClickListener {
            finish()
        }

        mUserId = intent.getLongExtra("mPosUserID", -1)
        val mUserName = intent.getStringExtra("mPosUserName")
        val mUserPhone = intent.getStringExtra("mPosUserPhone")

        myDatabaseHelper = MyDatabaseHelper(this)

        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvUserPhone = findViewById<TextView>(R.id.tvUserPhone)
        mLayoutNoTransaction = findViewById(R.id.layoutNoTransaction)
        mLayoutSubTotal = findViewById(R.id.layoutSubTotal)
        mTvTotalDeb = findViewById(R.id.tvTotalDebit)
        mTvTotalCre = findViewById(R.id.tvTotalCredit)
        mCvTotalBal = findViewById(R.id.cvTotalBalance)
        mTvTotalBalHeading = findViewById(R.id.tvTotalBalanceHeading)
        mTvTotalBal = findViewById(R.id.tvTotalBalance)
        mRecyclerView = findViewById(R.id.rvGetAllRecords)

        tvUserPhone.text = mUserPhone
        tvUserName.text = mUserName

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.isSmoothScrollbarEnabled = true
        linearLayoutManager.isAutoMeasureEnabled
        mRecyclerView.recycledViewPool.clear()
        mRecyclerView.clearOnScrollListeners()
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.addOnItemTouchListener(object : RecyclerItemClickListener(applicationContext,
            mRecyclerView, object : OnItemClickListener {
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

        LoaderManager.getInstance(this).initLoader(1, null, this)

        val mResultLauncherGeneratePdf: ActivityResultLauncher<Intent> =
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
                                    mUserName
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

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_add_trans -> {
                    showAddTransactionDialog(this, mUserId)
                }
                R.id.menu_search_trans_desc -> {
                    val searchView: SearchView = it?.actionView as SearchView
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
                    if (mAdapter.itemCount > 0) {
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
                                            mUserName
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
            true
        }

        MyAds.loadInterstitialAd(this)
    }

    private fun showAddTransactionDialog(context: Context, userID: Long) {
        try {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.transactions_add_dialog)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.setCancelable(false)
            val myDatabaseHelper = MyDatabaseHelper(context)
            val inputAmount = dialog.findViewById<TextInputEditText>(R.id.inputAmount)
            val toggleGroup =
                dialog.findViewById<MaterialButtonToggleGroup>(R.id.btnToggleGroupAmount)
            val btnCredit = dialog.findViewById<MaterialButton>(R.id.toggleBtnCredit)
            val btnDebit = dialog.findViewById<MaterialButton>(R.id.toggleBtnDebit)
            val inputDate = dialog.findViewById<TextInputEditText>(R.id.inputDate)
            val inputDescription = dialog.findViewById<TextInputEditText>(R.id.inputDescription)
            val addTransactions = dialog.findViewById<MaterialButton>(R.id.btnAddTransaction)
            val cancelTransactions = dialog.findViewById<MaterialButton>(R.id.btnCancelTransaction)

            var typeAmount = ""
            toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
                val checkedButton = dialog.findViewById<MaterialButton>(checkedId)
                typeAmount = checkedButton.text.toString()
                Log.v(Constants.LOG_TAG, "TypeAmount: $typeAmount")
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        when (typeAmount) {
                            getString(R.string.credit_plus) -> {
                                btnCredit.backgroundTintList =
                                    ContextCompat.getColorStateList(this, R.color.black)
                                btnDebit.backgroundTintList =
                                    ContextCompat.getColorStateList(
                                        this, android.R.color.transparent
                                    )
                            }
                            else -> {
                                btnCredit.backgroundTintList =
                                    ContextCompat.getColorStateList(
                                        this, android.R.color.transparent
                                    )
                                btnDebit.backgroundTintList =
                                    ContextCompat.getColorStateList(this, R.color.black)
                            }
                        }
                    }
                }
            }

            val dateFormat = SimpleDateFormat(Constants.DATE_TIME_PATTERN, Locale.getDefault())
            val currentDate = Calendar.getInstance()
            inputDate.setText(dateFormat.format(currentDate.time))
            inputDate.setOnClickListener {
                val listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    currentDate.set(year, month, dayOfMonth)
                    inputDate.setText(dateFormat.format(currentDate.time))
                }
                val datePicker = DatePickerDialog(
                    context,
                    listener,
                    currentDate.get(Calendar.YEAR),
                    currentDate.get(Calendar.MONTH),
                    currentDate.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.show()
            }

            addTransactions.setOnClickListener {
                var isSuccessfullyInserted = false
                var newAmount: Double = 0.toDouble()
                if (inputAmount.text.toString().trim().isNotEmpty()) {
                    newAmount = inputAmount.text.toString().trim().toDouble()
                }
                val newDate: String = inputDate.text.toString()
                val newDescription: String = inputDescription.text.toString().trim()

                when {
                    newAmount == 0.toDouble() -> {
                        ToastUtils.showLong(getString(R.string.enter_the_amount))
                    }
                    typeAmount.isEmpty() -> {
                        ToastUtils.showLong(getString(R.string.select_type_amount))
                    }
                    newDescription.trim().isEmpty() -> {
                        ToastUtils.showLong(getString(R.string.enter_transaction_description))
                    }
                    else -> {
                        val addNewTransaction = Transactions().apply {
                            userId = userID
                            credit = 0.toDouble()
                            debit = 0.toDouble()
                            if (typeAmount == getString(R.string.credit_plus)) {
                                credit = newAmount
                                isDebit = false
                            }
                            if (typeAmount == getString(R.string.debit_minus)) {
                                debit = newAmount
                                isDebit = true
                            }
                            date = newDate
                            description = newDescription
                            created = HelperFunctions.getDateTime()
                        }
                        isSuccessfullyInserted =
                            myDatabaseHelper.insertTransactions(addNewTransaction)
                    }
                }
                if (isSuccessfullyInserted) {
                    ToastUtils.showShort(getString(R.string.transaction_added_successfully))
                    Thread.sleep(200)
                    dialog.dismiss()
                }
            }
            cancelTransactions.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, e.message, e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun searchTransactionDescription(keyword: String) {
        val cursor = myDatabaseHelper.searchTransDesc(mUserId, keyword)
        mAdapter = TransactionsAdapter(this, cursor)
        mRecyclerView.adapter = mAdapter
    }

    private fun multiSelect(position: Int) {
        if (position < mAdapter.itemCount) {
            if (mActionMode != null) {
                if (mSelectedIds.contains(position)) {
                    mSelectedIds.remove(position)
                    mAdapter.removeSelectedIds(position)
                } else {
                    mSelectedIds.add(position)
                    mAdapter.addSelectedIds(mSelectedIds)
                }
                if (mSelectedIds.size > 0) {
                    //Show title selected item count on action mode.
                    mActionMode!!.title = "${mSelectedIds.size} selected"
                } else {
                    mActionMode!!.title = "" //Remove item count from action mode.
                    mActionMode!!.finish() //Hide action mode.
                }
                mAdapter.notifyDataSetChanged()
            }
        }
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
        val context: Context = applicationContext
        mAdapter = TransactionsAdapter(this, cursor!!)
        if (mAdapter.itemCount > 0) {
            mLayoutNoTransaction.visibility = View.GONE
            mRecyclerView.visibility = View.VISIBLE
            mLayoutSubTotal.visibility = View.VISIBLE
        } else {
            mLayoutNoTransaction.visibility = View.VISIBLE
            mRecyclerView.visibility = View.GONE
            mLayoutSubTotal.visibility = View.GONE
        }
        mRecyclerView.adapter = mAdapter

        val mUserCredit = myDatabaseHelper.getSumForColumns(mUserId, "Credit")
        val mUserDebit = myDatabaseHelper.getSumForColumns(mUserId, "Debit")
        val mUserBalance = mUserCredit - mUserDebit
        val mTotalBalance = HelperFunctions.getRoundedValue(mUserBalance)
        if (mTotalBalance > "0") {
            mCvTotalBal.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreenLight))
            mTvTotalBalHeading.setTextColor(ContextCompat.getColor(context, R.color.colorGreenDark))
            mTvTotalBal.setTextColor(ContextCompat.getColor(context, R.color.colorGreenDark))
        } else {
            mCvTotalBal.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRedLight))
            mTvTotalBalHeading.setTextColor(ContextCompat.getColor(context, R.color.colorRedDark))
            mTvTotalBal.setTextColor(ContextCompat.getColor(context, R.color.colorRedDark))
        }
        mTvTotalDeb.text = HelperFunctions.getRoundedValue(mUserDebit)
        mTvTotalCre.text = HelperFunctions.getRoundedValue(mUserCredit)
        mTvTotalBal.text = mTotalBalance
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
                        val trans = mAdapter.getTransList()
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
                    mAdapter.addSelectedIds(ArrayList())
                    val tempList = ArrayList<Int>()
                    for (i in 0 until mAdapter.itemCount) {
                        tempList.add(i)
                        mSelectedIds.add(i)
                    }
                    mAdapter.addSelectedIds(tempList)
                    mode?.title = "${mSelectedIds.size} selected"
                } else {
                    Log.v(Constants.LOG_TAG, "DeSelect All")
                    mIsSelectAll = false
                    mIsMultiSelect = false
                    mSelectedIds = ArrayList()
                    mAdapter.addSelectedIds(ArrayList())
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
        mAdapter.addSelectedIds(ArrayList())
    }

    override fun onDestroy() {
        super.onDestroy()
        myDatabaseHelper.close()
    }
}
