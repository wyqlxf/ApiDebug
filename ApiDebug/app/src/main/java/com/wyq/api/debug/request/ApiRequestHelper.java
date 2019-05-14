package com.wyq.api.debug.request;

import android.os.AsyncTask;
import android.os.Build;

import com.wyq.api.debug.interfaces.OnApiResultListener;

import java.util.Map;

/**
 * 创建人：WangYongQi<br>
 * 创建时间：2016年11月11日下午3:34:43<br>
 * 修改人： <br>
 * 修改时间： <br>
 * 修改备注：API请求辅助类
 *
 * @version V1.0
 */
public class ApiRequestHelper {

    // 声明一个实例
    private static ApiRequestHelper mInstance;

    /**
     * 獲取API請求輔助類的實例
     *
     * @return
     */
    public static ApiRequestHelper getInstance() {
        if (mInstance == null) {
            mInstance = new ApiRequestHelper();
        }
        return mInstance;
    }

    /**
     * 進行Get請求接口數據
     *
     * @param url
     */
    public void doGet(String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ApiRequest(url).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new ApiRequest(url).execute();
        }
    }

    /**
     * 進行Get請求接口數據，并對結果進行回調處理
     *
     * @param url
     * @param resultListener
     */
    public void doGet(String url, OnApiResultListener resultListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ApiRequest(url, resultListener).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new ApiRequest(url, resultListener).execute();
        }
    }

    /**
     * 進行Post請求接口數據，傳入post參數
     *
     * @param url
     * @param map
     */
    public void doPost(String url, Map<String, String> map) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ApiRequest(url, map).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new ApiRequest(url, map).execute();
        }
    }

    /**
     * 進行Post請求接口數據，傳入post參數，并對結果進行回調處理
     *
     * @param url
     * @param map
     * @param resultListener
     */
    public void doPost(String url, Map<String, String> map, OnApiResultListener resultListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ApiRequest(url, map, resultListener).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new ApiRequest(url, map, resultListener).execute();
        }
    }

    /**
     * 進行Post請求接口數據，傳入post參數，并對結果進行回調處理
     *
     * @param url
     * @param map
     * @param resultListener
     * @param sleepTime      休眠時間
     */
    public void doPost(String url, Map<String, String> map, OnApiResultListener resultListener, long sleepTime) {
        ApiRequest apiRequest = new ApiRequest(url, map, resultListener);
        apiRequest.setSleepTime(sleepTime);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            apiRequest.executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            apiRequest.execute();
        }
    }

}
