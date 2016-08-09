package net.brainified.vertx_websockets;

import java.util.Objects;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

final class Verticle extends AbstractVerticle {

  private final ChatMessageReceiverImpl handler;

  private final ChatMessagePublisher publisher;

  private final EventBus eventBus;

  @Inject
  public Verticle(final ChatMessageReceiverImpl handler, final ChatMessagePublisher publisher, final EventBus eventBus) {
    this.handler = handler;
    this.publisher = publisher;
    this.eventBus = eventBus;
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

    final SockJSHandler sockJSHandler = SockJSHandler.create(vertx).bridge(options, event -> {
      if (BridgeEventType.SEND.equals(event.type())) {
        final JsonObject rawMessage = event.getRawMessage();
        final JsonObject headers = rawMessage.getJsonObject("headers");
        if (Objects.nonNull(headers)) {
          headers.put("remoteAddress", event.socket().remoteAddress().toString());
        }
      }
      if (BridgeEventType.SOCKET_CREATED.equals(event.type())) {
        publisher.publish(event.socket().remoteAddress().toString() + " joined");
      }
      if (BridgeEventType.SOCKET_CLOSED.equals(event.type())) {
        publisher.publish(event.socket().remoteAddress().toString() + " left");
      }
      event.complete(true);
    });

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