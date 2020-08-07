package com.liqun.lib_network.okhttp.request;

import java.io.File;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CommonRequest {
    /**
     * 不带请求头的get请求
     * @param url
     * @param params
     * @return
     */
    public static Request createGetRequest(String url, RequestParams params){
        return createGetRequest(url, params, null);
    }
    /**
     * 可以带请求头的get请求
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static Request createGetRequest(String url, RequestParams params, RequestParams headers){
        StringBuilder urlBuilder = new StringBuilder(url).append("?");
        if (params != null) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                urlBuilder.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("&");
            }
        }
        // 添加请求头
        Headers.Builder headerBuild = new Headers.Builder();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.urlParams.entrySet()) {
                headerBuild.add(entry.getKey(), entry.getValue());
            }
        }
        Headers header = headerBuild.build();
        return new Request.Builder()
                .url(urlBuilder.substring(0, urlBuilder.length() - 1))
                .get()
                .headers(header)
                .build();
    }

    /**
     * 不带请求头的post请求
     * @param url
     * @param params
     * @return
     */
    public static Request createPostRequest(String url, RequestParams params){
        return createPostRequest(url, params, null);
    }

    /**
     * 可以带请求头的post请求
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static Request createPostRequest(String url, RequestParams params, RequestParams headers){
        FormBody.Builder formBodyBuild = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                formBodyBuild.add(entry.getKey(), entry.getValue());
            }
        }
        // 添加请求头
        Headers.Builder headerBuild = new Headers.Builder();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.urlParams.entrySet()) {
                headerBuild.add(entry.getKey(), entry.getValue());
            }
        }
        FormBody formBody = formBodyBuild.build();
        Headers header = headerBuild.build();
        Request request = new Request.Builder().url(url)
                .post(formBody)
                .headers(header)
                .build();
        return request;
    }

    /**
     * 文件上传请求
     */
    private static final MediaType FILE_TYPE = MediaType.parse("application/octet-stream");
    public static Request createMultiPostRequest(String url, RequestParams params){
        MultipartBody.Builder requestBody = new MultipartBody.Builder();
        requestBody.setType(MultipartBody.FORM);
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.fileParams.entrySet()) {
                if (entry.getValue() instanceof File) {
                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(FILE_TYPE, (File) entry.getValue()));
                } else if (entry.getValue() instanceof String) {
                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(null, (String) entry.getValue()));
                }
            }
        }
        return new Request.Builder()
                .url(url)
                .post(requestBody.build())
                .build();
    }
}
