package com.ahmed.customarsenal.client.gui;

import com.ahmed.customarsenal.item.CustomItem;
import com.ahmed.customarsenal.item.CustomItemCategory;
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

/**
 * Polished vanilla-styled Custom Items GUI with category navigation, search bar, scrolling,
 * item tooltips, and direct creative item dispensing.
 */
public class CustomInventoryScreen extends Screen {
    private static final int PANEL_WIDTH = 256;
    private static final int PANEL_HEIGHT = 186;
    private static final int COLS = 9;
    private static final int ROWS = 5;
    private static final int SLOT_SIZE = 18;

    private final Screen parent;
    private CustomItemCategory selectedCategory = CustomItemCategory.ALL;
    private EditBox searchBox;
    private List<CustomItem> filteredItems = new ArrayList<>();
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

        // Search Box
        this.searchBox = new EditBox(this.font, this.gridLeft, this.topPos + 16, 162, 14, Component.literal("Search"));
        this.searchBox.setMaxLength(40);
        this.searchBox.setHint(Component.literal("Search custom items..."));
        this.searchBox.setResponder(query -> {
            this.scrollRowOffset = 0;
            refreshItems();
        });
        this.addRenderableWidget(this.searchBox);

        // Category Buttons along the left column (clean vanilla button aesthetic)
        CustomItemCategory[] categories = CustomItemCategory.values();
        for (int i = 0; i < categories.length; i++) {
            final CustomItemCategory cat = categories[i];
            int btnY = this.topPos + 16 + (i * 17);
            Button catBtn = Button.builder(Component.literal(cat.getDisplayName()), btn -> {
                this.selectedCategory = cat;
                this.scrollRowOffset = 0;
                refreshItems();
            }).bounds(this.leftPos + 8, btnY, 60, 16).build();
            this.addRenderableWidget(catBtn);
        }

        // Top-right Vanilla Creative & Config navigation buttons
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

