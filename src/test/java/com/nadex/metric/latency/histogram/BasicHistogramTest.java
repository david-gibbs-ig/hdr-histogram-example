package com.nadex.metric.latency.histogram;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.HdrHistogram.Histogram;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BasicHistogramTest {

	long min = 10L;
	long max = TimeUnit.SECONDS.toNanos(10);
	Histogram histogram = new Histogram(max, 3);

	@BeforeEach
	public void init() {
		histogram.reset();
	}

	@Test
	public void test() throws InterruptedException {
		for (int i = 0; i < 100; ++i) {
			recordRandomLatency();
			TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextLong(min, 250));
		}
	}

	@AfterEach
	public void after() throws FileNotFoundException {
		PrintStream log = new PrintStream(new FileOutputStream("test.hgrm"), false);
		histogram.outputPercentileDistribution(log, 1000.0);
	}

	public void recordRandomLatency() {
		this.histogram.recordValue(ThreadLocalRandom.current().nextLong(min, max));
	}

}
