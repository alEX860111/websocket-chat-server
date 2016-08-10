package net.brainified.vertx_websockets;

import java.util.Objects;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;

final class BridgeEventHandler implements Handler<BridgeEvent> {

  @Override
  public void handle(final BridgeEvent event) {
    final String remoteAddress = event.socket().remoteAddress().toString();
    if (BridgeEventType.SEND.equals(event.type())) {
      final JsonObject rawMessage = event.getRawMessage();
      final JsonObject headers = rawMessage.getJsonObject("headers");
      if (Objects.nonNull(headers)) {
        headers.put("remoteAddress", remoteAddress);
      }
    }
    event.complete(true);
  }

}
