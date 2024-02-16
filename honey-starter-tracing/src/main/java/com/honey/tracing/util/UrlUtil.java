package com.honey.tracing.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.honey.tracing.constant.CommonConstants.HOST_PATTERN_STR;
import static com.honey.tracing.constant.CommonConstants.SLASH;

/**
 * Url处理工具类。
 */
public class UrlUtil {

    private static final Pattern HOST_PATTERN = Pattern.compile(HOST_PATTERN_STR);

    /**
     * 从请求URI中解析出域名。<br/>
     * http://www.baidu.com/<br/>
     * http://www.baidu.com<br/>
     * https://www.baidu.com/<br/>
     * https://www.baidu.com<br/>
     */
    public static String getHostFromUri(String uri) {
        if (!uri.endsWith(SLASH)) {
            // 如果uri不以/结尾则需要手动添加上
            // 否则正则匹配会无法将域名匹配出来
            uri = uri + SLASH;
        }
        if (StringUtils.isNotEmpty(uri)) {
            Matcher matcher = HOST_PATTERN.matcher(uri);
            if (matcher.find()) {
                return matcher.group(0);
            }
        }
        return StringUtils.EMPTY;
    }

}