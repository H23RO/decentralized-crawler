package ro.h23.dars.webcrawler.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import ro.h23.dars.webcrawler.exception.ExternalServiceException;


import javax.net.ssl.SSLException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;


public class GenericExternalApiService {

    private static final Logger logger = LoggerFactory.getLogger(GenericExternalApiService.class);

    private final WebClient webClient;

    private final String authenticationPath;
    private final String authenticationCredentials;

    private String authToken;

    /**
     *
     * @param serverBaseUrl
     * @param authenticationPath
     * @param authenticationCredentials "{\"email\": \"" + retrievalCoreApiConfig.getUsername() + "\", \"password\": \"" + retrievalCoreApiConfig.getPassword() + "\"}"
     */
    public GenericExternalApiService(String serverBaseUrl, String authenticationPath, String authenticationCredentials) {
        try {
            this.webClient = createWebClient(serverBaseUrl);
        } catch(SSLException e) {
            throw new ExternalServiceException(e);
        }
        this.authenticationPath = authenticationPath;
        this.authenticationCredentials = authenticationCredentials;
        this.authToken = null;
    }

    public <T> T sendRequest(HttpMethod httpMethod, String uriString, Object bodyObject, Class<T> clasz) {
        return sendRequest(httpMethod, uriString, bodyObject, clasz, false);
    }

    public <T> T sendRequest(HttpMethod httpMethod, String uriString, Object bodyObject, Class<T> clasz, boolean isAuthorization) {
        //logger.info("{} {}", httpMethod, uriString);
        if (isAuthorization || (authToken != null)) {
            UriSpec<RequestBodySpec> uriSpec = webClient.method(httpMethod);
            RequestBodySpec bodySpec = uriSpec.uri(uriString);
            //RequestHeadersSpec<?> headerSpec = bodySpec.bodyValue(bodyObject);
            ResponseEntity<T> result;
            if (authToken != null) {
                bodySpec = bodySpec
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                        .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                        .acceptCharset(StandardCharsets.UTF_8);
            } else {
                bodySpec = bodySpec
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                        .acceptCharset(StandardCharsets.UTF_8);
            }
            ResponseSpec responseSpec;
            if (bodyObject != null) {
                RequestHeadersSpec<?> headerSpec = bodySpec.bodyValue(bodyObject);
                responseSpec = headerSpec.retrieve();
            } else {
                responseSpec = bodySpec.retrieve();
            }
            try {
                result = responseSpec
                        /*.onStatus(HttpStatus::is4xxClientError,
                                error -> Mono.error(new RuntimeException("API not found")))
                        .onStatus(HttpStatus::is5xxServerError,
                                error -> Mono.error(new RuntimeException("Server is not responding")))*/
                        .toEntity(clasz)
                        //.bodyToMono(clasz)
                        .block();
                return result.getBody();
            } catch (WebClientResponseException.Unauthorized e) {
                if (isAuthorization) {
                    throw new ExternalServiceException("Invalid authorization credentials", e);
                }
                //System.out.println(">>>>> Unauthorized - " + AUTHENTICATION_CREDENTIALS);

            } catch (Exception e) {
                throw e;
            }
        }
        // authenticate and then send
        String responseString = sendRequest(HttpMethod.POST, authenticationPath,
                authenticationCredentials, String.class, true);
        ObjectMapper mapper = new ObjectMapper();
        try {
            authToken = mapper.readTree(responseString).get("token").asText();
        } catch (Exception e1) {
            e1.printStackTrace();
            System.exit(1);
        }
        return sendRequest(httpMethod, uriString, bodyObject, clasz, false);
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            final StringBuilder sb = new StringBuilder();
            sb.append("Request: \r\n" + clientRequest.method() + " " + clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> sb.append("\r\n").append(name + ": " + value)));
            //logger.info(sb.toString());
            return Mono.just(clientRequest);
        });
    }

    public WebClient createWebClient(String serverBaseUrl) throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        HttpClient httpClient = HttpClient.create()
                .secure(t -> t.sslContext(sslContext))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(10 * 60))
                        .addHandlerLast(new WriteTimeoutHandler(10 * 60)))
                .responseTimeout(Duration.ofSeconds(10 * 60));
        final int size = 100 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(serverBaseUrl)
                .exchangeStrategies(strategies)
                .filter(logRequest())
                .build();
        //return WebClient.create(expertSystemConfig.getUrl());
    }

}
