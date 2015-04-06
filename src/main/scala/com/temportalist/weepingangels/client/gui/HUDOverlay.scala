package com.temportalist.weepingangels.client.gui

import com.temportalist.origin.library.client.utility.{Rendering, TessRenderer}
import com.temportalist.origin.wrapper.common.extended.ExtendedEntityHandler
import com.temportalist.weepingangels.common.extended.AngelPlayer
import com.temportalist.weepingangels.common.{WAOptions, WeepingAngels}
import cpw.mods.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.{Gui, ScaledResolution}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType
import org.lwjgl.opengl.GL11

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
object HUDOverlay extends Gui() {

	val mc: Minecraft = Minecraft.getMinecraft
	val iconSize: Int = 9

	val healthTexture: ResourceLocation = new ResourceLocation(WeepingAngels.MODID,
		"textures/gui/angelHealth.png")
	val blackout: ResourceLocation = new ResourceLocation(WeepingAngels.MODID,
		"textures/gui/blackBlur.png"
	)

	var resolution: ScaledResolution = null

	@SubscribeEvent(priority = EventPriority.NORMAL)
	def renderOverlay(event: RenderGameOverlayEvent.Post): Unit = {

		if (event.isCanceled) {
			//return
		}

		val angelPlayer: AngelPlayer = ExtendedEntityHandler
				.getExtended(this.mc.thePlayer, classOf[AngelPlayer]).asInstanceOf[AngelPlayer]

		if (!angelPlayer.converting()) return

		val angelHealth: Float = angelPlayer.getAngelHealth()
		//System.out.println(angelHealth)

		val width: Int = event.resolution.getScaledWidth
		val height: Int = event.resolution.getScaledHeight

		// Used for transparency because it is immediately before crosshairs, which precedes all hud things
		if (event.`type` == ElementType.HELMET) {
			this.renderBlackoutWithAlpha(angelPlayer.getOpacityForBlackout(), width, height)
		}

		// Triggered to render the angel health

		if (event.`type` == ElementType.FOOD) {
			GL11.glPushMatrix()
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
			GL11.glDisable(GL11.GL_LIGHTING)
			GL11.glEnable(GL11.GL_BLEND)

			val left: Int = width / 2 - 91 + 100
			val top: Int = height - 39 - 10

			val healthBarX: Int = left + 0
			val healthBarY: Int = top

			Rendering.bindResource(this.healthTexture)

			// Draw background hearts
			for (i <- 0 until WAOptions.maxAngelHealth / 2) {
				this.drawTexturedModalRect(healthBarX + (i * 8), healthBarY, 0, 9, this.iconSize,
					this.iconSize)
			}

			val fullHearts: Int = Math.floor(angelHealth / 2.0F).asInstanceOf[Int]
			val halfHearts: Int = Math.floor(angelHealth - (fullHearts * 2)).asInstanceOf[Int]

			for (i <- 0 until fullHearts) {
				this.drawTexturedModalRect(healthBarX + (i * 8), healthBarY, 0, 0, this.iconSize,
					this.iconSize)
			}

			for (i <- 0 until halfHearts) {
				this.drawTexturedModalRect(healthBarX + (fullHearts * 8) + (i * 8), healthBarY, 9,
					0, this.iconSize, this.iconSize)
			}

			GL11.glPopMatrix()
		}

	}

	def renderBlackoutWithAlpha(alpha: Float, width: Int, height: Int): Unit = {
		this.renderBlackoutWithAlpha(alpha, 0.0D, 0.0D, width, height)
	}

	def renderBlackoutWithAlpha(alpha: Float, x: Double, y: Double, width: Double,
			height: Double): Unit = {
		GL11.glPushMatrix()

		GL11.glEnable(GL11.GL_BLEND)
		GL11.glDisable(GL11.GL_DEPTH_TEST)
		GL11.glDepthMask(false)
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

		GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha)

		GL11.glDisable(GL11.GL_ALPHA_TEST)

		Rendering.bindResource(this.blackout)

		TessRenderer.startQuads()
		TessRenderer.addVertex(x + 0.0D, y + height, -90.0D, 0.0D, 1.0D)
		TessRenderer.addVertex(x + width, y + height, -90.0D, 1.0D, 1.0D)
		TessRenderer.addVertex(x + width, y + 0.0D, -90.0D, 1.0D, 0.0D)
		TessRenderer.addVertex(x + 0.0D, y + 0.0D, -90.0D, 0.0D, 0.0D)
		TessRenderer.draw()

		GL11.glDepthMask(true)
		GL11.glEnable(GL11.GL_DEPTH_TEST)
		GL11.glDisable(GL11.GL_BLEND)

		GL11.glPopMatrix()
	}

}
