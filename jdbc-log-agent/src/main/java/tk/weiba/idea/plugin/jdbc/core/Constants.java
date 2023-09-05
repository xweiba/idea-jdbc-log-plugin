package tk.weiba.idea.plugin.jdbc.core;

import java.time.format.DateTimeFormatter;

public class Constants {

    public static final String JDBC_SQL_LOG_MARK = "JDBC_SQL_LOG - ";


    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // 定义线的长度
    public static final int lineLength = 80;
    public static final String replace = new String(new char[lineLength]).replace('\0', '-');
    public static final String EXCLUDE_CLASS = ",com.alibaba.druid.pool.DruidPooledPreparedStatement,";
}
