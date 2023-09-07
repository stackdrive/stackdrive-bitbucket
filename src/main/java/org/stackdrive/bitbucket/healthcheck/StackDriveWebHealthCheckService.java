package org.stackdrive.bitbucket.healthcheck;

import org.stackdrive.audit.http.StackDriveResponseHandler;
import org.stackdrive.bitbucket.globalconfig.GlobalConfigService;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class StackDriveWebHealthCheckService {

    private static final Logger log = LoggerFactory.getLogger(StackDriveWebHealthCheckService.class);

    private final GlobalConfigService globalConfigService;

    @Inject
    public StackDriveWebHealthCheckService(GlobalConfigService globalConfigService) {
        this.globalConfigService = globalConfigService;
    }

    private boolean isAlive() {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(12000)
                    .setConnectTimeout(12000)
                    .setSocketTimeout(12000)
                    .build();

            HttpGet httpGet = new HttpGet(globalConfigService.getBack() + "/repos");
            httpGet.setConfig(requestConfig);

            ResponseHandler<String> responseHandler = new StackDriveResponseHandler();
            String responseBody = httpclient.execute(httpGet, responseHandler);
            return !responseBody.isEmpty();
        } catch (Exception e) {
            log.warn("Beta test service unavailable");
        }
        return false;
    }

    void updateStatus() {
        if (isAlive()) {
            globalConfigService.setHealthCheckState("ACTIVE");
        } else {
            globalConfigService.setHealthCheckState("INACTIVE");
        }
    }
}