package org.springframework.cloud.gcp.autoconfigure.secretmanager;

import com.google.cloud.secretmanager.v1beta1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1beta1.ProjectName;
import com.google.cloud.secretmanager.v1beta1.Secret;
import com.google.cloud.secretmanager.v1beta1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1beta1.SecretManagerServiceClient.ListSecretsPagedResponse;
import com.google.cloud.secretmanager.v1beta1.SecretVersion;
import com.google.cloud.secretmanager.v1beta1.SecretVersionName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.cloud.gcp.core.GcpProjectIdProvider;
import org.springframework.core.env.EnumerablePropertySource;

public class SecretManagerPropertySource extends EnumerablePropertySource<SecretManagerServiceClient> {

	private static final String SECRETS_NAMESPACE = "spring-cloud-gcp.secrets.";

	private static final String LATEST_VERSION_STRING = "latest";

	private final Map<String, Object> properties;

	private final String[] propertyNames;

	public SecretManagerPropertySource(
			String propertySourceName,
			SecretManagerServiceClient client,
			GcpProjectIdProvider projectIdProvider) {

		super(propertySourceName, client);

		Map<String, Object> propertiesMap =
				initializePropertiesMap(client, projectIdProvider.getProjectId());

		this.properties = propertiesMap;
		this.propertyNames = propertiesMap.keySet().toArray(new String[propertiesMap.size()]);
	}

	@Override
	public String[] getPropertyNames() {
		return propertyNames;
	}

	@Override
	public Object getProperty(String name) {
		return properties.get(name);
	}

	private static Map<String, Object> initializePropertiesMap(SecretManagerServiceClient client, String projectId) {
		ListSecretsPagedResponse response = client.listSecrets(ProjectName.of(projectId));

		HashMap<String, Object> secretsMap = new HashMap<>();
		for (Secret secret : response.iterateAll()) {
			String secretId = extractSecretId(secret);
			if (secretId != null) {
				String secretName = SECRETS_NAMESPACE + secretId;
				String secretPayload = getSecretPayload(client, projectId, secretId);
				secretsMap.put(secretName, secretPayload);
			}
		}

		return secretsMap;
	}

	private static String getSecretPayload(
			SecretManagerServiceClient client, String projectId, String secretId) {

		SecretVersionName secretVersionName =
				SecretVersionName.newBuilder()
						.setProject(projectId)
						.setSecret(secretId)
						.setSecretVersion(LATEST_VERSION_STRING)
						.build();

		AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);
		return response.getPayload().getData().toStringUtf8();
	}

	/**
	 * Extracts the Secret ID from the {@link Secret}. The secret ID refers to the unique ID
	 * given to the secret when it is saved under a GCP project.
	 */
	private static String extractSecretId(Secret secret) {
		String[] secretNameTokens = secret.getName().split("/");
		if (secretNameTokens.length > 0) {
			return secretNameTokens[secretNameTokens.length - 1];
		}

		return null;
	}
}
