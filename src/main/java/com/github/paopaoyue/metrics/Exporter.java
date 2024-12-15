package com.github.paopaoyue.metrics;

import basemod.ModLabel;
import com.github.paopaoyue.metrics.data.CardPickData;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Exporter {

    private ArrayList<String> CharacterList;
    private Map<String, AbstractCard.CardColor> CharacterColorMap;
    private String SelectedCharacter;
    private ModLabel modLabel;

    public Exporter() {
        this.CharacterList = new ArrayList<>();
        this.CharacterColorMap = new HashMap<>();
        this.SelectedCharacter = "";
    }

    public void initialize() {
        for (AbstractPlayer character : CardCrawlGame.characterManager.getAllCharacters()) {
            this.CharacterColorMap.put(character.getTitle(character.chosenClass), character.getCardColor());
        }
        this.CharacterList.addAll(this.CharacterColorMap.keySet());
        this.setSelectedCharacter(getCharacterList().get(0));
    }

    public ArrayList<String> getCharacterList() {
        return this.CharacterList;
    }

    public void setSelectedCharacter(String character) {
        this.SelectedCharacter = character;
    }

    public void setExportResult(ModLabel modLabel) {
        this.modLabel = modLabel;
    }

    public void export() {
        if (this.SelectedCharacter.isEmpty()) {
            modLabel.text = "Character not selected";
            return;
        }
        if (!this.getCharacterList().contains(this.SelectedCharacter)) {
            modLabel.text = "Character not found";
            return;
        }
        AbstractCard.CardColor color = this.CharacterColorMap.get(this.SelectedCharacter);
        List<AbstractCard> cardList = getCardList(color);
        modLabel.text = "";
        String folderName = "export";
        String fileName = generateFileNameWithTimestamp(this.SelectedCharacter + "_" + CardCrawlGame.playerName, "csv");
        Path folderPath = Paths.get(folderName);
        if (!Files.exists(folderPath)) {
            try {
                Files.createDirectories(folderPath);
            } catch (IOException e) {
                MetricsMod.logger.error("Failed to create folder", e);
                modLabel.text = "Failed to create folder";
                return;
            }
        }
        Path fullPath = Paths.get(folderName, fileName);
        try (FileWriter writer = new FileWriter(fullPath.toFile())) {
            writer.append(getHeader());
            for (AbstractCard card : cardList) {
                writer.append(getRow(card));
            }
            MetricsMod.logger.info("Exported to {}", fullPath.toAbsolutePath());
            modLabel.text = "Exported to " + fullPath.toAbsolutePath();
        } catch (IOException e) {
            MetricsMod.logger.error("Failed to write file", e);
        }

    }

    private List<AbstractCard> getCardList(AbstractCard.CardColor color) {
        List<AbstractCard> cardList = new ArrayList<>();
        for (AbstractCard card : CardLibrary.getAllCards()) {
            if (card.color == color && card.rarity != AbstractCard.CardRarity.BASIC && card.rarity != AbstractCard.CardRarity.SPECIAL) {
                cardList.add(card);
            }
        }
        cardList.sort(Comparator.comparingInt(c -> c.rarity.ordinal()));
        return cardList;
    }

    private static String getRow(AbstractCard card) {
        CardPickData cardPickData = MetricsMod.metricsData.getOrCreateCardPickData(card.cardID);
        return String.join(",", Arrays.asList(
                card.cardID,
                card.name,
                card.type.toString(),
                card.rarity.toString(),
                Integer.toString(card.cost),
                cardPickData.drops > 0 ? String.format("%.2f", (float) cardPickData.picks / cardPickData.drops * 100) : "0",
                Integer.toString(cardPickData.drops),
                cardPickData.firstDropsF1 > 0 ? String.format("%.2f", (float) cardPickData.firstPicksF1 / cardPickData.firstDropsF1 * 100) : "0",
                Integer.toString(cardPickData.firstDropsF1),
                cardPickData.duplicateDropsF1 > 0 ? String.format("%.2f", (float) cardPickData.duplicatePicksF1 / cardPickData.duplicateDropsF1 * 100) : "0",
                Integer.toString(cardPickData.duplicateDropsF1),
                cardPickData.firstDropsF2 > 0 ? String.format("%.2f", (float) cardPickData.firstPicksF2 / cardPickData.firstDropsF2 * 100) : "0",
                Integer.toString(cardPickData.firstDropsF2),
                cardPickData.duplicateDropsF2 > 0 ? String.format("%.2f", (float) cardPickData.duplicatePicksF2 / cardPickData.duplicateDropsF2 * 100) : "0",
                Integer.toString(cardPickData.duplicateDropsF2),
                cardPickData.firstDropsF3 > 0 ? String.format("%.2f", (float) cardPickData.firstPicksF3 / cardPickData.firstDropsF3 * 100) : "0",
                Integer.toString(cardPickData.firstDropsF3),
                cardPickData.duplicateDropsF3 > 0 ? String.format("%.2f", (float) cardPickData.duplicatePicksF3 / cardPickData.duplicateDropsF3 * 100) : "0",
                Integer.toString(cardPickData.duplicateDropsF3)
        )) + "\n";
    }

    private static String getHeader() {
        return String.join(",", Arrays.asList(
                "Card ID",
                "Card Name",
                "Card Type",
                "Card Rarity",
                "Card Cost",
                "Overall Pick Rate",
                "Sample Size",
                "Act 1 First Pick Rate(%)",
                "Act 1 First Pick Sample Size",
                "Act 1 Duplicate Pick Rate(%)",
                "Act 1 Duplicate Pick Sample Size",
                "Act 2 First Pick Rate(%)",
                "Act 2 First Pick Sample Size",
                "Act 2 Duplicate Pick Rate(%)",
                "Act 2 Duplicate Pick Sample Size",
                "Act 3+ First Pick Rate(%)",
                "Act 3+ First Pick Sample Size",
                "Act 3+ Duplicate Pick Rate(%)",
                "Act 3+ Duplicate Pick Sample Size"
        )) + "\n";
    }

    private static String generateFileNameWithTimestamp(String baseName, String extension) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        return baseName + "_" + timestamp + "." + extension;
    }

    private static void createFolderIfNotExists(String folderName) throws IOException {
        Path folderPath = Paths.get(folderName);
        if (!Files.exists(folderPath)) {
            Files.createDirectories(folderPath);
        }
    }

}
