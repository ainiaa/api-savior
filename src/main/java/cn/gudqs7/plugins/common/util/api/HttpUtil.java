package cn.gudqs7.plugins.common.util.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author wq
 * @date 2021/9/21
 */
public class HttpUtil {

    private static final int TIMEOUT_MILLIS = 20_000;
    private static final int MAX_RESPONSE_BYTES = 10 * 1024 * 1024;

    public static String sendHttpWithBody(String requestUrl, String method, String outputStr, Map<String, String> headers) {
        return sendHttpWithBody(requestUrl, method, outputStr, headers, TIMEOUT_MILLIS);
    }

    static String sendHttpWithBody(String requestUrl, String method, String outputStr, Map<String, String> headers, int timeoutMillis) {
        return sendHttpWithBody(requestUrl, method, outputStr, headers, timeoutMillis, MAX_RESPONSE_BYTES);
    }

    static String sendHttpWithBody(String requestUrl, String method, String outputStr, Map<String, String> headers,
                                   int timeoutMillis, int maxResponseBytes) {
        if (maxResponseBytes < 1) {
            throw new IllegalArgumentException("maxResponseBytes must be positive");
        }
        HttpURLConnection conn = null;
        try {
            URL url = new URL(requestUrl);
            conn = (HttpURLConnection) url.openConnection();
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    conn.setRequestProperty(key, value);
                }
            }
            conn.setRequestMethod(method);
            conn.setConnectTimeout(timeoutMillis);
            conn.setReadTimeout(timeoutMillis);
            conn.setDoInput(true);
            conn.setDoOutput(outputStr != null);
            conn.setUseCaches(false);
            if (outputStr != null) {
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(outputStr.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                }
            }

            InputStream responseStream = conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST
                    ? conn.getErrorStream() : conn.getInputStream();
            if (responseStream == null) {
                return "";
            }
            try (InputStream inputStream = responseStream;
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                byte[] bytes = new byte[8192];
                int len;
                while ((len = inputStream.read(bytes)) != -1) {
                    if (outputStream.size() + len > maxResponseBytes) {
                        throw new IOException("HTTP response exceeds " + maxResponseBytes + " bytes");
                    }
                    outputStream.write(bytes, 0, len);
                }
                return outputStream.toString(StandardCharsets.UTF_8.name());
            }
        } catch (Exception e) {
            throw new RuntimeException("请求接口异常，错误信息：" + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }


}
