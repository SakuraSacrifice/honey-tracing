package com.honey.tracing.reporter;

import com.honey.tracing.exception.HoneyTracingException;
import io.jaegertracing.internal.LogData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static com.honey.tracing.constant.CommonConstants.*;
import static com.honey.tracing.reporter.HoneySpanReportEntity.SCALE;

public class HoneyDbStack {

    private String dbServer;
    private String dbName;
    private String sqlText;
    private String sqlParams;
    private String sqlDuration;
    private String sqlTimestamp;

    private HoneyDbStack() {

    }

    public String getDbServer() {
        return dbServer;
    }

    public void setDbServer(String dbServer) {
        this.dbServer = dbServer;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public String getSqlParams() {
        return sqlParams;
    }

    public void setSqlParams(String sqlParams) {
        this.sqlParams = sqlParams;
    }

    public String getSqlDuration() {
        return sqlDuration;
    }

    public void setSqlDuration(String sqlDuration) {
        this.sqlDuration = sqlDuration;
    }

    public String getSqlTimestamp() {
        return sqlTimestamp;
    }

    public void setSqlTimestamp(String sqlTimestamp) {
        this.sqlTimestamp = sqlTimestamp;
    }

    public static class HoneyDbStackBuilder {
        private LogData logData;

        private HoneyDbStackBuilder() {

        }

        public static HoneyDbStackBuilder builder() {
            return new HoneyDbStackBuilder();
        }

        public HoneyDbStackBuilder withLogData(LogData logData) {
            this.logData = logData;
            return this;
        }

        public HoneyDbStack build() {
            if (logData == null || logData.getFields() == null) {
                throw new HoneyTracingException();
            }
            Map<String, ?> logDataFields = logData.getFields();
            HoneyDbStack honeyDbStack = new HoneyDbStack();
            honeyDbStack.dbServer = (String) logDataFields.get(FIELD_DB_SERVER);
            honeyDbStack.dbName = (String) logDataFields.get(FIELD_DB_NAME);
            honeyDbStack.sqlText = (String) logDataFields.get(FIELD_SQL_TEXT);
            honeyDbStack.sqlParams = (String) logDataFields.get(FIELD_SQL_PARAMS);
            honeyDbStack.sqlDuration = new BigDecimal(String.valueOf(logDataFields.get(FIELD_SQL_DURATION)))
                    .divide(BigDecimal.valueOf(1000), SCALE , RoundingMode.DOWN).toString();
            honeyDbStack.sqlTimestamp = new BigDecimal(String.valueOf(logDataFields.get(FIELD_SQL_TIMESTAMP)))
                    .divide(BigDecimal.valueOf(1000), SCALE , RoundingMode.DOWN).toString();
            return honeyDbStack;
        }
    }

}