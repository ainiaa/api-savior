package cn.gudqs7.plugins.common.resolver;

import cn.gudqs7.plugins.common.enums.HttpMethod;
import cn.gudqs7.plugins.common.resolver.comment.AnnotationHolder;
import cn.gudqs7.plugins.common.util.structure.PsiAnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        for (PsiAnnotation annotation : psiMethod.getAnnotations()) {
            PsiAnnotation mappingAnnotation = findMappingAnnotation(annotation, new HashSet<>());
            if (mappingAnnotation != null) {
                mappings.add(new MappingInfo(paths(annotation, mappingAnnotation),
                        methods(mappingAnnotation, mappingAnnotation.getQualifiedName()),
                        params(annotation, mappingAnnotation)));
            }
        }
        return mappings;
    }

    public static List<String> resolveClassPaths(PsiClass psiClass) {
        for (PsiAnnotation annotation : psiClass.getAnnotations()) {
            PsiAnnotation mappingAnnotation = findMappingAnnotation(annotation, new HashSet<>());
            if (mappingAnnotation != null) {
                return paths(annotation, mappingAnnotation);
            }
        }
        return Collections.singletonList("");
    }

    public static List<String> resolveClassParams(PsiClass psiClass) {
        for (PsiAnnotation annotation : psiClass.getAnnotations()) {
            PsiAnnotation mappingAnnotation = findMappingAnnotation(annotation, new HashSet<>());
            if (mappingAnnotation != null) {
                return params(annotation, mappingAnnotation);
            }
        }
        return Collections.emptyList();
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

    private static List<String> paths(PsiAnnotation annotation, PsiAnnotation mappingAnnotation) {
        List<String> paths = pathValues(annotation);
        if (CollectionUtils.isEmpty(paths) && annotation != mappingAnnotation) {
            paths = pathValues(mappingAnnotation);
        }
        return CollectionUtils.isEmpty(paths) ? Collections.singletonList("") : paths;
    }

    private static List<String> pathValues(PsiAnnotation annotation) {
        List<String> paths = PsiAnnotationUtil.getAnnotationListValue(annotation, "value", null);
        if (CollectionUtils.isEmpty(paths)) {
            paths = PsiAnnotationUtil.getAnnotationListValue(annotation, "path", null);
        }
        return paths;
    }

    private static List<String> params(PsiAnnotation annotation, PsiAnnotation mappingAnnotation) {
        List<String> params = PsiAnnotationUtil.getAnnotationListValue(annotation, "params", null);
        if (CollectionUtils.isEmpty(params) && annotation != mappingAnnotation) {
            params = PsiAnnotationUtil.getAnnotationListValue(mappingAnnotation, "params", null);
        }
        return params == null ? Collections.emptyList() : params;
    }

    private static PsiAnnotation findMappingAnnotation(PsiAnnotation annotation, Set<PsiClass> visitedTypes) {
        if (visitedTypes.size() >= 8) {
            return null;
        }
        String qualifiedName = annotation.getQualifiedName();
        if (isMappingAnnotation(qualifiedName)) {
            return annotation;
        }
        PsiClass annotationType = annotation.resolveAnnotationType();
        if (annotationType == null || !visitedTypes.add(annotationType)) {
            return null;
        }
        PsiModifierList modifierList = annotationType.getModifierList();
        if (modifierList == null) {
            return null;
        }
        for (PsiAnnotation metaAnnotation : modifierList.getAnnotations()) {
            PsiAnnotation mappingAnnotation = findMappingAnnotation(metaAnnotation, visitedTypes);
            if (mappingAnnotation != null) {
                return mappingAnnotation;
            }
        }
        return null;
    }

    private static boolean isMappingAnnotation(String qualifiedName) {
        if (qualifiedName == null) {
            return false;
        }
        for (String mappingQName : MAPPING_QNAMES) {
            if (mappingQName.equals(qualifiedName)) {
                return true;
            }
        }
        return false;
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
