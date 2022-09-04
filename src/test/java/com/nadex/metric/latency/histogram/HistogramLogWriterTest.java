package com.nadex.metric.latency.histogram;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramLogWriter;
import org.HdrHistogram.SingleWriterRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HistogramLogWriterTest {

    protected HistogramLogWriter histogramLogWriter;
    protected PrintStream log;
    protected Histogram intervalHistogram = null;
    protected final SingleWriterRecorder recorder =
            new SingleWriterRecorder(
                    1,
                    Long.MAX_VALUE / 2,
                    5
            );
	long min = 10L;
	long max = TimeUnit.SECONDS.toNanos(1);    
	
    @BeforeEach
    public void init() throws FileNotFoundException {
        log = new PrintStream(new FileOutputStream("hlw.hgrm"), false);
        histogramLogWriter = new HistogramLogWriter(log);
//        histogramLogWriter.outputComment("[Logged with " + "1.0" + "]");
        histogramLogWriter.outputLogFormatVersion();
        long currentTimeMillis = System.currentTimeMillis();
		histogramLogWriter.outputStartTime(currentTimeMillis);
        histogramLogWriter.setBaseTime(currentTimeMillis);
        histogramLogWriter.outputLegend();
    }
    
	@Test
	void test() throws InterruptedException {
		for (int i = 0; i < 5; i++) {
	        long startTime = System.currentTimeMillis();
			for (int j = 0; j < 100; j++) {
				long nextLong = ThreadLocalRandom.current().nextLong(min, max);
				recorder.recordValue(nextLong);
				System.out.println("i: " + i + ", j: " + j + ", recorded " + nextLong);
				TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextLong(min, 250));
			}
			intervalHistogram = recorder.getIntervalHistogram();
			intervalHistogram.setStartTimeStamp(startTime);
			intervalHistogram.setEndTimeStamp(startTime);
			System.out.println("i: " + i);
	        if (intervalHistogram.getTotalCount() > 0) {
	        	System.out.println(" outputIntervalHistogram, i: " + i);
	            histogramLogWriter.outputIntervalHistogram(intervalHistogram);
	        }
			max = 2 * max;
		}
	}

}
