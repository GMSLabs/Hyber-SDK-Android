package com.hyber;

import com.hyber.log.HyberLogger;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;

import static okhttp3.internal.platform.Platform.INFO;

/**
 * An Hyber OkHttp interceptor which logs request and response information. Can be applied as an
 * {@linkplain OkHttpClient#interceptors() application interceptor} or as a {@linkplain
 * OkHttpClient#networkInterceptors() network interceptor}.
 */

public class HyberHttpLoggingInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public enum Level {
        /**
         * No logs.
         */
        NONE,
        /**
         * Logs request and response lines.
         * <p>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1 (3-byte body)
         *
         * <-- 200 OK (22ms, 6-byte body)
         * }</pre>
         */
        BASIC,
        /**
         * Logs request and response lines and their respective headers.
         * <p>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
         * }</pre>
         */
        HEADERS,
        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         * <p>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
         * }</pre>
         */
        FULL
    }

    public interface Logger {
        void log(String message);

        /**
         * A {@link Logger} defaults output appropriate for the current platform.
         */
        Logger DEFAULT = new Logger() {
            @Override
            public void log(String message) {
                Platform.get().log(INFO, message, null);
            }
        };
    }

    HyberHttpLoggingInterceptor() {
        this(Logger.DEFAULT);
    }

    private HyberHttpLoggingInterceptor(Logger logger) {
        this.logger = logger;
    }

    private final Logger logger;

    private volatile Level level = Level.NONE;

    /**
     * Change the level at which this interceptor logs.
     */
    HyberHttpLoggingInterceptor setLevel(Level level) {
        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        this.level = level;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Level level = this.level;
        StringBuilder logRequestSB = new StringBuilder();
        StringBuilder logResponseSB = new StringBuilder();

        Request request = chain.request();
        if (level == Level.NONE)
            return chain.proceed(request);

        boolean logBody = level == Level.FULL;
        boolean logHeaders = logBody || level == Level.HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();

        logRequestSB.append(String.format(Locale.getDefault(), "--> %s %s %s", request.method(), request.url(),
                connection != null ? connection.protocol() : Protocol.HTTP_1_1));

        if (!logHeaders && hasRequestBody)
            logRequestSB.append(String.format(Locale.getDefault(), " (%d-byte body)", requestBody.contentLength()));

        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null)
                    logRequestSB.append(String.format(Locale.getDefault(), "\nContent-Type: %s",
                            requestBody.contentType()));

                if (requestBody.contentLength() != -1)
                    logRequestSB.append(String.format(Locale.getDefault(), "\nContent-Length: %d",
                            requestBody.contentLength()));
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name))
                    logRequestSB.append(String.format(Locale.getDefault(), "\n%s: %s", name, headers.value(i)));
            }

            if (!logBody || !hasRequestBody) {
                logRequestSB.append(String.format(Locale.getDefault(), "\n--> END %s", request.method()));
            } else if (bodyEncoded(request.headers())) {
                logRequestSB.append(String.format(Locale.getDefault(), "\n--> END %s (encoded body omitted)",
                        request.method()));
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null)
                    charset = contentType.charset(UTF8);

                if (isPlaintext(buffer)) {
                    logRequestSB.append(String.format(Locale.getDefault(), "\n%s", buffer.readString(charset)));
                    logRequestSB.append(String.format(Locale.getDefault(), "\n--> END %s (%d-byte body)",
                            request.method(), requestBody.contentLength()));
                } else {
                    logRequestSB.append(String.format(Locale.getDefault(), "\n--> END %s (binary %d-byte body omitted)",
                            request.method(), requestBody.contentLength()));
                }
            }
        }

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            logResponseSB.append(String.format(Locale.getDefault(), "<-- HTTP FAILED: %s", e));
            HyberLogger.e(logRequestSB.toString());
            HyberLogger.e(logResponseSB.toString());
            throw e;
        }

        HyberLogger.d(logRequestSB.toString());

        ResponseBody responseBody = response.body();

        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";

        logResponseSB.append(String.format(Locale.getDefault(), "<-- %d %s %s (%dms%s)",
                response.code(), response.message(), response.request().url(), tookMs,
                (!logHeaders ? ", " + bodySize + " body" : "")));

        if (logHeaders) {
            Headers headers = response.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                logResponseSB.append(String.format(Locale.getDefault(), "\n%s: %s", headers.name(i), headers.value(i)));
            }

            if (!logBody || !HttpHeaders.hasBody(response)) {
                logResponseSB.append("\n<-- END HTTP");
            } else if (bodyEncoded(response.headers())) {
                logResponseSB.append("\n<-- END HTTP (encoded body omitted)");
            } else {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        logResponseSB.append("\nCouldn't decode the response body; charset is likely malformed.");
                        logResponseSB.append("\n<-- END HTTP");
                        return response;
                    }
                }

                if (!isPlaintext(buffer)) {
                    logResponseSB.append(String.format(Locale.getDefault(),
                            "\n<-- END HTTP (binary %d-byte body omitted)", buffer.size()));
                    return response;
                }

                if (contentLength != 0)
                    logResponseSB.append(String.format(Locale.getDefault(), "\n%s",
                            buffer.clone().readString(charset)));

                logResponseSB.append(String.format(Locale.getDefault(), "\n<-- END HTTP (%d-byte body)", buffer.size()));
            }
        }

        HyberLogger.d(logResponseSB.toString());

        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !"identity".equalsIgnoreCase(contentEncoding);
    }
}
