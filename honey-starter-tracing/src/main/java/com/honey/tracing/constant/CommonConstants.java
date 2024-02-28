package com.honey.tracing.constant;

public class CommonConstants {

    public static final double DEFAULT_SAMPLE_RATE = 1.0;

    public static final String HONEY_TRACER_NAME = "HoneyTracer";
    public static final String HONEY_REST_TEMPLATE_NAME = "HoneyRestTemplate";
    public static final String HONEY_KAFKA_NAME = "HoneyKafka";
    public static final String HONEY_DB_NAME = "HoneyDb";

    public static final String FIELD_HOST = "host";
    public static final String FIELD_API = "api";
    public static final String FIELD_HTTP_CODE = "httpCode";
    public static final String FIELD_SUB_SPAN_ID = "subSpanId";
    public static final String FIELD_SUB_HTTP_CODE = "subHttpCode";
    public static final String FIELD_SUB_TIMESTAMP = "subTimestamp";
    public static final String FIELD_SUB_DURATION = "subDuration";
    public static final String FIELD_SUB_HOST = "subHost";

    public static final String FIELD_DB_SERVER = "dbServer";
    public static final String FIELD_DB_NAME = "dbName";
    public static final String FIELD_SQL_TEXT = "sqlText";
    public static final String FIELD_SQL_PARAMS = "sqlParams";
    public static final String FIELD_SQL_DURATION = "sqlDuration";
    public static final String FIELD_SQL_TIMESTAMP = "sqlTimestamp";

    public static final String HOST_PATTERN_STR = "(?<=(https://|http://)).*?(?=/)";

    public static final String SLASH = "/";
    public static final String SLASH_DOUBLE = "//";

    public static final String LOG_EVENT_KIND = "logEventKind";
    public static final String LOG_EVENT_KIND_REQUEST_STACK = "requestStack";
    public static final String LOG_EVENT_KIND_DB_STACK = "dbStack";

}