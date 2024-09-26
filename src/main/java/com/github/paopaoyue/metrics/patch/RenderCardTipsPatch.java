package com.github.paopaoyue.metrics.patch;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.github.paopaoyue.metrics.data.CardPickStatData;
import com.github.paopaoyue.metrics.utility.Reflect;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SpirePatch(
        clz = TipHelper.class,
        method = "renderKeywords"
)
public class RenderCardTipsPatch {

    private static final Logger logger = LogManager.getLogger(RenderCardTipsPatch.class);

    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("Metrics:CardPickTips");

    static final float BODY_TEXT_WIDTH = 280.0F * Settings.scale;
    static final float TIP_DESC_LINE_SPACING = 26.0F * Settings.scale;

    static Method renderTipBoxMethod;

    static {
        try {
            renderTipBoxMethod = TipHelper.class.getDeclaredMethod("renderTipBox", float.class, float.class, SpriteBatch.class, String.class, String.class);
            renderTipBoxMethod.setAccessible(true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @SpirePostfixPatch
    public static void Postfix(float x, float y, SpriteBatch sb) {
        Boolean isCard = Reflect.getStaticPrivate(TipHelper.class,"isCard", Boolean.class);
        if (Boolean.FALSE.equals(isCard)) {
            return;
        }
        AbstractCard card = Reflect.getStaticPrivate(TipHelper.class,"card", AbstractCard.class);
        if (card == null) {
            return;
        }
        CardPickStatData data = CardFieldPatch.pickRate.get(card);
        if (data == null) {
            return;
        }
        try {
            String description = getDescription(data);
            float textHeight = -FontHelper.getSmartHeight(FontHelper.tipBodyFont, description, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - 7.0F * Settings.scale;
            Reflect.setStaticPrivate(TipHelper.class, "textHeight", textHeight);
            renderTipBoxMethod.invoke(null, x, y, sb, getTitle(), getDescription(data));
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static String getTitle() {
        return uiStrings.TEXT[0];
    }

    public static String getDescription(CardPickStatData data) {
        return uiStrings.TEXT[1] + data.samplePlayers +
                uiStrings.TEXT[2] + data.sampleSize +
                uiStrings.TEXT[3] + data.pickRate +
                uiStrings.TEXT[4] + data.firstPickRateF1 +
                uiStrings.TEXT[5] + data.duplicatePickRateF1 +
                uiStrings.TEXT[6] + data.firstPickRateF2 +
                uiStrings.TEXT[7] + data.duplicatePickRateF2 +
                uiStrings.TEXT[8] + data.firstPickRateF3 +
                uiStrings.TEXT[9] + data.duplicatePickRateF3 +
                uiStrings.TEXT[10] + data.generatedTime;
    }

}
