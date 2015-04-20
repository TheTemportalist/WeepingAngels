package com.temportalist.weepingangels.client.gui

import com.temportalist.origin.library.client.utility.{GuiHelper, Keys, Rendering}
import com.temportalist.origin.wrapper.client.gui.IGuiScreen
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.TickEvent
import cpw.mods.fml.relauncher.{SideOnly, Side}
import net.minecraft.client.gui.ScaledResolution
import net.minecraftforge.client.event.RenderGameOverlayEvent
import org.lwjgl.input.Mouse

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
trait IGuiDynamic extends IGuiScreen {

	final def render(): Unit = {
		this.drawMenu(0.05D, new ScaledResolution(
			Rendering.mc, Rendering.mc.displayWidth, Rendering.mc.displayHeight
		))
	}

	final def dismiss(): Unit = {
		this.mc.displayGuiScreen(null)
	}

	override def drawScreen(mouseX: Int, mouseY: Int, renderPartialTicks: Float): Unit = {}

	final def getMouseAngle(): Double = {
		getRelativeAngle(
			this.mc.displayWidth / 2, this.mc.displayHeight / 2, Mouse.getX, Mouse.getY
		)
	}

	final def getRelativeAngle(originX: Double, originY: Double, x: Double, y: Double): Double = {
		var angle: Double = Math.toDegrees(Math.atan2(y - originY, x - originX))

		// Remove 90 from the angle to make 0 and 180 at the top and bottom of the screen
		angle = angle - 90

		if (angle < 0) {
			angle = angle + 360
		}
		else if (angle > 360) {
			angle = angle - 360
		}

		angle
	}

	final def correctAngle(angle: Double): Double = {
		var angle2: Double = angle
		if (angle < 0) {
			angle2 = angle + 360
		}
		else if (angle > 360) {
			angle2 = angle - 360
		}

		angle2
	}

	def doesMouseAffect(): Boolean = Keys.isMouseDownLeft()

	def drawMenu(zLevel: Double, resolution: ScaledResolution): Unit

}

@SideOnly(Side.CLIENT)
object GuiDynamic {

	def getDynamic(): IGuiDynamic = {
		if (Rendering.mc.currentScreen != null &&
				Rendering.mc.currentScreen.isInstanceOf[IGuiDynamic])
			Rendering.mc.currentScreen.asInstanceOf[IGuiDynamic]
		else null
	}

	def display(menu: IGuiDynamic): Unit = {
		GuiHelper.display(menu)

		// this stuff grabs the cursor so it doesnt render in the radial menu
		Rendering.mc.inGameHasFocus = true
		Rendering.mc.mouseHelper.grabMouseCursor()

	}

	@SubscribeEvent
	def overlayEvent(event: RenderGameOverlayEvent): Unit = {
		if (event.`type` == RenderGameOverlayEvent.ElementType.CROSSHAIRS &&
				this.getDynamic() != null)
			event.setCanceled(true)
	}

	@SubscribeEvent
	def onClientTick(event: TickEvent.ClientTickEvent): Unit = {
		val dynamic: IGuiDynamic = this.getDynamic()
		if (dynamic != null && event.phase == TickEvent.Phase.START) {
			if (dynamic.doesMouseAffect()) {
				/* todo work on additive angles
				dynamic.dX += Mouse.getDX
				dynamic.dY += Mouse.getDY
				*/
			}
		}
	}

	@SubscribeEvent
	def onRenderTick(event: TickEvent.RenderTickEvent): Unit = {
		val dynamic: IGuiDynamic = this.getDynamic()
		if (dynamic != null) {
			if (event.phase == TickEvent.Phase.START) {
				Mouse.getDX
				Mouse.getDY
				Rendering.mc.mouseHelper.deltaX = 0
				Rendering.mc.mouseHelper.deltaY = 0
			}
			else if (!Rendering.mc.gameSettings.hideGUI && !Rendering.mc.isGamePaused) {
				dynamic.render()
			}
		}
	}

}
