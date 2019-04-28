package com.github.domwood.kiwi.kafka.resources;

import com.github.domwood.kiwi.kafka.utils.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Properties;

public abstract class KafkaResource<CLIENT> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected CLIENT client;
    private long lastKeepAlive;
    protected final Properties config;

    public KafkaResource(Properties props){
        this.lastKeepAlive = TimeProvider.getTime();
        this.config = props;
        this.client = createClient(props);
    }

    public void discard(){
        try{
            if(this.client != null){
                closeClient();
            }
        }
        catch (Exception e){
            logger.error("Attempted to close and admin client resource but failed ", e);
        }
        this.client = null;
    }

    protected abstract CLIENT createClient(Properties props);

    protected abstract void closeClient() throws Exception;

    public void keepAlive(){
        this.lastKeepAlive = TimeProvider.getTime();
    }

    public long getLastKeepAlive(){
        return this.lastKeepAlive;
    }

    public boolean isDiscarded(){
        return this.client == null;
    }
}
