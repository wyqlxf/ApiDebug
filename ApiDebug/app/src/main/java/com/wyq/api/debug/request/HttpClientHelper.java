package com.wyq.api.debug.request;

import android.content.Context;
import android.text.TextUtils;

import com.wyq.api.debug.request.store.CookieJarImpl;
import com.wyq.api.debug.request.store.PersistentCookieStore;
import com.wyq.api.debug.util.InfoUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 创建人：WangYongQi<br>
 * 创建时间：2016年9月23日下午3:33:34<br>
 * 修改人： <br>
 * 修改时间： <br>
 * 修改备注：
 *
 * @version V1.0
 */

public class HttpClientHelper {

    /**
     * OkHttpClient
     **/
    private static OkHttpClient okHttpClient;
    private static PersistentCookieStore persistentCookieStore;

    private final static int READ_TIMEOUT = 60;
    private final static int WRITE_TIMEOUT = 60;
    private final static int CONNECT_TIMEOUT = 60;

    public static void initHttpClient(Context context) {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);//设置读取超时时间
            builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);//设置写的超时时间
            builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);//设置连接超时时间
            persistentCookieStore = new PersistentCookieStore(context);
            CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
            builder.cookieJar(cookieJarImpl);
            okHttpClient = builder.build();
        }
    }

    public static Map<String, Object> doGet(String url) {
        Map<String, Object> map = new HashMap<>();
        String result = "";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/json;charset:utf-8")
                .addHeader("user-agent", encodeHeadInfo(InfoUtils.Companion.getInstance().getUserAgent()))
                .build();
        map.put("request_headers", request.headers());
        if (persistentCookieStore != null) {
            map.put("cookie", persistentCookieStore.get(HttpUrl.parse(url)).toString());
        }
        try {
            if (okHttpClient != null) {
                Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    result = response.body().string();
                }
                map.put("response_headers", response.headers());
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.put("result", result);
        map.put("spanned", JSONTemplate.parseJson(result));
        return map;
    }

    public static Map<String, Object> doPost(String url, Map<String, String> params) {
        Map<String, Object> map = new HashMap<>();
        String result = "";
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        try {
            if (params != null) {
                Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    String key = entry.getKey() + "";
                    String value = entry.getValue() + "";
                    if (!TextUtils.isEmpty(key)) {
                        bodyBuilder.add(key, value);
                    }
                }
            }
        } catch (Exception e) {
        }
        Request request = new Request.Builder().url(url)
                .addHeader("content-type", "application/json;charset:utf-8")
                .addHeader("user-agent", encodeHeadInfo(InfoUtils.Companion.getInstance().getUserAgent()))
                .post(bodyBuilder.build())
                .build();
        map.put("request_headers", request.headers());
        if (persistentCookieStore != null) {
            map.put("cookie", persistentCookieStore.get(HttpUrl.parse(url)).toString());
        }
        try {
            if (okHttpClient != null) {
                Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    result = response.body().string();
                }
                map.put("response_headers", response.headers());
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.put("result", result);
        map.put("spanned", JSONTemplate.parseJson(result));
        return map;
    }

    private static String encodeHeadInfo(String headInfo) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            if (!TextUtils.isEmpty(headInfo)) {
                int length = headInfo.length();
                for (int i = 0; i < length; i++) {
                    char c = headInfo.charAt(i);
                    if (c <= '\u001f' || c >= '\u007f') {
                        stringBuffer.append(String.format("\\u%04x", (int) c));
                    } else {
                        stringBuffer.append(c);
                    }
                }
            }
        } catch (Exception ex) {
            return "" + headInfo;
        }
        return stringBuffer.toString();
    }

}
