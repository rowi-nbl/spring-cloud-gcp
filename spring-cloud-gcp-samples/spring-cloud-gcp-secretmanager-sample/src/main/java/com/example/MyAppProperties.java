package com.example;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("myapp")
public class MyAppProperties {

	private String secret;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
}
