package net.brainified.vertx_websockets;

import javax.inject.Provider;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

final class EventBusProvider implements Provider<EventBus> {

  private final Vertx vertx;

  public EventBusProvider(final Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public EventBus get() {
    return vertx.eventBus();
  }

}
