package com.github.paopaoyue.metrics.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.github.paopaoyue.metrics.MetricsMod;
import com.github.paopaoyue.metrics.data.CardPickData;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.shop.ShopScreen;

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
