package com.ahmed.customarsenal.client.gui;

import com.ahmed.customarsenal.ability.AbilityTrigger;
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
    private static final int SLOT_SIZE = 20;

    private final Screen parent;
    private CustomItemCategory selectedCategory = CustomItemCategory.ALL;
    private EditBox searchBox;
    private List<CustomItem> filteredItems = new ArrayList<>();
    private final List<Button> slotButtons = new ArrayList<>();
    private int scrollRowOffset = 0;

    private int panelWidth;
    private int panelHeight;
    private int leftPos;
    private int topPos;
    private int gridLeft;
    private int gridTop;
    private int cols;
    private int rows;

    public CustomInventoryScreen(Screen parent) {
        super(Component.literal("Custom Arsenal"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // Dynamically scale to current screen width/height & GUI scale
        this.panelWidth = Mth.clamp(this.width - 28, 230, 340);
        this.panelHeight = Mth.clamp(this.height - 36, 176, 236);
        this.leftPos = (this.width - this.panelWidth) / 2;
        this.topPos = (this.height - this.panelHeight) / 2 + 8;

        int catColWidth = 64;
        this.gridLeft = this.leftPos + catColWidth + 8;
        this.gridTop = this.topPos + 28;

        int availGridW = this.panelWidth - catColWidth - 28;
        int availGridH = this.panelHeight - 82;
        this.cols = Math.max(5, availGridW / SLOT_SIZE);
        this.rows = Math.max(3, availGridH / SLOT_SIZE);

        this.searchBox = new EditBox(this.font, this.gridLeft, this.topPos + 10, this.cols * SLOT_SIZE, 14, Component.literal("Search"));
        this.searchBox.setMaxLength(32);
        this.searchBox.setHint(Component.literal("Search..."));
        this.searchBox.setResponder(q -> {
            this.scrollRowOffset = 0;
            refreshItems();
        });
        this.addRenderableWidget(this.searchBox);

        CustomItemCategory[] categories = CustomItemCategory.values();
        int catBtnH = Math.min(16, (this.panelHeight - 20) / categories.length);
        for (int i = 0; i < categories.length; i++) {
            final CustomItemCategory cat = categories[i];
            int btnY = this.topPos + 10 + (i * catBtnH);
            this.addRenderableWidget(Button.builder(Component.literal(cat.getDisplayName()), btn -> {
                this.selectedCategory = cat;
                this.scrollRowOffset = 0;
                refreshItems();
            }).bounds(this.leftPos + 6, btnY, catColWidth - 4, catBtnH - 1).build());
        }

        int scrollX = this.gridLeft + this.cols * SLOT_SIZE + 3;
        this.addRenderableWidget(Button.builder(Component.literal("▲"), btn -> {
            this.scrollRowOffset = Math.max(0, this.scrollRowOffset - 1);
            updateSlotButtons();
        }).bounds(scrollX, this.gridTop, 14, 18).build());

        this.addRenderableWidget(Button.builder(Component.literal("▼"), btn -> {
            int maxRows = Math.max(0, (int) Math.ceil((double) filteredItems.size() / cols) - rows);
            this.scrollRowOffset = Mth.clamp(this.scrollRowOffset + 1, 0, maxRows);
            updateSlotButtons();
        }).bounds(scrollX, this.gridTop + (this.rows - 1) * SLOT_SIZE, 14, 18).build());

        this.slotButtons.clear();
        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.cols; c++) {
                final int slotIndex = r * this.cols + c;
                int sx = this.gridLeft + c * SLOT_SIZE;
                int sy = this.gridTop + r * SLOT_SIZE;
                Button slotBtn = Button.builder(Component.empty(), b -> onSlotClicked(slotIndex))
                        .bounds(sx, sy, SLOT_SIZE, SLOT_SIZE)
                        .build();
                this.slotButtons.add(slotBtn);
                this.addRenderableWidget(slotBtn);
            }
        }

        this.addRenderableWidget(Button.builder(Component.literal("Configure"), btn ->
                this.minecraft.setScreen(new CustomArsenalConfigScreen(this))
        ).bounds(this.leftPos + this.panelWidth - 68, this.topPos - 18, 68, 16).build());

        if (this.parent != null) {
            this.addRenderableWidget(Button.builder(Component.literal("← Back"), btn ->
                    this.minecraft.setScreen(this.parent)
            ).bounds(this.leftPos, this.topPos - 18, 56, 16).build());
        }

        refreshItems();
    }

    private void onSlotClicked(int localIdx) {
        if (this.minecraft == null || this.minecraft.player == null || !this.minecraft.player.isCreative()) return;
        int itemIdx = (this.scrollRowOffset * this.cols) + localIdx;
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
            int itemIdx = (this.scrollRowOffset * this.cols) + i;
            this.slotButtons.get(i).active = itemIdx >= 0 && itemIdx < this.filteredItems.size();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0x90000000);
        graphics.fill(leftPos, topPos, leftPos + panelWidth, topPos + panelHeight, 0xFFC6C6C6);
        graphics.fill(leftPos + 2, topPos + 2, leftPos + panelWidth - 2, topPos + panelHeight - 2, 0xFF373737);
        graphics.fill(leftPos + 4, topPos + 4, leftPos + panelWidth - 4, topPos + panelHeight - 4, 0xFFC6C6C6);

        super.render(graphics, mouseX, mouseY, partialTick);

        CustomItem hoveredItem = null;
        for (int r = 0; r < this.rows; r++) {
            for (int c = 0; c < this.cols; c++) {
                int sx = this.gridLeft + c * SLOT_SIZE;
                int sy = this.gridTop + r * SLOT_SIZE;
                int idx = (r + scrollRowOffset) * this.cols + c;
                if (idx >= 0 && idx < filteredItems.size()) {
                    CustomItem item = filteredItems.get(idx);
                    // Rarity border tint
                    int rarityCol = 0x88000000 | item.getCustomData().customRarity().getColorHex();
                    graphics.fill(sx + 1, sy + 1, sx + SLOT_SIZE - 1, sy + 2, rarityCol);

                    ItemStack stack = new ItemStack(item);
                    graphics.renderItem(stack, sx + 2, sy + 2);

                    if (mouseX >= sx && mouseX < sx + SLOT_SIZE && mouseY >= sy && mouseY < sy + SLOT_SIZE) {
                        hoveredItem = item;
                    }
                }
            }
        }

        // Clean, concise bottom bar (Only Item Name, Rarity, Ability Name, Keybind, Cooldown)
        int infoTop = this.gridTop + this.rows * SLOT_SIZE + 4;
        int infoBottom = this.topPos + this.panelHeight - 6;
        graphics.fill(this.gridLeft, infoTop, this.leftPos + this.panelWidth - 8, infoBottom, 0xFF1E1E1E);

        if (hoveredItem != null) {
            CustomItemData d = hoveredItem.getCustomData();
            graphics.drawString(this.font, d.displayName() + " [" + d.customRarity().getDisplayName() + "]",
                    this.gridLeft + 4, infoTop + 3, 0xFF000000 | d.customRarity().getColorHex(), true);

            int y = infoTop + 14;
            int colOffset = 0;
            for (CustomAbility ab : d.abilities()) {
                AbilityTrigger trig = AbilityTrigger.fromSlotId(ab.keySlot());
                String label = trig.getShortLabel() + ": " + ab.displayName() + " (" + (ab.getEffectiveCooldownTicks() / 20) + "s)";
                graphics.drawString(this.font, label, this.gridLeft + 4 + colOffset, y, 0xFFFFDD55, false);
                if (colOffset == 0) {
                    colOffset = (this.panelWidth - 84) / 2;
                } else {
                    colOffset = 0;
                    y += 10;
                }
            }
        } else {
            graphics.drawString(this.font, selectedCategory.getDisplayName() + " (" + filteredItems.size() + ")",
                    this.gridLeft + 4, infoTop + 6, 0xFFCCCCCC, false);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxRows = Math.max(0, (int) Math.ceil((double) filteredItems.size() / this.cols) - this.rows);
        if (scrollY < 0) {
            this.scrollRowOffset = Mth.clamp(this.scrollRowOffset + 1, 0, maxRows);
            updateSlotButtons();
            return true;
        } else if (scrollY > 0) {
            this.scrollRowOffset = Mth.clamp(this.scrollRowOffset - 1, 0, maxRows);
            updateSlotButtons();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
