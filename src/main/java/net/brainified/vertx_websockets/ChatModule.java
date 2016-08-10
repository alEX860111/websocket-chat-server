package net.brainified.vertx_websockets;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

final class ChatModule extends AbstractModule {

  private final Vertx vertx;

  public ChatModule(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  protected void configure() {
    bind(Vertx.class).toInstance(vertx);
    bind(EventBus.class).toInstance(vertx.eventBus());

    bind(Router.class).toProvider(RouterProvider.class).in(Scopes.SINGLETON);
    bind(SockJSHandler.class).toProvider(SockJSHandlerProvider.class).in(Scopes.SINGLETON);

    bind(ChatMessagePublisher.class).to(ChatMessagePublisherImpl.class);
    bind(ChatMessageReceiver.class).to(ChatMessageReceiverImpl.class);
    bind(new TypeLiteral<Handler<BridgeEvent>>() {
    }).to(BridgeEventHandler.class);
  }
}