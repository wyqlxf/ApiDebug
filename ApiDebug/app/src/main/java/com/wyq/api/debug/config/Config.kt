package com.wyq.api.debug.config

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/23 10:22
 * 类描述：应用配置
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
object Config {

    // 本地文件路径(实时功能读取本地文件)
    const val filePath = "/YourFileName/encrypt/"
    // 文件加密密钥(实时功能本地文件的密钥)
    const val encryptionKey = "YourEncryptionKey"

    // 正式环境
    const val host = "www.xxx.com"
    // debug环境
    const val debugHost = "www.debug.xxx.com"
    // dev环境
    const val devHost = "www.dev.xxx.com"
    // 默认列表请求URL(根据device=ios/android/touch来区分各端)
    const val apiListUrl = "https://www.xxx.com/api/list?"
    // postMan功能的默认地址
    const val defaultAddressUrl = "https://www.xxx.com"
    // web端DEV的调试接口
    const val webDevUrl = "https://m.dev.xxx.com"
    // 主app的包名
    const val mainAppPackageName = "YourMainAppPackageName"
    // 主app的类名
    const val mainAppClassName = "YourMainAppClassName"
    // 这里填写你的主app测试包的下载链接，可以借助WinSCP工具上传app
    const val mainAppDownloadUrl = "https://www.xxx.591.com.hk/download/android/appName.apk"

}