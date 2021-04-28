package br.com.kafkautils.utils

import reactor.core.publisher.Mono
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.Callable
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class FutureUtilsSpec extends Specification {

	@Shared
	ExecutorService executorService = Executors.newFixedThreadPool(1)

	FutureUtils futureUtils

	void setup() {
		futureUtils = new FutureUtils()
	}

	void clenaup() {
		futureUtils.close()
	}

	void clenaupSpec() {
		executorService.shutdown()
	}

	void "convert completed future to mono"() {
		given:
		Integer ret = 10
		Future<Integer> future = executorService.submit({
			return ret
		} as Callable<Integer>) as Future<Integer>
		Thread.sleep(10)
		when:
		Mono<Integer> mono = futureUtils.toMono(future)
		Integer result = mono.block()
		then:
		result == ret
	}

	void "convert future to mono"() {
		given:
		Integer ret = 10
		Future<Integer> future = executorService.submit({
			Thread.sleep(200)
			return ret
		} as Callable<Integer>) as Future<Integer>
		Thread.sleep(10)
		when:
		Mono<Integer> mono = futureUtils.toMono(future)
		Integer result = mono.block()
		then:
		result == ret
	}

	void "convert Cancellation future to mono"() {
		given:
		Integer ret = 10
		Future<Integer> future = executorService.submit({
			Thread.sleep(10)
			return ret
		} as Callable<Integer>) as Future<Integer>
		future.cancel(true)
		when:
		Mono<Integer> mono = futureUtils.toMono(future)
		mono.block()
		then:
		thrown(CancellationException)
	}

	void "convert Interrupted future to mono"() {
		given:
		Integer ret = 10
		ExecutorService executorService = Executors.newFixedThreadPool(1)
		Future<Integer> future = executorService.submit({
			Thread.sleep(10)
			return ret
		} as Callable<Integer>) as Future<Integer>
		executorService.shutdownNow()
		when:
		Mono<Integer> mono = futureUtils.toMono(future)
		mono.block()
		then:
		thrown(RuntimeException)
	}

	void "convert error future to mono"() {
		given:
		ExecutorService executorService = Executors.newFixedThreadPool(1)
		Future<Integer> future = executorService.submit({
			throw new IllegalStateException("ops")
		} as Callable<Integer>) as Future<Integer>
		executorService.shutdownNow()
		when:
		Mono<Integer> mono = futureUtils.toMono(future)
		mono.block()
		then:
		thrown(RuntimeException)
	}
}
