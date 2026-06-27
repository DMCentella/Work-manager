package com.microservicios.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "notificaciones.exchange";

    public static final String QUEUE_TAREA_CREADA = "tarea.creada.queue";
    public static final String QUEUE_TAREA_COMPLETADA = "tarea.completada.queue";
    public static final String QUEUE_ASISTENCIA = "asistencia.registrada.queue";


    public static final String RK_TAREA_CREADA = "tarea.creada";
    public static final String RK_TAREA_COMPLETADA = "tarea.completada";
    public static final String RK_ASISTENCIA = "asistencia.registrada";


    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue queueTareaCreada() {
        return new Queue(QUEUE_TAREA_CREADA, true);
    }

    @Bean
    public Queue queueTareaCompletada() {
        return new Queue(QUEUE_TAREA_COMPLETADA, true);
    }

    @Bean
    public Queue queueAsistencia() {
        return new Queue(QUEUE_ASISTENCIA, true);
    }



    @Bean
    public Binding bindingTareaCreada() {
        return BindingBuilder.bind(queueTareaCreada()).to(exchange()).with(RK_TAREA_CREADA);
    }

    @Bean
    public Binding bindingTareaCompletada() {
        return BindingBuilder.bind(queueTareaCompletada()).to(exchange()).with(RK_TAREA_COMPLETADA);
    }

    @Bean
    public Binding bindingAsistencia() {
        return BindingBuilder.bind(queueAsistencia()).to(exchange()).with(RK_ASISTENCIA);
    }



    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
