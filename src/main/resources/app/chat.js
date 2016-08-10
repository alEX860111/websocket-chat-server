$(function() {
  var form = $('#message-form');
  var messageInput = $('#message').focus();
  var chat = $('#chat');

  var eb = new EventBus('http://localhost:8080/eventbus');

  eb.onopen = function() {

    // set a handler to receive a message
    eb.registerHandler('chat.to.client', function(error, message) {
      console.log('received a message: ' + JSON.stringify(message));
      console.log(message.body);
      var element = $('<div></div>');
      // element.addClass('text-left');
      element.addClass('text-success');
      element.text(message.body);
      chat.append(element);
      chat.scrollTop(chat.get(0).scrollHeight);
    });

    // send a message
    form.submit(submit);
    eb.send('chat.to.server', 'joined');
  };

  function submit(e) {
    eb.send('chat.to.server', messageInput.val());
    form.trigger('reset');
    e.preventDefault();
  }
});
