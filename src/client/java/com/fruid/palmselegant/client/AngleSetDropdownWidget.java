package com.fruid.palmselegant.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class AngleSetDropdownWidget extends AbstractWidget {

    private static final int ROW_HEIGHT = 12;
    private static final int ROW_SPACING = 13;
    private static final int MIN_OPEN_WIDTH = 180;
    private static final int TOP_PADDING = 0;
    private static final int BOTTOM_PADDING = 0;

    private boolean open = false;
    private int scrollOffset = 0;

    private final Runnable refreshScreen;

    private final int dropdownX;
    private final int dropdownY;
    private final int dropdownWidth;

    public AngleSetDropdownWidget(int x, int y, int width, int height, Runnable refreshScreen) {
        super(
                0,
                0,
                Minecraft.getInstance().getWindow().getGuiScaledWidth(),
                Minecraft.getInstance().getWindow().getGuiScaledHeight(),
                Component.literal("")
        );

        this.refreshScreen = refreshScreen;
        this.dropdownX = x;
        this.dropdownY = y;
        this.dropdownWidth = width;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        Minecraft minecraft = Minecraft.getInstance();

        int x = getOpenX();
        int y = this.open ? getOpenY() : this.dropdownY;
        int width = getOpenWidth();

        if (!this.open) {
            graphics.fill(x, y, x + width, y + ROW_HEIGHT, 0xAA111111);

            String text = AngleManager.getCurrentSetName() + " ▼";

            graphics.text(
                    minecraft.font,
                    text,
                    x + width / 2 - minecraft.font.width(text) / 2,
                    y + 2,
                    0xFFFFFFFF
            );

            drawBorder(graphics, x, y, width, ROW_HEIGHT, 0xFFFFFFFF);
            return;
        }

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        graphics.fill(0, 0, screenWidth, screenHeight, 0x66000000);

        int rowY = y;
        boolean needsScrolling = AngleManager.SETS.size() > getVisibleSetRows();

        drawRow(graphics, minecraft, x, rowY, width, "-", 0xAA111111, 0xFFFFFFFF);
        rowY += ROW_SPACING;

        if (needsScrolling) {
            boolean canScrollUp = this.scrollOffset > 0;
            boolean upHovered = isInside(mouseX, mouseY, x, rowY, width, ROW_HEIGHT);

            drawRow(
                    graphics,
                    minecraft,
                    x,
                    rowY,
                    width,
                    "▲ Go Up ▲",
                    upHovered && canScrollUp ? 0xAA334455 : 0xAA1A2633,
                    canScrollUp ? 0xFFFFFFFF : 0xFF777777
            );

            rowY += ROW_SPACING;
        }

        int visibleRows = getVisibleSetRows();
        int endIndex = Math.min(this.scrollOffset + visibleRows, AngleManager.SETS.size());

        for (int i = this.scrollOffset; i < endIndex; i++) {
            String setName = AngleManager.SETS.get(i);

            boolean current = setName.equals(AngleManager.getCurrentSetName());
            boolean hovered = isInside(mouseX, mouseY, x, rowY, width, ROW_HEIGHT);

            int background = 0xAA111111;
            int textColor = 0xFFFFFFFF;

            if (current) {
                background = 0xAA665500;
                textColor = 0xFFFFFF55;
            } else if (hovered) {
                background = 0xAA555555;
            }

            drawRow(graphics, minecraft, x, rowY, width, setName, background, textColor);
            rowY += ROW_SPACING;
        }

        boolean newHovered = isInside(mouseX, mouseY, x, rowY, width, ROW_HEIGHT);

        drawRow(
                graphics,
                minecraft,
                x,
                rowY,
                width,
                "＋ New Angle Set ＋",
                newHovered ? 0xAA225533 : 0xAA11331F,
                0xFF55FF88
        );

        rowY += ROW_SPACING;

        if (needsScrolling) {
            boolean canScrollDown = this.scrollOffset < getMaxScrollOffset();
            boolean downHovered = isInside(mouseX, mouseY, x, rowY, width, ROW_HEIGHT);

            drawRow(
                    graphics,
                    minecraft,
                    x,
                    rowY,
                    width,
                    "▼ Go Down ▼",
                    downHovered && canScrollDown ? 0xAA334455 : 0xAA1A2633,
                    canScrollDown ? 0xFFFFFFFF : 0xFF777777
            );
        }

        drawBorder(graphics, x, y, width, getOpenHeight(), 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return false;
    }

    public boolean handleScreenClick(MouseButtonEvent event) {
        double mouseX = event.x();
        double mouseY = event.y();

        int x = getOpenX();
        int closedY = this.dropdownY;
        int width = getOpenWidth();

        if (!this.open) {
            if (isInside(mouseX, mouseY, x, closedY, width, ROW_HEIGHT)) {
                this.open = true;
                clampScrollOffset();
                return true;
            }

            return false;
        }

        int y = getOpenY();
        int rowY = y;

        boolean needsScrolling = AngleManager.SETS.size() > getVisibleSetRows();

        if (isInside(mouseX, mouseY, x, rowY, width, ROW_HEIGHT)) {
            this.open = false;
            return true;
        }

        rowY += ROW_SPACING;

        if (needsScrolling) {
            if (isInside(mouseX, mouseY, x, rowY, width, ROW_HEIGHT)) {
                scrollUp();
                return true;
            }

            rowY += ROW_SPACING;
        }

        int visibleRows = getVisibleSetRows();
        int endIndex = Math.min(this.scrollOffset + visibleRows, AngleManager.SETS.size());

        for (int i = this.scrollOffset; i < endIndex; i++) {
            String setName = AngleManager.SETS.get(i);

            if (isInside(mouseX, mouseY, x, rowY, width, ROW_HEIGHT)) {
                AngleManager.setCurrentSet(setName);
                this.open = false;
                this.refreshScreen.run();
                return true;
            }

            rowY += ROW_SPACING;
        }

        if (isInside(mouseX, mouseY, x, rowY, width, ROW_HEIGHT)) {
            this.open = false;

            Minecraft.getInstance().gui.setScreen(
                    new NewAngleSetScreen(Minecraft.getInstance().gui.screen())
            );

            return true;
        }

        rowY += ROW_SPACING;

        if (needsScrolling) {
            if (isInside(mouseX, mouseY, x, rowY, width, ROW_HEIGHT)) {
                scrollDown();
                return true;
            }
        }

        this.open = false;
        return true;
    }

    public boolean handleScreenScroll(double mouseX, double mouseY, double scrollY) {
        if (!this.open) {
            return false;
        }

        if (scrollY > 0) {
            scrollUp();
        } else if (scrollY < 0) {
            scrollDown();
        }

        return true;
    }

    private void scrollUp() {
        this.scrollOffset--;
        clampScrollOffset();
    }

    private void scrollDown() {
        this.scrollOffset++;
        clampScrollOffset();
    }

    private void clampScrollOffset() {
        this.scrollOffset = Math.max(0, Math.min(this.scrollOffset, getMaxScrollOffset()));
    }

    private int getMaxScrollOffset() {
        return Math.max(0, AngleManager.SETS.size() - getVisibleSetRows());
    }

    private int getVisibleSetRows() {
        Minecraft minecraft = Minecraft.getInstance();

        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        int spaceBelow = screenHeight - this.dropdownY;
        int spaceAbove = this.dropdownY;

        int availableHeight = spaceBelow + spaceAbove;

        int reservedRowsWithScroll = 4;
        int reservedRowsWithoutScroll = 2;

        int possibleWithoutScroll = Math.max(1, availableHeight / ROW_SPACING - reservedRowsWithoutScroll);
        boolean needsScrolling = AngleManager.SETS.size() > possibleWithoutScroll;

        int reservedRows = needsScrolling ? reservedRowsWithScroll : reservedRowsWithoutScroll;
        int possibleRows = Math.max(1, availableHeight / ROW_SPACING - reservedRows);

        return Math.min(AngleManager.SETS.size(), possibleRows);
    }

    private int getOpenHeight() {
        boolean needsScrolling = AngleManager.SETS.size() > getVisibleSetRows();

        int rows = 1;
        rows += getVisibleSetRows();
        rows += 1;

        if (needsScrolling) {
            rows += 2;
        }

        return (rows - 1) * ROW_SPACING + ROW_HEIGHT;
    }

    private int getOpenY() {
        Minecraft minecraft = Minecraft.getInstance();

        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        int openHeight = getOpenHeight();

        int normalY = this.dropdownY;
        int bottomY = normalY + openHeight;

        if (bottomY <= screenHeight - BOTTOM_PADDING) {
            return normalY;
        }

        int centeredY = (screenHeight - openHeight) / 2;

        return Math.max(
                TOP_PADDING,
                Math.min(centeredY, screenHeight - BOTTOM_PADDING - openHeight)
        );
    }

    private int getOpenWidth() {
        Minecraft minecraft = Minecraft.getInstance();
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();

        return Math.min(Math.max(this.dropdownWidth, MIN_OPEN_WIDTH), screenWidth - 40);
    }

    private int getOpenX() {
        Minecraft minecraft = Minecraft.getInstance();
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();

        return Math.max(10, screenWidth / 2 - getOpenWidth() / 2);
    }

    private void drawRow(
            GuiGraphicsExtractor graphics,
            Minecraft minecraft,
            int x,
            int y,
            int width,
            String text,
            int backgroundColor,
            int textColor
    ) {
        graphics.fill(x, y, x + width, y + ROW_HEIGHT, backgroundColor);

        int textX = x + width / 2 - minecraft.font.width(text) / 2;

        graphics.text(
                minecraft.font,
                text,
                textX,
                y + 2,
                textColor
        );
    }

    private boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x
                && mouseX < x + width
                && mouseY >= y
                && mouseY < y + height;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    private void drawBorder(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + 1, color);
        graphics.fill(x, y + height - 1, x + width, y + height, color);
        graphics.fill(x, y, x + 1, y + height, color);
        graphics.fill(x + width - 1, y, x + width, y + height, color);
    }
}