package org.stackdrive.bitbucket.solution;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;

import javax.inject.Inject;
import java.util.Map;

public class RepositorySolutionCondition implements Condition {

    private final SolutionService solutionService;

    @Inject
    public RepositorySolutionCondition(SolutionService solutionService) {
        this.solutionService = solutionService;
    }

    @Override
    public void init(Map<String, String> map) throws PluginParseException {
        // Не требует предварительной инициализации
    }

    @Override
    public boolean shouldDisplay(Map<String, Object> context) {
        Repository repository = (Repository) context.get("repository");
        if (repository == null) {
            return false;
        } else {
            return solutionService.hasSolutionPermission(repository);
        }
    }
}