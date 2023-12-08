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

package com.xkcoding.mq.rocketmq;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientConfigurationBuilder;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.SessionCredentialsProvider;
import org.apache.rocketmq.client.apis.StaticSessionCredentialsProvider;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.apache.rocketmq.client.apis.producer.TransactionChecker;
import org.apache.rocketmq.shaded.commons.lang3.StringUtils;

import java.time.Duration;

/**
 * Each client will establish an independent connection to the server node within a process.
 *
 * <p>In most cases, the singleton mode can meet the requirements of higher concurrency.
 * If multiple connections are desired, consider increasing the number of clients appropriately.
 */
public class ProducerSingleton {
    private static volatile Producer PRODUCER;
    private static volatile Producer TRANSACTIONAL_PRODUCER;

    private ProducerSingleton() {
    }

    private static Producer buildProducer(TransactionChecker checker, String... topics) throws ClientException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();



        ClientConfigurationBuilder configurationBuilder = ClientConfiguration.newBuilder();
        configurationBuilder.setEndpoints(MqCnst.ENDPOINTS);
        configurationBuilder.enableSsl(false);
        configurationBuilder.setRequestTimeout(Duration.ofSeconds(5l));
        if (StringUtils.isNotEmpty(MqCnst.ACCESS_KEY) || StringUtils.isNotEmpty(MqCnst.SECRET_KEY)) {
            SessionCredentialsProvider sessionCredentialsProvider = new StaticSessionCredentialsProvider(MqCnst.ACCESS_KEY, MqCnst.SECRET_KEY);
            configurationBuilder.setCredentialProvider(sessionCredentialsProvider);
        }


        ClientConfiguration clientConfiguration = configurationBuilder.build();


        final ProducerBuilder builder = provider.newProducerBuilder()
            .setClientConfiguration(clientConfiguration)
            // Set the topic name(s), which is optional but recommended. It makes producer could prefetch
            // the topic route before message publishing.
            .setTopics(topics);
        if (checker != null) {
            // Set the transaction checker.
            builder.setTransactionChecker(checker);
        }
        return builder.build();
    }

    public static Producer getInstance(String... topics) throws ClientException {
        if (null == PRODUCER) {
            synchronized (ProducerSingleton.class) {
                if (null == PRODUCER) {
                    PRODUCER = buildProducer(null, topics);
                }
            }
        }
        return PRODUCER;
    }

    public static Producer getTransactionalInstance(TransactionChecker checker,
                                                    String... topics) throws ClientException {
        if (null == TRANSACTIONAL_PRODUCER) {
            synchronized (ProducerSingleton.class) {
                if (null == TRANSACTIONAL_PRODUCER) {
                    TRANSACTIONAL_PRODUCER = buildProducer(checker, topics);
                }
            }
        }
        return TRANSACTIONAL_PRODUCER;
    }
}
