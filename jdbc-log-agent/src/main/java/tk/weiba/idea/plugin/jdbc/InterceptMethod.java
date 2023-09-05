package tk.weiba.idea.plugin.jdbc;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

import static tk.weiba.idea.plugin.jdbc.core.Constants.*;

public class InterceptMethod {

    @RuntimeType
    public static Object intercept(@This Object obj, @Origin Method method, @SuperCall Callable<?> callable, @AllArguments Object... args) throws Exception {
        try {
            return callable.call();
        } finally {
            String className = obj.getClass().getName();
            if (className.contains("com.mysql.cj.jdbc.ClientPreparedStatement")) {
                String originalSql = (String) (BeanUtil.getFieldValue(BeanUtil.getFieldValue(obj, "query"), "originalSql"));
                String replaceSql = ReflectUtil.invoke(obj, "asSql");
                outputSql("Mysql", originalSql, replaceSql);
            } else if (className.contains("org.mariadb.jdbc.ClientSidePreparedStatement")) {
                String originalSql = (String) BeanUtil.getFieldValue(obj, "sqlQuery");
                String replaceSql = replacePlaceholders(originalSql, (Object[]) BeanUtil.getFieldValue(obj, "parameters"));
                outputSql("Mariadb", originalSql, replaceSql);
            } else if (!EXCLUDE_CLASS.contains(className)){
                System.out.println(JDBC_SQL_LOG_MARK + " 未适配: " + className);
            }
        }
    }

    private static void outputSql(String type, String originalSql, String replaceSql) {
        // 移除换行符 多空变为一空
        originalSql = originalSql.replaceAll("\\n", " ").replaceAll("\\s+", " ");
        replaceSql = replaceSql.replaceAll("\\n", " ").replaceAll("\\s+", " ");

        StringBuilder output = new StringBuilder();
        output.append(JDBC_SQL_LOG_MARK).append(replace).append("\n");
        output.append(JDBC_SQL_LOG_MARK).append(" ThreadId:").append(Thread.currentThread().getId());
        output.append("--Type:").append(type);
        output.append("--Time:").append(LocalDateTime.now().format(formatter)).append("\n");
        output.append(JDBC_SQL_LOG_MARK).append(" 原始SQL：").append(originalSql).append("\n");
        output.append(JDBC_SQL_LOG_MARK).append(" 执行SQL：").append(replaceSql).append("\n");
        output.append(JDBC_SQL_LOG_MARK).append(replace);
        System.out.println(output);
    }

    public static String replacePlaceholders(String template, Object... replacements) {
        if (replacements == null || replacements.length == 0) {
            return template; // 如果没有提供替换值，返回原始模板
        }

        StringBuilder result = new StringBuilder();
        int replacementIndex = 0;
        int templateIndex = 0;

        while (templateIndex < template.length()) {
            int placeholderIndex = template.indexOf("?", templateIndex);
            if (placeholderIndex == -1) {
                // 如果没有更多占位符，将剩余的模板内容添加到结果中并退出循环
                result.append(template.substring(templateIndex));
                break;
            }

            // 将占位符之前的内容添加到结果中
            result.append(template, templateIndex, placeholderIndex);

            // 如果还有可用的替换值，将其添加到结果中，否则添加占位符
            if (replacementIndex < replacements.length) {
                result.append(replacements[replacementIndex]);
            } else {
                result.append("?");
            }

            // 更新索引以继续寻找下一个占位符
            templateIndex = placeholderIndex + 1;
            replacementIndex++;
        }

        return result.toString();
    }

}
