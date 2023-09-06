package org.stackdrive.bitbucket;

import com.google.common.collect.ImmutableSet;

public interface EventCode {

    String SOLUTION_PROJECT_VIEW_REPORT = "SolutionProjectViewReport";

    String SOLUTION_REPOSITORY_VIEW_REPORT = "SolutionRepositoryViewReport";

    String SOLUTION_REPOSITORY_ENABLE_REQUEST = "SolutionRepositoryEnableRequest";

    String SOLUTION_REPOSITORY_DISABLE_REQUEST = "SolutionRepositoryDisableRequest";

    String SOLUTION_PROJECT_ENABLE_REQUEST = "SolutionProjectEnableRequest";

    String SOLUTION_PROJECT_DISABLE_REQUEST = "SolutionProjectDisableRequest";

    String AR_EVENT = "AREvent";

    /**
     * Множество событий которые требуют отправки AREvent-ов
     */
    ImmutableSet<String> AR_EVENTS = ImmutableSet.<String>of(
            SOLUTION_PROJECT_VIEW_REPORT,
            SOLUTION_REPOSITORY_VIEW_REPORT,
            SOLUTION_REPOSITORY_ENABLE_REQUEST,
            SOLUTION_PROJECT_ENABLE_REQUEST
    );
}