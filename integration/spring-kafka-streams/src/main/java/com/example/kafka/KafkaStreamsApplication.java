/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
@EnableKafkaStreams
public class KafkaStreamsApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaStreamsApplication.class, args);
	}

	@Bean
	KStream<String, String> stream(StreamsBuilder builder) {
		KStream<String, String> stream = builder.stream("crStreamsIn");
		stream.map((k, v) -> new KeyValue<>(k, v.toUpperCase())).to("crStreamsOut");
		return stream;
	}

	@KafkaListener(id = "cr", topics = "crStreamsOut")
	public void listen(String in) {
		System.out.println("++++++Received:" + in);
	}

	@Bean
	public NewTopic topic1() {
		return TopicBuilder.name("crStreamsIn").partitions(1).replicas(1).build();
	}

	@Bean
	public NewTopic topic2() {
		return TopicBuilder.name("crStreamsOut").partitions(1).replicas(1).build();
	}

	@Bean
	public ApplicationRunner runner(KafkaTemplate<Object, Object> template) {
		return args -> {
			template.send("crStreamsIn", "foo");
			System.out.println("++++++Sent:foo");
		};
	}

}
