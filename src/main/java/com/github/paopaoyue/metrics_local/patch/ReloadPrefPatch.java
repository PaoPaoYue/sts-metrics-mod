package com.github.paopaoyue.metrics_local.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.github.paopaoyue.metrics_local.MetricsMod;
import com.megacrit.cardcrawl.core.CardCrawlGame;

@SpirePatch(
        clz = CardCrawlGame.class,
        method = "reloadPrefs"
)
public class ReloadPrefPatch {

    @SpirePostfixPatch
    public static void Postfix() {
        MetricsMod.metricsData.reset();
        MetricsMod.metricsData.LoadRunData();
    }
}
