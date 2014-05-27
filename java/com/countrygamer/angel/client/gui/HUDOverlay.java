package com.countrygamer.angel.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import com.countrygamer.angel.common.WeepingAngels;
import com.countrygamer.angel.common.extended.ExtendedAngelPlayer;
import com.countrygamer.core.Base.Plugin.ExtendedEntity;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HUDOverlay extends Gui {
	
	private Minecraft			mc;
	private static final int	BUFF_ICON_SIZE			= 9;
	public static final int		maxHUDHealth			= 20;
	
	public HUDOverlay(Minecraft mc) {
		super();
		this.mc = mc;
	}
	
	private static final ResourceLocation	health	= new ResourceLocation(WeepingAngels.PLUGIN_ID,
															"textures/gui/angelHealth.png");
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		if (event.isCancelable() || event.type != ElementType.EXPERIENCE) {
			return;
		}
		
		int width = event.resolution.getScaledWidth();
		int xPos = (int) (width / 2 * 0.04) + width / 2;
		int height = event.resolution.getScaledHeight();
		int yPos = (height / 2 + height / 4) + (int) (height / 4 * 0.19);
		ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings,
				this.mc.displayWidth, this.mc.displayHeight);
		int k = scaledresolution.getScaledWidth();
		int l = scaledresolution.getScaledHeight();
		
		ExtendedAngelPlayer angelPlayer = (ExtendedAngelPlayer) ExtendedEntity.getExtended(
				this.mc.thePlayer, ExtendedAngelPlayer.class);
		float angelHealth = angelPlayer.getAngelHealth();
		if (angelHealth > 0.0F) {
			
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			this.mc.getTextureManager().bindTexture(health);
			
			for (int i = 0; i < HUDOverlay.maxHUDHealth / 2; i++) {
				this.drawTexturedModalRect(xPos + (i * 9), yPos, 0, 9, BUFF_ICON_SIZE,
						BUFF_ICON_SIZE);
			}
			
			int fullHearts = (int) Math.floor(angelHealth / 2);
			int halfHearts = (int) (angelHealth - (fullHearts * 2));
			int xPosLastFullHeart = 0;
			for (int i = 0; i < fullHearts; i++) {
				xPosLastFullHeart = xPos + (i * 9);
				this.drawTexturedModalRect(xPosLastFullHeart, yPos, 0, 0, BUFF_ICON_SIZE,
						BUFF_ICON_SIZE);
			}
			for (int i = 0; i < halfHearts; i++) {
				if (xPosLastFullHeart <= 0)
					this.drawTexturedModalRect(xPos + (i * 9), yPos, 9, 0, BUFF_ICON_SIZE,
							BUFF_ICON_SIZE);
				else
					this.drawTexturedModalRect(xPosLastFullHeart + ((i + 1) * 9), yPos, 9, 0,
							BUFF_ICON_SIZE, BUFF_ICON_SIZE);
			}
			
			this.renderBlur(k, l, angelHealth / (float) (HUDOverlay.maxHUDHealth));
		}
	}
	
	protected static final ResourceLocation	blackBlur	= new ResourceLocation(
																WeepingAngels.PLUGIN_ID,
																"textures/gui/blackBlur.png");
	
	protected void renderBlur(int x, int y, float alpha) {
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		
		this.mc.getTextureManager().bindTexture(blackBlur);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(0.0D, (double) y, -90.0D, 0.0D, 1.0D);
		tessellator.addVertexWithUV((double) x, (double) y, -90.0D, 1.0D, 1.0D);
		tessellator.addVertexWithUV((double) x, 0.0D, -90.0D, 1.0D, 0.0D);
		tessellator.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
		tessellator.draw();
		
		GL11.glDisable(GL11.GL_BLEND);
		
	}
	
}