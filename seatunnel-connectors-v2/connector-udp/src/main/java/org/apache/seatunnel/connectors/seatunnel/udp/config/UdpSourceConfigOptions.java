/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.seatunnel.connectors.seatunnel.udp.config;

import org.apache.seatunnel.api.configuration.Option;
import org.apache.seatunnel.api.configuration.Options;
import org.apache.seatunnel.connectors.seatunnel.udp.vertx.UdpModule;

public class UdpSourceConfigOptions {
    private static final int DEFAULT_MAX_RETRIES = 3;

    public static final Option<String> HOST =
            Options.key("host").stringType().defaultValue("0.0.0.0").withDescription("udp host");

    public static final Option<Integer> PORT =
            Options.key("port").intType().defaultValue(9999).withDescription("udp port");

    public static final Option<String> TYPE =
            Options.key("type").stringType().defaultValue(UdpModule.UDP_TEXT.name()).withDescription("udp type,default is " + UdpModule.UDP_TEXT.name());

    public static final Option<String> CHARSET =
            Options.key("charset").stringType().defaultValue("UTF-8").withDescription("charset ,default is UTF-8");

    public static final Option<Integer> MAX_RETRIES =
            Options.key("max_retries")
                    .intType()
                    .defaultValue(DEFAULT_MAX_RETRIES)
                    .withDescription("default value is " + DEFAULT_MAX_RETRIES + ", max retries");
}
