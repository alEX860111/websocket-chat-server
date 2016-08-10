package net.brainified.vertx_websockets;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

final class ChatServerVerticle extends AbstractVerticle {

  private final Vertx vertx;

  private final Router router;

  @Inject
  public ChatServerVerticle(final Vertx vertx, final Router router) {
    this.vertx = vertx;
    this.router = router;
  }

  @Override
  public void start(final Future<Void> fut) {
    vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8080), result -> {
      if (result.succeeded()) {
        fut.complete();
      } else {
        fut.fail(result.cause());
      }
    });
  }

}