package com.github.mirs.banxiaoxiao.framework.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class encapsulation some convenient methods for {@link RestTemplate}.
 *
 * @author zw
 */
public class RestTemplateHelper {

  private static final Logger logger = LoggerFactory.getLogger(RestTemplateHelper.class);

  private final RestTemplate restTemplate;

  public RestTemplateHelper(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public <R extends RestResponse> R get(String url, ParameterizedTypeReference ptr,
      Object... uriVariables) {
    return get(url, ptr, null, uriVariables);
  }

  public <R extends RestResponse> R get(String url, ParameterizedTypeReference ptr,
      Map<String, String> parameters,
      Object... uriVariables) {
    UriComponentsBuilder components = UriComponentsBuilder.fromHttpUrl(url);
    if (parameters != null) {
      for (Map.Entry<String, String> e : parameters.entrySet()) {
        components.queryParam(e.getKey(), e.getValue());
      }
    }
    return get(components.build().toUriString(), null, ptr, uriVariables);
  }

  public <R extends RestResponse> R get(String url, ParameterizedTypeReference ptr,
      Map<String, Object> parameters,
      HttpHeaders headers,
      Object... uriVariables) {
    UriComponentsBuilder components = UriComponentsBuilder.fromHttpUrl(url);
    if (parameters != null) {
      for (Map.Entry<String, Object> e : parameters.entrySet()) {
        components.queryParam(e.getKey(), e.getValue());
      }
    }
    return get(components.build().toUriString(), new HttpEntity(parameters, headers), ptr, uriVariables);
  }

  @SuppressWarnings("unchecked")
  private <R extends RestResponse> R get(String url, HttpEntity entity,
      ParameterizedTypeReference ptr,
      Object... uriVariables) {
    logRequest(url, null, null, uriVariables);
    return (R) restTemplate.exchange(url, HttpMethod.GET, entity, ptr, uriVariables).getBody();
  }

  public <T> T postForObject(String url, Object request, Class<T> responseType,
      Object... uriVariables) {
    logRequest(url, null, request, uriVariables);
    return restTemplate.postForObject(url, request, responseType, uriVariables);
  }

  @SuppressWarnings("unchecked")
  public <R extends RestResponse> R postJson(String url, Object obj, ParameterizedTypeReference ptr,
      Object... uriVariables) {
    HttpHeaders headers = new HttpHeaders();
    HttpEntity<Object> httpEntity = new HttpEntity(obj, headers);
    return (R) restTemplate.exchange(url, HttpMethod.POST, httpEntity, ptr, uriVariables).getBody();
  }

  @SuppressWarnings("unchecked")
  public <R extends RestResponse> R postJson(String url, Object obj, HttpHeaders headers,
      ParameterizedTypeReference ptr,
      Object... uriVariables) {
    HttpEntity<Object> httpEntity = new HttpEntity(obj, headers);
    return (R) restTemplate.exchange(url, HttpMethod.POST, httpEntity, ptr, uriVariables).getBody();
  }

  @SuppressWarnings("unchecked")
  public <R extends RestResponse> R postForm(String url, Map<String, String> parameters,
      ParameterizedTypeReference ptr,
      Object... uriVariables) {
    logRequest(url, parameters, null, uriVariables);
    return (R) restTemplate
        .exchange(url, HttpMethod.POST, createEntity(parameters), ptr, uriVariables).getBody();
  }

  @SuppressWarnings("unchecked")
  private HttpEntity createEntity(Map<String, String> parameters) {
    HttpHeaders headers = new HttpHeaders();
    if (parameters != null) {
      LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
      for (Map.Entry<String, String> e : parameters.entrySet()) {
        params.add(e.getKey(), e.getValue() + "");
      }
      return new HttpEntity(params, headers);
    }

    return new HttpEntity(headers);
  }

  private void logRequest(String url, Map<String, String> parameters, Object request,
      Object... uriVariables) {
    StringBuilder sb = new StringBuilder("Request url: {}, ");
    List<Object> params = new ArrayList<>(4);
    params.add(url);
    if (parameters != null) {
      sb.append("Parameters: {}, ");
      params.add(parameters);
    }
    if (request != null) {
      sb.append("Request: {}, ");
      params.add(request);
    }
    if (uriVariables != null) {
      sb.append("UriVariables: {}");
      params.add(uriVariables);
    }
    logger.info(sb.toString(), params.toArray());
  }

  public void delete(String url, Object... urlVariables) throws RestClientException {
    logRequest(url, null, null, urlVariables);
    restTemplate.execute(url, HttpMethod.DELETE, null, null, urlVariables);
  }
}
