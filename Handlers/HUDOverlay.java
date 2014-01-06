package WeepingAngels.Handlers;

import java.util.Collection;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;

import WeepingAngels.WeepingAngelsMod;
import WeepingAngels.Handlers.Player.ExtendedPlayer;
import WeepingAngels.lib.Reference;

public class HUDOverlay extends Gui {

	private Minecraft mc;
	private static final int BUFF_ICON_SIZE = 9;
	private static final int BUFF_ICON_SPACING = HUDOverlay.BUFF_ICON_SIZE + 2;
	private static final int BUFF_ICON_BASE_U_OFFSET = 0;
	private static final int BUFF_ICON_BASE_V_OFFSET = 198;
	private static final int BUFF_ICONS_PER_ROW = 8;

	public HUDOverlay(Minecraft mc) {
		super();
		this.mc = mc;
	}

	private static final ResourceLocation health = new ResourceLocation(
			Reference.MOD_ID_LOWERCASE, "textures/gui/angelHealth.png");

	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void angelConvertHUDOverlay(RenderGameOverlayEvent.Post event) {
		if (event.isCancelable() || event.type != ElementType.EXPERIENCE) {
			return;
		}

		int width = event.resolution.getScaledWidth();
		int xPos = (int) (width / 2 * 0.04) + width / 2;
		int height = event.resolution.getScaledHeight();
		int yPos = (height / 2 + height / 4) + (int) (height / 4 * 0.19);

		ExtendedPlayer playerProperties = ExtendedPlayer.get(this.mc.thePlayer);
		float angelHealth = playerProperties.getAngelHealth();
		if (angelHealth > 0.0F) {
			// angelHealth = 5;

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			this.mc.getTextureManager().bindTexture(health);

			for (int i = 0; i < WeepingAngelsMod.maxHealth / 2; i++) {
				this.drawTexturedModalRect(xPos + (i * 9), yPos, 0, 9,
						BUFF_ICON_SIZE, BUFF_ICON_SIZE);
			}

			int fullHearts = (int) Math.floor(angelHealth / 2);
			int halfHearts = (int) (angelHealth - (fullHearts * 2));
			int xPosLastFullHeart = 0;
			for (int i = 0; i < fullHearts; i++) {
				xPosLastFullHeart = xPos + (i * 9);
				this.drawTexturedModalRect(xPosLastFullHeart, yPos, 0, 0,
						BUFF_ICON_SIZE, BUFF_ICON_SIZE);
			}
			for (int i = 0; i < halfHearts; i++) {
				if (xPosLastFullHeart <= 0)
					this.drawTexturedModalRect(xPos + (i * 9), yPos, 9, 0,
							BUFF_ICON_SIZE, BUFF_ICON_SIZE);
				else
					this.drawTexturedModalRect(xPosLastFullHeart
							+ ((i + 1) * 9), yPos, 9, 0, BUFF_ICON_SIZE,
							BUFF_ICON_SIZE);
			}
			// this.drawTexturedModalRect(xPos, yPos, 0, 0, BUFF_ICON_SIZE,
			// BUFF_ICON_SIZE);

		}
	}

}
