package com.github.paopaoyue.metrics.data;

import com.github.paopaoyue.metrics.api.IMetricsCaller;
import com.github.paopaoyue.metrics.proto.MetricsProto;
import com.github.paopaoyue.metrics.utility.ExpiringCache;
import com.github.paopaoyue.rpcmod.RpcApi;
import com.megacrit.cardcrawl.cards.AbstractCard;
import io.github.paopaoyue.mesh.rpc.api.CallOption;
import io.github.paopaoyue.mesh.rpc.util.RespBaseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CardPickStatCache {

    private static final Logger logger = LogManager.getLogger(CardPickStatCache.class);

    private final ExpiringCache<String, CardPickStatData> cache = new ExpiringCache<>(1, TimeUnit.MINUTES);

    public CardPickStatData get(AbstractCard card) {
        return cache.get(getCacheKey(card.cardID, card.upgraded));
    }

    public CardPickStatData get(String cardID, boolean upgraded) {
        return cache.get(getCacheKey(cardID, upgraded));
    }

    public void fetchFromServer(Class<? extends AbstractCard> clazz, String cardID, boolean upgraded) {
        IMetricsCaller metricsCaller = RpcApi.getCaller(IMetricsCaller.class);
        MetricsProto.MGetCardPickStatRequest.Builder requestBuilder = MetricsProto.MGetCardPickStatRequest.newBuilder();
        MetricsProto.CardIdentifier.Builder cardIdentifierBuilder = MetricsProto.CardIdentifier.newBuilder();
        cardIdentifierBuilder.setClasspath(clazz.getName());
        cardIdentifierBuilder.setCardId(cardID);
        cardIdentifierBuilder.setUpgraded(upgraded);
        requestBuilder.addCardIdentifiers(cardIdentifierBuilder.build());
        fetch(requestBuilder.build());
    }

    public void fetchFromServer(List<AbstractCard> cards) {
        MetricsProto.MGetCardPickStatRequest.Builder requestBuilder = MetricsProto.MGetCardPickStatRequest.newBuilder();
        for (AbstractCard card : cards) {
            MetricsProto.CardIdentifier.Builder cardIdentifierBuilder = MetricsProto.CardIdentifier.newBuilder();
            cardIdentifierBuilder.setClasspath(card.getClass().getName());
            cardIdentifierBuilder.setCardId(card.cardID);
            cardIdentifierBuilder.setUpgraded(card.upgraded);
            requestBuilder.addCardIdentifiers(cardIdentifierBuilder.build());
        }
        fetch(requestBuilder.build());
    }

    public void fetch(MetricsProto.MGetCardPickStatRequest request) {
        IMetricsCaller metricsCaller = RpcApi.getCaller(IMetricsCaller.class);
        MetricsProto.MGetCardPickStatResponse response = metricsCaller.mGetCardPickStat(
                request,
                new CallOption().setTimeout(Duration.ofSeconds(3)));
        if (!RespBaseUtil.isOK(response.getBase())) {
            logger.error("Failed to fetch card pick stat from server: {}", response.getBase().getMessage());
        } else {
            for (MetricsProto.CardPickStat stat : response.getCardPickStatsList()) {
                CardPickStatData statData = new CardPickStatData();
                statData.cardId = stat.getCardIdentifier().getCardId();
                if (stat.hasPickRate()) statData.pickRate = String.format("%.2f%%", stat.getPickRate() * 100);
                if (stat.hasFirstPickRateF1()) statData.firstPickRateF1 = String.format("%.2f%%", stat.getFirstPickRateF1() * 100);
                if (stat.hasFirstPickRateF2()) statData.firstPickRateF2 = String.format("%.2f%%", stat.getFirstPickRateF2() * 100);
                if (stat.hasFirstPickRateF3()) statData.firstPickRateF3 = String.format("%.2f%%", stat.getFirstPickRateF3() * 100);
                if (stat.hasDuplicatePickRateF1()) statData.duplicatePickRateF1 = String.format("%.2f%%", stat.getDuplicatePickRateF1() * 100);
                if (stat.hasDuplicatePickRateF2()) statData.duplicatePickRateF2 = String.format("%.2f%%", stat.getDuplicatePickRateF2() * 100);
                if (stat.hasDuplicatePickRateF3()) statData.duplicatePickRateF3 = String.format("%.2f%%", stat.getDuplicatePickRateF3() * 100);
                statData.samplePlayers = stat.getSamplePlayers();
                statData.sampleSize = stat.getSampleSize();
                statData.generatedTime = LocalDateTime.ofEpochSecond(stat.getTimeStamp(), 0, ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now())).toString();
                cache.put(getCacheKey(statData.cardId, stat.getCardIdentifier().getUpgraded()), statData);
            }
        }
    }

    private String getCacheKey(String cardId, boolean upgraded) {
        return cardId + (upgraded ? "+" : "");
    }

}
