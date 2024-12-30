package com.github.paopaoyue.metrics_local.data;

public class CardPickData {

    public String metricsID;

    public int drops;
    public int picks;

    public int firstPicksF1;
    public int firstDropsF1;
    public int duplicatePicksF1;
    public int duplicateDropsF1;

    public int firstPicksF2;
    public int firstDropsF2;
    public int duplicatePicksF2;
    public int duplicateDropsF2;

    public int firstPicksF3;
    public int firstDropsF3;
    public int duplicatePicksF3;
    public int duplicateDropsF3;

    public CardPickData(String metricsID) {
        this.metricsID = metricsID;
    }

    public CardPickStatData generateStatData() {
        CardPickStatData statData = new CardPickStatData();
        statData.sampleSize = drops;
        if (drops > 0) {
            statData.pickRate = String.format("%.2f%%", (float) picks / drops * 100);
        }
        if (firstDropsF1 > 0) {
            statData.firstPickRateF1 = String.format("%.2f%%", (float) firstPicksF1 / firstDropsF1 * 100);
        }
        if (firstDropsF2 > 0) {
            statData.firstPickRateF2 = String.format("%.2f%%", (float) firstPicksF2 / firstDropsF2 * 100);
        }
        if (firstDropsF3 > 0) {
            statData.firstPickRateF3 = String.format("%.2f%%", (float) firstPicksF3 / firstDropsF3 * 100);
        }
        if (duplicateDropsF1 > 0) {
            statData.duplicatePickRateF1 = String.format("%.2f%%", (float) duplicatePicksF1 / duplicateDropsF1 * 100);
        }
        if (duplicateDropsF2 > 0) {
            statData.duplicatePickRateF2 = String.format("%.2f%%", (float) duplicatePicksF2 / duplicateDropsF2 * 100);
        }
        if (duplicateDropsF3 > 0) {
            statData.duplicatePickRateF3 = String.format("%.2f%%", (float) duplicatePicksF3 / duplicateDropsF3 * 100);
        }
        return statData;
    }

}
