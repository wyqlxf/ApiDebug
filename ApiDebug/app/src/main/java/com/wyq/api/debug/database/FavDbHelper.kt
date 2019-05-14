package com.wyq.api.debug.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.wyq.api.debug.base.BaseApplication
import com.wyq.api.debug.entity.Api
import java.util.*

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2019/1/9 10:44
 * 类描述：收藏数据库辅助类
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
class FavDbHelper {

    // 声明一个上下文
    private var mContext: Context

    constructor(context: Context) {
        mContext = context
    }

    companion object {

        private var instance: FavDbHelper? = null

        fun getInstance(): FavDbHelper {
            if (instance == null) {
                instance = FavDbHelper(BaseApplication.context)
            }
            return instance!!
        }
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2019/1/9 10:54
     * 方法描述：是否已经收藏了,true代表已收藏，false代表未收藏
     **/
    fun isAlreadyFav(identifier: String): Boolean {
        var flag = false
        var cursor: Cursor? = null
        var db: SQLiteDatabase? = null
        try {
            db = ApiDataBase.getInstance(mContext).readableDatabase
            cursor = db?.query(ApiDataBase.favTable, null,
                    ApiDataBase.identifier + "='" + identifier + "'", null, null, null, null)
            flag = cursor!!.count > 0
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            cursor?.let {
                if (!it.isClosed) {
                    it.close()
                }
            }
            db?.let {
                if (!it.isOpen) {
                    it.close()
                }
            }
        }
        return flag
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2019/1/9 11:07
     * 方法描述：添加收藏
     **/
    fun addFavorite(url: String, identifier: String, name: String, type: String, time: String, parameter: String, description: String) {
        var cursor: Cursor? = null
        var db: SQLiteDatabase? = null
        try {
            db = ApiDataBase.getInstance(mContext).writableDatabase
            cursor = db?.query(ApiDataBase.favTable, null,
                    ApiDataBase.identifier + "='" + identifier + "'", null, null, null, null)
            val values = ContentValues()
            values.put(ApiDataBase.url, url)
            values.put(ApiDataBase.identifier, identifier)
            values.put(ApiDataBase.name, name)
            values.put(ApiDataBase.type, type)
            values.put(ApiDataBase.time, time)
            values.put(ApiDataBase.parameter, parameter)
            values.put(ApiDataBase.description, description)
            if (cursor!!.count > 0) {
                // 已存在记录，更新记录
                db.update(ApiDataBase.favTable, values,
                        ApiDataBase.identifier + "='" + identifier + "'", null)
            } else {
                // 该记录不存在，先添加
                db.insert(ApiDataBase.favTable, null, values)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            cursor?.let {
                if (!it.isClosed) {
                    it.close()
                }
            }
            db?.let {
                if (!it.isOpen) {
                    it.close()
                }
            }
        }
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2019/1/9 11:16
     * 方法描述：移除收藏
     **/
    fun removeFavorite(identifier: String) {
        var cursor: Cursor? = null
        var db: SQLiteDatabase? = null
        try {
            db = ApiDataBase.getInstance(mContext).writableDatabase
            cursor = db?.query(ApiDataBase.favTable, null,
                    ApiDataBase.identifier + "='" + identifier + "'", null, null, null, null)
            cursor?.let {
                while (it.moveToNext()) {
                    db.delete(ApiDataBase.favTable, ApiDataBase.identifier + "='" + identifier + "'", null)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            cursor?.let {
                if (!it.isClosed) {
                    it.close()
                }
            }
            db?.let {
                if (!it.isOpen) {
                    it.close()
                }
            }
        }
    }


    /**
     * 获取收藏数据集合
     *
     * @return
     */
    fun getList(): MutableList<Api> {
        val list = ArrayList<Api>()
        var cursor: Cursor? = null
        var db: SQLiteDatabase? = null
        try {
            db = ApiDataBase.getInstance(mContext).readableDatabase
            cursor = db?.query(ApiDataBase.favTable, null, null, null, null, null, null)
            while (cursor!!.moveToNext()) {
                val name = cursor.getString(cursor
                        .getColumnIndex(ApiDataBase.name))
                val url = cursor.getString(cursor
                        .getColumnIndex(ApiDataBase.url))
                val type = cursor.getString(cursor
                        .getColumnIndex(ApiDataBase.type))
                val parameter = cursor.getString(cursor
                        .getColumnIndex(ApiDataBase.parameter))
                val description = cursor.getString(cursor
                        .getColumnIndex(ApiDataBase.description))
                val time = cursor.getString(cursor
                        .getColumnIndex(ApiDataBase.time))
                val api = Api()
                api.name = name
                api.type = type
                api.url = url
                api.parameter = parameter
                api.description = description
                try {
                    api.time = time.toLong()
                } catch (ex: Exception) {
                    api.time = 0
                }
                list.add(api)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            cursor?.let {
                if (!it.isClosed) {
                    it.close()
                }
            }
            db?.let {
                if (!it.isOpen) {
                    it.close()
                }
            }
        }
        return list
    }

}