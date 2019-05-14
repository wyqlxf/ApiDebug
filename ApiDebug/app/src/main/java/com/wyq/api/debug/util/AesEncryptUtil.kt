package com.addcn.android.hk591new.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import com.wyq.api.debug.base.BaseApplication
import java.io.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 创建人： WangYongQi
 * 创建时间：2018/7/6.
 * 文件说明：加密/解密工具类
 *  AES:对称加密算法
 *  AES 高级加密标准（英语：Advanced Encryption Standard，缩写：AES）
 *  这个标准用来替代原先的DES,Android 中的AES 加密 秘钥 key 必须为16/24/32位字节，否则抛异常
 */
object AesEncryptUtil {

    /**
     * 判断是否有读写权限
     */
    private fun isHavePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(BaseApplication.context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 没有权限
            return false
        }
        return true
    }

    /**
     * 创建AES秘钥
     */
    private fun createAesKey(password: String): SecretKeySpec {
        var data: ByteArray? = null
        val sb = StringBuilder(32)
        sb.append(password)
        while (sb.length < 32) {
            sb.append("0")
        }
        if (sb.length > 32) {
            sb.setLength(32)
        }
        try {
            data = sb.toString().toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
        }
        return SecretKeySpec(data, "AES")
    }

    /**
     * 返回加密或解密的字节
     * mode:加密or解密模式
     * AES文本加密/解密 秘钥为32位字符串
     */
    private fun getAesEncryptByte(b: ByteArray, key: String, mode: Int): ByteArray? {
        try {
            val k = createAesKey(key)
            val cipher = Cipher.getInstance("AES/CFB/NoPadding")
            // 使用CFB加密，需要设置IV
            cipher.init(mode, k, IvParameterSpec(
                    ByteArray(cipher.blockSize)))
            return cipher.doFinal(b)
        } catch (e: Exception) {
        }
        return null
    }

    /**
     * 字节转16进制
     */
    private fun byteToHex(b: ByteArray): String {
        val sb = StringBuilder(b.size * 2)
        try {
            for (aB in b) {
                // 整数转成十六进制
                var tmp = Integer.toHexString(aB.toInt() and 0XFF)
                if (tmp.length == 1) {
                    sb.append("0")
                }
                sb.append(tmp)
            }
        } catch (ex: Exception) {
        }
        // 转成大写
        return sb.toString().toUpperCase()
    }

    /**
     *  16进制转字节
     */
    private fun hexToByte(text: String?): ByteArray {
        if (text == null || text.length < 2) {
            return ByteArray(0)
        }
        var inputString = text.toLowerCase()
        val l = inputString.length / 2
        val result = ByteArray(l)
        for (i in 0 until l) {
            val tmp = inputString.substring(2 * i, 2 * i + 2)
            result[i] = (Integer.parseInt(tmp, 16) and 0xFF).toByte()
        }
        return result
    }

    /**
     * AES加密字符串
     */
    fun aesEncryptString(text: String, key: String): String {
        var b = text.toByteArray(charset("UTF-8"))
        var data = getAesEncryptByte(b, key, Cipher.ENCRYPT_MODE)
        return if (data == null) {
            ""
        } else {
            byteToHex(data)
        }
    }

    /**
     * AES加密文本
     * 文本/秘钥/文件路径/文件名称
     */
    fun aesEncryptText(text: String, key: String, filePath: String, fileName: String) {
        if (isHavePermission()) {
            if (android.os.Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED) {
                try {
                    var result = aesEncryptString(text, key)
                    if (!TextUtils.isEmpty(result)) {
                        var fileDir = File(Environment.getExternalStorageDirectory().canonicalPath + filePath)
                        if (!fileDir.exists()) {
                            fileDir.mkdirs()
                        }
                        var file = File(fileDir, fileName)
                        if (file.exists()) {
                            file.delete()
                        }
                        val stream = FileOutputStream(file)
                        val oWriter = OutputStreamWriter(stream)
                        val bWriter = BufferedWriter(oWriter)
                        bWriter.write(result)
                        bWriter.close()
                        oWriter.close()
                        stream.close()
                    }
                } catch (ex: Exception) {
                }
            }
        }
    }

    /**
     * AES加密文件
     */
    fun aesEncryptFile(key: String, filePath: String, fileName: String) {
        if (isHavePermission()) {
            if (android.os.Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED) {
                try {
                    var fileDir = File(Environment.getExternalStorageDirectory().canonicalPath + filePath)
                    if (fileDir.exists()) {
                        var file = File(fileDir, fileName)
                        if (file.exists()) {
                            val fis = FileInputStream(file)
                            val inputReader = InputStreamReader(fis)
                            val bufReader = BufferedReader(inputReader)
                            val sb = StringBuilder()
                            while (true) {
                                var line = bufReader.readLine()
                                if (line != null) sb.append(line) else break
                            }
                            bufReader.close()
                            inputReader.close()
                            fis.close()
                            aesEncryptText(sb.toString(), key, filePath, fileName)
                        }
                    }
                } catch (ex: Exception) {
                }
            }
        }
    }

    /**
     * AES解密字符串
     */
    fun aesDecodeString(text: String, key: String): String {
        var b = hexToByte(text)
        var data = getAesEncryptByte(b, key, Cipher.DECRYPT_MODE)
        return if (data == null) {
            ""
        } else {
            String(data, charset("UTF-8"))
        }
    }

    /**
     * AES解密字文件
     */
    fun aesDecodeText(key: String, filePath: String, fileName: String): String {
        var text = ""
        if (isHavePermission()) {
            if (android.os.Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED) {
                try {
                    var fileDir = File(Environment.getExternalStorageDirectory().canonicalPath + filePath)
                    if (fileDir.exists()) {
                        var file = File(fileDir, fileName)
                        if (file.exists()) {
                            val fis = FileInputStream(file)
                            val inputReader = InputStreamReader(fis)
                            val bufReader = BufferedReader(inputReader)
                            val sb = StringBuilder()
                            while (true) {
                                var line = bufReader.readLine()
                                if (line != null) sb.append(line) else break
                            }
                            bufReader.close()
                            inputReader.close()
                            fis.close()
                            return aesDecodeString(sb.toString(), key)
                        }
                    }
                } catch (ex: Exception) {
                }
            }
        }
        return text
    }

}