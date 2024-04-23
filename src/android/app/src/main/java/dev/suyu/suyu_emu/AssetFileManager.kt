package dev.suyu.suyu_emu

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipInputStream
import java.util.zip.ZipEntry

class AssetFileManager(private val context: Context) {

    fun copyProdKeys() {
        copyAsset("prod.keys", "keys", "prod.keys")
    }

    fun copyFolderFromAssets() {
        val sourceFolderName = "01007EF00011E000"
        val destinationDir = File(context.getExternalFilesDir("load"), sourceFolderName)

        if (!destinationDir.exists()) {
            try {
                destinationDir.mkdirs() // 创建目标文件夹
                copyAssetFolder("Turnip-24.1.0.adpkg_R18.zip", destinationDir)
                copyAssetFolder("Turnip-24.1.0.adpkg_R17.zip", destinationDir)
                copyAssetFolder("Turnip-24.1.0.adpkg_R16.zip", destinationDir)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    private fun copyAssetFolder(assetFileName: String, destinationDir: File) {
        val inputStream = context.assets.open(assetFileName)
        ZipInputStream(inputStream).use { zipInputStream ->
            var entry: ZipEntry?
            val buffer = ByteArray(4096)
            while (zipInputStream.nextEntry.also { entry = it } != null) {
                val entryFile = File(destinationDir, entry!!.name)
                if (entry!!.isDirectory) {
                    entryFile.mkdirs()
                } else {
                    FileOutputStream(entryFile).use { outputStream ->
                        var length: Int
                        while (zipInputStream.read(buffer).also { length = it } > 0) {
                            outputStream.write(buffer, 0, length)
                        }
                    }
                }
                zipInputStream.closeEntry()
            }
        }
    }

    private fun copyAsset(assetName: String, subDirectory: String, destinationFileName: String) {
        val destinationDir = File(context.getExternalFilesDir(null), subDirectory)
        destinationDir.mkdirs()

        val destinationFile = File(destinationDir, destinationFileName)

        if (!destinationFile.exists()) {
            try {
                context.assets.open(assetName).use { inputStream ->
                    FileOutputStream(destinationFile).use { outputStream ->
                        val buffer = ByteArray(1024)
                        var length: Int
                        while (inputStream.read(buffer).also { length = it } > 0) {
                            outputStream.write(buffer, 0, length)
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
