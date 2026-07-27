package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.ReconResult;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class ReconSummaryCollector 
        implements Collector<ReconResult, ReconSummary.Builder, ReconSummary> {

    @Override
    public Supplier<ReconSummary.Builder> supplier() {
        return ReconSummary.Builder::new;
    }

    @Override
    public BiConsumer<ReconSummary.Builder, ReconResult> accumulator() {
        return (builder, result) -> {
            builder.total++;
            if (result.status() == ReconResult.Status.MATCHED) {
                builder.matched++;
            } else if (result.status() == ReconResult.Status.BREAK) {
                builder.broken++;
            }
        };
    }

    @Override
    public BinaryOperator<ReconSummary.Builder> combiner() {
        return (left, right) -> {
            ReconSummary.Builder out = new ReconSummary.Builder();
            out.total = left.total + right.total;
            out.matched = left.matched + right.matched;
            out.broken = left.broken + right.broken;
            return out;
        };
    }

    @Override
    public Function<ReconSummary.Builder, ReconSummary> finisher() {
        return b -> new ReconSummary(b.total, b.matched, b.broken);
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.singleton(Characteristics.UNORDERED);
    }
}
