package com.pcitech.iLife.modules.mod.service;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;

//@RunWith(SpringJUnit4ClassRunner.class) 
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:spring-context.xml","classpath:spring-context-activiti.xml","classpath:spring-context-jedis.xml","classpath:spring-context-shiro.xml"}) 
public class KafkaTest {

//	@Test
    public void sendMsg(){
    	Properties props = new Properties();
        props.put("bootstrap.servers", "42.96.153.179:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //生产者发送消息 
        String topic = "test";
        Producer<String, String> procuder = new KafkaProducer<String,String>(props);
        for (int i = 1; i <= 10; i++) {
            String value = "value_" + i;
            ProducerRecord<String, String> msg = new ProducerRecord<String, String>(topic, value);
            procuder.send(msg);
        }
        //列出topic的相关信息
        List<PartitionInfo> partitions = new ArrayList<PartitionInfo>() ;
        partitions = procuder.partitionsFor(topic);
        for(PartitionInfo p:partitions)
        {
            System.out.println(p);
        }
        System.out.println("send message over.");
        procuder.close(100,TimeUnit.MILLISECONDS);
    }
}
