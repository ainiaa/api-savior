package cn.gudqs7.plugins.search.resolver;

import cn.gudqs7.plugins.common.enums.HttpMethod;
import cn.gudqs7.plugins.common.pojo.resolver.CommentInfo;
import cn.gudqs7.plugins.common.resolver.comment.AnnotationHolder;
import cn.gudqs7.plugins.common.resolver.RequestMappingResolver;
import cn.gudqs7.plugins.common.util.jetbrain.ExceptionUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author wq
 * @date 2022/5/28
 */
public class ApiResolverService {

    static final int MAX_COMPOSED_CONTROLLER_DEPTH = 8;

    private final Project project;

    public ApiResolverService(Project project) {
        this.project = project;
    }

    public static ApiResolverService getInstance(Project project) {
        return new ApiResolverService(project);
    }

    @NotNull
    public List<ApiNavigationItem> getApiNavigationItemList() {
        List<ApiNavigationItem> navigationItemList = new ArrayList<>();
        Set<PsiClass> psiClassSet = new LinkedHashSet<>();
        Deque<String> annotationNames = new ArrayDeque<>(Arrays.asList("Controller", "RestController"));
        Map<String, Integer> annotationDepths = new HashMap<>();
        annotationDepths.put("Controller", 0);
        annotationDepths.put("RestController", 0);
        while (!annotationNames.isEmpty()) {
            String supportAnnotation = annotationNames.removeFirst();
            int depth = annotationDepths.get(supportAnnotation);
            Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance().get(supportAnnotation, project, GlobalSearchScope.projectScope(project));
            for (PsiAnnotation psiAnnotation : psiAnnotations) {
                PsiClass psiClass = PsiTreeUtil.getParentOfType(psiAnnotation, PsiClass.class);
                if (psiClass == null) {
                    continue;
                }
                if (psiClass.isAnnotationType()) {
                    String annotationName = psiClass.getName();
                    if (annotationName != null && depth < MAX_COMPOSED_CONTROLLER_DEPTH
                            && !annotationDepths.containsKey(annotationName)) {
                        annotationDepths.put(annotationName, depth + 1);
                        annotationNames.addLast(annotationName);
                    }
                } else {
                    psiClassSet.add(psiClass);
                }
            }
        }
        for (PsiClass psiClass : psiClassSet) {
            try {
                navigationItemList.addAll(getServiceItemList(psiClass));
            } catch (Exception e) {
                String classQname = psiClass.getQualifiedName();
                ExceptionUtil.logException(e, String.format("扫描接口时出错, 接口类全限定名为: %s; 错误信息为: %s", classQname, e.getMessage()));
            }
        }
        return navigationItemList;
    }

    protected List<ApiNavigationItem> getServiceItemList(@NotNull PsiClass psiClass) {
        List<ApiNavigationItem> navigationItemList = new ArrayList<>(2);
        List<MethodPathInfo> methodPathList = new ArrayList<>(32);

        List<String> classParams = RequestMappingResolver.resolveClassParams(psiClass);
        List<String> classPaths = RequestMappingResolver.resolveClassPaths(psiClass);

        String classQname = psiClass.getQualifiedName();
        PsiMethod[] psiMethods = psiClass.getMethods();
        for (PsiMethod psiMethod : psiMethods) {
            try {
                methodPathList.addAll(getMethodPathList(psiMethod, psiClass));
            } catch (Exception e) {
                String methodName = psiMethod.getName();
                String methodQname = classQname + "#" + methodName;
                ExceptionUtil.logException(e, String.format("扫描接口时出错, 方法为: %s; 错误信息为: %s", methodQname, e.getMessage()));
            }
        }

        for (String classPath : classPaths) {
            for (MethodPathInfo methodPathInfo : methodPathList) {
                PsiMethod psiMethod = methodPathInfo.getPsiMethod();
                HttpMethod httpMethod = methodPathInfo.getHttpMethod();
                String methodPath = methodPathInfo.getMethodPath();
                List<String> methodParams = methodPathInfo.getParams();

                // 获取 params 信息, 先从方法的注解取, 取不到则尝试类的注解
                String param = "";
                if (CollectionUtils.isNotEmpty(methodParams)) {
                    param = "?" + String.join("&", methodParams);
                } else if (CollectionUtils.isNotEmpty(classParams)) {
                    param = "?" + String.join("&", classParams);
                }
                String fullPath = RequestMappingResolver.joinPaths(classPath, methodPath) + param;
                navigationItemList.add(new ApiNavigationItem(psiMethod, httpMethod, fullPath, methodPathInfo));
            }
        }
        return navigationItemList;
    }

    @NotNull
    private List<MethodPathInfo> getMethodPathList(@NotNull PsiMethod psiMethod, PsiClass psiClass) {
        List<MethodPathInfo> methodPathList = new ArrayList<>(8);
        String location = psiClass.getName() + "#" + psiMethod.getName();
        CommentInfo commentInfo = AnnotationHolder.getPsiMethodHolder(psiMethod).getCommentInfo();
        String description = commentInfo.getValue("");
        for (RequestMappingResolver.MappingInfo mapping : RequestMappingResolver.resolveMethodMappings(psiMethod)) {
            for (String methodPath : mapping.getPaths()) {
                for (HttpMethod httpMethod : mapping.getMethods()) {
                    methodPathList.add(new MethodPathInfo(psiMethod, httpMethod, methodPath, location, description, mapping.getParams()));
                }
            }
        }
        return methodPathList;
    }
}
