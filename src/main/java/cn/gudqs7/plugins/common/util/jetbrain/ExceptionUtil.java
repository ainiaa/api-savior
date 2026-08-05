package cn.gudqs7.plugins.common.util.jetbrain;

import cn.gudqs7.plugins.common.base.error.CanIgnoreException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.IndexNotReadyException;
import lombok.Lombok;


/**
 * @author wq
 */
public class ExceptionUtil {

    private static final Logger LOG = Logger.getInstance(ExceptionUtil.class);

    public static void logException(Throwable throwable) {
        logException(throwable, "");
    }

    public static void logException(Throwable throwable, String addition) {
        LOG.error(throwable);
        NotificationUtil.showError("插件运行失败, " + addition + "详细错误信息已写入 IDEA 日志。");
    }

    public static void handleException(Throwable throwable) {
        if (needIgnoredIdeaException(throwable)) {
            throw Lombok.sneakyThrow(throwable);
        }
        if (throwable instanceof CanIgnoreException) {
            logException(throwable, "");
        } else {
            String addition = "请根据错误信息到项目公开 Issue 页面手动反馈；请勿提交密钥、令牌或其他敏感信息。\n";
            logException(throwable, addition);
            throw Lombok.sneakyThrow(throwable);
        }
    }

    private static boolean needIgnoredIdeaException(Throwable throwable) {
        return throwable instanceof ProcessCanceledException
                || throwable instanceof IndexNotReadyException;
    }

    public static void handleSyntaxError(String code) throws RuntimeException {
        throw new CanIgnoreException("您的代码可能存在语法错误, 无法为您生成代码, 参考信息: " + code);
    }
}
