package com.wyq.api.debug.request;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.wyq.api.debug.interfaces.OnApiResultListener;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建人：WangYongQi<br>
 * 创建时间：2016年11月11日下午3:34:43<br>
 * 修改人： <br>
 * 修改时间： <br>
 * 修改备注：
 *
 * @version V1.0
 */

class ApiRequest extends AsyncTask<String, String, Map<String, Object>> {

    /**
     * 请求接口
     **/
    private String mUrl;
    /**
     * post参数
     **/
    private Map<String, String> mMap;
    /**
     * 休眠時間
     */
    private long sleepTime = 0;

    /**
     * 当前请求方式
     **/
    private RequestWay mRequestWay = RequestWay.OTHRER;

    private OnApiResultListener onResultListener;

    private enum RequestWay {
        OTHRER, GET, POST
    }

    /**
     * 创建人：WangYongQi<br>
     * 创建时间：2016年11月14日 上午10:08:59 <br>
     * 构造方法描述：Get请求构造函数
     *
     * @param url
     */
    public ApiRequest(String url) {
        mUrl = url;
        mRequestWay = RequestWay.GET;
    }

    /**
     * 创建人：WangYongQi<br>
     * 创建时间：2016年11月14日 上午10:09:11 <br>
     * 构造方法描述：Post请求构造函数
     *
     * @param url
     * @param map
     */
    public ApiRequest(String url, Map<String, String> map) {
        mUrl = url;
        if (map == null) {
            map = new HashMap<String, String>();
        }
        mMap = map;
        mRequestWay = RequestWay.POST;
    }

    /**
     * 创建人：WangYongQi<br>
     * 创建时间：2016年11月14日 上午10:08:59 <br>
     * 构造方法描述：Get请求构造函数
     *
     * @param url
     * @param resultListener
     */
    public ApiRequest(String url, OnApiResultListener resultListener) {
        mUrl = url;
        mRequestWay = RequestWay.GET;
        onResultListener = resultListener;
    }

    /**
     * 创建人：WangYongQi<br>
     * 创建时间：2016年11月14日 上午10:09:11 <br>
     * 构造方法描述：Post请求构造函数
     *
     * @param url
     * @param map
     */
    public ApiRequest(String url, Map<String, String> map, OnApiResultListener resultListener) {
        mUrl = url;
        if (map == null) {
            map = new HashMap<String, String>();
        }
        mMap = map;
        mRequestWay = RequestWay.POST;
        onResultListener = resultListener;
    }

    /**
     * 設置休眠時間
     *
     * @param sleepTime
     */
    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    protected Map<String, Object> doInBackground(String... params) {
        // 請求結果
        Map<String, Object> map = null;
        // 請求的起始時間
        long startTime = System.currentTimeMillis();
        // 判斷請求連接是否為空
        if (!TextUtils.isEmpty(mUrl)) {
            // 判斷是否需要休眠
            if (mRequestWay == RequestWay.GET) {
                // Get请求
                map = HttpClientHelper.doGet(mUrl);
            } else if (mRequestWay == RequestWay.POST) {
                // Post请求
                map = HttpClientHelper.doPost(mUrl, mMap);
            }
        }
        // 判斷是否需要休眠
        if (sleepTime > 0) {
            try {
                // 請求的結束時間
                long endTime = System.currentTimeMillis();
                // 如果結束時間大於起始時間
                if (endTime > startTime) {
                    // 結束時間減去起始時間就等於請求已消耗的時間
                    long consumingTime = endTime - startTime;
                    // 如果消耗的時間小於需要休眠的時間則休眠，否則不再休眠
                    if (sleepTime > consumingTime) {
                        // 真正的休眠時間等於原本需要休眠的時間減去已消耗的時間
                        Thread.sleep(sleepTime - consumingTime);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    @Override
    protected void onPostExecute(Map<String, Object> map) {
        if (onResultListener != null) {
            onResultListener.onResult(map);
        }
    }

}
