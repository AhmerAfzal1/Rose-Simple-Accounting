package com.ahmer.accounting.helper

import android.content.Context
import android.database.Cursor
import androidx.loader.content.AsyncTaskLoader

open class MyCursorLoader(context: Context) : AsyncTaskLoader<Cursor>(context) {

    private var mCursor: Cursor? = null

    override fun loadInBackground(): Cursor? {
        TODO("Not yet implemented")
    }

    override fun deliverResult(cursor: Cursor?) {
        if (isReset) {
            // An async query came in while the loader is stopped
            cursor?.close()
            return
        }
        mCursor = cursor

        if (isStarted) {
            super.deliverResult(cursor)
        }
    }

    override fun onStartLoading() {
        if (mCursor != null) {
            deliverResult(mCursor)
        }
        if (takeContentChanged() || mCursor == null) {
            forceLoad()
        }
    }

    override fun onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad()
    }

    override fun onCanceled(cursor: Cursor?) {
        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }
    }

    override fun onReset() {
        // Ensure the loader is stopped
        onStopLoading()

        if (mCursor != null && !mCursor!!.isClosed) {
            mCursor!!.close()
        }
        mCursor = null
    }
}