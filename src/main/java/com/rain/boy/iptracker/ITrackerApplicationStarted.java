package com.rain.boy.iptracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by STZHANG on 2017/9/9.
 */
@Component
public class ITrackerApplicationStarted implements ApplicationListener<ApplicationReadyEvent> {

    private static Logger LOG = LoggerFactory.getLogger(ITrackerApplicationStarted.class);

    @Value("${tracker.mode:server}")
    private String mode;

    @Value("${tracker.server.url:}")
    private String serverUrl = null;

    @Value("${tracker.client.name:default}")
    private String trackerClientName = null;


    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        if("client".equalsIgnoreCase(mode) && !StringUtils.isEmpty(serverUrl)){
                Executor.fixedRate(new Runnable() {
                    @Override
                    public void run() {
                        LOG.info("start to run retry.");
                        String url = serverUrl + "/try/{clientname}";
                        restTemplate.getForObject(url, String.class, trackerClientName);
                        LOG.info("run retry complete.");
                    }
                }, 1, 5 * 60 );
        }
    }
}
