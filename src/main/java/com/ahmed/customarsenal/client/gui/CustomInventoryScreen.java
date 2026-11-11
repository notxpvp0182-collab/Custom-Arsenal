package com.ahmed.customarsenal.client.gui;

import com.ahmed.customarsenal.ability.CustomAbility;
import com.ahmed.customarsenal.item.CustomItem;
import com.ahmed.customarsenal.item.CustomItemCategory;
import com.ahmed.customarsenal.item.CustomItemData;
import com.ahmed.customarsenal.item.CustomItemSearch;
import com.ahmed.customarsenal.network.CreativeGiveItemC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CustomInventoryScreen extends Screen {
    private static final int PANEL_WIDTH = 256;
    private static final int PANEL_HEIGHT = 196;
    private static final int COLS = 8;
    private static final int ROWS = 4;
    private static final int SLOT_SIZE = 20;

    private final Screen parent;
    private CustomItemCategory selectedCategory = CustomItemCategory.ALL;
    private EditBox searchBox;
    private List<CustomItem> filteredItems = new ArrayList<>();
    private final List<Button> slotButtons = new ArrayList<>();
    private int scrollRowOffset = 0;

    private int leftPos;
    private int topPos;
    private int gridLeft;
    private int gridTop;

    public CustomInventoryScreen(Screen parent) {
        super(Component.translatable("gui.custom_arsenal.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - PANEL_WIDTH) / 2;
        this.topPos = (this.height - PANEL_HEIGHT) / 2;
        this.gridLeft = this.leftPos + 74;
        this.gridTop = this.topPos + 34;

        this.searchBox = new EditBox(this.font, this.gridLeft, this.topPos + 16, 150, 14, Component.literal("Search"));
        this.searchBox.setMaxLength(40);
        this.searchBox.setHint(Component.literal("Search custom items..."));
        this.searchBox.setResponder(query -> {
            this.scrollRowOffset = 0;
            refreshItems();
        });
        this.addRenderableWidget(this.searchBox);

        CustomItemCategory[] categories = CustomItemCategory.values();
        for (int i = 0; i < categories.length; i++) {
            final CustomItemCategory cat = categories[i];
            int btnY = this.topPos + 16 + (i * 18);
            Button catBtn = Button.builder(Component.literal(cat.getDisplayName()), btn -> {
                this.selectedCategory = cat;
                this.scrollRowOffset = 0;
                refreshItems();
            }).bounds(this.leftPos + 8, btnY, 60, 16).build();
            this.addRenderableWidget(catBtn);
        }

        // Scroll Up / Down buttons
        this.addRenderableWidget(Button.builder(Component.literal("▲"), btn -> {
            this.scrollRowOffset = Math.max(0, this.scrollRowOffset - 1);
            updateSlotButtons();
        }).bounds(this.gridLeft + COLS * SLOT_SIZE + 2, this.gridTop, 14, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("▼"), btn -> {
            int maxRows = Math.max(0, (int) Math.ceil((double) filteredItems.size() / COLS) - ROWS);
            this.scrollRowOffset = Mth.clamp(this.scrollRowOffset + 1, 0, maxRows);
            updateSlotButtons();
        }).bounds(this.gridLeft + COLS * SLOT_SIZE + 2, this.gridTop + (ROWS - 1) * SLOT_SIZE, 14, 20).build());

        // Interactive Item Slot Buttons
        this.slotButtons.clear();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                final int slotIndex = row * COLS + col;
                int slotX = this.gridLeft + col * SLOT_SIZE;
                int slotY = this.gridTop + row * SLOT_SIZE;
                Button slotBtn = Button.builder(Component.empty(), btn -> onSlotClicked(slotIndex))
                        .bounds(slotX, slotY, SLOT_SIZE, SLOT_SIZE)
                        .build();
                this.slotButtons.add(slotBtn);
                this.addRenderableWidget(slotBtn);
            }
        }

        this.addRenderableWidget(Button.builder(Component.literal("Config"), btn ->
                this.minecraft.setScreen(new CustomArsenalConfigScreen(this))
        ).bounds(this.leftPos + PANEL_WIDTH - 68, this.topPos - 22, 68, 20).build());

        if (this.parent != null) {
            this.addRenderableWidget(Button.builder(Component.literal("← Vanilla Inventory"), btn ->
                    this.minecraft.setScreen(this.parent)
            ).bounds(this.leftPos, this.topPos - 22, 110, 20).build());
        }

        refreshItems();
    }

    private void onSlotClicked(int localSlotIdx) {
        if (this.minecraft == null || this.minecraft.player == null || !this.minecraft.player.isCreative()) {
            return;
        }
        int itemIdx = (this.scrollRowOffset * COLS) + localSlotIdx;
        if (itemIdx >= 0 && itemIdx < this.filteredItems.size()) {
            CustomItem item = this.filteredItems.get(itemIdx);
            ClientPlayNetworking.send(new CreativeGiveItemC2SPayload(item.getCustomData().id(), 1));
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ITEM_PICKUP, 1.0f));
        }
    }

    private void refreshItems() {
        String q = this.searchBox != null ? this.searchBox.getValue() : "";
        this.filteredItems = CustomItemSearch.filter(this.selectedCategory, q);
        updateSlotButtons();
    }

    private void updateSlotButtons() {
        for (int i = 0; i < this.slotButtons.size(); i++) {
            int itemIdx = (this.scrollRowOffset * COLS) + i;
            Button btn = this.slotButtons.get(i);
            btn.active = itemIdx >= 0 && itemIdx < this.filteredItems.size();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Outer Vanilla-styled Panel
        graphics.fill(leftPos, topPos, leftPos + PANEL_WIDTH, topPos + PANEL_HEIGHT, 0xFFC6C6C6);
        graphics.fill(leftPos + 2, topPos + 2, leftPos + PANEL_WIDTH - 2, topPos + PANEL_HEIGHT - 2, 0xFF373737);
        graphics.fill(leftPos + 4, topPos + 4, leftPos + PANEL_WIDTH - 4, topPos + PANEL_HEIGHT - 4, 0xFFC6C6C6);

        graphics.drawString(this.font, "Custom Arsenal — " + selectedCategory.getDisplayName(),
                this.gridLeft, this.topPos + 6, 0xFF202020, false);

        super.render(graphics, mouseX, mouseY, partialTick);

        CustomItem hoveredItem = null;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int slotX = this.gridLeft + col * SLOT_SIZE;
                int slotY = this.gridTop + row * SLOT_SIZE;
                int index = (row + scrollRowOffset) * COLS + col;
                if (index >= 0 && index < filteredItems.size()) {
                    CustomItem item = filteredItems.get(index);
                    ItemStack stack = new ItemStack(item);
                    graphics.renderItem(stack, slotX + 2, slotY + 2);
                    graphics.renderItemDecorations(this.font, stack, slotX + 2, slotY + 2);

                    if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE && mouseY >= slotY && mouseY < slotY + SLOT_SIZE) {
                        hoveredItem = item;
                    }
                }
            }
        }

        // Rich Item Inspector Panel at the bottom
        int infoTop = this.gridTop + ROWS * SLOT_SIZE + 6;
        graphics.fill(this.gridLeft, infoTop, this.leftPos + PANEL_WIDTH - 8, this.topPos + PANEL_HEIGHT - 8, 0xFF222222);

        if (hoveredItem != null) {
            CustomItemData d = hoveredItem.getCustomData();
            graphics.drawString(this.font, d.displayName() + " [" + d.element().displayName() + "]",
                    this.gridLeft + 4, infoTop + 4, 0xFF000000 | d.element().primaryColor(), true);
            graphics.drawString(this.font, "ATK: " + d.attackDamage() + " | SPD: " + d.attackSpeed() + " | Click to Take",
                    this.gridLeft + 4, infoTop + 16, 0xFFCCCCCC, false);

            int lineY = infoTop + 28;
            for (CustomAbility ab : d.abilities()) {
                String key = switch (ab.keySlot()) {
                    case "slot_1" -> "R";
                    case "slot_2" -> "G";
                    case "slot_3" -> "V";
                    case "shield" -> "C";
                    default -> "R";
                };
                graphics.drawString(this.font, "[" + key + "] " + ab.displayName() + " (" + (ab.getEffectiveCooldownTicks() / 20.0f) + "s)",
                        this.gridLeft + 4, lineY, 0xFFFFDD55, false);
                lineY += 10;
            }
        } else {
            graphics.drawString(this.font, "Hover an item to inspect abilities & stats.", this.gridLeft + 4, infoTop + 10, 0xFFAAAAAA, false);
            graphics.drawString(this.font, "Click any item slot to add to Creative Inventory.", this.gridLeft + 4, infoTop + 24, 0xFF888888, false);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
