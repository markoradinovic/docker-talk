package rs.codecentric;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class DbVerticle extends AbstractVerticle {

	private static final Logger LOG = LoggerFactory.getLogger(DbVerticle.class);

	private static String url = "192.168.99.100:5432/hm-demo";
	private static String user = "pgadmin";
	private static String password = "pgadmin";

	static {
		System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
		SLF4JBridgeHandler.install();

		if (System.getenv("PG_URL") != null) {
			url = System.getenv("PG_URL");
			LOG.info("Found PG_URL environment variable: " + url);
		}
		if (System.getenv("PG_USER") != null) {
			user = System.getenv("PG_USER");
			LOG.info("Found PG_USER environment variable: " + user);
		}
		if (System.getenv("PG_PASS") != null) {
			password = System.getenv("PG_PASS");
			LOG.info("Found PG_PASS environment variable: " + password);
		}
	}

	private DBI db;
	private JDBCClient client;

	@Override
	public void start() throws Exception {
		db = new DBI("jdbc:postgresql://" + url, user, password);
		client = JDBCClient.createShared(vertx,
				new JsonObject().put("url", "jdbc:postgresql://" + url).put("user", user).put("password", password)
						.put("driver_class", "org.postgresql.Driver").put("max_pool_size", 10));

		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.get("/").handler(this::handleRootRequest);
		router.get("/db").handler(this::handleDb);
		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
	}

	private void handleRootRequest(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		JsonObject result = buildResult();
		response.putHeader("content-type", "application/json").end(result.encodePrettily());
		LOG.debug("Handle ROOT request.");
	}
	
	private void handleDb(RoutingContext routingContext) {
		client.getConnection(connection -> {
			if (connection.failed()) {
				LOG.error(connection.cause().getMessage(), connection.cause());
				return;
			}
			SQLConnection sqlConnection = connection.result();
			sqlConnection.query("select version();", resultHandler -> {
				if (resultHandler.failed()) {
					LOG.error(resultHandler.cause().getMessage(), resultHandler.cause());
					return;
				}
				JsonObject result = resultHandler.result().getRows().get(0);
				HttpServerResponse response = routingContext.response();
				response.putHeader("content-type", "application/json").end(result.encodePrettily());
				sqlConnection.close();
			});
		});
	}

	private JsonObject buildResult() {
		try (Handle handle = db.open()) {
			return new JsonObject(handle.createQuery("select version()").first());
		}
	}

}
