package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.ReconResult;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class ReconSummaryCollectorTest {

    @Test
    void testCollector_serialVsParallel_10kItems() {
        List<ReconResult> results = IntStream.range(0, 10000)
                .mapToObj(i -> {
                    if (i % 3 == 0) {
                        return ReconResult.matched("REF" + i);
                    } else {
                        return ReconResult.breakResult("REF" + i, "MISMATCH", "test details");
                    }
                })
                .toList();

        ReconSummary serialSummary = results.stream().collect(new ReconSummaryCollector());
        ReconSummary parallelSummary = results.parallelStream().collect(new ReconSummaryCollector());

        assertThat(serialSummary.total()).isEqualTo(10000);
        assertThat(serialSummary.matched()).isEqualTo(3334);
        assertThat(serialSummary.broken()).isEqualTo(6666);

        // Parallel MUST produce identical outcome
        assertThat(parallelSummary).isEqualTo(serialSummary);
    }
}
