package tk.weiba.idea.plugin.jdbc;


import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import tk.weiba.idea.plugin.jdbc.core.Constants;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class JDBCStatementProxy implements ClassFileTransformer {

    //JVM 首先尝试在代理类上调用以下方法
    public static void premain(String agentArgs, Instrumentation inst) {
        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule) -> {
            return builder
                    .method(ElementMatchers.named("execute")) // 拦截任意方法
                    .intercept(MethodDelegation.to(InterceptMethod.class)); // 委托
        };
        new AgentBuilder
                .Default()
                .type(ElementMatchers.isSubTypeOf(java.sql.PreparedStatement.class))
                .transform(transformer)
                .installOn(inst);
        System.out.println(Constants.JDBC_SQL_LOG_MARK + "java.sql.PreparedStatement execute 开启代理");
    }

    //如果代理类没有实现上面的方法，那么 JVM 将尝试调用该方法
    public static void premain(String agentArgs) {
        System.out.println(Constants.JDBC_SQL_LOG_MARK + "default premain");
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        System.out.println(Constants.JDBC_SQL_LOG_MARK + "default transform");
        return new byte[0];
    }
}
