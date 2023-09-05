package tk.weiba.idea.plugin.mysql;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.Callable;

public class MonitorMethod {

    @RuntimeType
    public static Object intercept(@This Object obj, @Origin Method method, @SuperCall Callable<?> callable, @AllArguments Object... args) throws Exception {
        try {
            return callable.call();
        } finally {
            System.out.println("线程ID：" + Thread.currentThread().getId());
            String originalSql = (String) (BeanUtil.getFieldValue(BeanUtil.getFieldValue(obj, "query"), "originalSql"));
            String replaceSql = ReflectUtil.invoke(obj, "asSql");
            System.out.println("原始SQL：\r\n" + originalSql);
            System.out.println("执行SQL：\r\n" + replaceSql);
        }
    }

}
