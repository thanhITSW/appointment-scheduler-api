package com.appointment.constant;

import java.util.List;

public final class HeaderConstants {

    private HeaderConstants() {}

    public static final String X_TOTAL_COUNT = "x-total-count";
    public static final String LINK_FORMAT = "<{0}>; rel=\"{1}\"";
    public static final String LINK = "link";
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "access-control-expose-headers";
    public static final List<String> IP_HEADER_CANDIDATES = List.of(
            "True-Client-IP",
            "CF-Connecting-IP",
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR",
            "X-Real-IP"
    );
}
