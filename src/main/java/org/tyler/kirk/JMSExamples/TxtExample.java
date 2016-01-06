package org.tyler.kirk.JMSExamples;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;

public class TxtExample {
    public static void main(String[] args) throws Exception {
        TxtExample txt = new TxtExample();
        txt.runTest();
      }
    
    private void runTest() throws Exception {
      Properties properties = new Properties();
      InputStream input = new FileInputStream("txt.properties");
      properties.load(input);
      Context context = new InitialContext(properties);
      
      ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("qpidConnectionFactory");
      Connection connection = connectionFactory.createConnection();
      connection.start();
      
      Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
      Topic tylerTopic = (Topic) context.lookup("myname");
      
//      MessageConsumer sub1 = session.createDurableSubscriber(tylerTopic, "sub1");
      MessageProducer messageProducer = session.createProducer(tylerTopic);
      
      TextMessage message = session.createTextMessage("Word of the day: unputdownable");
      messageProducer.send(message);
      message = session.createTextMessage("so interesting or suspenseful as to compel reading.");
      messageProducer.send(message);
      session.commit();
      
//      message = (TextMessage) sub1.receive(1000);
//      session.commit();
//      System.out.println("Sub1 recieved the message: " + message.getText());
      
//      session.unsubscribe("sub1");
      connection.close();
      context.close();
      
      }
}
