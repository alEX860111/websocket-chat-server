package net.brainified.vertx_websockets;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

final class Verticle extends AbstractVerticle {

  private final ChatMessageReceiver handler;

  private final EventBus eventBus;

  private final Handler<BridgeEvent> bridgeEventHandler;

  @Inject
  public Verticle(final ChatMessageReceiver handler, final EventBus eventBus,
      final Handler<BridgeEvent> bridgeEventHandler) {
    this.handler = handler;
    this.eventBus = eventBus;
    this.bridgeEventHandler = bridgeEventHandler;
  }

  @Override
  public void start(final Future<Void> fut) {
    eventBus.consumer("chat.to.server").handler(handler);

    final SockJSHandler sockJSHandler = createSockJSHandler();

    final Router router = createRouter(sockJSHandler);

    vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8080), result -> {
      if (result.succeeded()) {
        fut.complete();
      } else {
        fut.fail(result.cause());
      }
    });

  }

  private SockJSHandler createSockJSHandler() {
    final BridgeOptions options = new BridgeOptions()
        .addInboundPermitted(new PermittedOptions().setAddress("chat.to.server"))
        .addOutboundPermitted(new PermittedOptions().setAddress("chat.to.client"));

    final SockJSHandler sockJSHandler = SockJSHandler.create(vertx).bridge(options, bridgeEventHandler);

    return sockJSHandler;
  }

  private Router createRouter(final SockJSHandler sockJSHandler) {
    final Router router = Router.router(vertx);
    router.route("/eventbus/*").handler(sockJSHandler);
    router.route("/app/*").handler(StaticHandler.create("../../workspace_js/websocket-chat-client/app"));
    router.route("/assets/*").handler(StaticHandler.create("../../workspace_js/websocket-chat-client/node_modules"));
    return router;
  }

}