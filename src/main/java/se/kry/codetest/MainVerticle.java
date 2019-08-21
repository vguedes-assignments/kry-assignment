package se.kry.codetest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {

  // private HashMap<String, String> services = new HashMap<>();
  //TODO use this
  // private DBConnector connector;
  private BackgroundPoller poller = new BackgroundPoller();
  private ServiceList serviceList = new ServiceList();

  @Override
  public void start(Future<Void> startFuture) {
    // connector = new DBConnector(vertx);
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    // services.put("https://www.kry.se", "UNKNOWN");
    // serviceList.addService("https://www.kry.se", "LIVI");

    vertx.setPeriodic(1000 * 60, timerId -> poller.pollServices(serviceList.services));
    setRoutes(router);
    vertx
        .createHttpServer()
        .requestHandler(router)
        .listen(8080, result -> {
          if (result.succeeded()) {
            System.out.println("KRY code test service started");
            startFuture.complete();
          } else {
            startFuture.fail(result.cause());
          }
        });
  }

  private void setRoutes(Router router){
    router.route("/*").handler(StaticHandler.create());
    router.get("/service").handler(req -> {
      List<JsonObject> jsonServices = serviceList.services
          .entrySet()
          .stream()
          .map(service ->
              new JsonObject()
                  .put("url", service.getValue().url)
                  .put("name", service.getValue().name)
                  .put("addedAt", service.getValue().addedAt)
                  .put("status", service.getValue().status))
          .collect(Collectors.toList());
      req.response()
          .putHeader("content-type", "application/json")
          .end(new JsonArray(jsonServices).encode());
    });
    router.post("/service").handler(req -> {
      JsonObject jsonBody = req.getBodyAsJson();
      serviceList.addService(jsonBody.getString("url"), jsonBody.getString("name"));
      req.response()
          .putHeader("content-type", "text/plain")
          .end("OK");
    });
    router.delete("/service").handler(req -> {
      JsonObject jsonBody = req.getBodyAsJson();
      serviceList.removeService(jsonBody.getString("url"));
      req.response()
          .putHeader("content-type", "text/plain")
          .end("OK");
    });
  }

}



