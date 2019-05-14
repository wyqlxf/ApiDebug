package com.wyq.api.debug.request;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 创建人： WangYongQi
 * 创建时间：2018/7/23.
 * 文件说明：自定义JSON模板
 * 字体颜色：#217417
 */
public class JSONTemplate {

    /**
     * 解析JSON模板
     *
     * @param json
     * @return
     */
    public static Spanned parseJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return Html.fromHtml("sorry, not data");
        }
        StringBuffer buffer = new StringBuffer();
        try {
            JsonElement map = new Gson().fromJson(json, JsonElement.class);
            JsonElement rootJsonElement = new Gson().toJsonTree(map);
            buffer.append("{\n");
            addJson(rootJsonElement, buffer, 0);
            buffer.append("}");
            return Html.fromHtml(buffer.toString().replace("\n", "<br>"));
        } catch (Exception ex) {
            return Html.fromHtml(json);
        }
    }

    /**
     * 添加JSON数据
     *
     * @param obj
     * @param buffer
     * @param flag
     */
    private static void addJson(JsonElement obj, StringBuffer buffer, int flag) {
        String space = "";
        switch (flag) {
            case 0:
                // 遍历根目录
                space = "";
                break;
            case 1:
                // 遍历JSON对象
                space = "&emsp";
                break;
            case 2:
                // 遍历JSON对象里面的数组
                space = "&emsp&emsp";
                break;
            case 3:
                // 遍历JSON对象里面的数组中的值
                space = "&emsp&emsp&emsp";
                break;
        }
        if (obj.isJsonObject()) {
            // 如果是JSON对象
            Set<Map.Entry<String, JsonElement>> set = obj.getAsJsonObject().entrySet();
            Iterator<Map.Entry<String, JsonElement>> keys = set.iterator();
            while (keys.hasNext()) {
                Map.Entry<String, JsonElement> type = keys.next();
                String key = type.getKey();
                JsonElement value = type.getValue();
                if (value.isJsonArray()) {
                    // 如果是数组
                    buffer.append(space + "\"" + key + "\"" + ":&emsp");
                    buffer.append("[\n");

                    addJson(value, buffer, 2);

                    buffer.append(space + "],");
                    buffer.append("\n");
                } else if (value.isJsonObject()) {
                    // 如果是对象
                    buffer.append(space + "\"" + key + "\"" + ":&emsp");
                    buffer.append("{\n");

                    addJson(value, buffer, 1);

                    buffer.append(space + "},");
                    buffer.append("\n");
                } else {
                    // add value
                    buffer.append(space + "\"" + key + "\"" + ":&emsp" + "<font color=\"#217417\">" + "\"" + value.getAsString() + "\"" + "</font>" + ",");
                    buffer.append("\n");
                }
            }
        } else if (obj.isJsonArray()) {
            // 如果是JSON数组
            if (obj.getAsJsonArray().size() > 0) {
                for (int i = 0; i < obj.getAsJsonArray().size(); i++) {
                    JsonElement tag = obj.getAsJsonArray().get(i);
                    if (tag.isJsonObject()) {
                        // 如果是对象
                        buffer.append(space + "{");
                        buffer.append("\n");

                        addJson(tag, buffer, 3);

                        if (i == obj.getAsJsonArray().size() - 1) {
                            buffer.append(space + "}");
                        } else {
                            buffer.append(space + "},");
                        }
                        buffer.append("\n");
                    } else {
                        // add value
                        try {
                            buffer.append(space + "\"" + tag.getAsString() + "\"");
                        } catch (Exception ex) {
                        }
                    }
                }
            }
        }
    }

}
