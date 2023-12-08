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
import org.apache.rocketmq.client.apis.SessionCredentialsProvider;
import org.apache.rocketmq.client.apis.StaticSessionCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerClientConf {
    private static final Logger log = LoggerFactory.getLogger(ConsumerClientConf.class);

    private ConsumerClientConf() {
    }

    @SuppressWarnings({"resource", "InfiniteLoopStatement"})
    public  static ClientConfiguration clientConfiguration(){

        SessionCredentialsProvider sessionCredentialsProvider =
            new StaticSessionCredentialsProvider(MqCnst.ACCESS_KEY, MqCnst.SECRET_KEY);


        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
            .setEndpoints(MqCnst.ENDPOINTS)
            // On some Windows platforms, you may encounter SSL compatibility issues. Try turning off the SSL option in
            // client configuration to solve the problem please if SSL is not essential.
            // .enableSsl(false)
            .setCredentialProvider(sessionCredentialsProvider)
            .build();

        return clientConfiguration;
    }
}
