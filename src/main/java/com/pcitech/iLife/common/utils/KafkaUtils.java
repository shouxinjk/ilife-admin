package com.pcitech.iLife.common.utils;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaUtils {
	Logger logger = LoggerFactory.getLogger(KafkaUtils.class);
	private static Producer<String,String> getProducer(){
	    	Properties props = new Properties();
        props.put("bootstrap.servers", "42.96.153.179:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //构建producer
        return new KafkaProducer<String,String>(props);
	}
	
	public static void send(boolean async,String message) {
		Producer<String, String> producer = getProducer();
		String topic = "test";
		ProducerRecord<String, String> msg = new ProducerRecord<String, String>(topic, message);
		if(async) {
			producer.send(msg, new Callback() {
                public void onCompletion(RecordMetadata metadata, Exception e) {
                    if(e != null) {
                        e.printStackTrace();
                    } else {
                        System.out.println("The offset of the record we just sent is: " + metadata.offset());
                    }
                }
            });
		}else {
			producer.send(msg);
		}
		producer.close(100,TimeUnit.MILLISECONDS);
	}
}
