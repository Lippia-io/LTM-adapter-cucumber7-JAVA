package ltm;

import ltm.models.run.request.TestDTO;
import ltm.models.run.response.RunDTO;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

final class TestManagerAPIClient {
    private static final String TEST_MANAGER_USER_KEY = System.getProperty("TEST_MANAGER_USERNAME");
    private static final String TEST_MANAGER_PASS_KEY = System.getProperty("TEST_MANAGER_PASSWORD");
    private static final String TEST_MANAGER_API_HOST_KEY = System.getProperty("TEST_MANAGER_API_HOST");
    private static final String TEST_MANAGER_API_PORT_KEY = System.getProperty("TEST_MANAGER_API_PORT");
    private static final String TEST_MANAGER_RUN_NAME = System.getProperty("TEST_MANAGER_RUN_NAME");
    private static final String TEST_MANAGER_PROJECT_CODE = System.getProperty("TEST_MANAGER_PROJECT_CODE");
    private static final String TEST_MANAGER_REPOSITORY_URL = System.getProperty("TEST_MANAGER_REPOSITORY_URL");
    private static final String TEST_MANAGER_REPOSITORY_BRANCH = System.getProperty("TEST_MANAGER_REPOSITORY_BRANCH");

    private static RestTemplate restTemplate;

    private static String apiUrl;

    private static void initializeRestTemplate(){
        apiUrl = getAPIUrl();

        if(apiUrl.startsWith("https://")) {
            TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;

            SSLContext sslContext;
            try {
                sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
                SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
                CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
                HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
                requestFactory.setHttpClient(httpClient);
                restTemplate = new RestTemplate(requestFactory);
            } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
                e.printStackTrace();
            }
        } else {
            restTemplate = new RestTemplate();
        }
    }

    private static RestTemplate getRestInstance() {
        if(restTemplate==null)
            initializeRestTemplate();
        return restTemplate;
    }

    private static HttpHeaders getApiHeaders() {
        if (TEST_MANAGER_USER_KEY == null) {
            throw new IllegalArgumentException("TEST_MANAGER_USERNAME must not be null");
        }

        if (TEST_MANAGER_PASS_KEY == null) {
            throw new IllegalArgumentException("TEST_MANAGER_PASSWORD must not be null");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, "application/json");
        headers.set("username", TEST_MANAGER_USER_KEY);
        headers.set("password", TEST_MANAGER_PASS_KEY);
        return headers;
    }

    private static String getAPIUrl() {
        if(apiUrl != null) {
            return apiUrl;
        }

        String uri = TEST_MANAGER_API_HOST_KEY;

        if(TEST_MANAGER_API_PORT_KEY != null && !TEST_MANAGER_API_PORT_KEY.isEmpty()) {
            uri +=  ":" + TEST_MANAGER_API_PORT_KEY;
        }

        return uri;
    }

    public static RunDTO createRun() {
        if (TEST_MANAGER_RUN_NAME == null || TEST_MANAGER_RUN_NAME.isEmpty()) {
            throw new IllegalArgumentException("TEST_MANAGER_RUN_NAME cannot be null or empty");
        }

        if (TEST_MANAGER_PROJECT_CODE == null || TEST_MANAGER_PROJECT_CODE.isEmpty()) {
            throw new IllegalArgumentException("TEST_MANAGER_RUN_NAME cannot be null or empty");
        }

        if (TEST_MANAGER_REPOSITORY_URL == null || TEST_MANAGER_REPOSITORY_URL.isEmpty()) {
            throw new IllegalArgumentException("TEST_MANAGER_REPOSITORY_URL cannot be null or empty");
        }

        if (TEST_MANAGER_REPOSITORY_BRANCH == null || TEST_MANAGER_REPOSITORY_BRANCH.isEmpty()) {
            throw new IllegalArgumentException("TEST_MANAGER_REPOSITORY_BRANCH cannot be null or empty");
        }

        ltm.models.run.request.RunDTO run = new ltm.models.run.request.RunDTO(
                TEST_MANAGER_RUN_NAME, TEST_MANAGER_PROJECT_CODE, TEST_MANAGER_REPOSITORY_URL, TEST_MANAGER_REPOSITORY_BRANCH);

        String url = getAPIUrl() + "/runs";
        HttpEntity<ltm.models.run.request.RunDTO> request = new HttpEntity<>(run, getApiHeaders());
        return getRestInstance().postForObject(url, request, RunDTO.class);
    }

    public static void createTest(TestDTO test) {
        String url = getAPIUrl() + "/tests";
        HttpEntity<TestDTO> request = new HttpEntity<>(test, getApiHeaders());
        getRestInstance().postForObject(url, request, Object.class);
    }
}