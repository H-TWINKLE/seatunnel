package org.apache.seatunnel.connectors.seatunnel.udp.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.apache.seatunnel.connectors.seatunnel.udp.config.UdpSourceParameter;

import java.nio.charset.Charset;

import static org.apache.seatunnel.connectors.seatunnel.udp.vertx.UdpModule.UDP_BYTE;
import static org.apache.seatunnel.connectors.seatunnel.udp.vertx.UdpModule.UDP_TEXT;

@Slf4j
public class UdpServerVerticle extends AbstractVerticle {

    private UdpSourceParameter parameter;

    public UdpServerVerticle(UdpSourceParameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public void start() throws Exception {
        final DatagramSocket socket = this.vertx.createDatagramSocket(new DatagramSocketOptions());
        socket.listen(parameter.getPort(), parameter.getHost(), asyncRes -> {
            if (asyncRes.succeeded()) {
                socket.handler(packet -> {
                    final byte[] bytes = packet.data().getBytes(0, packet.data().length());
                    final EventBus eventBus = this.vertx.eventBus();
                    sendContent(eventBus, bytes);
                });
            } else {
                log.error("Can't get message", asyncRes.cause());
            }
        });
    }

    private void sendContent(EventBus eventBus, byte[] bytes) {
        if (parameter.getType().equals(UDP_BYTE.name())) {
            eventBus.send(UDP_BYTE.name(), bytes);
        } else if (parameter.getType().equals(UDP_TEXT.name())) {
            sendTextContent(eventBus, bytes);
        } else {
            sendTextContent(eventBus, bytes);
        }
    }

    private void sendTextContent(EventBus eventBus, byte[] bytes) {
        eventBus.send(UDP_TEXT.name(), new String(bytes, Charset.forName(parameter.getCharset())));
    }
}
