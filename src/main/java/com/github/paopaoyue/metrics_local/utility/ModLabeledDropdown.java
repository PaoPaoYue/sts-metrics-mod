package com.github.paopaoyue.metrics_local.utility;

import basemod.patches.com.megacrit.cardcrawl.helpers.TipHelper.HeaderlessTip;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import basemod.ModPanel;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import basemod.ModLabel;
import com.megacrit.cardcrawl.screens.options.DropdownMenuListener;
import basemod.IUIElement;

public class ModLabeledDropdown implements IUIElement, DropdownMenuListener
{
    private static final float TEXT_X_OFFSET = 0.0f;
    private static final float TEXT_Y_OFFSET = 8.0f;
    public ModLabel text;
    public String tooltip;
    public DropdownMenu dropdownMenu;
    private float xPos;
    private float yPos;
    public ModPanel parent;
    private final BiConsumer<Integer, String> onChangeSeletionTo;
    private final Consumer<DropdownMenu> dropdownMenuUpdate;

    public ModLabeledDropdown(final String labelText, final String tooltipText, final float xPos, final float yPos, final Color color, final BitmapFont font, final ModPanel p, final ArrayList<String> options, final Consumer<ModLabel> labelUpdate, final Consumer<DropdownMenu> dropdownMenuUpdate, final BiConsumer<Integer, String> onChangeSeletionTo) {
        this.dropdownMenu = new DropdownMenu((DropdownMenuListener)this, (ArrayList)options, font, color);
        this.xPos = xPos;
        this.yPos = yPos;
        this.tooltip = tooltipText;
        this.text = new ModLabel(labelText, xPos + 0.0f, yPos + 8.0f, color, font, p, (Consumer)labelUpdate);
        this.parent = p;
        this.onChangeSeletionTo = onChangeSeletionTo;
        this.dropdownMenuUpdate = dropdownMenuUpdate;
    }

    public void render(final SpriteBatch sb) {
        this.dropdownMenu.render(sb, this.xPos, this.yPos);
        this.text.render(sb);
        if (this.tooltip != null && this.dropdownMenu.getHitbox().hovered) {
            HeaderlessTip.renderHeaderlessTip(InputHelper.mX + 60.0f * Settings.scale, InputHelper.mY - 50.0f * Settings.scale, this.tooltip);
        }
    }

    public void update() {
        this.dropdownMenu.update();
        this.dropdownMenuUpdate.accept(this.dropdownMenu);
        this.text.update();
    }

    public int renderLayer() {
        return 1;
    }

    public int updateOrder() {
        return 1;
    }

    public void changedSelectionTo(final DropdownMenu dropdownMenu, final int i, final String s) {
        this.onChangeSeletionTo.accept(i, s);
    }

    public void set(final float xPos, final float yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.text.set(xPos + 40.0f, yPos + 8.0f);
    }

    public void setX(final float xPos) {
        this.xPos = xPos;
        this.text.setX(xPos + 40.0f);
    }

    public void setY(final float yPos) {
        this.yPos = yPos;
        this.text.setY(yPos + 8.0f);
    }

    public float getX() {
        return this.xPos;
    }

    public float getY() {
        return this.yPos;
    }
}

