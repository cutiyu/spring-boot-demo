/*
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
 */

package com.xkcoding.mq.rocketmq.consumer;

import com.xkcoding.mq.rocketmq.ConsumerClientConf;
import com.xkcoding.mq.rocketmq.MqCnst;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;
import org.apache.rocketmq.client.apis.message.MessageId;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AsyncSimpleConsumerExample {
    private static final Logger log = LoggerFactory.getLogger(AsyncSimpleConsumerExample.class);

    private AsyncSimpleConsumerExample() {
    }

    @SuppressWarnings({"resource", "InfiniteLoopStatement"})
    public static void main(String[] args) throws ClientException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        ClientConfiguration clientConfiguration = ConsumerClientConf.clientConfiguration();

        String consumerGroup = "yourConsumerGroup";
        String tag = "yourMessageTagA";
        String topic = MqCnst.topic_aigc;

        Duration awaitDuration = Duration.ofSeconds(30);
        FilterExpression filterExpression = new FilterExpression(tag, FilterExpressionType.TAG);
        // In most case, you don't need to create too many consumers, singleton pattern is recommended.
        SimpleConsumer consumer = provider.newSimpleConsumerBuilder()
            .setClientConfiguration(clientConfiguration)
            .setConsumerGroup(consumerGroup)
            // set await duration for long-polling.
            .setAwaitDuration(awaitDuration)
            // Set the subscription for the consumer.
            .setSubscriptionExpressions(Collections.singletonMap(topic, filterExpression))
            .build();

        // Max message num for each long polling.
        int maxMessageNum = 16;
        // Set message invisible duration after it is received.
        Duration invisibleDuration = Duration.ofSeconds(15);
        // Set individual thread pool for receive callback.
        ExecutorService receiveCallbackExecutor = Executors.newCachedThreadPool();
        // Set individual thread pool for ack callback.
        ExecutorService ackCallbackExecutor = Executors.newCachedThreadPool();
        // Receive message.
        do {
            final CompletableFuture<List<MessageView>> future0 = consumer.receiveAsync(maxMessageNum,
                invisibleDuration);
            future0.whenCompleteAsync(((messages, throwable) -> {
                if (null != throwable) {
                    log.error("Failed to receive message from remote", throwable);
                    // Return early.
                    return;
                }
                log.info("Received {} message(s)", messages.size());
                // Using messageView as key rather than message id because message id may be duplicated.
                final Map<MessageView, CompletableFuture<Void>> map =
                    messages.stream().collect(Collectors.toMap(message -> message, consumer::ackAsync));
                for (Map.Entry<MessageView, CompletableFuture<Void>> entry : map.entrySet()) {
                    final MessageId messageId = entry.getKey().getMessageId();
                    final CompletableFuture<Void> future = entry.getValue();
                    future.whenCompleteAsync((v, t) -> {
                        if (null != t) {
                            log.error("Message is failed to be acknowledged, messageId={}", messageId, t);
                            // Return early.
                            return;
                        }
                        log.info("Message is acknowledged successfully, messageId={}", messageId);
                    }, ackCallbackExecutor);
                }

            }), receiveCallbackExecutor);
        } while (true);
        // Close the simple consumer when you don't need it anymore.
        // You could close it manually or add this into the JVM shutdown hook.
        // consumer.close();
    }
}
