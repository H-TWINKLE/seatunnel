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

package org.apache.seatunnel.connectors.seatunnel.udp.source;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.apache.seatunnel.api.source.Collector;
import org.apache.seatunnel.api.table.type.SeaTunnelRow;
import org.apache.seatunnel.connectors.seatunnel.common.source.AbstractSingleSplitReader;
import org.apache.seatunnel.connectors.seatunnel.common.source.SingleSplitReaderContext;
import org.apache.seatunnel.connectors.seatunnel.udp.config.UdpSourceParameter;
import org.apache.seatunnel.connectors.seatunnel.udp.exception.UdpConnectorErrorCode;
import org.apache.seatunnel.connectors.seatunnel.udp.exception.UdpConnectorException;
import org.apache.seatunnel.connectors.seatunnel.udp.vertx.UdpModule;
import org.apache.seatunnel.connectors.seatunnel.udp.vertx.UdpServerVerticle;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class UdpSourceReader extends AbstractSingleSplitReader<SeaTunnelRow> {
    private final UdpSourceParameter parameter;
    private final SingleSplitReaderContext context;
    private Vertx vertx;

    private final Object LOCK = new Object();
    private final AtomicReference<Exception> errRef = new AtomicReference<>();

    UdpSourceReader(UdpSourceParameter parameter, SingleSplitReaderContext context) {
        this.parameter = parameter;
        this.context = context;
    }

    @Override
    public void open() throws Exception {
        vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(20));

        String hostInfo = StrUtil.format("host:[{}], port:[{}], type:[{}]",
                this.parameter.getHost(),
                this.parameter.getPort(),
                this.parameter.getType());

        vertx.deployVerticle(new UdpServerVerticle(this.parameter))
                .onComplete(res -> {
                    if (res.succeeded()) {
                        log.info("start deploy udp server successfully, {} ", hostInfo);
                    } else {
                        log.error("failed to deploy udp server");
                        errRef.set(new UdpConnectorException(UdpConnectorErrorCode.UDP_SERVER_START_FAILED, res.cause()));
                    }
                    toNotifyAll();
                });

        log.info("prepare to start udp server, {} ", hostInfo);

        // 强制异步转换为同步的方法
        ThreadUtil.sync(LOCK);

        if (errRef.get() != null) {
            throw errRef.get();
        }
    }

    private void toNotifyAll() {
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }

    @Override
    public void close() throws IOException {
        if (vertx != null) {
            try {
                vertx.close();
            } catch (Exception e) {
                log.error("failed to close vertx", e);
                throw new UdpConnectorException(UdpConnectorErrorCode.UDP_STOP_FAILED, e.getCause());
            }
        }
    }

    @Override
    public void pollNext(Collector<SeaTunnelRow> output) throws Exception {
        final EventBus eventBus = this.vertx.eventBus();
        eventBus.<byte[]>consumer(UdpModule.UDP_BYTE.name(), handler -> {
            final byte[] body = handler.body();
            output.collect(new SeaTunnelRow(new Object[]{body}));
        });
        eventBus.<String>consumer(UdpModule.UDP_TEXT.name(), handler -> {
            final String body = handler.body();
            output.collect(new SeaTunnelRow(new Object[]{body}));
        });
    }
}
