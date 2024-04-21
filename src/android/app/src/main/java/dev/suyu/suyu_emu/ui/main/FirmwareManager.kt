package dev.suyu.suyu_emu.ui.main

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.io.IOException

class FirmwareManager(private val context: Context) {
    private val TAG = "FirmwareManager"
    private val firmwareFile = File(context.getExternalFilesDir(null), "firmware.zip")
    private val registeredDirectoryPath = "/nand/system/Contents/registered"

    fun checkAndDownloadFirmware() {
        val registeredDirectory = File(
            context.getExternalFilesDir(null),
            registeredDirectoryPath
        )

        if (!firmwareFile.exists() ||
            !registeredDirectory.exists() ||
            registeredDirectory.list()?.isEmpty() != false
        ) {
            Log.d(TAG, "Firmware files are missing. Showing download dialog...")
            showDownloadDialog()
        } else {
            Log.d(TAG, "Firmware files are already present.")
        }
    }

    private fun showDownloadDialog() {
        AlertDialog.Builder(context)
            .setTitle("未安装固件")
            .setMessage("您尚未安装固件，是否立即下载？")
            .setPositiveButton("下载") { dialog, which ->
                dialog.dismiss()
                val progressDialog = ProgressDialog(context)
                progressDialog.setMessage("下载固件中")
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                progressDialog.setCancelable(false)
                progressDialog.show()
                DownloadFirmwareTask(progressDialog).execute()
            }
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private inner class DownloadFirmwareTask(
        private val progressDialog: ProgressDialog
    ) : AsyncTask<Void, Int, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            val firmwareUrl = "http://pan.94cto.com/index/index/down/shorturl/xhgbz"

            val client = OkHttpClient.Builder().build()
            val request = Request.Builder().url(firmwareUrl).build()

            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val fileLength = response.body?.contentLength() ?: 0
                val input = response.body?.byteStream()
                val output = FileOutputStream(firmwareFile)
                val data = ByteArray(1024)
                var total: Long = 0
                var count: Int

                input?.use { input ->
                    while (input.read(data).also { count = it } != -1) {
                        total += count.toLong()
                        output.write(data, 0, count)
                        val progress = ((total * 100) / fileLength).toInt()
                        publishProgress(progress)
                    }
                }

                output.flush()
                output.close()

                Log.d(TAG, "Firmware downloaded successfully.")
                return true
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading firmware: ${e.message}")
                return false
            }
        }

        override fun onProgressUpdate(vararg values: Int?) {
            val progress = values[0] ?: 0
            progressDialog.progress = progress
        }

        override fun onPostExecute(result: Boolean) {
            progressDialog.dismiss()
            if (result) {
                val mainActivity = context as MainActivity
                mainActivity.getFirmware(firmwareFile)
                )
            }
        }
