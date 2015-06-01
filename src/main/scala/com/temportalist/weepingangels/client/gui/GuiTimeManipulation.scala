package com.temportalist.weepingangels.client.gui

import com.temportalist.origin.api.client.gui.GuiScreenBase
import com.temportalist.origin.api.client.utility.Rendering
import com.temportalist.weepingangels.common.WeepingAngels
import com.temportalist.weepingangels.common.network.PacketSetTime
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.gui.{Gui, ScaledResolution}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class GuiTimeManipulation(val player: EntityPlayer) extends GuiScreenBase with IGuiDynamic {

	override def drawMenu(zLevel: Double, resolution: ScaledResolution): Unit = {
		val angle: Double = -this.correctAngle(this.getMouseAngle())
		//GlStateManager.pushMatrix()
		GL11.glPushMatrix()

		Rendering.bindResource(new ResourceLocation(WeepingAngels.MODID, "textures/gui/clock.png"))
		//GlStateManager.rotate
		GL11.glTranslated(resolution.getScaledWidth / 2, resolution.getScaledHeight / 2, 0)
		GL11.glRotatef(angle.toFloat, 0, 0, 1)
		GL11.glTranslated(-80, -80, 0)
		//this.drawGradientRect(100, 100, 0, 0, 160, 160)
		Gui.func_146110_a(0, 0, 0, 0, 160, 160, 160, 160)

		//GlStateManager.popMatrix()
		GL11.glPopMatrix()
	}

	/*
	var dawn, noon, sunset, midnight: GuiButton = null
	var p1hr, p2hr: GuiButton = null

	override def initGui(): Unit = {
		super.initGui()

		val guiL: Int = this.getX()
		val guiT: Int = this.getY()

		this.dawn = this.makeButton(guiL + 10, guiT + 10, 40, 20, "Dawn")
		this.noon = this.makeButton(guiL + 60, guiT + 10, 40, 20, "Noon")
		this.sunset = this.makeButton(guiL + 100, guiT + 10, 40, 20, "Sunset")
		this.midnight = this.makeButton(guiL + 140, guiT + 10, 40, 20, "Midnight")

	}

	def makeButton(x: Int, y: Int, w: Int, h: Int, text: String): GuiButton = {
		val button: GuiButton = new GuiButton(this.buttonList.size(), x, y, w, h, text)
		this.addButton(button)
		button
	}

	override def actionPerformed(button: GuiButton): Unit = {
		var setTime: Int = -1
		if (button.id == this.dawn.id) {
			setTime = 0 // day = 1000
		}
		else if (button.id == this.noon.id) {
			setTime = 6000
		}
		else if (button.id == this.sunset.id) {
			setTime = 12500 // night = 13000
		}
		else if (button.id == this.midnight.id) {
			setTime = 17500
		}
		if (setTime >= 0) {
			PacketHandler.sendToServer(WeepingAngels.MODID,
				new PacketSetTime(setTime)
			)
		}
		Minecraft.getMinecraft.displayGuiScreen(null)
	}
	*/

	override def onGuiClosed(): Unit = {
		super.onGuiClosed()
		val angle: Double = 360 - this.correctAngle(this.getMouseAngle())
		var time: Double = angle / 360D * 24000 - 18000
		if (time < 0) time += 24000
		new PacketSetTime(player.getEntityWorld.provider.dimensionId, time.toInt).sendToServer()
	}

}
