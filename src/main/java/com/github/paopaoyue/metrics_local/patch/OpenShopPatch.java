package com.github.paopaoyue.metrics_local.patch;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.github.paopaoyue.metrics_local.MetricsMod;
import com.github.paopaoyue.metrics_local.data.CardPickData;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.shop.ShopScreen;

@SpirePatch(
        clz = ShopScreen.class,
        method = "open"
)
public class OpenShopPatch {

    @SpirePostfixPatch
    public static void Postfix(ShopScreen __instance) {
        if (MetricsMod.isDisplayDisabled()) return;
        for (AbstractCard card : __instance.coloredCards) {
            CardPickData cardPickData = MetricsMod.metricsData.getOrCreateCardPickData(card.getMetricID());
            CardFieldPatch.pickRate.set(card, cardPickData.generateStatData());
        }

    }
}
