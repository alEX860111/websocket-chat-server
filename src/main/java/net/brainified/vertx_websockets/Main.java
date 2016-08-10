package net.brainified.vertx_websockets;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public final class Main {

  public static void main(final String[] args) {
    final Vertx vertx = Vertx.vertx();
    final Injector injector = Guice.createInjector(new ChatModule(vertx));

    //TODO better way to configure eventBus?
    final EventBus eventBus = injector.getInstance(EventBus.class);
    eventBus.consumer("chat.to.server").handler(injector.getInstance(ChatMessageReceiver.class));

    vertx.deployVerticle(injector.getInstance(ChatServerVerticle.class));
  }

}
