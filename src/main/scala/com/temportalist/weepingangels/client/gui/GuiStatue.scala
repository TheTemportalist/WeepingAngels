package com.temportalist.weepingangels.client.gui

import com.temportalist.origin.library.client.utility.Rendering
import com.temportalist.origin.wrapper.client.gui.GuiScreenWrapper
import com.temportalist.weepingangels.common.entity.EntityAngel
import com.temportalist.weepingangels.common.network.PacketModifyStatue
import com.temportalist.weepingangels.common.tile.TEStatue
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.gui.{GuiButton, GuiTextField}
import net.minecraft.client.renderer.{OpenGlHelper, RenderHelper}
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class GuiStatue(val tileEntity: TEStatue) extends GuiScreenWrapper() {

	var facial: GuiButtonIterator = null
	var arms: GuiButtonIterator = null
	var rotationField: GuiTextField = null
	var corruptionField: GuiTextField = null
	// Variable used for rendering
	var angelEntity: EntityAngel = null

	val coords = Map(
		"face" -> Array[Int](30, 40),
		"arms" -> Array[Int](30, 80),
		"rot" -> Array[Int](40, 120, 100, 20),
		"corr" -> Array[Int](40, 160, 100, 20),
		"bkgd" -> Array[Int](200, 30, 150, 170),
		"angel" -> Array[Int](275, 190, 75)
	)

	this.setupGui("Edit Statue", null)

	override def initGui(): Unit = {
		super.initGui()

		var bID: Int = 0
		var coord: Array[Int] = null

		coord = this.coords.get("face").get
		this.facial = new GuiButtonIterator(bID, coord(0), coord(1), Array(
			"Calm", "Angry"
		))
		bID += 1
		this.buttonList.asInstanceOf[java.util.List[GuiButton]].add(this.facial)

		coord = this.coords.get("arms").get
		this.arms = new GuiButtonIterator(bID, coord(0), coord(1), Array(
			"Hiding", "Peaking", "Confident"
		))
		bID += 1
		this.buttonList.asInstanceOf[java.util.List[GuiButton]].add(this.arms)

		coord = this.coords.get("rot").get
		this.rotationField = new GuiTextField(
			this.fontRendererObj, coord(0), coord(1), coord(2), coord(3)
		)
		this.setupTextField(this.rotationField, 100)

		coord = this.coords.get("corr").get
		this.corruptionField = new GuiTextField(
			this.fontRendererObj, coord(0), coord(1), coord(2), coord(3)
		)
		this.setupTextField(this.corruptionField, 100)

		this.updateComponents()

	}

	def syncStatue(id: Int, value: Float): Unit = {
		new PacketModifyStatue(
			this.tileEntity, id, value
		).sendToBoth()
	}

	override def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Unit = {
		super.mouseClicked(mouseX, mouseY, mouseButton)
		if (this.facial.mousePressed(this.mc, mouseX, mouseY)) {
			this.facial.onPressed(mouseButton)
			this.syncStatue(1, this.facial.getIndex.asInstanceOf[Float])
		}
		if (this.arms.mousePressed(this.mc, mouseX, mouseY)) {
			this.arms.onPressed(mouseButton)
			this.syncStatue(2, this.arms.getIndex.asInstanceOf[Float])
		}
	}

	override def canKeyType(textField: GuiTextField, letter: Char, key: Int): Boolean = {
		if (key == 14) return true
		if (textField == this.rotationField)
			Character.isDigit(letter) || letter == '.' || letter == '-'
		else if (textField == this.corruptionField)
			Character.isDigit(letter) || letter == '-'
		else true
	}

	override def onKeyTyped(textField: GuiTextField): Unit = {
		if (textField == this.rotationField) {
			this.syncStatue(3, this.parseFromField(this.rotationField.getText))
		}
		else if (textField == this.corruptionField) {
			this.syncStatue(4, this.parseFromField(this.corruptionField.getText).toInt)
		}
	}

	def parseFromField(text: String): Float = {
		var value: Float = 0.0F
		try {
			value = text.toFloat
		}
		catch {
			case e: NumberFormatException =>
		}
		value
	}

	def updateComponents(): Unit = {
		this.facial.updateIndexAndText(this.tileEntity.getFacialState)
		this.arms.updateIndexAndText(this.tileEntity.getArmState)
		this.rotationField.setText(this.tileEntity.getRotation + "")
		this.corruptionField.setText(this.tileEntity.getCorruption() + "")
	}

	override def doesGuiPauseGame(): Boolean = {
		false
	}

	override protected def drawGuiBackground(): Unit = {
		GL11.glPushMatrix()
		// todo move this to a common helper class
		HUDOverlay.renderBlackoutWithAlpha(0.7F, this.width, this.height)
		// Draw statue background
		val coord: Array[Int] = this.coords.get("bkgd").get
		HUDOverlay.renderBlackoutWithAlpha(1.0F, coord(0), coord(1), coord(2), coord(3))
		GL11.glPopMatrix()
	}

	override protected def drawGuiBackgroundLayer(mouseX: Int, mouseY: Int,
			renderPartialTicks: Float): Unit = {
		super.drawGuiBackgroundLayer(mouseX, mouseY, renderPartialTicks)

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
		val x: Int = 140 + 125
		val y: Int = 30 + 150

		if (this.angelEntity == null)
			this.angelEntity = new EntityAngel(this.mc.theWorld) {
				override def getCorruption(): Int = tileEntity.getCorruption()
			}
		this.angelEntity.setAngryState(this.tileEntity.getFacialState.asInstanceOf[Byte])
		this.angelEntity.setArmState(this.tileEntity.getArmState.asInstanceOf[Byte])
		this.angelEntity.setYoungestAdult()

		val coord: Array[Int] = this.coords.get("angel").get
		this.drawStatue(
			coord(0), coord(1), coord(2),
			-this.tileEntity.getRotation, this.angelEntity
		)

	}

	override def drawTitle(x: Int, y: Int): Unit = {
		this.drawString(this.title,
			this.getX() + (this.getWidth() / 2) - (this.getStringWidth(this.title) / 2), 10, -1
		)
	}

	def drawStatue(x: Int, y: Int, scale: Int, rotation: Float, entity: EntityLivingBase): Unit = {

		//GlStateManager.enableColorMaterial()
		GL11.glEnable(GL11.GL_COLOR_MATERIAL)

		//GlStateManager.pushMatrix()
		GL11.glPushMatrix()
		// Move to position on screen
		//GlStateManager.translate
		GL11.glTranslatef(x.asInstanceOf[Float], y.asInstanceOf[Float], 50.0F)

		// Scale
		//GlStateManager.scale(
		GL11.glScalef(
			(-scale).asInstanceOf[Float],
			scale.asInstanceOf[Float],
			scale.asInstanceOf[Float]
		)
		// Turn right side up
		//GlStateManager.rotate
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F)
		// todo apply and revert? what effect does this have?
		//GlStateManager.rotate
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F)
		RenderHelper.enableStandardItemLighting
		//GlStateManager.rotate
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F)
		//GlStateManager.rotate
		GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F)
		//GlStateManager.rotate
		GL11.glRotatef(-rotation, 0.0F, 1.0F, 0.0F)
		//GlStateManager.translate
		GL11.glTranslatef(0.0F, entity.getYOffset.asInstanceOf[Float], 0.0F)

		Rendering.renderManager.playerViewY = 180.0F
		Rendering.renderManager.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0F, 1F)
		//GlStateManager.popMatrix()
		GL11.glPopMatrix()

		RenderHelper.disableStandardItemLighting

		//GlStateManager.disableRescaleNormal()
		//GL11.glDisable(GL11.GL_RES)

		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit)
		//GlStateManager.disableTexture2D()
		GL11.glDisable(GL11.GL_TEXTURE_2D)
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit)

	}

}
