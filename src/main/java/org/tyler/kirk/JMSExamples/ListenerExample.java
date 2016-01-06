package org.tyler.kirk.JMSExamples;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;


public class ListenerExample implements javax.jms.MessageListener {

	public static void main(String[] args) throws Exception {
		
		Properties properties = new Properties();
		InputStream input = new FileInputStream("listener.properties");
		properties.load(input);
		Context context = new InitialContext(properties);
		
		//Obtains the topic
		Topic tylerTopic = (Topic) context.lookup("myname");
		
		//Creates connection factory
		ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("qpidConnectionFactory");

		//Creates connection
		Connection connection = connectionFactory.createConnection();

		//Creates session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		//Creates a consumer
		MessageConsumer messageConsumer = session.createDurableSubscriber(tylerTopic, "sub1");
		
		//Sets the listener
		ListenerExample listener = new ListenerExample();
		messageConsumer.setMessageListener(listener);
		
		connection.start();
		
		System.out.println("Waiting for messages");
		for(int i = 0; i < 60; i++) {
			Thread.sleep(1000);
			System.out.println(".");
		}
		System.out.println();
		
		connection.close();
		System.out.println("Fin");
	}
	
	public void onMessage(Message message) {
		TextMessage msg = (TextMessage) message;
		try {
			System.out.println("recieved: " + msg.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
