package net.brainified.vertx_websockets;

import javax.inject.Inject;
import javax.inject.Provider;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

final class RouterProvider implements Provider<Router> {

  private final Vertx vertx;

  private final SockJSHandler sockJSHandler;

  @Inject
  public RouterProvider(final Vertx vertx, final SockJSHandler sockJSHandler) {
    this.vertx = vertx;
    this.sockJSHandler = sockJSHandler;
  }

  @Override
  public Router get() {
    final Router router = Router.router(vertx);
    router.route("/eventbus/*").handler(sockJSHandler);
    router.route("/app/*").handler(StaticHandler.create("../../workspace_js/websocket-chat-client/app"));
    router.route("/assets/*").handler(StaticHandler.create("../../workspace_js/websocket-chat-client/node_modules"));
    return router;
  }

}
