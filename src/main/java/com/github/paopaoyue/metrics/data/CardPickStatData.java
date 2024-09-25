package com.github.paopaoyue.metrics.data;

import java.time.LocalDateTime;

public class CardPickStatData {
    public String cardId;

    public String pickRate = "--%";

    public String firstPickRateF1 = "--%";
    public String firstPickRateF2 = "--%";
    public String firstPickRateF3 = "--%";
    public String duplicatePickRateF1 = "--%";
    public String duplicatePickRateF2 = "--%";
    public String duplicatePickRateF3 = "--%";

    public int samplePlayers;
    public long sampleSize;
    public String generatedTime;
}
