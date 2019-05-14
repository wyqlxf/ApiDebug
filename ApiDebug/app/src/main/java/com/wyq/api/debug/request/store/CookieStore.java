package com.wyq.api.debug.request.store;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public interface CookieStore {

    void add(HttpUrl uri, List<Cookie> cookie);

    List<Cookie> get(HttpUrl uri);

    boolean remove(HttpUrl uri, Cookie cookie);

    boolean removeAll();

}
