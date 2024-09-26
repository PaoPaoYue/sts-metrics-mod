package com.github.paopaoyue.metrics.patch;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.github.paopaoyue.metrics.MetricsMod;
import com.github.paopaoyue.metrics.data.CardPickStatData;
import com.github.paopaoyue.metrics.utility.Async;
import com.github.paopaoyue.metrics.utility.Reflect;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class SingleCardPopupPatch {

    private static final Logger logger = LogManager.getLogger(SingleCardPopupPatch.class.getName());

    private static CardPickStatData cardPickStatData;

    private static final long DEBOUNCE_DELAY_MS = 300;


    private static void setCardPickStatData(SingleCardViewPopup popup) {
        Async.run(() -> {
            try {
                Thread.sleep(DEBOUNCE_DELAY_MS);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }

            AbstractCard card = Reflect.getPrivate(SingleCardViewPopup.class, popup, "card", AbstractCard.class);
            Boolean upgraded = Reflect.getStaticPrivate(SingleCardViewPopup.class, "isViewingUpgrade", Boolean.class);
            if (card == null || upgraded == null) {
                logger.error("Failed to get card or upgraded from SingleCardViewPopup");
                return;
            }
            cardPickStatData = MetricsMod.cardPickStatCache.get(card.cardID, upgraded);
            if (cardPickStatData == null) {

                AbstractCard finalCard = Reflect.getPrivate(SingleCardViewPopup.class, popup, "card", AbstractCard.class);
                if (finalCard == null || finalCard != card) {
                    return;
                }

                MetricsMod.cardPickStatCache.fetchFromServer(card.getClass(), card.cardID, upgraded);
                cardPickStatData = MetricsMod.cardPickStatCache.get(card);
            }
        });
    }

    @SpirePatch2(
            clz = SingleCardViewPopup.class,
            method = "open",
            paramtypez = {AbstractCard.class}
    )
    public static class OpenSingleCardPopupPatch {

        public static void Postfix(SingleCardViewPopup __instance) {
            cardPickStatData = null;
            setCardPickStatData(__instance);
        }

    }

    @SpirePatch2(
            clz = SingleCardViewPopup.class,
            method = "open",
            paramtypez = {AbstractCard.class, CardGroup.class}
    )
    public static class OpenSingleCardPopupPatch2 {

        @SpirePostfixPatch
        public static void Postfix(SingleCardViewPopup __instance) {
            cardPickStatData = null;
            setCardPickStatData(__instance);
        }

    }

    @SpirePatch2(
            clz = SingleCardViewPopup.class,
            method = "updateUpgradePreview"
    )
    public static class UpdateUpgradePreviewPatch {

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(SingleCardViewPopup __instance) {
            logger.info("UpdateUpgradePreviewPatch");
            cardPickStatData = null;
            setCardPickStatData(__instance);
        }

        private static class Locator extends SpireInsertLocator {

            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(SingleCardViewPopup.class, "isViewingUpgrade");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }

    }

    @SpirePatch2(
            clz = SingleCardViewPopup.class,
            method = "renderCost"
    )
    public static class PopupRenderCostPatch {

        @SpirePostfixPatch
        public static void Postfix(SingleCardViewPopup __instance, SpriteBatch sb) {
            if (cardPickStatData != null) {
                FontHelper.renderFont(sb, FontHelper.SCP_cardEnergyFont, cardPickStatData.pickRate, Settings.WIDTH / 2.0f + 80.0f * Settings.scale, Settings.HEIGHT / 2.0f - 360.0f * Settings.scale, Settings.CREAM_COLOR);
            }
        }

    }

    @SpirePatch2(
            clz = SingleCardViewPopup.class,
            method = "renderTips"
    )
    public static class PopupRenderTipsPatch {

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(SingleCardViewPopup __instance, SpriteBatch sb) {
            if (cardPickStatData != null) {
                try {
                    String title = RenderCardTipsPatch.getTitle();
                    String description = RenderCardTipsPatch.getDescription(cardPickStatData);
                    float textHeight = -FontHelper.getSmartHeight(FontHelper.tipBodyFont, description, RenderCardTipsPatch.BODY_TEXT_WIDTH, RenderCardTipsPatch.TIP_DESC_LINE_SPACING) - 7.0F * Settings.scale;
                    Reflect.setStaticPrivate(TipHelper.class, "textHeight", textHeight);
                    RenderCardTipsPatch.renderTipBoxMethod.invoke(null, Settings.WIDTH / 2f - 660.0f * Settings.scale, Settings.HEIGHT / 2f - 140f * Settings.yScale, sb, title, description);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        private static class Locator extends SpireInsertLocator {

            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "isEmpty");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }

    }

}
