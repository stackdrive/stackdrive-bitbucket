package org.stackdrive.bitbucket.healthcheck;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;

@Named("healthCheckMonitor")
public class HealthCheckMonitor implements LifecycleAware {

    private static final Logger log = LoggerFactory.getLogger(HealthCheckMonitor.class);

    private static final Duration INTERVAL = Duration.ofSeconds(30);
    private static final JobId JOB_ID = JobId.of(HealthCheckJob.class.getSimpleName());
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of(HealthCheckJob.class.getName());

    @ComponentImport
    private final SchedulerService schedulerService;

    private final StackDriveWebHealthCheckService stackDriveWebHealthCheckService;

    @Inject
    public HealthCheckMonitor(SchedulerService schedulerService,
                              StackDriveWebHealthCheckService stackDriveWebHealthCheckService) {
        this.schedulerService = schedulerService;
        this.stackDriveWebHealthCheckService = stackDriveWebHealthCheckService;
    }

    @Override
    public void onStart() {
        HealthCheckJob jobRunner = new HealthCheckJob(stackDriveWebHealthCheckService);
        schedulerService.registerJobRunner(JOB_RUNNER_KEY, jobRunner);
        try {
            schedulerService.scheduleJob(
                    JOB_ID,
                    JobConfig.forJobRunnerKey(JOB_RUNNER_KEY)
                            .withRunMode(RunMode.RUN_ONCE_PER_CLUSTER)
                            .withSchedule(Schedule.forInterval(INTERVAL.toMillis(),
                                    Date.from(ZonedDateTime.now().plus(INTERVAL).toInstant()))));
        } catch (SchedulerServiceException e) {
            log.warn("Failed to schedule " + JOB_RUNNER_KEY, e);
        }
    }

    @Override
    public void onStop() {
        schedulerService.unscheduleJob(JOB_ID);
        schedulerService.unregisterJobRunner(JOB_RUNNER_KEY);
    }
}