package net.brainified.vertx_websockets;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

import javax.inject.Inject;

import io.vertx.core.eventbus.EventBus;

final class ChatMessagePublisherImpl implements ChatMessagePublisher {

  private static final String CLIENT_ADDRESS = "chat.to.client";

  private final EventBus eventBus;

  @Inject
  public ChatMessagePublisherImpl(final EventBus eventBus) {
    this.eventBus = eventBus;
  }

  @Override
  public void publish(final String msg) {
    final String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM)
        .format(Date.from(Instant.now()));
    eventBus.publish(CLIENT_ADDRESS, timestamp + ", " + msg);
  }

}