    private void refreshItems() {
        String q = this.searchBox != null ? this.searchBox.getValue() : "";
        this.filteredItems = CustomItemSearch.filter(this.selectedCategory, q);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        // Outer Vanilla-styled Panel
        graphics.fill(leftPos, topPos, leftPos + PANEL_WIDTH, topPos + PANEL_HEIGHT, 0xFFC6C6C6);
        graphics.renderOutline(leftPos, topPos, PANEL_WIDTH, PANEL_HEIGHT, 0xFF373737);
        graphics.fill(leftPos + 2, topPos + 2, leftPos + PANEL_WIDTH - 2, topPos + PANEL_HEIGHT - 2, 0xFF8B8B8B);
        graphics.fill(leftPos + 4, topPos + 4, leftPos + PANEL_WIDTH - 4, topPos + PANEL_HEIGHT - 4, 0xFFC6C6C6);

        // Header Title
        graphics.drawString(this.font, "Custom Arsenal — " + selectedCategory.getDisplayName(),
                this.gridLeft, this.topPos + 6, 0xFF373737, false);

        // Item Slots Grid (9x5)
        CustomItem hoveredItem = null;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int slotX = this.gridLeft + col * SLOT_SIZE;
                int slotY = this.gridTop + row * SLOT_SIZE;

                // Vanilla recessed slot bevel
                graphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF8B8B8B);
                graphics.fill(slotX, slotY, slotX + 17, slotY + 1, 0xFF373737);
                graphics.fill(slotX, slotY, slotX + 1, slotY + 17, 0xFF373737);
                graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0xFF8B8B8B);

                int index = (row + scrollRowOffset) * COLS + col;
                if (index >= 0 && index < filteredItems.size()) {
                    CustomItem item = filteredItems.get(index);
                    ItemStack stack = new ItemStack(item);
                    graphics.renderItem(stack, slotX + 1, slotY + 1);
                    graphics.renderItemDecorations(this.font, stack, slotX + 1, slotY + 1);

                    if (mouseX >= slotX && mouseX < slotX + 18 && mouseY >= slotY && mouseY < slotY + 18) {
                        graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0x80FFFFFF);
                        hoveredItem = item;
                    }
                }
            }
        }

        // Scrollbar track
        int scrollX = this.gridLeft + COLS * SLOT_SIZE + 4;
        int scrollHeight = ROWS * SLOT_SIZE;
        graphics.fill(scrollX, this.gridTop, scrollX + 10, this.gridTop + scrollHeight, 0xFF555555);
        int maxRows = Math.max(1, (int) Math.ceil((double) filteredItems.size() / COLS) - ROWS);
        float scrollRatio = maxRows <= 0 ? 0f : (float) scrollRowOffset / maxRows;
        int knobY = this.gridTop + Math.round((scrollHeight - 16) * scrollRatio);
        graphics.fill(scrollX + 1, knobY, scrollX + 9, knobY + 16, 0xFFAAAAAA);

        // Footer Info Panel for Hovered Item
        int infoTop = this.gridTop + ROWS * SLOT_SIZE + 6;
        graphics.fill(this.gridLeft, infoTop, this.gridLeft + 174, this.topPos + PANEL_HEIGHT - 8, 0xFF2B2B2B);
        graphics.renderOutline(this.gridLeft, infoTop, 174, (this.topPos + PANEL_HEIGHT - 8) - infoTop, 0xFF555555);

        if (hoveredItem != null) {
            graphics.drawString(this.font, hoveredItem.getCustomData().displayName(), this.gridLeft + 4, infoTop + 4,
                    0xFF000000 | hoveredItem.getCustomData().element().primaryColor(), true);
            String clickHint = this.minecraft.player != null && this.minecraft.player.isCreative()
                    ? "Left-Click: Take 1 | Shift-Click: Stack"
                    : "Creative Mode required to take items";
            graphics.drawString(this.font, clickHint, this.gridLeft + 4, infoTop + 16, 0xFFAAAAAA, false);
            graphics.drawString(this.font, "ID: custom_arsenal:" + hoveredItem.getCustomData().id(),
                    this.gridLeft + 4, infoTop + 28, 0xFF777777, false);
        } else {
            graphics.drawString(this.font, "Hover an item to inspect abilities & stats.", this.gridLeft + 4, infoTop + 10, 0xFFAAAAAA, false);
            graphics.drawString(this.font, "Total Showing: " + filteredItems.size() + " custom items", this.gridLeft + 4, infoTop + 22, 0xFF888888, false);
        }

        super.render(graphics, mouseX, mouseY, partialTick);

        if (hoveredItem != null) {
            graphics.renderTooltip(this.font, new ItemStack(hoveredItem), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.minecraft != null && this.minecraft.player != null && this.minecraft.player.isCreative()) {
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    int slotX = this.gridLeft + col * SLOT_SIZE;
                    int slotY = this.gridTop + row * SLOT_SIZE;
                    if (mouseX >= slotX && mouseX < slotX + 18 && mouseY >= slotY && mouseY < slotY + 18) {
                        int index = (row + scrollRowOffset) * COLS + col;
                        if (index >= 0 && index < filteredItems.size()) {
                            CustomItem item = filteredItems.get(index);
                            int count = hasShiftDown() ? item.getCustomData().maxStackSize() : 1;
                            ClientPlayNetworking.send(new CreativeGiveItemC2SPayload(item.getCustomData().id(), count));
                            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ITEM_PICKUP, 1.0f));
                            return true;
                        }
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int totalRows = (int) Math.ceil((double) filteredItems.size() / COLS);
        int maxOffset = Math.max(0, totalRows - ROWS);
        if (scrollY < 0) {
            this.scrollRowOffset = Mth.clamp(this.scrollRowOffset + 1, 0, maxOffset);
            return true;
        } else if (scrollY > 0) {
            this.scrollRowOffset = Mth.clamp(this.scrollRowOffset - 1, 0, maxOffset);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
