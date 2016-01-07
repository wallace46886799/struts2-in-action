
package com.dreamslab.session.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 通过Watcher，保证返回的zk一定是顺利建立连接的
 *
 * @author gaofeng
 * @date Sep 13, 2013 9:41:30 AM
 * @id $Id$
 */
public class ZookeeperConnector implements Watcher {
    
    private CountDownLatch signal = new CountDownLatch(1);
    
    private static final Logger log = LoggerFactory.getLogger(ZookeeperConnector.class);

    /**
     * 连接ZK客户端，通过CDL作同步。
     * 
     * @param servers
     * @param sessionTimeout
     * @return
     */
    public ZooKeeper connect(String servers, int sessionTimeout) {
    
        ZooKeeper zk;
        try {
            zk = new ZooKeeper(servers, sessionTimeout, this);
            signal.await(15, TimeUnit.SECONDS);
            if(zk.getState() == ZooKeeper.States.CONNECTED){
                return zk;
            }else{
                return null;
            }
        } catch (IOException e) {
            log.error("", e);
        } catch (InterruptedException e) {
            log.error("", e);
        }
        return null;
    }
    
    public void process(WatchedEvent event) {
        try{
            KeeperState state = event.getState();
            if (state == KeeperState.SyncConnected) {
                log.info("Connect to Zookeeper Server successfully! ");
            }else if(state == KeeperState.Disconnected || state == KeeperState.Expired){
                log.info("Disconnected from Zookeeper Server! ");
            }
        }finally{
            signal.countDown();
        }

    }
}
