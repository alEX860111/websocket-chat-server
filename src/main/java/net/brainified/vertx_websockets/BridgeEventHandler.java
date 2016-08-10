package net.brainified.vertx_websockets;

import java.util.Objects;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;

final class BridgeEventHandler implements Handler<BridgeEvent> {

  private final ChatMessagePublisher publisher;

  @Inject
  public BridgeEventHandler(final ChatMessagePublisher publisher) {
    this.publisher = publisher;
  }

  @Override
  public void handle(final BridgeEvent event) {
    final String remoteAddress = event.socket().remoteAddress().toString();
    if (BridgeEventType.SEND.equals(event.type())) {
      addHeader(event, remoteAddress);
    }
    if (BridgeEventType.SOCKET_CREATED.equals(event.type())) {
      publisher.publish(remoteAddress + " joined");
    }
    if (BridgeEventType.SOCKET_CLOSED.equals(event.type())) {
      publisher.publish(remoteAddress + " left");
    }
    event.complete(true);
  }

  private void addHeader(final BridgeEvent event, final String remoteAddress) {
    final JsonObject rawMessage = event.getRawMessage();
    final JsonObject headers = rawMessage.getJsonObject("headers");
    if (Objects.nonNull(headers)) {
      headers.put("remoteAddress", remoteAddress);
    }
  }

}
