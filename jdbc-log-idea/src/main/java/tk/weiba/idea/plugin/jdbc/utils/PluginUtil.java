package tk.weiba.idea.plugin.jdbc.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;

import java.io.File;
import java.util.List;

public class PluginUtil {

    private static final IdeaPluginDescriptor IDEA_PLUGIN_DESCRIPTOR;

    static {
        PluginId pluginId = PluginId.getId("tk.weiba.idea.plugin.jdbc");
        IDEA_PLUGIN_DESCRIPTOR = PluginManager.getPlugin(pluginId);
    }


    public static String getAgentCoreJarPath() {
        return getJarPathByStartWith("jdbc-log-agent");
    }


    private static String getJarPathByStartWith(String startWith) {
        if (IDEA_PLUGIN_DESCRIPTOR == null) {
            System.out.println("Idea JDBC LOG Plugin插件目录获取失败");
            return null;
        }
        final String quotes = "\"";
        List<File> files = FileUtil.loopFiles(IDEA_PLUGIN_DESCRIPTOR.getPath());
        for (File file : files) {
            String name = file.getName();
            if (name.startsWith(startWith)) {
                String pathStr = FileUtil.getCanonicalPath(file);
                if (StrUtil.contains(pathStr, StrUtil.SPACE)) {
                    return StrUtil.builder().append(quotes).append(pathStr).append(quotes).toString();
                }
                return pathStr;
            }
        }
        return StrUtil.EMPTY;
    }

}
