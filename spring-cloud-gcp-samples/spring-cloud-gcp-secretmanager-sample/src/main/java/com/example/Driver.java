package com.example;

import com.google.cloud.secretmanager.v1beta1.AccessSecretVersionRequest;
import com.google.cloud.secretmanager.v1beta1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1beta1.AddSecretVersionRequest;
import com.google.cloud.secretmanager.v1beta1.CreateSecretRequest;
import com.google.cloud.secretmanager.v1beta1.GetSecretVersionRequest;
import com.google.cloud.secretmanager.v1beta1.ListSecretVersionsRequest;
import com.google.cloud.secretmanager.v1beta1.ListSecretVersionsResponse;
import com.google.cloud.secretmanager.v1beta1.ProjectName;
import com.google.cloud.secretmanager.v1beta1.Replication;
import com.google.cloud.secretmanager.v1beta1.Replication.Automatic;
import com.google.cloud.secretmanager.v1beta1.Secret;
import com.google.cloud.secretmanager.v1beta1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1beta1.SecretManagerServiceClient.ListSecretVersionsPagedResponse;
import com.google.cloud.secretmanager.v1beta1.SecretManagerServiceClient.ListSecretsPagedResponse;
import com.google.cloud.secretmanager.v1beta1.SecretName;
import com.google.cloud.secretmanager.v1beta1.SecretPayload;
import com.google.cloud.secretmanager.v1beta1.SecretVersionName;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gcp.core.DefaultGcpProjectIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableConfigurationProperties(MyAppProperties.class)
public class Driver {

  @Autowired
  SecretManagerServiceClient client;

  @Autowired
  Environment environment;

  public static void main(String[] args) {
    SpringApplication.run(Driver.class, args);
  }

  @Bean
  public CommandLineRunner commandLineRunner() {
    return (args) -> {
      System.out.println(environment.getProperty("foo"));
    };
  }
}
