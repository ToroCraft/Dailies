package net.torocraft.dailies.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderPipelines;
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
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BADGE_TEXTURE, x, y, 0.0F, 0.0F, width, height, 256, 256);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BADGE_TEXTURE, x + 6, y + 14, 0.0F, 76.0F, 108, 10, 256, 256);
		int progress = (int) Math.ceil(108 * ((double) quest.progress / (double) quest.target.quantity));
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BADGE_TEXTURE, x + 6, y + 14, 0.0F, 86.0F, progress, 10, 256, 256);

		guiGraphics.nextStratum();
		
		Font font = mc.font;
		String formattedQuestName = font.plainSubstrByWidth(quest.name, 110);
		int textX = x + 60 - font.width(formattedQuestName) / 2; // Manual centering
		guiGraphics.drawString(font, formattedQuestName, textX, y + 5, 0xFFFFFFFF, false);
		
		String barText = buildQuestProgressRatioString();
		if (mc.level != null && mc.level.getGameTime() % 120 < 60) {
			barText = font.plainSubstrByWidth(quest.description, 110);
		}
		int textX2 = x + 60 - font.width(barText) / 2; // Manual centering
		guiGraphics.drawString(font, barText, textX2, y + 15, 0xFFFFFFFF, false);

		if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
			List<net.minecraft.network.chat.Component> tooltipComponents = Arrays.asList(
				net.minecraft.network.chat.Component.literal(quest.name),
				net.minecraft.network.chat.Component.literal(quest.description)
			);
			guiGraphics.setTooltipForNextFrame(font, tooltipComponents, java.util.Optional.empty(), mouseX, mouseY);
		}
	}

	private String buildQuestProgressRatioString() {
		return quest.progress + "/" + quest.target.quantity;
	}

	public void renderAccept(GuiGraphics guiGraphics) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BADGE_TEXTURE, x, y, 0.0F, 0.0F, width, height, 256, 256);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BADGE_TEXTURE, x + 6, y + 14, 0.0F, 76.0F, 108, 10, 256, 256);
		
		guiGraphics.nextStratum();
		
		Font font = mc.font;
		String formattedQuestName = font.plainSubstrByWidth(quest.name, 110);
		String questDescription = font.plainSubstrByWidth(quest.description, 110);
		
		int textX1 = x + 60 - font.width(formattedQuestName) / 2; // Manual centering
		int textX2 = x + 60 - font.width(questDescription) / 2; // Manual centering
		guiGraphics.drawString(font, formattedQuestName, textX1, y + 5, 0xFFFFFFFF, false);
		guiGraphics.drawString(font, questDescription, textX2, y + 15, 0xFFFFFFFF, false);
	}

	private void renderTooltip(GuiGraphics guiGraphics, List<String> lines, int mouseX, int mouseY, Font font) {
		// Convert strings to Components for modern tooltip rendering
		List<net.minecraft.network.chat.Component> components = lines.stream()
			.map(net.minecraft.network.chat.Component::literal)
			.collect(java.util.stream.Collectors.toList());
		guiGraphics.setTooltipForNextFrame(font, components, java.util.Optional.empty(), mouseX, mouseY);
	}
}
