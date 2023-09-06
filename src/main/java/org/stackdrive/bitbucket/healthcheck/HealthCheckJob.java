package org.stackdrive.bitbucket.healthcheck;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;

import javax.annotation.Nullable;

public class HealthCheckJob implements JobRunner {

    private final StackDriveWebHealthCheckService stackDriveWebHealthCheckService;

    public HealthCheckJob(StackDriveWebHealthCheckService stackDriveWebHealthCheckService) {
        this.stackDriveWebHealthCheckService = stackDriveWebHealthCheckService;
    }

    @Nullable
    @Override
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        stackDriveWebHealthCheckService.updateStatus();
        return JobRunnerResponse.success();
    }
}
