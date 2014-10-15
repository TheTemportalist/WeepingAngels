package com.countrygamer.weepingangels.client.gui

import com.countrygamer.cgo.library.common.nethandler.PacketHandler
import com.countrygamer.cgo.wrapper.client.gui.GuiScreenWrapper
import com.countrygamer.weepingangels.common.WeepingAngels
import com.countrygamer.weepingangels.common.network.PacketSetTime
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.EntityPlayer

/**
 *
 *
 * @author CountryGamer
 */
@SideOnly(Side.CLIENT)
class GuiTimeManipulation(val player: EntityPlayer) extends GuiScreenWrapper() {

	var dawn, noon, sunset, midnight: GuiButton = null
	var p1hr, p2hr: GuiButton = null

	override def initGui(): Unit = {
		super.initGui()

		val guiL: Int = this.getGuiLeft()
		val guiT: Int = this.getGuiTop()

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
			PacketHandler.sendToServer(WeepingAngels.pluginID,
				new PacketSetTime(setTime)
			)
		}
		Minecraft.getMinecraft.displayGuiScreen(null)
	}

}
