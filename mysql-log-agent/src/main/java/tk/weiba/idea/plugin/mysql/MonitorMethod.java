package tk.weiba.idea.plugin.mysql;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.logging.SimpleFormatter;

public class MonitorMethod {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // 定义线的长度
    private static final int lineLength = 80;
    private static final String replace = new String(new char[lineLength]).replace('\0', '-');

    @RuntimeType
    public static Object intercept(@This Object obj, @Origin Method method, @SuperCall Callable<?> callable, @AllArguments Object... args) throws Exception {
        try {
            return callable.call();
        } finally {
           // 构建输出字符串
            StringBuilder output = new StringBuilder();
            output.append("\r\n");
            output.append(replace).append("\n");
            output.append("mysql_log - ThreadId:").append(Thread.currentThread().getId()).append("--Time:").append(LocalDateTime.now().format(formatter)).append("\n");
            String originalSql = (String) (BeanUtil.getFieldValue(BeanUtil.getFieldValue(obj, "query"), "originalSql"));
            String replaceSql = ReflectUtil.invoke(obj, "asSql");
            output.append("mysql_log - 原始SQL：\r\n").append(originalSql).append("\n");
            output.append("mysql_log - 执行SQL：\r\n").append(replaceSql).append("\n");
            output.append(replace).append("\n");
            output.append("\r\n");
            System.out.println(output.toString());
        }
    }

}
