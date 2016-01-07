
package com.dreamslab.session.local;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dreamslab.session.Configuration;
import com.dreamslab.session.SessionClient;
import com.dreamslab.session.SessionMetaData;

/**
 * 
 * session timeout检查任务
 * 
 * @author gaofeng
 * @date Sep 18, 2013 11:10:14 AM
 * @id $Id$
 */
public class TimeoutCheckTask implements Callable<Boolean> {
    
    private static final Logger log = LoggerFactory.getLogger(TimeoutCheckTask.class);
    
    private int sleepTime;
    private SessionClient client;
    
    public TimeoutCheckTask() {
    
        sleepTime = Configuration.getTimeoutCheckInteval();
    }
    
    @Override
    public Boolean call() throws InterruptedException {
    
        while (true) {
            try {
                List<String> sessionIds = client.getSessions();
                if (sessionIds == null) {
                    continue;
                }
                for (String sessionId : sessionIds) {
                    SessionMetaData metadata = client.getSession(sessionId);
                    if (metadata == null) {
                        continue;
                    }
                    if (!metadata.isValid()) {
                        Map<String, Object> map = client.removeSession(sessionId);
                        if (map == null) {
                            log.warn("Failed to remove session node: " + sessionId);
                        } else {
                            if (log.isInfoEnabled()) {
                                log.info("Removed session node: " + sessionId + " successfully.");
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("Session超时定时任务发生异常，", ex);
            } finally {
                TimeUnit.SECONDS.sleep(sleepTime);
            }
        }
    }
}
