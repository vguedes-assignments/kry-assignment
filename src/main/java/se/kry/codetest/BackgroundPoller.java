package se.kry.codetest;

import io.vertx.core.Future;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.net.HttpURLConnection;
import java.net.URL;

public class BackgroundPoller {


  public Future<List<String>> pollServices(Map<String, Service> services) {
    System.out.println(services);
    services.forEach((key, value) -> {
      try {
        HttpURLConnection httpConnection = (HttpURLConnection) new URL(key).openConnection();
        httpConnection.setRequestMethod("GET"); 
        httpConnection.setConnectTimeout(5000);
        int responseCode = httpConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
          services.get(key).setStatus(Service.Status.OK);
        } else {
          services.get(key).setStatus(Service.Status.FAIL);
        }
      } catch (Exception e) {
        Future.failedFuture(e.getMessage());
      }
    });
    return Future.succeededFuture();
  }
}
