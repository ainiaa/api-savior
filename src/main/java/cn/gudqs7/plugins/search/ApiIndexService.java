package cn.gudqs7.plugins.search;

import cn.gudqs7.plugins.search.resolver.ApiNavigationItem;
import cn.gudqs7.plugins.search.resolver.ApiResolverService;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import com.intellij.util.concurrency.AppExecutorUtil;

import java.util.Collections;
import java.util.List;

/**
 * Project-scoped API search snapshot. Call while read access is held.
 */
public final class ApiIndexService implements Disposable {

    private final Project project;
    private volatile Snapshot snapshot;

    public ApiIndexService(Project project) {
        this.project = project;
        DumbService.getInstance(project).runWhenSmart(() -> ReadAction.nonBlocking(this::getItems)
                .expireWith(project)
                .submit(AppExecutorUtil.getAppExecutorService()));
    }

    public List<ApiNavigationItem> getItems() {
        long modificationCount = PsiManager.getInstance(project).getModificationTracker().getJavaStructureModificationCount();
        Snapshot current = snapshot;
        if (current != null && current.modificationCount == modificationCount) {
            return current.items;
        }
        List<ApiNavigationItem> items = Collections.unmodifiableList(ApiResolverService.getInstance(project).getApiNavigationItemList());
        snapshot = new Snapshot(modificationCount, items);
        return items;
    }

    @Override
    public void dispose() {
        snapshot = null;
    }

    private static final class Snapshot {

        private final long modificationCount;
        private final List<ApiNavigationItem> items;

        private Snapshot(long modificationCount, List<ApiNavigationItem> items) {
            this.modificationCount = modificationCount;
            this.items = items;
        }
    }
}
