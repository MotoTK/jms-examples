package org.tyler.kirk.JMSExamples;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class StocksExample {

    public StocksExample() {
    }

    public static void main(String[] args) throws Exception {
      StocksExample stocks = new StocksExample();
      stocks.runTest();
    }

    private void runTest() throws Exception {
      Properties properties = new Properties();
      InputStream input = new FileInputStream("stocks.properties");
      properties.load(input);
      Context context = new InitialContext(properties);

      ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("qpidConnectionFactory");
      Connection connection = connectionFactory.createConnection();
      connection.start();

      Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
      Topic priceTopic = (Topic) context.lookup("myprices");
      
      MessageConsumer subscriber1 = session.createDurableSubscriber(priceTopic, "sub1");
      MessageConsumer subscriber2 = session.createDurableSubscriber(priceTopic, "sub2" /*, "price > 150", false*/ );
      MessageProducer messageProducer = session.createProducer(priceTopic);

      Message message = session.createMessage();
      message.setStringProperty("instrument", "IBM");
      message.setIntProperty("price", 100);
      messageProducer.send(message);
      session.commit();

      message = subscriber1.receive(1000);
      session.commit();
      System.out.println("Subscriber 1 received : " + message);

      message = subscriber2.receive(1000);
      session.commit();
      System.out.println("Subscriber 2 received : " + message);
      
      session.unsubscribe("sub0");
      session.unsubscribe("sub1");
      session.unsubscribe("sub2");
      connection.close();
      context.close();
    }
}
