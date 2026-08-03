package cn.gudqs7.plugins.common.base.action;

import com.intellij.psi.PsiClass;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractBatchDocerSaviorTest {

    @Test
    void retainsDistinctClassesWithoutQualifiedNames() {
        Set<PsiClass> classes = new TreeSet<>(AbstractBatchDocerSavior.psiClassComparator());

        classes.add(psiClass(null));
        classes.add(psiClass(null));

        assertEquals(2, classes.size());
    }

    @Test
    void deduplicatesClassesWithSameQualifiedName() {
        Set<PsiClass> classes = new TreeSet<>(AbstractBatchDocerSavior.psiClassComparator());

        classes.add(psiClass("example.UserController"));
        classes.add(psiClass("example.UserController"));

        assertEquals(1, classes.size());
    }

    private PsiClass psiClass(String qualifiedName) {
        return (PsiClass) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{PsiClass.class},
                (proxy, method, args) -> "getQualifiedName".equals(method.getName()) ? qualifiedName : null);
    }
}
