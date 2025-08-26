package net.torocraft.dailies.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.torocraft.dailies.quests.DailyQuest;
import java.util.List;
import java.util.Arrays;

public class GuiDailyBadge extends GuiComponent {
	private static final ResourceLocation BADGE_TEXTURE = new ResourceLocation("dailies", "textures/gui/badge_bg.png");
	private final DailyQuest quest;
	private final int x, y, width = 120, height = 28;
	private final Minecraft mc = Minecraft.getInstance();

	public GuiDailyBadge(DailyQuest quest, int x, int y) {
		this.quest = quest;
		this.x = x;
		this.y = y;
	}

	public void render(PoseStack poseStack, int mouseX, int mouseY, int screenWidth, int screenHeight) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindForSetup(BADGE_TEXTURE);
		blit(poseStack, x, y, 0, 0, width, height);
		blit(poseStack, x + 6, y + 14, 0, 76, 108, 10);
		int progress = (int) Math.ceil(108 * ((double) quest.progress / (double) quest.target.quantity));
		blit(poseStack, x + 6, y + 14, 0, 86, progress, 10);

		Font font = mc.font;
		String formattedQuestName = font.plainSubstrByWidth(quest.name, 110);
		drawCenteredString(poseStack, font, formattedQuestName, x + 60, y + 5, 0xffffff);
		String barText = buildQuestProgressRatioString();
		if (mc.level.getGameTime() % 120 < 60) {
			barText = font.plainSubstrByWidth(quest.description, 110);
		}
		drawCenteredString(poseStack, font, barText, x + 60, y + 15, 0xffffff);

		if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
			renderTooltip(poseStack, Arrays.asList(quest.name, quest.description), mouseX, mouseY, font);
		}
	}

	private String buildQuestProgressRatioString() {
		return quest.progress + "/" + quest.target.quantity;
	}

	public void renderAccept(PoseStack poseStack) {
		mc.getTextureManager().bindForSetup(BADGE_TEXTURE);
		blit(poseStack, x, y, 0, 0, width, height);
		blit(poseStack, x + 6, y + 14, 0, 76, 108, 10);
		Font font = mc.font;
		String formattedQuestName = font.plainSubstrByWidth(quest.name, 110);
		String questDescription = font.plainSubstrByWidth(quest.description, 110);
		drawCenteredString(poseStack, font, formattedQuestName, x + 60, y + 5, 0xffffff);
		drawCenteredString(poseStack, font, questDescription, x + 60, y + 15, 0xffffff);
	}

	private void renderTooltip(PoseStack poseStack, List<String> lines, int mouseX, int mouseY, Font font) {
		Screen screen = mc.screen;
		if (screen != null) {
			// Convert strings to Components for modern tooltip rendering
			List<net.minecraft.network.chat.Component> components = lines.stream()
				.map(net.minecraft.network.chat.Component::literal)
				.collect(java.util.stream.Collectors.toList());
			screen.renderComponentTooltip(poseStack, components, mouseX, mouseY, font);
		}
	}
}
