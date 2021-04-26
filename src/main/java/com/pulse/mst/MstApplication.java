package com.pulse.mst;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class MstApplication {



        @PostConstruct
        void init() {
            TimeZone.setDefault(TimeZone.getTimeZone("Asia/Colombo"));
        }

        public static void main(String[] args) {
            SpringApplication.run(MstApplication.class, args);
            Logger logger= LoggerFactory.getLogger(MstApplication.class);
//            System.out.println("Application started  time="+ LocalDateTime.now());
//            System.out.println(":::::::::::::::::::::::::::::::::::::::::::::Pulse Start:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            logger.info("Application started  time="+ LocalDateTime.now());
            logger.info(":::::::::::::::::::::::::::::::::::::::::::::Pulse Start:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

        }



}
