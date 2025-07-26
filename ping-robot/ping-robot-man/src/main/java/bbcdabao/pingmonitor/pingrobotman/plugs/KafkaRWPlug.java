/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package bbcdabao.pingmonitor.pingrobotman.plugs;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bbcdabao.pingmonitor.common.domain.extraction.annotation.ExtractionFieldMark;
import bbcdabao.pingmonitor.pingrobotapi.IPingMoniterPlug;

public class KafkaRWPlug implements IPingMoniterPlug {

    private final Logger logger = LoggerFactory.getLogger(KafkaRWPlug.class);

    @ExtractionFieldMark(descriptionCn = "Kafka地址", descriptionEn = "Kafka Bootstrap Servers")
    private String bootstrapServers;

    @ExtractionFieldMark(descriptionCn = "Topic名称", descriptionEn = "Kafka Topic")
    private String topic;

    @Override
    public String doPingExecute(int timeOutMs) throws Exception {
        String uniqueKey = UUID.randomUUID().toString();
        String testValue = "ping_test_" + System.currentTimeMillis();

        // 1. 发送消息
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", bootstrapServers);
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {
            producer.send(new ProducerRecord<>(topic, uniqueKey, testValue));
            producer.flush();
            logger.info("KafkaRWPlug sent message to topic [{}]", topic);
        }

        // 2. 消费消息进行验证
        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", bootstrapServers);
        consumerProps.put("group.id", "ping-monitor-" + UUID.randomUUID());
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
            consumer.subscribe(Collections.singletonList(topic));

            long startTime = System.currentTimeMillis();
            boolean found = false;

            while (System.currentTimeMillis() - startTime < timeOutMs) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, String> record : records) {
                    if (uniqueKey.equals(record.key()) && testValue.equals(record.value())) {
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }

            if (!found) {
                throw new Exception("Kafka read/write validation failed: message not found in topic.");
            }
        }

        String result = "kafka:" + topic + ":ok";
        logger.info("KafkaRWPlug success: {}", result);
        return result;
    }
}
