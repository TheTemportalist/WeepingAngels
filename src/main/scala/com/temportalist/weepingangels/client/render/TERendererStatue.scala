package com.temportalist.weepingangels.client.render

import com.temportalist.origin.api.client.render.TERenderer
import com.temportalist.origin.api.client.utility.{TessRenderer, Rendering}
import com.temportalist.weepingangels.client.render.models.ModelWeepingAngel
import com.temportalist.weepingangels.common.WAOptions
import com.temportalist.weepingangels.common.entity.EntityAngel
import com.temportalist.weepingangels.common.tile.TEStatue
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.{RenderBlocks, RenderHelper}
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import org.lwjgl.opengl.GL11

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class TERendererStatue() extends TERenderer(WAOptions.weepingAngel1) {

	// create a variable for the entity instance
	var angelEntity: EntityAngel = null
	// Get an instance of the angel model
	val angelModel: ModelWeepingAngel = new ModelWeepingAngel()
	var renderer: RenderBlocks = null

	override protected def render(tileEntity: TileEntity, renderPartialTicks: Float,
			f5: Float): Unit = {
		// Check for an invalid
		if (tileEntity == null || !tileEntity.isInstanceOf[TEStatue]) return
		if (tileEntity.getBlockMetadata != 0) return

		// Cast
		val statueTE: TEStatue = tileEntity.asInstanceOf[TEStatue]

		{
			GL11.glPushMatrix()
			RenderHelper.disableStandardItemLighting()
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
			GL11.glEnable(GL11.GL_BLEND)
			// Weird rendering stuff
			if (Minecraft.isAmbientOcclusionEnabled) {
				GL11.glShadeModel(GL11.GL_SMOOTH)
			}
			else {
				GL11.glShadeModel(GL11.GL_FLAT)
			}

			TessRenderer.startQuads()
			TessRenderer.getTess().setTranslation(
				-(statueTE.xCoord + 0.5),
				-(statueTE.yCoord + 0.5),
				-(statueTE.zCoord + 0.5)
			)
			this.bindTexture(TextureMap.locationBlocksTexture)
			if (this.renderer == null) this.renderer = new RenderBlocks(Rendering.mc.theWorld)
			this.renderer.renderBlockByRenderType(
				Blocks.stone_slab, statueTE.xCoord, statueTE.yCoord, statueTE.zCoord)
			TessRenderer.draw()
			TessRenderer.getTess().setTranslation(0, 0, 0)
			RenderHelper.enableStandardItemLighting()
			GL11.glPopMatrix()
		}

		/*
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F)
		GlStateManager.rotate(-90, 0, 1, 0)
		GlStateManager.translate(0.0F, -1.5F, 0.0F)
		*/
		//GL11.glRotated(180, 0, 0, 1)
		GL11.glRotated(180, 0, 1, 0)
		//GL11.glTranslated(0, -1.5, 0)
		//GlStateManager.rotate
		GL11.glRotatef(statueTE.getRotation, 0.0F, 1.0F, 0.0F)

		if (this.angelEntity == null)
			this.angelEntity = new EntityAngel(statueTE.getWorldObj) {
				override def getCorruption(): Int = {
					statueTE.getCorruption()
				}

				override def getTextureID(isAngry: Boolean): Int = statueTE.getEntityTex(isAngry)
			}
		this.angelModel.isChild = false
		this.angelEntity.setAngryState(statueTE.getFacialState.asInstanceOf[Byte])
		this.angelEntity.setArmState(statueTE.getArmState.asInstanceOf[Byte])
		//println(statueTE.texIDs(0))
		RenderManager.instance.renderEntityWithPosYaw(angelEntity, 0, 0, 0, 0, 1F)
		/*
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, statueTE.getEntityTex(
			this.angelEntity.getAngryState > 0
		))
		this.angelModel.render(angelEntity, 0, 0, 0, 0, 0, f5)
		*/

	}

}
