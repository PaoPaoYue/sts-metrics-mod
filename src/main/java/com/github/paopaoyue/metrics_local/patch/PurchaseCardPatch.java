package com.github.paopaoyue.metrics_local.patch;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.github.paopaoyue.metrics_local.data.MetricsData;
import com.megacrit.cardcrawl.cards.AbstractCard;
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
        if (Loader.isModLoaded("sts-metrics")) return;
        CardFieldPatch.pickRate.set(hoveredCard, null);
    }

    private static class Locator extends SpireInsertLocator {

        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(MetricsData.class, "addShopPurchaseData");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
        }
    }

}
