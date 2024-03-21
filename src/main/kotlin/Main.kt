import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.Tuple


@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Column(){
            Text(text = "Kotlin Compose Desktop Database Connection with Vertx Driver")
            Button(onClick = {
                fetchUser()
            }) {
                Text("Fetch User")
            }
            Button(onClick = {
                fetchPreparedUser()
            }) {
                Text("Fetch Prepared User")
            }
            Button(onClick = {
                insertPreparedUser()
            }) {
                Text("Insert Prepared User")
            }
            Button(onClick = {
                insertBatchUser()
            }) {
                Text("Insert Batch User")
            }
            Button(onClick = {
                deleteWithReturn()
            }) {
                Text("Delete with return")
            }
        }
    }
}

fun fetchUser() {
    client
        .query("SELECT username, email FROM users WHERE username='jrdemadara'")
        .execute()
        .onComplete { ar ->
            if (ar.succeeded()) {
                val result: RowSet<Row> = ar.result()
                result.forEach {
                    println(it.getValue("username"))
                    println(it.getValue("email"))
                }

            } else {
                println("Failure: " + ar.cause().message)
            }
        }
}

fun fetchPreparedUser() {
    client
        .preparedQuery("SELECT username, email FROM users WHERE username=$1")
        .execute(Tuple.of("jrdemadara"))
        .onComplete { ar ->
            if (ar.succeeded()) {
                val result: RowSet<Row> = ar.result()
                result.forEach {
                    println(it.getValue("username"))
                    println(it.getValue("email"))
                }

            } else {
                println("Failure: " + ar.cause().message)
            }
        }
}

fun insertPreparedUser(){
    client
        .preparedQuery("INSERT INTO users (username, email) VALUES ($1, $2)")
        .execute(Tuple.of("jrdemadara", "jrdemadara@protonmail.com"))
        .onComplete { ar ->
            if (ar.succeeded()) {
                val rows = ar.result()
                println(rows.rowCount())
            } else {
                System.out.println("Failure: " + ar.cause().message)
            }
        }
}

fun insertBatchUser(){
    val batch: MutableList<Tuple> = ArrayList()
    batch.add(Tuple.of("jrdemadara", "jrdemadara@protonmail.com"))
    batch.add(Tuple.of("heroditon", "heroditon@mail.com"))

// Execute the prepared batch
    client
        .preparedQuery("INSERT INTO users (username, email) VALUES ($1, $2)")
        .executeBatch(batch)
        .onComplete { res ->
            if (res.succeeded()) {
                // Process rows
                val rows = res.result()
                println(rows.rowCount())
            } else {
                System.out.println("Batch failed " + res.cause())
            }
        }
}

fun deleteWithReturn(){
    client
        .query("DELETE FROM users RETURNING username")
        .execute()
        .onSuccess { rows ->
            for (row in rows) {
                println("deleted username: " + row.getString("username"))
            }
        }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
