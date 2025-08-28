package net.torocraft.dailies.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.torocraft.dailies.quests.DailyQuest;
import java.util.List;
import java.util.Arrays;

public class GuiDailyBadge {
	private static final ResourceLocation BADGE_TEXTURE = ResourceLocation.fromNamespaceAndPath("dailies", "textures/gui/badge_bg.png");
	private final DailyQuest quest;
	private final int x, y, width = 120, height = 28;
	private final Minecraft mc = Minecraft.getInstance();

	public GuiDailyBadge(DailyQuest quest, int x, int y) {
		this.quest = quest;
		this.x = x;
		this.y = y;
	}

	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, int screenWidth, int screenHeight) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		guiGraphics.blit(RenderType::guiTextured, BADGE_TEXTURE, x, y, 0.0F, 0.0F, width, height, 256, 256);
		guiGraphics.blit(RenderType::guiTextured, BADGE_TEXTURE, x + 6, y + 14, 0.0F, 76.0F, 108, 10, 256, 256);
		int progress = (int) Math.ceil(108 * ((double) quest.progress / (double) quest.target.quantity));
		guiGraphics.blit(RenderType::guiTextured, BADGE_TEXTURE, x + 6, y + 14, 0.0F, 86.0F, progress, 10, 256, 256);

		Font font = mc.font;
		String formattedQuestName = font.plainSubstrByWidth(quest.name, 110);
		guiGraphics.drawCenteredString(font, formattedQuestName, x + 60, y + 5, 0xffffff);
		String barText = buildQuestProgressRatioString();
		if (mc.level.getGameTime() % 120 < 60) {
			barText = font.plainSubstrByWidth(quest.description, 110);
		}
		guiGraphics.drawCenteredString(font, barText, x + 60, y + 15, 0xffffff);

		if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
			List<net.minecraft.network.chat.Component> tooltipComponents = Arrays.asList(
				net.minecraft.network.chat.Component.literal(quest.name),
				net.minecraft.network.chat.Component.literal(quest.description)
			);
			guiGraphics.renderComponentTooltip(font, tooltipComponents, mouseX, mouseY);
		}
	}

	private String buildQuestProgressRatioString() {
		return quest.progress + "/" + quest.target.quantity;
	}

	public void renderAccept(GuiGraphics guiGraphics) {
		guiGraphics.blit(RenderType::guiTextured, BADGE_TEXTURE, x, y, 0.0F, 0.0F, width, height, 256, 256);
		guiGraphics.blit(RenderType::guiTextured, BADGE_TEXTURE, x + 6, y + 14, 0.0F, 76.0F, 108, 10, 256, 256);
		Font font = mc.font;
		String formattedQuestName = font.plainSubstrByWidth(quest.name, 110);
		String questDescription = font.plainSubstrByWidth(quest.description, 110);
		guiGraphics.drawCenteredString(font, formattedQuestName, x + 60, y + 5, 0xffffff);
		guiGraphics.drawCenteredString(font, questDescription, x + 60, y + 15, 0xffffff);
	}

	private void renderTooltip(GuiGraphics guiGraphics, List<String> lines, int mouseX, int mouseY, Font font) {
		// Convert strings to Components for modern tooltip rendering
		List<net.minecraft.network.chat.Component> components = lines.stream()
			.map(net.minecraft.network.chat.Component::literal)
			.collect(java.util.stream.Collectors.toList());
		guiGraphics.renderComponentTooltip(font, components, mouseX, mouseY);
	}
}
