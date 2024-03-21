
import io.vertx.pgclient.PgBuilder
import io.vertx.pgclient.PgConnectOptions
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.PoolOptions
import io.vertx.sqlclient.SqlClient

var connectOptions: PgConnectOptions = PgConnectOptions() //MySQLConnectOptions() for MySQL
    .setPort(5432)
    .setHost("localhost")
    .setDatabase("compose_desktop")
    .setUser("postgres")
    .setPassword("E=mc2")

// Pool options
var poolOptions: PoolOptions = PoolOptions()
    .setMaxSize(5)

// Create the pooled client
// Use client if you want to execute a single query
var client: SqlClient = PgBuilder //MySQLBuilder for MySQL
    .client()
    .with(poolOptions)
    .connectingTo(connectOptions)
    .build()

// Create the pool
// Use pool if you are executing transactions
var pool: Pool = PgBuilder //MySQLBuilder for MySQL
    .pool()
    .with(poolOptions)
    .connectingTo(connectOptions)
    .build()

