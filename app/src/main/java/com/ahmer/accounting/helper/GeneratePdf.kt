package com.ahmer.accounting.helper

import android.content.Context
import android.net.Uri
import android.provider.BaseColumns
import android.util.Log
import com.ahmer.accounting.R
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*

class GeneratePdf {

    companion object {

        fun createPdf(context: Context, uri: Uri, id: Long, userName: String): Boolean {
            val database = MyDatabaseHelper(context)
            val mDocument = Document(PageSize.A4, 54F, 54F, 54F, 54F)
            val mOrderBy: String = BaseColumns._ID + " ASC"
            val mCursor = database.getAllTransactionsByUserId(id, mOrderBy)
            try {
                val mOutPutStream = context.contentResolver.openOutputStream(uri)

                val mTotalCredit = database.getSumForColumns(id, "Credit", false)
                val mTotalDebit = database.getSumForColumns(id, "Debit", false)
                val mTotalBalance = mTotalCredit - mTotalDebit

                val pdfWriter = PdfWriter.getInstance(mDocument, mOutPutStream)
                val headerFooter = HeaderFooterPageEvent(context)
                pdfWriter.pageEvent = headerFooter
                mDocument.open()
                mDocument.addCreationDate()
                mDocument.addAuthor(context.getString(R.string.app_name))
                mDocument.addTitle("$userName Account Statement")
                mDocument.addCreator(context.getString(R.string.app_name))
                mDocument.addSubject(context.getString(R.string.app_name))

                val font = Font(Font.FontFamily.HELVETICA)
                font.color = BaseColor.BLACK
                font.size = 16F
                font.style = Font.NORMAL
                val mParagraph = Paragraph("$userName Account Statement", font)
                mParagraph.spacingAfter = 20F
                mParagraph.alignment = Element.ALIGN_CENTER
                mDocument.add(mParagraph)

                val mTableMain = PdfPTable(5)
                mTableMain.widthPercentage = 100F
                mTableMain.setTotalWidth(floatArrayOf(36F, 72F, 199F, 90F, 90F))
                mTableMain.isLockedWidth = true
                mTableMain.addCell(cellFormat("Sr", true))
                mTableMain.addCell(cellFormat(Constants.TranColumn.DATE, true))
                mTableMain.addCell(cellFormat(Constants.TranColumn.DESCRIPTION, true))
                mTableMain.addCell(cellFormat(Constants.TranColumn.DEBIT, true))
                mTableMain.addCell(cellFormat(Constants.TranColumn.CREDIT, true))

                var srNo = 0
                mCursor.moveToFirst()
                if (mCursor.moveToFirst()) do {
                    srNo += 1
                    val mDate: String = HelperFunctions.convertDateTimeShortFormat(
                        mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.TranColumn.DATE)),
                        true
                    )
                    val mDescription: String =
                        mCursor.getString(mCursor.getColumnIndexOrThrow(Constants.TranColumn.DESCRIPTION))
                    val mCredit: String =
                        HelperFunctions.getRoundedValue(
                            mCursor.getDouble(
                                mCursor.getColumnIndexOrThrow(
                                    Constants.TranColumn.CREDIT
                                )
                            )
                        )
                    val mDebit: String =
                        HelperFunctions.getRoundedValue(
                            mCursor.getDouble(
                                mCursor.getColumnIndexOrThrow(
                                    Constants.TranColumn.DEBIT
                                )
                            )
                        )
                    mTableMain.addCell(cellFormat(srNo.toString(), false, "Center"))
                    mTableMain.addCell(cellFormat(mDate, false, "Center"))
                    mTableMain.addCell(cellFormat(mDescription, false))
                    if (mDebit == "0") {
                        mTableMain.addCell("")
                    } else {
                        mTableMain.addCell(cellFormat(mDebit, false, "Right"))
                    }
                    if (mCredit == "0") {
                        mTableMain.addCell("")
                    } else {
                        mTableMain.addCell(cellFormat(mCredit, false, "Right"))
                    }
                } while (mCursor.moveToNext())

                val mTableTotal = PdfPTable(3)
                mTableTotal.widthPercentage = 100F
                mTableTotal.setTotalWidth(floatArrayOf(307F, 90F, 90F))
                mTableTotal.isLockedWidth = true
                mTableTotal.addCell(cellFormat("Total", false, "Center", true))
                mTableTotal.addCell(
                    cellFormat(
                        HelperFunctions.getRoundedValue(mTotalDebit),
                        false,
                        "Right",
                        true
                    )
                )
                mTableTotal.addCell(
                    cellFormat(
                        HelperFunctions.getRoundedValue(mTotalCredit),
                        false,
                        "Right",
                        true
                    )
                )

                val mTableBalance = PdfPTable(2)
                mTableBalance.widthPercentage = 100F
                mTableBalance.setTotalWidth(floatArrayOf(307F, 180F))
                mTableBalance.isLockedWidth = true
                mTableBalance.addCell(cellFormat("Balance", false, "Center", true))
                mTableBalance.addCell(
                    cellFormat(
                        HelperFunctions.getRoundedValue(mTotalBalance),
                        false,
                        "Right",
                        true
                    )
                )

                mDocument.add(mTableMain)
                mDocument.add(mTableTotal)
                mDocument.add(mTableBalance)
                return true
            } catch (de: DocumentException) {
                Log.e(Constants.LOG_TAG, de.message, de)
                FirebaseCrashlytics.getInstance().recordException(de)
                return false
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, e.message, e)
                FirebaseCrashlytics.getInstance().recordException(e)
                return false
            } finally {
                mDocument.close()
                mCursor.close()
            }
        }

        private fun cellFormat(
            string: String,
            isForTable: Boolean,
            alignment: String = "",
            isCellForTotal: Boolean = false
        ): PdfPCell {
            val font = Font(Font.FontFamily.HELVETICA)
            font.color = BaseColor.BLACK
            if (isForTable) {
                font.size = 14F
                font.style = Font.BOLD
            } else {
                if (isCellForTotal) {
                    font.size = 14F
                    font.style = Font.BOLD
                } else {
                    font.size = 12F
                    font.style = Font.NORMAL
                }
            }
            val pdfPCell = PdfPCell(Phrase(string, font))
            if (isForTable) {
                pdfPCell.verticalAlignment = Element.ALIGN_MIDDLE
                pdfPCell.horizontalAlignment = Element.ALIGN_CENTER
                pdfPCell.paddingTop = 5F
                pdfPCell.paddingBottom = 8F
            } else {
                when (alignment) {
                    "Right" -> {
                        pdfPCell.verticalAlignment = Element.ALIGN_MIDDLE
                        pdfPCell.horizontalAlignment = Element.ALIGN_RIGHT
                    }
                    "Center" -> {
                        pdfPCell.verticalAlignment = Element.ALIGN_MIDDLE
                        pdfPCell.horizontalAlignment = Element.ALIGN_CENTER
                    }
                    "" -> {
                        pdfPCell.verticalAlignment = Element.ALIGN_MIDDLE
                    }
                }
                if (isCellForTotal) {
                    pdfPCell.paddingTop = 5F
                    pdfPCell.paddingBottom = 7F
                } else {
                    pdfPCell.paddingTop = 3F
                    pdfPCell.paddingBottom = 5F
                }
            }
            return pdfPCell
        }
    }

    class HeaderFooterPageEvent(context: Context) : PdfPageEventHelper() {

        private val mContext = context

        override fun onStartPage(writer: PdfWriter, document: Document?) {
            val font = Font(Font.FontFamily.HELVETICA)
            font.color = BaseColor.BLACK
            font.size = 10F
            font.style = Font.NORMAL

            ColumnText.showTextAligned(
                writer.directContent,
                Element.ALIGN_CENTER,
                Phrase(mContext.getString(R.string.app_name), font),
                80F,
                800F,
                0F
            )

            ColumnText.showTextAligned(
                writer.directContent,
                Element.ALIGN_CENTER,
                Phrase(
                    "${HelperFunctions.getDateTime("dd MMM yyyy")} - ${
                        HelperFunctions.getDateTime("hh:mm:ss a")
                    }", font
                ),
                477F,
                800F,
                0F
            )

        }

        override fun onEndPage(writer: PdfWriter, document: Document) {
            val font = Font(Font.FontFamily.HELVETICA)
            font.color = BaseColor.BLACK
            font.size = 10F
            font.style = Font.NORMAL

            val phrase = Phrase("", font)
            val chunk = Chunk(Constants.PLAY_STORE_LINK)
            chunk.setAnchor(Constants.PLAY_STORE_LINK)
            phrase.add(chunk)

            ColumnText.showTextAligned(
                writer.directContent,
                Element.ALIGN_CENTER,
                Phrase(phrase),
                170F,
                35F,
                0F
            )
            ColumnText.showTextAligned(
                writer.directContent,
                Element.ALIGN_CENTER,
                Phrase("Page " + document.pageNumber, font),
                530F,
                35F,
                0F
            )
        }
    }
}