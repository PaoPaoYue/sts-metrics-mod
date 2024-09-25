package com.github.paopaoyue.metrics.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.github.paopaoyue.metrics.MetricsMod;
import com.github.paopaoyue.metrics.data.CardPickData;
import com.github.paopaoyue.metrics.utility.Async;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "nextRoomTransition",
        paramtypez = {SaveFile.class}
)
public class NextRoomTransPatch {

    @SpirePrefixPatch
    public static void Prefix(AbstractDungeon __instance) {
        for (RewardItem reward : AbstractDungeon.combatRewardScreen.rewards) {
            if (reward.type == RewardItem.RewardType.CARD) {
                CardPickData cardPickData = new CardPickData();
                for (AbstractCard card : reward.cards) {
                    cardPickData.addNotPickedCard(card);
                }
                MetricsMod.metricsData.addCardPickData(cardPickData);
            }
        }
    }

    @SpirePostfixPatch
    public static void Postfix(AbstractDungeon __instance) {
        Async.run(() -> {
            MetricsMod.metricsData.flushToServer();
        });

        MetricsMod.metricsData.updateFloor();
    }
}
