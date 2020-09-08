package com.liqun.liqunbusiness.api;

import com.liqun.lib_network.okhttp.CommonOkHttpClient;
import com.liqun.lib_network.okhttp.listener.DisposeDataHandle;
import com.liqun.lib_network.okhttp.listener.DisposeDataListener;
import com.liqun.lib_network.okhttp.request.CommonRequest;
import com.liqun.lib_network.okhttp.request.RequestParams;

/**
 * 请求中心
 */
public class RequestCenter {
    static class HttpConstants {
        private static final String ROOT_URL = "http://imooc.com/api";
        //private static final String ROOT_URL = "http://39.97.122.129";

        /**
         * 首页请求接口
         */
        private static String HOME_RECOMMAND = ROOT_URL + "/module_voice/home_recommand";

        private static String HOME_RECOMMAND_MORE = ROOT_URL + "/module_voice/home_recommand_more";

        private static String HOME_FRIEND = ROOT_URL + "/module_voice/home_friend";

        /**
         * 登陆接口
         */
        public static String LOGIN = ROOT_URL + "/module_voice/login_phone";
    }

    //根据参数发送所有post请求
    public static void postRequest(String url, RequestParams params, DisposeDataListener listener,
                                  Class<?> clazz) {
        CommonOkHttpClient.post(CommonRequest.
                createPostRequest(url, params), new DisposeDataHandle(listener, clazz));
    }
}
