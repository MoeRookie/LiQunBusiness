package com.liqun.lib_network.okhttp.request;

import java.io.File;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * build the request
 */
public class CommonRequest {
    /**
     * ressemble the params to the url
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
     * @param headersParams
     * @return
     */
    public static Request createGetRequest(String url, RequestParams params, RequestParams headersParams){
        StringBuilder stringBuilder = new StringBuilder(url).append("?");
        if (params != null) { // 遍历参数
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                stringBuilder.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("&");
            }
        }
        Headers.Builder headersBuilder = new Headers.Builder();
        if (headersParams != null) { // 遍历请求头
            for (Map.Entry<String, String> entry : headersParams.urlParams.entrySet()) {
                headersBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        Headers headers = headersBuilder.build();
        Request request = new Request.Builder()
                .url(stringBuilder.toString().trim().substring(0, stringBuilder.length() - 1))
                .get()
                .headers(headers)
                .build();
        return request;
    }
    /**
     * create the key-value Request
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
     * @param headersParams
     * @return
     */
    public static Request createPostRequest(String url, RequestParams params, RequestParams headersParams){
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (params != null) { // 遍历参数
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                formBodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        Headers.Builder headersBuilder = new Headers.Builder();
        if (headersParams != null) { // 遍历请求头
            for (Map.Entry<String, String> entry : headersParams.urlParams.entrySet()) {
                headersBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        FormBody formBody = formBodyBuilder.build();
        Headers headers = headersBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .headers(headers)
                .build();
        return request;
    }

    private static final MediaType FILE_TYPE = MediaType.parse("application/octet-stream");

    /**
     * 文件上传请求
     * @param url
     * @param params
     * @return
     */
    public static Request createMultiPostRequest(String url, RequestParams params) {
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        if (params != null) { // 参数遍历
            for (Map.Entry<String, Object> entry : params.fileParams.entrySet()) {
                if (entry.getValue() instanceof File) {
                    multipartBodyBuilder
                            .addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                                    RequestBody.create(FILE_TYPE, (File) entry.getValue()));
                } else if (entry.getValue() instanceof String) {
                    multipartBodyBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(null, (String) entry.getValue()));
                }
            }
        }
        MultipartBody multipartBody = multipartBodyBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
        return request;
    }
}
