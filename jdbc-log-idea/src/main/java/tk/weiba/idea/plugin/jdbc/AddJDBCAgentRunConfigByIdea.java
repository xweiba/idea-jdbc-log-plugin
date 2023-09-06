package tk.weiba.idea.plugin.jdbc;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import com.intellij.openapi.projectRoots.Sdk;
import tk.weiba.idea.plugin.jdbc.core.Constants;
import tk.weiba.idea.plugin.jdbc.utils.PluginUtil;

import java.util.Objects;

public class AddJDBCAgentRunConfigByIdea extends JavaProgramPatcher {

    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {

        if (!(configuration instanceof RunConfiguration)) {
            return;
        }

        String runClassName = configuration.getClass().getName();
        if (Constants.EXCLUDE_RUN_CONFIG_CLASS.contains(runClassName)) {
            return;
        }

        System.out.println(runClassName);

        Sdk jdk = javaParameters.getJdk();

        if (Objects.isNull(jdk)) {
            return;
        }

        JavaSdkVersion version = JavaSdk.getInstance().getVersion(jdk);

        if (Objects.isNull(version)) {
            return;
        }

        if (version.compareTo(JavaSdkVersion.JDK_1_8) < 0) {
            return;
        }

        String agentCoreJarPath = PluginUtil.getAgentCoreJarPath();

        if (agentCoreJarPath == null || agentCoreJarPath.trim().isEmpty()) {
            return;
        }

        RunConfiguration runConfiguration = (RunConfiguration) configuration;
        ParametersList vmParametersList = javaParameters.getVMParametersList();
        vmParametersList.addParametersString("-javaagent:" + agentCoreJarPath);
        vmParametersList.addNotEmptyProperty("tk.weiba.idea.plugin.jdbc.projectId", runConfiguration.getProject().getLocationHash());

    }

}
