package com.dbtraining.reconx.service;

public record ReconSummary(long total, long matched, long broken) {
    public static ReconSummary empty() {
        return new ReconSummary(0, 0, 0);
    }

    public static class Builder {
        public long total = 0;
        public long matched = 0;
        public long broken = 0;
    }
}
