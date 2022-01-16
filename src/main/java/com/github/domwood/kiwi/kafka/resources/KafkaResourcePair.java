package com.github.domwood.kiwi.kafka.resources;

import com.github.domwood.kiwi.exceptions.KafkaResourceClientCloseException;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Properties;

//A little awkward, TODO look at how to improve
public class KafkaResourcePair<R1 extends AbstractKafkaResource, R2 extends AbstractKafkaResource> extends AbstractKafkaResource<Pair<R1, R2>> {

    private final R1 client1;
    private final R2 client2;

    public KafkaResourcePair(R1 client1, R2 client2) {
        super(new Properties());
        this.client1 = client1;
        this.client2 = client2;
    }

    @Override
    protected Pair<R1, R2> createClient(ImmutableMap<Object, Object> props) {
        return Pair.of(client1, client2);
    }

    @Override
    protected void closeClient() throws KafkaResourceClientCloseException {
        Exception error = null;
        try {
            this.client1.closeClient();
        } catch (Exception e) {
            error = e;
        }
        try {
            this.client2.closeClient();
        } catch (Exception e) {
            error = e;
        }
        if (error != null) {
            if (error instanceof KafkaResourceClientCloseException) {
                throw (KafkaResourceClientCloseException) error;
            } else {
                throw new KafkaResourceClientCloseException(error.getMessage(), error);
            }
        }
    }

    public R2 getRight() {
        return this.client2;
    }

    public R1 getLeft() {
        return this.client1;
    }
}
