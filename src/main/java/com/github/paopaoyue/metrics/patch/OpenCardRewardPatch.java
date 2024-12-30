package com.github.paopaoyue.metrics.patch;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.github.paopaoyue.metrics.MetricsMod;
import com.github.paopaoyue.metrics.data.CardPickData;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.screens.CardRewardScreen;

@SpirePatch(
        clz = CardRewardScreen.class,
        method = "open"
)
public class OpenCardRewardPatch {

    @SpirePostfixPatch
    public static void Postfix(CardRewardScreen __instance) {
        if (MetricsMod.isDisplayDisabled()) return;
        if (ModHelper.isModEnabled("sts-metrics")) return;
        for (AbstractCard card : __instance.rewardGroup) {
            CardPickData cardPickData = MetricsMod.metricsData.getOrCreateCardPickData(card.getMetricID());
            CardFieldPatch.pickRate.set(card, cardPickData.generateStatData());
        }
    }

}
