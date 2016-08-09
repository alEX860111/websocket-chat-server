package net.brainified.vertx_websockets;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.vertx.core.eventbus.EventBus;

@RunWith(MockitoJUnitRunner.class)
public class ChatMessagePublisherImplTest {

  @Captor
  private ArgumentCaptor<String> responseCaptor;

  @Mock
  private EventBus eventBus;

  @InjectMocks
  private ChatMessagePublisherImpl publisher;

  @Test
  public void test() {
    publisher.publish("message");
    verify(eventBus).publish(eq("chat.to.client"), responseCaptor.capture());
    assertTrue(responseCaptor.getValue().endsWith("message"));
  }

}
