package com.appointment.utils;

import com.appointment.constant.HeaderConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

@Slf4j
public final class RequestUtils
{
    private RequestUtils() {}

    public static String getIpAddressFromHeader(HttpHeaders headers)
    {
        for (String header : HeaderConstants.IP_HEADER_CANDIDATES)
        {
            final String ip = headers.getFirst(header);
            if (StringUtils.isNotBlank(ip))
            {
                return ip;
            }
        }

        return "-";
    }
}
