package net.brainified.vertx_websockets;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

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
      }
    });
    vertx.deployVerticle(injector.getInstance(Verticle.class));
  }

}
