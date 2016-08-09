package net.brainified.vertx_websockets;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.Message;

@RunWith(MockitoJUnitRunner.class)
public class ChatMessageReceiverImplTest {

  @Mock
  private MultiMap headers;

  @Mock
  private Message<Object> message;

  @Captor
  private ArgumentCaptor<String> responseCaptor;

  @Mock
  private ChatMessagePublisher publisher;

  @InjectMocks
  private ChatMessageReceiverImpl handler;

  @Test
  public void test() {
    when(headers.get("remoteAddress")).thenReturn("remoteAddress");
    when(message.headers()).thenReturn(headers);
    when(message.body()).thenReturn("body");
    handler.handle(message);
    verify(publisher).publish(responseCaptor.capture());
    assertTrue(responseCaptor.getValue().endsWith("remoteAddress: body"));
  }

}
