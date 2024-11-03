package com.github.paopaoyue.metrics.patch;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.github.paopaoyue.metrics.MetricsMod;
import com.github.paopaoyue.metrics.data.CardPickData;
import com.github.paopaoyue.metrics.data.MetricsData;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.shop.ShopScreen;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        clz = ShopScreen.class,
        method = "purchaseCard"
)
public class PurchaseCardPatch {

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void Prefix(ShopScreen __instance, AbstractCard hoveredCard) {
        CardFieldPatch.pickRate.set(hoveredCard, null);
    }

    private static class Locator extends SpireInsertLocator {

        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(MetricsData.class, "addShopPurchaseData");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
        }
    }

}
