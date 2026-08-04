package cn.gudqs7.plugins.savior.savior.base;

import cn.gudqs7.plugins.common.enums.MoreCommentTagEnum;
import cn.gudqs7.plugins.common.pojo.resolver.CommentInfo;
import cn.gudqs7.plugins.common.pojo.resolver.StructureAndCommentInfo;
import cn.gudqs7.plugins.common.resolver.comment.AnnotationHolder;
import cn.gudqs7.plugins.common.resolver.structure.StructureAndCommentResolver;
import cn.gudqs7.plugins.common.util.structure.PsiAnnotationUtil;
import cn.gudqs7.plugins.common.util.structure.PsiClassUtil;
import cn.gudqs7.plugins.savior.pojo.ApiMethodInfo;
import cn.gudqs7.plugins.savior.reader.Java2ApiReader;
import cn.gudqs7.plugins.savior.reader.Java2MapReader;
import cn.gudqs7.plugins.savior.theme.Theme;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wq
 * @date 2021/5/19
 */
public abstract class AbstractSavior<T> extends BaseSavior {

    protected final Java2MapReader java2JsonReader;
    protected final Java2ApiReader java2ApiReader;

    public AbstractSavior(Theme theme) {
        super(theme);
        java2JsonReader = new Java2MapReader(theme);
        java2ApiReader = new Java2ApiReader();
    }

    public List<PsiMethod> getMethodList(PsiClass psiClass) {
        // 获取该类相关的所有方法, 包括父类和接口
        List<PsiMethod> methodList = PsiClassUtil.getAllMethods(psiClass);
        // 过滤信息
        methodList.removeIf(this::filterMethod);

        // 根据 @Order 注解 以及字母顺序, 从小到大排序
        methodList.sort(this::orderByMethod);
        return methodList;
    }

    protected int orderByMethod(PsiMethod publicMethod, PsiMethod publicMethod2) {
        PsiAnnotation orderAnnotation = publicMethod.getAnnotation("org.springframework.core.annotation.Order");
        int order = Integer.MAX_VALUE;
        if (orderAnnotation != null) {
            order = PsiAnnotationUtil.getAnnotationValue(orderAnnotation, "value", order);
        }
        PsiAnnotation orderAnnotation2 = publicMethod2.getAnnotation("org.springframework.core.annotation.Order");
        int order2 = Integer.MAX_VALUE;
        if (orderAnnotation2 != null) {
            order2 = PsiAnnotationUtil.getAnnotationValue(orderAnnotation2, "value", order2);
        }
        return compareOrderValues(order, order2);
    }

    static int compareOrderValues(int order, int order2) {
        return Integer.compare(order, order2);
    }

    protected boolean filterMethod(PsiMethod method) {
        // 个人认为, 添加了 @Override 注解的方法, 一般相关注视/注解信息均位于父类(或接口)处, 因此此处的方法可以过滤调, 避免出现重复
        boolean overrideFromSuperClassOrInterface = method.hasAnnotation("java.lang.Override");
        if (overrideFromSuperClassOrInterface) {
            return true;
        }
        if (method.isConstructor()) {
            return true;
        }
        PsiModifierList modifierList = method.getModifierList();
        if (modifierList.hasModifierProperty(PsiModifier.STATIC)) {
            return true;
        }
        if (modifierList.hasModifierProperty(PsiModifier.PRIVATE)) {
            return true;
        }
        // 最后排除 Lombok 生成的方法
        return "LombokLightModifierList".equals(modifierList.toString());
    }

    /**
     * 获取方法上的ActionName注释
     *
     * @param method 方法
     * @return 方法上的ActionName注释
     */
    protected String getMethodActionName(PsiMethod method) {
        AnnotationHolder psiMethodHolder = AnnotationHolder.getPsiMethodHolder(method);
        CommentInfo propertyForMethod = psiMethodHolder.getCommentInfo();
        if (propertyForMethod != null) {
            return propertyForMethod.getSingleStr(MoreCommentTagEnum.AMP_ACTION_NAME.getTag(), null);
        }
        return null;
    }

    /**
     * 根据 PsiMethod 获取想要的数据
     *
     * @param project            项目
     * @param publicMethod       方法
     * @param interfaceClassName 接口类名
     * @param jumpHidden         是否跳过 hidden 检查
     * @return 想要的数据
     */
    protected T getDataByMethod(Project project, String interfaceClassName, PsiMethod publicMethod, boolean jumpHidden) {
        return getDataByMethod(project, interfaceClassName, publicMethod, new HashMap<>(2), jumpHidden);
    }


    /**
     * 根据 PsiMethod 获取想要的数据
     *
     * @param project            项目
     * @param publicMethod       方法
     * @param interfaceClassName 接口类名
     * @param param              配置参数
     * @param jumpHidden         是否跳过 hidden 检查
     * @return 想要的数据
     */
    protected T getDataByMethod(Project project, String interfaceClassName, PsiMethod publicMethod, Map<String, Object> param, boolean jumpHidden) {
        ApiMethodInfo apiMethodInfo = resolveApiMethodInfo(project, interfaceClassName, publicMethod, jumpHidden);
        if (apiMethodInfo == null) {
            return null;
        }
        return getDataByStructureAndCommentInfo(apiMethodInfo, param);
    }

    protected ApiMethodInfo resolveApiMethodInfo(Project project, String interfaceClassName, PsiMethod publicMethod, boolean jumpHidden) {
        AnnotationHolder annotationHolder = AnnotationHolder.getPsiMethodHolder(publicMethod);
        CommentInfo commentInfo = annotationHolder.getCommentInfo();

        if (!jumpHidden) {
            boolean hidden = commentInfo.isHidden(false);
            if (hidden || theme.handleMethodHidden(annotationHolder)) {
                return null;
            }
        }
        List<String> hiddenRequest = commentInfo.getHiddenRequest();
        List<String> onlyRequest = commentInfo.getOnlyRequest();
        PsiParameterList parameterTypes = publicMethod.getParameterList();
        StructureAndCommentInfo paramStructureAndCommentInfo = resolveStructure(project, hiddenRequest, onlyRequest)
                .resolveFromParameterList(parameterTypes);

        List<String> hiddenResponse = commentInfo.getHiddenResponse();
        List<String> onlyResponse = commentInfo.getOnlyResponse();
        PsiTypeElement returnTypeElement = publicMethod.getReturnTypeElement();
        StructureAndCommentInfo returnStructureAndCommentInfo = resolveStructure(project, hiddenResponse, onlyResponse)
                .resolveFromReturnVal(returnTypeElement);

        return new ApiMethodInfo(
                project, interfaceClassName, publicMethod, commentInfo,
                paramStructureAndCommentInfo, returnStructureAndCommentInfo
        );
    }

    private StructureAndCommentResolver resolveStructure(Project project, List<String> hiddenKeys, List<String> onlyKeys) {
        StructureAndCommentResolver resolver = new StructureAndCommentResolver();
        resolver.setProject(project);
        resolver.setFieldFilter(hiddenKeys, onlyKeys);
        return resolver;
    }

    protected abstract T getDataByStructureAndCommentInfo(ApiMethodInfo apiMethodInfo, Map<String, Object> param);

}
