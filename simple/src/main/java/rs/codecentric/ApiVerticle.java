package rs.codecentric;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ApiVerticle extends AbstractVerticle {

	static {
		SLF4JBridgeHandler.install();
		System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
	}

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Override
	public void start() throws Exception {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.get("/").handler(this::handleRootRequest);
		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
	}

	private void handleRootRequest(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		JsonObject result = buildResult();
		response.putHeader("content-type", "application/json").end(result.encodePrettily());
		LOG.debug("Handle ROOT request.");
	}

	private JsonObject buildResult() {
		return new JsonObject().put("hostname", getHostname()).put("ip", getIpAddresses()).put("envrionment", getEnv());
	}

	private String getHostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "Unknown hostname";
		}
	}

	private String getIpAddresses() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return "Unknown IP";
		}
	}

	private JsonObject getEnv() {
		return new JsonObject(new HashMap<>(System.getenv()));
	}
}
