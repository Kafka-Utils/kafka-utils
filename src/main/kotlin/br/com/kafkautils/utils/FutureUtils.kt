package br.com.kafkautils.utils

import java.io.Closeable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.annotation.PreDestroy
import javax.inject.Singleton
import reactor.core.publisher.Mono


@Singleton
class FutureUtils: Closeable {

    private val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    fun <T> toMono(future: Future<T>): Mono<T> {
        return Mono.fromFuture(toCompletableFuture(future))
    }

    @PreDestroy
    override fun close() {
        executorService.shutdown()
    }

    private fun <T> toCompletableFuture(future: Future<T>): CompletableFuture<T> {
        val completableFuture = CompletableFuture<T>()
        tryToComplete(future, completableFuture)
        return completableFuture
    }

    private fun <T> tryToComplete(future: Future<T>, completableFuture: CompletableFuture<T>) {
        if (future.isDone) {
            try {
                completableFuture.complete(future.get())
            } catch (e: ExecutionException) {
                completableFuture.completeExceptionally(e.cause)
            } catch (e: Exception) {
                completableFuture.completeExceptionally(e)
            }
            return
        }
        if (future.isCancelled) {
            completableFuture.cancel(true)
            return
        }
        executorService.schedule({ tryToComplete(future, completableFuture) }, 10, TimeUnit.MILLISECONDS)
    }
}