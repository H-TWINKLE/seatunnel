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

import org.apache.commons.lang3.StringUtils;
import org.apache.seatunnel.shade.com.typesafe.config.Config;

import java.io.Serializable;
import java.util.Objects;

import static org.apache.seatunnel.connectors.seatunnel.udp.config.UdpSourceConfigOptions.*;

public class UdpSourceParameter implements Serializable {
    private final String host;
    private final Integer port;
    private final String type;
    private final String charset;

    public String getHost() {
        return StringUtils.isBlank(host) ? HOST.defaultValue() : host;
    }

    public Integer getPort() {
        return Objects.isNull(port) ? PORT.defaultValue() : port;
    }

    public String getType() {
        return Objects.isNull(type) ? TYPE.defaultValue() : type;
    }

    public String getCharset() {
        return Objects.isNull(charset) ? CHARSET.defaultValue() : charset;
    }

    public UdpSourceParameter(Config config) {
        if (config.hasPath(HOST.key())) {
            this.host = config.getString(HOST.key());
        } else {
            this.host = HOST.defaultValue();
        }

        if (config.hasPath(PORT.key())) {
            this.port = config.getInt(PORT.key());
        } else {
            this.port = PORT.defaultValue();
        }

        if (config.hasPath(CHARSET.key())) {
            this.charset = config.getString(CHARSET.key());
        } else {
            this.charset = CHARSET.defaultValue();
        }

        if (config.hasPath(TYPE.key())) {
            this.type = config.getString(TYPE.key());
        } else {
            this.type = TYPE.defaultValue();
        }
    }
}
