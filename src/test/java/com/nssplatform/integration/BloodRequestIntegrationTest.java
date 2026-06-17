package com.nssplatform.integration;

import com.nssplatform.model.BloodRequest;
import com.nssplatform.model.BloodRequestRepository;
import nl.jqno.equalsverifier.Assert;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest
public class BloodRequestIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BloodRequestRepository bloodRequestRepository;

    @Test
    public void testValidateRequest() {
        when(bloodRequestRepository.save(any(BloodRequest.class))).thenReturn(Mono.just(new BloodRequest()));
        BloodRequest bloodRequest = new BloodRequest();
        // Mock request body
        Mono<BloodRequest> response = webTestClient.post()
                .uri("/blood-request")
                .bodyValue(bloodRequest)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(BloodRequest.class)
                .getResponseBody();
        Assert.assertThat(response, Assert.hasSameClassAs(BloodRequest.class));
        verify(bloodRequestRepository, times(1)).save(any(BloodRequest.class));
    }

    @Test
    public void testValidateRequestNull() {
        when(bloodRequestRepository.save(any(BloodRequest.class))).thenReturn(Mono.just(new BloodRequest()));
        BloodRequest bloodRequest = null;
        Assert.assertThat(() -> webTestClient.post()
                .uri("/blood-request")
                .bodyValue(bloodRequest)
                .accept(MediaType.APPLICATION_JSON)
                .exchange(), Assert.shouldThrow(NullPointerException.class));
    }

    @Test
    public void testValidateRequestMissingFields() {
        BloodRequest bloodRequest = new BloodRequest();
        bloodRequest.setName("John Doe");
        when(bloodRequestRepository.save(any(BloodRequest.class))).thenReturn(Mono.just(new BloodRequest()));
        Mono<BloodRequest> response = webTestClient.post()
                .uri("/blood-request")
                .bodyValue(bloodRequest)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .returnResult(BloodRequest.class)
                .getResponseBody();
        Assert.assertThat(response, Assert.hasSameClassAs(BloodRequest.class));
        verifyNoInteractions(bloodRequestRepository);
    }
}