package com.wyq.api.debug.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/9 10:27
 * 类描述：API数据库
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
class ApiDataBase private constructor(context: Context) : SQLiteOpenHelper(context, "ApiDatabase.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // 手动设置开始事务
        db.beginTransaction()
        try {
            /**
             * 创建“收藏表”
             */
            val favSql = ("CREATE TABLE " + favTable
                    + " (" + id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + url + " text,"
                    + name + " text,"
                    + type + " text,"
                    + time + " text,"
                    + parameter + " text,"
                    + identifier + " text,"
                    + description + " text );")
            db.execSQL(favSql)

            /**
             * 创建“过滤表”
             */
            val filterSql = ("CREATE TABLE " + filterTable
                    + " (" + id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + url + " text,"
                    + name + " text,"
                    + type + " text,"
                    + time + " text,"
                    + parameter + " text,"
                    + identifier + " text,"
                    + description + " text );")
            db.execSQL(filterSql)
            // 设置事务处理成功，不设置会自动回滚不提交
            db.setTransactionSuccessful()
        } catch (e: Exception) {
        } finally {
            // 处理完成
            db.endTransaction()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {

        // 声明一个数据库对象
        private var instance: ApiDataBase? = null
        // 收藏表
        const val favTable = "fav_table"
        // 过滤表
        const val filterTable = "filter_table"

        // 数据库ID，自增
        const val id = "_id"
        // 链接
        const val url: String = "url"
        // 识别码
        const val identifier: String = "identifier"
        // 名称
        const val name: String = "name"
        // 类型
        const val type: String = "type"
        // 时间戳
        const val time: String = "time"
        // 参数
        const val parameter: String = "parameter"
        // 链接描述
        const val description: String = "description"

        fun getInstance(context: Context): ApiDataBase {
            if (instance == null) {
                instance = ApiDataBase(context)
            }
            return instance!!
        }

    }

}