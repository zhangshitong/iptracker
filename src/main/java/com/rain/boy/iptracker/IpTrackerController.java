package com.rain.boy.iptracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by STZHANG on 2017/9/9.
 */
@Controller
public class IpTrackerController{
    private static Logger LOG = LoggerFactory.getLogger(Executor.class);

    private AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();


    private Map<String, IpInfo> nameIps = new HashMap<>();

    private static ExecutorService executor = Executors.newFixedThreadPool(2);


    @RequestMapping("/track/{name}")
    @ResponseBody
    public List<IpInfo> track(@PathVariable("name") String name){
        List<IpInfo> res = new ArrayList<>();
        if(!StringUtils.isEmpty(name) && nameIps.containsKey(name)){
            IpInfo ipInfo =  nameIps.get(name);
            res.add(ipInfo);
        }
        return res;
    }


    @RequestMapping("/try/{name}")
    @ResponseBody
    public String tryToRecord(@PathVariable("name") String name, HttpServletRequest  request){
        if(!StringUtils.isEmpty(name)){
            LOG.info("start to run record."+ name);
            String realIpAddress = CusAccessObjectUtil.getIpAddress(request);
            if(!StringUtils.isEmpty(realIpAddress)){
                LOG.info("start to run record."+ name + ": " + realIpAddress);
                ListenableFuture future = asyncRestTemplate.getForEntity("http://ip-api.com/json/{ip}", Map.class, realIpAddress);
                 future.addCallback(new ListenableFutureCallback<ResponseEntity<Map<String, String>>>(){
                     @Override
                     public void onFailure(Throwable ex) {
                        LOG.error("", ex);
                     }

                     @Override
                     public void onSuccess(ResponseEntity<Map<String, String>> responseEntity) {
                         Map<String, String> result = responseEntity.getBody();
                         LOG.info("ip-api.com , success"+ result);
                         IpInfo ipInfo = new IpInfo();
                         ipInfo.setName(name);
                         ipInfo.setIpAddress(realIpAddress);
                         String status = result.getOrDefault("status", "");
                         if("success".equalsIgnoreCase(status)){
                             LOG.info("ip-api.com , success");
                             String country = result.getOrDefault("country", "");
                             String countryCode = result.getOrDefault("countryCode", "");
                             String regionName = result.getOrDefault("regionName", "");
                             String region = result.getOrDefault("region", "");
                             String timezone = result.getOrDefault("timezone", "");
                             String city = result.getOrDefault("city", "");
                             String isp = result.getOrDefault("isp", "");
                             Object lat0 = result.getOrDefault("lat", "");
                             String lat = lat0.toString();
                             Object lon0 = result.getOrDefault("lon", "");
                             String lon = lon0.toString();
                             String as = result.getOrDefault("as", "");
                             ipInfo.setTimezone(timezone);
                             ipInfo.setCountry(country);
                             ipInfo.setCountryCode(countryCode);
                             ipInfo.setCity(city);
                             ipInfo.setIsp(isp);
                             ipInfo.setRegion(region);
                             ipInfo.setRegionName(regionName);
                             ipInfo.setAs(as);
                             ipInfo.setLon(lon);
                             ipInfo.setLat(lat);
                         }
                         ipInfo.setTimestamp(getCurrentTime(ipInfo.getTimezone()));
                         nameIps.put(name, ipInfo);
                     }
                 });
            }
        }
        return "true";
    }

    private String getCurrentTime(String timezone){
        ZoneId shanghai = ZoneId.of("Asia/Shanghai");
        if(!StringUtils.isEmpty(timezone)){
            shanghai = ZoneId.of(timezone);
        }
        LocalDateTime localtDateAndTime = LocalDateTime.now(ZoneOffset.UTC);
        ZonedDateTime dateAndTimeInUTC = localtDateAndTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime dateAndTimeInNewYork  = dateAndTimeInUTC.withZoneSameInstant(shanghai);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");
        String formatedDataTime = dateAndTimeInNewYork.format(formatter);
        LOG.info("formatedDataTime "+formatedDataTime);
        return formatedDataTime;
    }


}
