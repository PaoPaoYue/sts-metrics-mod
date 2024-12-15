package com.github.paopaoyue.metrics.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.JsonSyntaxException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.stats.RunData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static basemod.BaseMod.gson;

public class MetricsData {

    private static final Logger logger = LogManager.getLogger(MetricsData.class);

    private final Map<String, CardPickData> cardPickDataMap = new HashMap<>();

    public CardPickData getOrCreateCardPickData(String cardID) {
        if (!cardPickDataMap.containsKey(cardID)) {
            cardPickDataMap.put(cardID, new CardPickData(cardID));
        }
        return cardPickDataMap.get(cardID);
    }

    public void reset() {
        this.cardPickDataMap.clear();
    }

    public void LoadRunData() {
        FileHandle[] folders = Gdx.files.local("runs").list();
        int amt = folders.length;
        for (int i = 0; i < amt; i++) {
            FileHandle folder = folders[i];

            if (CardCrawlGame.saveSlot == 0) {
                if (folder.name().contains("0_") || folder.name().contains("1_") || folder.name().contains("2_")) {
                    continue;
                }
            } else {
                if (!folder.name().contains(CardCrawlGame.saveSlot + "_")) {
                    continue;
                }
            }

            FileHandle[] files = folder.list();

            for (int j = 0; j < files.length; j++) {
                FileHandle file = files[j];
                try {
                    RunData data = (RunData) gson.fromJson(file.readString(), RunData.class);
                    if (data != null && data.timestamp == null) {
                        data.timestamp = file.nameWithoutExtension();
                        String exampleDaysSinceUnixStr = "17586";
                        boolean assumeDaysSinceUnix = (data.timestamp
                                .length() == exampleDaysSinceUnixStr.length());
                        if (assumeDaysSinceUnix)
                            try {
                                long days = Long.parseLong(data.timestamp);
                                data.timestamp = Long.toString(days * 86400L);
                            } catch (NumberFormatException e) {
                                logger.info("Run file {} name is could not be parsed into a Timestamp.", file.path());
                                data = null;
                            }
                    }
                    if (data != null)
                        try {
                            AbstractPlayer.PlayerClass.valueOf(data.character_chosen);
                            this.addFromRunData(data);
                        } catch (NullPointerException | IllegalArgumentException e) {
                            logger.debug("Run file {} does not use a real character or mod not enabled: {}", file.path(), data.character_chosen);
                        }
                } catch (JsonSyntaxException ex) {
                    logger.info("Failed to load RunData from JSON file: " + file.path());
                }
            }
        }
    }

    private void addFromRunData(RunData runData) {
        Set<String> pickedCards = new HashSet<>();
        runData.card_choices.forEach(choice -> {
            if (Objects.equals(choice.picked, "SKIP"))
                choice.picked = null;
            this.addCardPickData(choice.picked, choice.not_picked, pickedCards, choice.floor);
            if (choice.picked != null)
                pickedCards.add(choice.picked);
        });
    }

    private void addCardPickData(String picked, List<String> notPicked, Set<String> deck, int floorNum) {
        if (picked != null) {
            if (!cardPickDataMap.containsKey(picked)) {
                cardPickDataMap.put(picked, new CardPickData(picked));
            }
        }
        for (String metricsID : notPicked) {
            if (!cardPickDataMap.containsKey(metricsID)) {
                cardPickDataMap.put(metricsID, new CardPickData(metricsID));
            }
        }

        if (picked != null) {
            CardPickData data = cardPickDataMap.get(picked);
            boolean isDuplicate = deck.contains(picked);
            data.picks++;
            data.drops++;
            if (floorNum < 17) {
                if (!isDuplicate) {
                    data.firstPicksF1++;
                    data.firstDropsF1++;
                } else {
                    data.duplicatePicksF1++;
                    data.duplicateDropsF1++;
                }
            } else if (floorNum < 34) {
                if (!isDuplicate) {
                    data.firstPicksF2++;
                    data.firstDropsF2++;
                } else {
                    data.duplicatePicksF2++;
                    data.duplicateDropsF2++;
                }
            } else {
                if (!isDuplicate) {
                    data.firstPicksF3++;
                    data.firstDropsF3++;
                } else {
                    data.duplicatePicksF3++;
                    data.duplicateDropsF3++;
                }
            }
        }

        for (String metricsID : notPicked) {
            CardPickData data = cardPickDataMap.get(metricsID);
            boolean isDuplicate = deck.contains(metricsID);
            data.drops++;
            if (floorNum < 17) {
                if (!isDuplicate) {
                    data.firstDropsF1++;
                } else {
                    data.duplicateDropsF1++;
                }
            } else if (floorNum < 34) {
                if (!isDuplicate) {
                    data.firstDropsF2++;
                } else {
                    data.duplicateDropsF2++;
                }
            } else {
                if (!isDuplicate) {
                    data.firstDropsF3++;
                } else {
                    data.duplicateDropsF3++;
                }
            }
        }

    }

}
