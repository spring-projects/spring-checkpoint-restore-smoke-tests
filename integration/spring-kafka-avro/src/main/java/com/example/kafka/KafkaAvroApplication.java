package com.example.kafka;

import java.util.List;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.BatchMessageListener;
import org.springframework.kafka.listener.CommonContainerStoppingErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class KafkaAvroApplication {

	public static void main(String[] args) {
		System.out.println(System.getProperty("java.class.path"));
		SpringApplication.run(KafkaAvroApplication.class, args);
	}

	@Bean
	public KafkaAdmin.NewTopics topics() {
		return new KafkaAdmin.NewTopics(TopicBuilder.name("cr1").partitions(1).replicas(1).build(),
				TopicBuilder.name("cr2").partitions(1).replicas(1).build(),
				TopicBuilder.name("cr3").partitions(1).replicas(1).build(),
				TopicBuilder.name("cr4").partitions(1).replicas(1).build(),
				TopicBuilder.name("cr5").partitions(1).replicas(1).build(),
				TopicBuilder.name("cr6").partitions(1).replicas(1).build(),
				TopicBuilder.name("cr7").partitions(1).replicas(1).build(),
				TopicBuilder.name("cr8").partitions(1).replicas(1).build());
	}

	@Bean
	public ConcurrentMessageListenerContainer<Object, Object> container3(NotAComponentMessageListener listener,
			ConsumerFactory cf, ProducerFactory pf, ConcurrentKafkaListenerContainerFactory<Object, Object> factory) {

		((DefaultKafkaConsumerFactory<Object, Object>) cf).setValueDeserializer(new ThingAvroSerde());
		((DefaultKafkaProducerFactory<Object, SpecificRecord>) pf).setValueSerializer(new ThingAvroSerde());
		factory.setCommonErrorHandler(new CommonContainerStoppingErrorHandler());
		ConcurrentMessageListenerContainer<Object, Object> container = factory.createContainer("cr3");
		container.getContainerProperties().setGroupId("cr3");
		container.getContainerProperties().setMessageListener(listener);
		return container;
	}

	@Bean
	public ConcurrentMessageListenerContainer<Object, Object> container6(BML6 listener, ConsumerFactory cf,
			ProducerFactory pf, ConcurrentKafkaListenerContainerFactory<Object, Object> factory) {

		factory.setCommonErrorHandler(new CommonContainerStoppingErrorHandler());
		ConcurrentMessageListenerContainer<Object, Object> container = factory.createContainer("cr6");
		container.getContainerProperties().setGroupId("cr6");
		container.getContainerProperties().setMessageListener(listener);
		return container;
	}

	@Bean
	public ConcurrentMessageListenerContainer<Object, Object> container7(BML7 listener, ConsumerFactory cf,
			ProducerFactory pf, ConcurrentKafkaListenerContainerFactory<Object, Object> factory) {

		factory.setCommonErrorHandler(new CommonContainerStoppingErrorHandler());
		ConcurrentMessageListenerContainer<Object, Object> container = factory.createContainer("cr7");
		container.getContainerProperties().setGroupId("cr7");
		container.getContainerProperties().setMessageListener(listener);
		return container;
	}

	@Bean
	NotAComponentMessageListener otherListner() {
		return new NotAComponentMessageListener();
	}

	@Bean
	public ApplicationRunner runner(KafkaTemplate<Object, Object> template) {
		return args -> {
			Thing1 thing1 = Thing1.newBuilder().setStringField("thing1Value").setIntField(42).build();
			template.send("cr1", thing1);
			Thing2 thing2 = Thing2.newBuilder().setStringField("thing2Value").setIntField(42).build();
			template.send("cr2", thing2);
			Thing3 thing3 = Thing3.newBuilder().setStringField("thing3Value").setIntField(42).build();
			template.send("cr3", thing3);
			Thing4 thing4 = Thing4.newBuilder().setStringField("thing4Value").setIntField(42).build();
			template.send("cr4", thing4);
			Thing5 thing5 = Thing5.newBuilder().setStringField("thing5Value").setIntField(42).build();
			template.send("cr5", thing5);
			Thing6 thing6 = Thing6.newBuilder().setStringField("thing5Value").setIntField(42).build();
			template.send("cr6", thing6);
			Thing7 thing7 = Thing7.newBuilder().setStringField("thing5Value").setIntField(42).build();
			template.send("cr7", thing7);
			Thing8 thing8 = Thing8.newBuilder().setStringField("thing5Value").setIntField(42).build();
			template.send("cr8", thing8);
			System.out.println("++++++Sent:" + thing1 + thing2 + thing3 + thing4 + thing5 + thing6 + thing7 + thing8);
		};
	}

}

@Component
class RecordListener {

	@KafkaListener(id = "cr", topics = "cr1")
	void listen1(Thing1 thing1) {
		System.out.println("++++++1:Received " + thing1.getClass().getSimpleName() + ":" + thing1);
	}

	@KafkaListener(id = "cr2", topics = "cr2")
	void listen2(ConsumerRecord<String, Thing2> record) {
		System.out.println("++++++2:Received " + record.value().getClass().getSimpleName() + ":" + record);
	}

	@KafkaListener(id = "cr4", topics = "cr4", batch = "true")
	void listen4(List<Thing4> records) {
		System.out.println("++++++4:Received " + records.get(0).getClass().getSimpleName() + ":" + records.get(0));
	}

	@KafkaListener(id = "cr8", topics = "cr8", batch = "true")
	void listen8(List<ConsumerRecord<String, Thing8>> records) {
		ConsumerRecord<String, Thing8> record = records.iterator().next();
		System.out.println("++++++8:Received " + record.value().getClass().getSimpleName() + ":" + record.value());
	}

}

class NotAComponentMessageListener implements MessageListener<String, Thing3> {

	@Override
	public void onMessage(ConsumerRecord<String, Thing3> record) {
		System.out.println("++++++3:Received " + record.value().getClass().getSimpleName() + ":" + record);
	}

}

@Component
class BML6 implements BatchMessageListener<String, Thing6> {

	@Override
	public void onMessage(List<ConsumerRecord<String, Thing6>> records) {
		System.out
			.println("++++++6:Received " + records.get(0).value().getClass().getSimpleName() + ":" + records.get(0));
	}

}

@Component
class BML7 implements BatchMessageListener<String, Thing7> {

	@Override
	public void onMessage(List<ConsumerRecord<String, Thing7>> data) {
	}

	@Override
	public void onMessage(ConsumerRecords<String, Thing7> records, Acknowledgment acknowledgment,
			Consumer<String, Thing7> consumer) {
		ConsumerRecord<String, Thing7> record = records.iterator().next();
		System.out.println("++++++7:Received " + record.value().getClass().getSimpleName() + ":" + record);
	}

	@Override
	public boolean wantsPollResult() {
		return true;
	}

}

@Component
@KafkaListener(id = "cr5", topics = "cr5")
class Thing5Listener {

	@KafkaHandler
	public void listen(Thing5 thing5) {
		System.out.println("++++++5:Received " + thing5.getClass().getSimpleName() + ":" + thing5);
	}

}
