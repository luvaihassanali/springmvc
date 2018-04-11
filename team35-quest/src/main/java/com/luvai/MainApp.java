package com.luvai;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApp {
	static InetAddress ip;

	public static void main(String[] args) {
		SpringApplication.run(MainApp.class, args);

		try {
			ip = InetAddress.getLocalHost();
			System.out.println("Current IP address : " + ip.getHostAddress());

		} catch (UnknownHostException e) {

			e.printStackTrace();
		}
	}
}
