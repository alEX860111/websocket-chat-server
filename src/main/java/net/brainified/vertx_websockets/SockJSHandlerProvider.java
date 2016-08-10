package net.brainified.vertx_websockets;

import javax.inject.Inject;
import javax.inject.Provider;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

final class SockJSHandlerProvider implements Provider<SockJSHandler> {

  private final Vertx vertx;

  private final Handler<BridgeEvent> bridgeEventHandler;

  @Inject
  public SockJSHandlerProvider(final Vertx vertx, final Handler<BridgeEvent> bridgeEventHandler) {
    this.vertx = vertx;
    this.bridgeEventHandler = bridgeEventHandler;
  }

  @Override
  public SockJSHandler get() {
    final BridgeOptions options = new BridgeOptions()
        .addInboundPermitted(new PermittedOptions().setAddress("chat.to.server"))
        .addOutboundPermitted(new PermittedOptions().setAddress("chat.to.client"));

    return SockJSHandler.create(vertx).bridge(options, bridgeEventHandler);
  }

}
