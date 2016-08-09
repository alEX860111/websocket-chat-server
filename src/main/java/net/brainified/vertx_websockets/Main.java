package net.brainified.vertx_websockets;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;

public final class Main {

  public static void main(String[] args) {
    final Vertx vertx = Vertx.vertx();
    final EventBusProvider eventBusProvider = new EventBusProvider(vertx);

    final Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(EventBus.class).toProvider(eventBusProvider).in(Singleton.class);
        bind(ChatMessagePublisher.class).to(ChatMessagePublisherImpl.class);
        bind(ChatMessageReceiver.class).to(ChatMessageReceiverImpl.class);
        bind(new TypeLiteral<Handler<BridgeEvent>>() {}).to(BridgeEventHandler.class);
      }
    });
    vertx.deployVerticle(injector.getInstance(Verticle.class));
  }

}
