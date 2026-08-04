package cn.gudqs7.plugins.common.util;

import cn.gudqs7.plugins.common.enums.PluginSettingEnum;
import cn.gudqs7.plugins.common.util.structure.BaseTypeParseUtil;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * @author wq
 */
public class PluginSettingHelper {

    private static final String CONFIG_FILE_PATH = "docer-config.properties";
    private static final ThreadLocal<Map<String, String>> CONFIG = ThreadLocal.withInitial(() -> new HashMap<>(16));

    /**
     * 将配置保存到缓存
     *
     * @param config 配置
     */
    public static void saveConfigToCache(Map<String, String> config) {
        Map<String, String> currentConfig = CONFIG.get();
        currentConfig.clear();
        if (config != null) {
            currentConfig.putAll(config);
        }
    }

    /**
     * 清除配置缓存
     */
    public static void clearConfigCache() {
        CONFIG.remove();
    }

    /**
     * 配置是否存在
     *
     * @return boolean
     */
    public static boolean configExists() {
        return !CONFIG.get().isEmpty();
    }

    /**
     * 配置是否不存在
     *
     * @return boolean
     */
    public static boolean configNotExists() {
        return !configExists();
    }

    /**
     * 获取配置项
     *
     * @param pluginSettingEnum 插件设置枚举
     * @return {@link T}
     */
    public static <T> T getConfigItem(PluginSettingEnum pluginSettingEnum) {
        return getConfigItem(pluginSettingEnum, null);
    }

    /**
     * 获取配置项
     *
     * @param pluginSettingEnum 插件设置枚举
     * @param defaultVal        默认值
     * @return {@link T}
     */
    public static <T> T getConfigItem(PluginSettingEnum pluginSettingEnum, T defaultVal) {
        if (pluginSettingEnum == null) {
            return defaultVal;
        }
        String settingKey = pluginSettingEnum.getSettingKey();
        switch (pluginSettingEnum.getType()) {
            case BOOL:
                boolean defaultBool = false;
                if (defaultVal instanceof Boolean) {
                    defaultBool = (boolean) defaultVal;
                }
                return (T) getConfigItemBool(settingKey, defaultBool);
            case STRING:
                String defaultStr = null;
                if (defaultVal instanceof String) {
                    defaultStr = (String) defaultVal;
                }
                return (T) getConfigItem(settingKey, defaultStr);
            case INTEGER:
                Integer defaultInt = null;
                if (defaultVal instanceof Integer) {
                    defaultInt = (Integer) defaultVal;
                }
                return (T) getConfigItemInt(settingKey, defaultInt);
            default:
                return defaultVal;
        }
    }

    /**
     * 获取配置项
     *
     * @param key 关键
     * @return {@link String}
     */
    public static String getConfigItem(String key) {
        return getConfigItem(key, null);
    }

    /**
     * 获取配置项
     *
     * @param key        关键
     * @param defaultVal 默认值
     * @return {@link String}
     */
    public static String getConfigItem(String key, String defaultVal) {
        if (configNotExists()) {
            return defaultVal;
        }
        return CONFIG.get().getOrDefault(key, defaultVal);
    }

    /**
     * 获取配置项bool
     *
     * @param key 关键
     * @return boolean
     */
    public static boolean getConfigItemBool(String key) {
        return getConfigItemBool(key, false);
    }

    /**
     * 获取配置项bool
     *
     * @param key        关键
     * @param defaultVal 默认值
     * @return boolean
     */
    public static Boolean getConfigItemBool(String key, boolean defaultVal) {
        String configItem = getConfigItem(key);
        if (configItem != null) {
            return BaseTypeParseUtil.parseBoolean(configItem, defaultVal);
        }
        return defaultVal;
    }

    /**
     * 获取配置项int
     *
     * @param key 关键
     * @return {@link Integer}
     */
    public static Integer getConfigItemInt(String key) {
        return getConfigItemInt(key, null);
    }

    /**
     * 获取配置项int
     *
     * @param key        关键
     * @param defaultVal 默认值
     * @return {@link Integer}
     */
    public static Integer getConfigItemInt(String key, Integer defaultVal) {
        String configItem = getConfigItem(key);
        if (configItem != null) {
            return BaseTypeParseUtil.parseInt(configItem, defaultVal);
        }
        return defaultVal;
    }

    // region init config

    /**
     * 初始化配置信息
     *
     * @param project            项目
     * @param currentVirtualFile 与此文件同一个 src 下的优先
     */
    public static void initConfig(Project project, VirtualFile currentVirtualFile) {
        Map<String, String> config = withReadAccess(() -> getConfigFromFile(project, currentVirtualFile));
        saveConfigToCache(config);
    }

    static <T> T withReadAccess(Supplier<T> supplier) {
        return ReadAction.compute(supplier::get);
    }

    /**
     * 根据默认的配置文件获取配置信息
     *
     * @param project            项目
     * @param currentVirtualFile 与此文件同一个 src 下的优先
     * @return 配置信息
     */
    @SneakyThrows
    public static Map<String, String> getConfigFromFile(Project project, VirtualFile currentVirtualFile) {
        ProjectConfigFileCache configFileCache = project.getService(ProjectConfigFileCache.class);
        String configScope = getConfigScope(project, currentVirtualFile);
        VirtualFile cachedConfigFile = configFileCache.getConfigFile(configScope);
        if (cachedConfigFile != null && cachedConfigFile.exists()) {
            return getConfig(configFileCache, configScope, cachedConfigFile);
        }
        PsiFile[] filesByName = FilenameIndex.getFilesByName(project, CONFIG_FILE_PATH, GlobalSearchScope.projectScope(project));
        if (currentVirtualFile != null) {
            for (PsiFile psiFile : filesByName) {
                VirtualFile virtualFile = psiFile.getVirtualFile();
                if (virtualFile != null && configScope.equals(getConfigScope(project, virtualFile))) {
                    configFileCache.setConfigFile(configScope, virtualFile);
                    return getConfig(configFileCache, configScope, virtualFile);
                }
            }
        }
        String projectBasePath = project.getBasePath();
        String defaultConfigPath = projectBasePath + File.separator + CONFIG_FILE_PATH;
        VirtualFile virtualFileByDefault = LocalFileSystem.getInstance().findFileByPath(defaultConfigPath);
        if (virtualFileByDefault != null) {
            configFileCache.setConfigFile(configScope, virtualFileByDefault);
            return getConfig(configFileCache, configScope, virtualFileByDefault);
        }
        return null;
    }

    private static Map<String, String> getConfig(ProjectConfigFileCache cache, String scope, VirtualFile configFile) throws IOException {
        Map<String, String> cachedConfig = cache.getConfig(scope, configFile);
        if (cachedConfig != null) {
            return cachedConfig;
        }
        Map<String, String> config = toMap(configFile);
        cache.setConfig(scope, configFile, config);
        return config;
    }

    private static Map<String, String> toMap(VirtualFile configFile) throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = configFile.getInputStream()) {
            properties.load(inputStream);
        }
        Map<String, String> map = new HashMap<>(8);
        for (Object key : properties.keySet()) {
            Object val = properties.get(key);
            map.put(String.valueOf(key), String.valueOf(val));
        }
        return map;
    }

    private static String getConfigScope(Project project, VirtualFile virtualFile) {
        if (virtualFile != null) {
            VirtualFile contentRoot = ProjectFileIndex.getInstance(project).getContentRootForFile(virtualFile);
            if (contentRoot != null) {
                return contentRoot.getPath();
            }
        }
        String basePath = project.getBasePath();
        return basePath == null ? "" : basePath;
    }

    // endregion init config
}
