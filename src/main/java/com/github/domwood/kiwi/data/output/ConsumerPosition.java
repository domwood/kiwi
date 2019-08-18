package com.github.domwood.kiwi.data.output;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@JsonDeserialize(as = ImmutableConsumerPosition.class)
@JsonSerialize(as = ImmutableConsumerPosition.class)
@Value.Immutable
@Value.Style(depluralize = true)
public interface ConsumerPosition {

    Long startValue();
    Long endValue();
    Long consumerPosition();
    Integer percentage();
    Integer totalRecords();

    @Value.Default
    default Integer skippedPercentage(){
        return 0;
    }
}
