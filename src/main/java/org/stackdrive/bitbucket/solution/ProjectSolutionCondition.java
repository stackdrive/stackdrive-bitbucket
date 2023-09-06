package org.stackdrive.bitbucket.solution;

import com.atlassian.bitbucket.project.Project;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;

import javax.inject.Inject;
import java.util.Map;

public class ProjectSolutionCondition implements Condition {

    private final SolutionService solutionService;

    @Inject
    public ProjectSolutionCondition(SolutionService solutionService) {
        this.solutionService = solutionService;
    }

    @Override
    public void init(Map<String, String> map) throws PluginParseException {
        // Не требует предварительной инициализации
    }

    @Override
    public boolean shouldDisplay(Map<String, Object> context) {
        Project project = (Project) context.get("project");
        if (project == null) {
            return false;
        } else {
            return solutionService.hasSolutionPermission(project);
        }
    }
}