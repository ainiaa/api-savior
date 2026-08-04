package cn.gudqs7.plugins.common.util;

import cn.gudqs7.plugins.common.enums.PluginSettingEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

/**
 * @author wq
 * @date 2022/6/3
 */
public class WebEnvironmentUtil {

    private static final ThreadLocal<String> IP = new ThreadLocal<>();

    public static String getIp() {
        String ip = IP.get();
        if (ip != null) {
            return ip;
        }
        try {
            String defaultIp = PluginSettingHelper.getConfigItem(PluginSettingEnum.DEFAULT_IP);
            if (StringUtils.isNotBlank(defaultIp)) {
                IP.set(defaultIp);
                return defaultIp;
            }

            String useRealIp = PluginSettingHelper.getConfigItem(PluginSettingEnum.USE_REAL_IP);
            if (StringUtils.isNotBlank(useRealIp)) {
                String hostAddress = InetAddress.getLocalHost().getHostAddress();
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface next = networkInterfaces.nextElement();
                    Enumeration<InetAddress> inetAddresses = next.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        String hostAddress0 = inetAddress.getHostAddress();
                        if (inetAddress.isLoopbackAddress() || hostAddress0.contains(":")) {
                            continue;
                        }
                        if (inetAddress.isSiteLocalAddress()) {
                            hostAddress = hostAddress0;
                            break;
                        }
                    }
                }
                IP.set(hostAddress);
                return hostAddress;
            }
        } catch (UnknownHostException | SocketException ignored) {}
        IP.set("127.0.0.1");
        return "127.0.0.1";
    }

    public static void emptyIp() {
        IP.remove();
    }

    /**
     * 获取 Spring Boot 项目配置文件中的网络端口
     *
     * @param project        项目
     * @param containingFile 与此文件同 src 的配置文件优先
     * @return 网络端口
     */
    public static String getPortByConfigFile(Project project, PsiFile containingFile) {
        return project.getService(ServerPortCache.class).getPort(containingFile);
    }

    static String findPortByConfigFile(Project project, PsiFile containingFile) {
        String nameYml = "application.yml";
        String portByYmlFile = getPortByYamlFile(nameYml, project, containingFile);
        if (StringUtils.isNotBlank(portByYmlFile)) {
            return portByYmlFile;
        }
        String nameYaml = "application.yaml";
        String portByYamlFile = getPortByYamlFile(nameYaml, project, containingFile);
        if (StringUtils.isNotBlank(portByYamlFile)) {
            return portByYamlFile;
        }
        String portByPropertiesFile = getPortByPropertiesFile(project, containingFile);
        if (StringUtils.isNotBlank(portByPropertiesFile)) {
            return portByPropertiesFile;
        }
        return null;
    }

    private static String getPortByPropertiesFile(Project project, PsiFile containingFile) {
        PsiFile[] filesByName = FilenameIndex.getFilesByName(project, "application.properties", GlobalSearchScope.projectScope(project));
        if (filesByName.length > 0) {
            String backPort = null;
            for (PsiFile psiFile : filesByName) {
                VirtualFile virtualFile = psiFile.getVirtualFile();
                try (InputStream inputStream = virtualFile.getInputStream()) {
                    Properties properties = new Properties();
                    properties.load(inputStream);
                    String port = properties.getProperty("server.port");
                    if (StringUtils.isNotBlank(port)) {
                        if (containingFile != null) {
                            if (sameContentRoot(project, containingFile, virtualFile)) {
                                return port;
                            }
                        }
                        if (backPort == null && isProjectRootConfig(project, virtualFile)) {
                            backPort = port;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            return backPort;
        }
        return null;
    }

    private static String getPortByYamlFile(String name, Project project, PsiFile containingFile) {
        PsiFile[] filesByName = FilenameIndex.getFilesByName(project, name, GlobalSearchScope.projectScope(project));
        if (filesByName.length > 0) {
            String backPort = null;
            for (PsiFile psiFile : filesByName) {
                String text = psiFile.getText();
                try {
                    Map<String, Object> map = new YAMLMapper().readValue(text, new TypeReference<Map<String, Object>>() {
                    });
                    if (map != null && map.size() > 0) {
                        Object serverObj = map.get("server");
                        if (serverObj instanceof Map) {
                            Map server = (Map) serverObj;
                            Object portObj = server.get("port");
                            if (portObj != null) {
                                String port = portObj.toString();
                                if (containingFile != null) {
                                    if (sameContentRoot(project, containingFile, psiFile.getVirtualFile())) {
                                        return port;
                                    }
                                }
                                if (backPort == null && isProjectRootConfig(project, psiFile.getVirtualFile())) {
                                    backPort = port;
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            return backPort;
        }
        return null;
    }

    private static boolean sameContentRoot(Project project, PsiFile containingFile, VirtualFile configFile) {
        if (containingFile == null || containingFile.getVirtualFile() == null || configFile == null) {
            return false;
        }
        VirtualFile sourceRoot = ProjectFileIndex.getInstance(project).getContentRootForFile(containingFile.getVirtualFile());
        VirtualFile configRoot = ProjectFileIndex.getInstance(project).getContentRootForFile(configFile);
        return sourceRoot != null && sourceRoot.equals(configRoot);
    }

    static boolean isProjectRootConfig(Project project, VirtualFile configFile) {
        if (project == null || configFile == null) {
            return false;
        }
        return isProjectRootConfig(project.getBasePath(), configFile.getPath());
    }

    static boolean isProjectRootConfig(String projectBasePath, String configPath) {
        return projectBasePath != null && configPath != null && projectBasePath.equals(new File(configPath).getParent());
    }

}
