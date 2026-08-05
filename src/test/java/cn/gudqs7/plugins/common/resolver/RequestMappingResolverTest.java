package cn.gudqs7.plugins.common.resolver;

import cn.gudqs7.plugins.common.enums.HttpMethod;
import cn.gudqs7.plugins.common.resolver.comment.AnnotationHolder;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestMappingResolverTest {

    @Test
    void joinsControllerAndMethodPathsWithoutDuplicateSeparators() {
        assertEquals("/api/users", RequestMappingResolver.joinPaths("/api/", "/users"));
        assertEquals("/users", RequestMappingResolver.joinPaths("", "users"));
        assertEquals("/", RequestMappingResolver.joinPaths("", ""));
    }

    @Test
    void resolvesPatchMappingAsPatchMethod() {
        assertEquals(HttpMethod.PATCH,
                RequestMappingResolver.fixedMethod(AnnotationHolder.QNAME_OF_PATCH_MAPPING));
    }

    @Test
    void resolvesGetMethodFromComposedMappingAnnotation() {
        PsiAnnotation getMapping = annotation(AnnotationHolder.QNAME_OF_GET_MAPPING, null);
        PsiAnnotation apiGet = annotation("example.ApiGet", psiClass(getMapping));
        PsiMethod method = (PsiMethod) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{PsiMethod.class},
                (proxy, invoked, args) -> "getAnnotations".equals(invoked.getName())
                        ? new PsiAnnotation[]{apiGet} : null);

        RequestMappingResolver.MappingInfo mapping = RequestMappingResolver.resolveMethodMappings(method).get(0);

        assertEquals(Collections.singletonList(HttpMethod.GET), mapping.getMethods());
        assertEquals(Collections.singletonList(""), mapping.getPaths());
    }

    private PsiAnnotation annotation(String qualifiedName, PsiClass annotationType) {
        return (PsiAnnotation) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{PsiAnnotation.class},
                (proxy, invoked, args) -> {
                    if ("getQualifiedName".equals(invoked.getName())) {
                        return qualifiedName;
                    }
                    if ("resolveAnnotationType".equals(invoked.getName())) {
                        return annotationType;
                    }
                    return null;
                });
    }

    private PsiClass psiClass(PsiAnnotation... annotations) {
        PsiModifierList modifierList = (PsiModifierList) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{PsiModifierList.class},
                (proxy, invoked, args) -> "getAnnotations".equals(invoked.getName()) ? annotations : null);
        return (PsiClass) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{PsiClass.class},
                (proxy, invoked, args) -> {
                    if ("getModifierList".equals(invoked.getName())) {
                        return modifierList;
                    }
                    if ("hashCode".equals(invoked.getName())) {
                        return System.identityHashCode(proxy);
                    }
                    return null;
                });
    }
}
