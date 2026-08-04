package cn.gudqs7.plugins.common.resolver;

import cn.gudqs7.plugins.common.enums.HttpMethod;
import cn.gudqs7.plugins.common.resolver.comment.AnnotationHolder;
import cn.gudqs7.plugins.common.util.structure.PsiAnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Spring and gateway endpoint mapping reader. Call only while read access is held.
 */
public final class RequestMappingResolver {

    private static final String[] MAPPING_QNAMES = {
            AnnotationHolder.QNAME_OF_MAPPING,
            AnnotationHolder.QNAME_OF_GET_MAPPING,
            AnnotationHolder.QNAME_OF_POST_MAPPING,
            AnnotationHolder.QNAME_OF_PATCH_MAPPING,
            AnnotationHolder.QNAME_OF_PUT_MAPPING,
            AnnotationHolder.QNAME_OF_DELETE_MAPPING,
            AnnotationHolder.QNAME_OF_GAGEWAY_GET_MAPPING,
            AnnotationHolder.QNAME_OF_GAGEWAY_POST_MAPPING,
            AnnotationHolder.QNAME_OF_GAGEWAY_PUT_MAPPING,
            AnnotationHolder.QNAME_OF_GAGEWAY_DELETE_MAPPING
    };

    private RequestMappingResolver() {
    }

    public static boolean hasMethodMapping(PsiMethod psiMethod) {
        return !resolveMethodMappings(psiMethod).isEmpty();
    }

    public static boolean hasMappingAnnotation(AnnotationHolder annotationHolder) {
        for (String qName : MAPPING_QNAMES) {
            if (annotationHolder.hasAnnotation(qName)) {
                return true;
            }
        }
        return false;
    }

    public static List<MappingInfo> resolveMethodMappings(PsiMethod psiMethod) {
        List<MappingInfo> mappings = new ArrayList<>();
        for (String qName : MAPPING_QNAMES) {
            PsiAnnotation annotation = psiMethod.getAnnotation(qName);
            if (annotation != null) {
                mappings.add(new MappingInfo(paths(annotation), methods(annotation, qName), params(annotation)));
            }
        }
        return mappings;
    }

    public static List<String> resolveClassPaths(PsiClass psiClass) {
        PsiAnnotation annotation = psiClass.getAnnotation(AnnotationHolder.QNAME_OF_MAPPING);
        return annotation == null ? Collections.singletonList("") : paths(annotation);
    }

    public static List<String> resolveClassParams(PsiClass psiClass) {
        PsiAnnotation annotation = psiClass.getAnnotation(AnnotationHolder.QNAME_OF_MAPPING);
        return annotation == null ? Collections.emptyList() : params(annotation);
    }

    public static String joinPaths(String classPath, String methodPath) {
        String normalizedClassPath = normalizePath(classPath);
        String normalizedMethodPath = normalizePath(methodPath);
        if ("/".equals(normalizedClassPath)) {
            return normalizedMethodPath;
        }
        if ("/".equals(normalizedMethodPath)) {
            return normalizedClassPath;
        }
        return normalizedClassPath + normalizedMethodPath;
    }

    private static List<String> paths(PsiAnnotation annotation) {
        List<String> paths = PsiAnnotationUtil.getAnnotationListValue(annotation, "value", null);
        if (CollectionUtils.isEmpty(paths)) {
            paths = PsiAnnotationUtil.getAnnotationListValue(annotation, "path", null);
        }
        return CollectionUtils.isEmpty(paths) ? Collections.singletonList("") : paths;
    }

    private static List<String> params(PsiAnnotation annotation) {
        List<String> params = PsiAnnotationUtil.getAnnotationListValue(annotation, "params", null);
        return params == null ? Collections.emptyList() : params;
    }

    private static List<HttpMethod> methods(PsiAnnotation annotation, String qName) {
        HttpMethod fixedMethod = fixedMethod(qName);
        if (fixedMethod != null) {
            return Collections.singletonList(fixedMethod);
        }
        List<String> methodNames = PsiAnnotationUtil.getAnnotationListValue(annotation, "method", null);
        if (CollectionUtils.isEmpty(methodNames)) {
            return Collections.singletonList(HttpMethod.ALL);
        }
        List<HttpMethod> methods = new ArrayList<>(methodNames.size());
        for (String methodName : methodNames) {
            methods.add(HttpMethod.of(methodName));
        }
        return methods;
    }

    static HttpMethod fixedMethod(String qName) {
        if (AnnotationHolder.QNAME_OF_GET_MAPPING.equals(qName) || AnnotationHolder.QNAME_OF_GAGEWAY_GET_MAPPING.equals(qName)) {
            return HttpMethod.GET;
        }
        if (AnnotationHolder.QNAME_OF_POST_MAPPING.equals(qName) || AnnotationHolder.QNAME_OF_GAGEWAY_POST_MAPPING.equals(qName)) {
            return HttpMethod.POST;
        }
        if (AnnotationHolder.QNAME_OF_PATCH_MAPPING.equals(qName)) {
            return HttpMethod.PATCH;
        }
        if (AnnotationHolder.QNAME_OF_PUT_MAPPING.equals(qName) || AnnotationHolder.QNAME_OF_GAGEWAY_PUT_MAPPING.equals(qName)) {
            return HttpMethod.PUT;
        }
        if (AnnotationHolder.QNAME_OF_DELETE_MAPPING.equals(qName) || AnnotationHolder.QNAME_OF_GAGEWAY_DELETE_MAPPING.equals(qName)) {
            return HttpMethod.DELETE;
        }
        return null;
    }

    private static String normalizePath(String path) {
        if (path == null || path.isEmpty() || "/".equals(path)) {
            return "/";
        }
        String normalized = path.startsWith("/") ? path : "/" + path;
        return normalized.endsWith("/") ? normalized.substring(0, normalized.length() - 1) : normalized;
    }

    public static final class MappingInfo {

        private final List<String> paths;
        private final List<HttpMethod> methods;
        private final List<String> params;

        private MappingInfo(List<String> paths, List<HttpMethod> methods, List<String> params) {
            this.paths = paths;
            this.methods = methods;
            this.params = params;
        }

        public List<String> getPaths() {
            return paths;
        }

        public List<HttpMethod> getMethods() {
            return methods;
        }

        public List<String> getParams() {
            return params;
        }
    }
}
