package net.brainified.vertx_websockets;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;

final class Verticle extends AbstractVerticle {

  public Verticle() {
  }

  @Override
  public void start(final Future<Void> fut) {
    final EventBus eb = vertx.eventBus();

    // Register to listen for messages coming IN to the server
    eb.consumer("chat.to.server").handler(message -> {
      // Create a timestamp string
      String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date.from(Instant.now()));
      // Send the message back out to all clients with the timestamp prepended.
      eb.publish("chat.to.client", timestamp + ": " + message.body());
    });

    final BridgeOptions opts = new BridgeOptions()
        .addInboundPermitted(new PermittedOptions().setAddress("chat.to.server"))
        .addOutboundPermitted(new PermittedOptions().setAddress("chat.to.client"));

    final SockJSHandler sockJSHandler = SockJSHandler.create(vertx).bridge(opts, event -> {
      System.out.println(event.type());
      System.out.println(event.socket().remoteAddress());
      if (BridgeEventType.SOCKET_CREATED.equals(event.type())) {
        eb.publish("chat.to.client", event.type().toString() +" " + event.socket().remoteAddress().toString());
      }
      if (BridgeEventType.SOCKET_CLOSED.equals(event.type())) {
        eb.publish("chat.to.client", event.type().toString() +" " + event.socket().remoteAddress().toString());
      }

      event.complete(true);
    });

    final Router router = Router.router(vertx);
    router.route("/eventbus/*").handler(sockJSHandler);
    router.route("/app/*").handler(StaticHandler.create("../../workspace_js/websocket-chat-client/app"));
    router.route("/assets/*").handler(StaticHandler.create("../../workspace_js/websocket-chat-client/node_modules"));

    vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8080), result -> {
      if (result.succeeded()) {
        fut.complete();
      } else {
        fut.fail(result.cause());
      }
    });
    


  }

}