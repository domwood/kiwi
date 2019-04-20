package com.github.domwood.kiwi.api;

import com.github.domwood.kiwi.data.input.CreateTopicRequest;
import com.github.domwood.kiwi.data.output.BrokerInfoList;
import com.github.domwood.kiwi.data.output.BrokerLogInfoList;
import com.github.domwood.kiwi.data.output.TopicInfo;
import com.github.domwood.kiwi.data.output.TopicList;
import com.github.domwood.kiwi.kafka.provision.KafkaResourceProvider;
import com.github.domwood.kiwi.kafka.provision.KafkaTaskProvider;
import com.github.domwood.kiwi.kafka.resources.KafkaAdminResource;
import com.github.domwood.kiwi.kafka.task.admin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static com.github.domwood.kiwi.utilities.Constants.API_ENDPOINT;

@RestController
@RequestMapping(API_ENDPOINT)
public class AdminController {

    private final KafkaResourceProvider resourceProvider;
    private final KafkaTaskProvider taskProvider;

    @Autowired
    public AdminController(KafkaTaskProvider kafkaTaskProvider,
                           KafkaResourceProvider resourceProvider){
        this.resourceProvider = resourceProvider;
        this.taskProvider = kafkaTaskProvider;
    }

    @Async
    @GetMapping("/listTopics")
    @ResponseBody
    public CompletableFuture<TopicList> listTopics(@RequestParam(required = false) String bootStrapServers){
        KafkaAdminResource adminResource = resourceProvider.kafkaAdminResource(bootStrapServers);
        ListTopics listTopics = this.taskProvider.listTopics();
        return listTopics.execute(adminResource, null);
    }

    @Async
    @GetMapping("/topicInfo")
    @ResponseBody
    public CompletableFuture<TopicInfo> listTopics(@RequestParam(required = false) String bootStrapServers,
                                                   @RequestParam String topic){
        KafkaAdminResource adminResource = resourceProvider.kafkaAdminResource(bootStrapServers);
        TopicInformation topicInformation = this.taskProvider.topicInfo();
        return topicInformation.execute(adminResource, topic);
    }

    @Async
    @GetMapping("/brokers")
    @ResponseBody
    public CompletableFuture<BrokerInfoList> brokers(@RequestParam(required = false) String bootStrapServers){
        KafkaAdminResource adminResource = resourceProvider.kafkaAdminResource(bootStrapServers);
        BrokerInformation brokerInformation = this.taskProvider.brokerInformation();
        return brokerInformation.execute(adminResource, null);
    }

    @Async
    @GetMapping("/logs")
    @ResponseBody
    public CompletableFuture<BrokerLogInfoList> brokers(@RequestParam Integer brokerId,
                                                        @RequestParam(required = false) String bootStrapServers){
        KafkaAdminResource adminResource = resourceProvider.kafkaAdminResource(bootStrapServers);
        BrokerLogInformation brokerInformation = this.taskProvider.brokerLogInformation();
        return brokerInformation.execute(adminResource, brokerId);
    }

    @Async
    @PostMapping("/createTopic")
    @ResponseBody
    public CompletableFuture<Void> createTopic(@RequestParam(required = false) String bootStrapServers,
                                                              @RequestBody CreateTopicRequest createTopicRequest){
        KafkaAdminResource adminResource = resourceProvider.kafkaAdminResource(bootStrapServers);
        CreateTopic createTopic = this.taskProvider.createTopic();
        return createTopic.execute(adminResource, createTopicRequest);
    }

}
