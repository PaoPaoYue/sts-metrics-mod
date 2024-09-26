package com.github.paopaoyue.metrics.data;

import com.github.paopaoyue.metrics.api.IMetricsCaller;
import com.github.paopaoyue.metrics.proto.MetricsProto;
import com.github.paopaoyue.rpcmod.RpcApi;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import io.github.paopaoyue.mesh.rpc.api.CallOption;
import io.github.paopaoyue.mesh.rpc.util.RespBaseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricsData {

    private static final Logger logger = LogManager.getLogger(MetricsData.class);

    private static final Map<Settings.GameLanguage, String> languageToRegionMap = new HashMap<>();

    static {
        languageToRegionMap.put(Settings.GameLanguage.ENG, "en");
        languageToRegionMap.put(Settings.GameLanguage.DUT, "nl");
        languageToRegionMap.put(Settings.GameLanguage.EPO, "eo");
        languageToRegionMap.put(Settings.GameLanguage.PTB, "pt-BR");
        languageToRegionMap.put(Settings.GameLanguage.ZHS, "cn");
        languageToRegionMap.put(Settings.GameLanguage.ZHT, "tw");
        languageToRegionMap.put(Settings.GameLanguage.FIN, "fi");
        languageToRegionMap.put(Settings.GameLanguage.FRA, "fr");
        languageToRegionMap.put(Settings.GameLanguage.DEU, "de");
        languageToRegionMap.put(Settings.GameLanguage.GRE, "el");
        languageToRegionMap.put(Settings.GameLanguage.IND, "id");
        languageToRegionMap.put(Settings.GameLanguage.ITA, "it");
        languageToRegionMap.put(Settings.GameLanguage.JPN, "ja");
        languageToRegionMap.put(Settings.GameLanguage.KOR, "ko");
        languageToRegionMap.put(Settings.GameLanguage.NOR, "no");
        languageToRegionMap.put(Settings.GameLanguage.POL, "pl");
        languageToRegionMap.put(Settings.GameLanguage.RUS, "ru");
        languageToRegionMap.put(Settings.GameLanguage.SPA, "es");
        languageToRegionMap.put(Settings.GameLanguage.SRP, "sr");
        languageToRegionMap.put(Settings.GameLanguage.SRB, "sr");
        languageToRegionMap.put(Settings.GameLanguage.THA, "th");
        languageToRegionMap.put(Settings.GameLanguage.TUR, "tr");
        languageToRegionMap.put(Settings.GameLanguage.UKR, "uk");
        languageToRegionMap.put(Settings.GameLanguage.VIE, "vi");
        languageToRegionMap.put(Settings.GameLanguage.WWW, "www");
    }

    // General data
    private String playerName;
    private String characterName;
    private int act;
    private int floor;
    private int ascensionLevel;
    private String region;

    // Card pick data
    private final List<CardPickData> cardPickList = new ArrayList<>();

    public void reset() {
        this.playerName = CardCrawlGame.playerName;
        this.characterName = AbstractDungeon.player.name;
        this.region = languageToRegionMap.getOrDefault(Settings.language, "en");
        this.act = AbstractDungeon.actNum;
        this.floor = AbstractDungeon.floorNum;
        this.ascensionLevel = AbstractDungeon.ascensionLevel;

        cardPickList.clear();
    }

    public void updateFloor() {
        this.floor = AbstractDungeon.floorNum;
        this.act = AbstractDungeon.actNum;
        this.ascensionLevel = AbstractDungeon.ascensionLevel;
    }

    public void addCardPickData(CardPickData cardPickData) {
        cardPickList.add(cardPickData);
    }

    public void flushToServer() {
        IMetricsCaller metricsCaller = RpcApi.getCaller(IMetricsCaller.class);
        for (CardPickData cardPickData : cardPickList) {
            MetricsProto.MCreateCardPickRequest.Builder builder = MetricsProto.MCreateCardPickRequest.newBuilder()
                            .setLevel(this.floor)
                            .setAscension(this.ascensionLevel)
                            .setUserName(this.playerName)
                            .setCharacterName(this.characterName)
                            .setRegion(this.region)
                            .setTimestamp(System.currentTimeMillis() / 1000);
            for (int i = 0; i < cardPickData.pickedCards.size(); i++) {
                AbstractCard card = cardPickData.pickedCards.get(i);
                builder.addPicked(MetricsProto.CardPick.newBuilder()
                        .setCardIdentifier(
                                MetricsProto.CardIdentifier.newBuilder()
                                        .setClasspath(card.getClass().getName())
                                        .setCardId(card.cardID)
                                        .setUpgraded(card.upgraded)
                                        .build()
                        )
                        .setCardType(card.type.name())
                        .setCardRarity(card.rarity.name())
                        .setCardCost(card.cost)
                        .setNumInDeck(cardPickData.pickedCardsNumInDeck.get(i))
                        .build());
            }
            for (int i = 0; i < cardPickData.notPickedCards.size(); i++) {
                AbstractCard card = cardPickData.notPickedCards.get(i);
                builder.addUnpicked(MetricsProto.CardPick.newBuilder()
                        .setCardIdentifier(
                                MetricsProto.CardIdentifier.newBuilder()
                                        .setClasspath(card.getClass().getName())
                                        .setCardId(card.cardID)
                                        .setUpgraded(card.upgraded)
                                        .build()
                        )
                        .setCardType(card.type.name())
                        .setCardRarity(card.rarity.name())
                        .setCardCost(card.cost)
                        .setNumInDeck(cardPickData.notPickedCardsNumInDeck.get(i))
                        .build());
            }
            MetricsProto.MCreateCardPickRequest request = builder.build();
            logger.info("MCreateCardPick request: {}", request);
            MetricsProto.MCreateCardPickResponse response = metricsCaller.mCreateCardPick(
                    request,
                    new CallOption().setTimeout(Duration.ofSeconds(3))
            );
            if (!RespBaseUtil.isOK(response.getBase())) {
                logger.error("Failed to flush card pick data to server: {}", response.getBase().getMessage());
            } else {
                logger.info("MCreateCardPick response: {}", response);
            }
        }
        this.cardPickList.clear();
    }

}
