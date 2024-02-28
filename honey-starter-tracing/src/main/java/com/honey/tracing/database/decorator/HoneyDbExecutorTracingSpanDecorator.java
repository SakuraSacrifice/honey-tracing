package com.honey.tracing.database.decorator;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import io.jaegertracing.internal.JaegerSpan;
import io.opentracing.Span;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.type.TypeHandlerRegistry;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;

import static com.honey.tracing.constant.CommonConstants.*;

/**
 * 作用于{@link Executor}的装饰器，装饰{@link Span}。
 */
public class HoneyDbExecutorTracingSpanDecorator implements HoneyDbExecutorTracingDecorator {

    private static final String QUESTION_MARK = "?";
    private static final String COMMA = ",";
    private static final String NULL_STR = "null";
    private static final char SPACE_CHAR = ' ';
    private static final String LINE_BREAK_REGEX = "\n";

    @Override
    public void onExecute(Invocation invocation, Span span) {
        // 设置数据库服务端地址信息和数据库名
        try {
            DataSource dataSource = ((MappedStatement) invocation.getArgs()[0]).getConfiguration().getEnvironment().getDataSource();
            assembleDbServerAndName(dataSource, (JaegerSpan) span);
        } catch (Exception e) {
            // do nothing
        }

        // 设置SQL语句和参数信息
        try {
            assembleSqlTextAndParams(invocation.getArgs(), (JaegerSpan) span);
        } catch (Exception e) {
            // do nothing
        }
    }

    @Override
    public void onFinish(Invocation invocation, Span span) {
        // do nothing
    }

    @Override
    public void onError(Invocation invocation, Exception exception, Span span) {
        // do nothing
    }

    private void assembleDbServerAndName(DataSource dataSource, JaegerSpan span) {
        String url = StringUtils.EMPTY;
        try {
            if (dataSource instanceof HikariDataSource) {
                url = ((HikariDataSource) dataSource).getJdbcUrl();
            } else if (dataSource instanceof DruidDataSource) {
                url = ((DruidDataSource) dataSource).getUrl();
            } else if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
                url = ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).getUrl();
            } else {
                // 无法判断数据库连接池类型的情况下才通过连接拿url
                Connection connection = dataSource.getConnection();
                DatabaseMetaData metaData = connection.getMetaData();
                url = metaData.getURL();
            }
        } catch (Exception e) {
            // do nothing
        }
        if (StringUtils.isNotEmpty(url)) {
            // 从连接串中解析出数据库服务端地址信息
            int left = url.indexOf(SLASH_DOUBLE) + 2;
            int mid = url.indexOf(SLASH, left);
            int right = url.indexOf(QUESTION_MARK);
            span.setTag(FIELD_DB_SERVER, url.substring(left, mid));
            if (right == -1) {
                span.setTag(FIELD_DB_NAME, url.substring(mid + 1));
            } else {
                span.setTag(FIELD_DB_NAME, url.substring(mid + 1, right));
            }
        }
    }

    private void assembleSqlTextAndParams(Object[] args, Span span) {
        MappedStatement mappedStatement = ((MappedStatement) args[0]);
        // 先获取SQL
        BoundSql boundSql;
        if (args.length == 6) {
            boundSql = ((BoundSql) args[5]);
        } else {
            boundSql = mappedStatement.getBoundSql(args[1]);
        }
        span.setTag(FIELD_SQL_TEXT, toPrettySql(boundSql.getSql()));

        // 再获取Params
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Object parameterObject = boundSql.getParameterObject();
        TypeHandlerRegistry typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        if (null != parameterMappings) {
            String[] paramStrs = new String[parameterMappings.size()];
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = mappedStatement.getConfiguration().newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    if (null == value) {
                        paramStrs[i] = NULL_STR;
                    } else {
                        paramStrs[i] = value.toString();
                    }
                }
            }
            String sqlParamsStr = String.join(COMMA, paramStrs);
            span.setTag(FIELD_SQL_PARAMS, sqlParamsStr);
        }
    }

    private String toPrettySql(String sql) {
        // 去除换行符
        sql = sql.replaceAll(LINE_BREAK_REGEX, StringUtils.EMPTY);
        // 去除多余空格
        StringBuilder sqlBuilder = new StringBuilder();
        boolean necessarySpace = true;
        for (int i = 0; i < sql.length(); i++) {
            if (sql.charAt(i) == SPACE_CHAR) {
                if (necessarySpace) {
                    sqlBuilder.append(sql.charAt(i));
                }
                necessarySpace = false;
            } else {
                sqlBuilder.append(sql.charAt(i));
                necessarySpace = true;
            }
        }
        return sqlBuilder.toString();
    }

}