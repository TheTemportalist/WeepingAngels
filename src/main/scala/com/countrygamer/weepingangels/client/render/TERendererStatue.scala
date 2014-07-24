package com.countrygamer.weepingangels.client.render

import com.countrygamer.cgo.common.lib.util.UtilRender
import com.countrygamer.cgo.wrapper.client.render.TERenderer
import com.countrygamer.weepingangels.client.render.models.ModelWeepingAngel
import com.countrygamer.weepingangels.common.WAOptions
import com.countrygamer.weepingangels.common.entity.EntityWeepingAngel
import com.countrygamer.weepingangels.common.tile.TileEntityStatue
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.{RenderBlocks, RenderHelper, Tessellator}
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import org.lwjgl.opengl.GL11

/**
 *
 *
 * @author CountryGamer
 */
@SideOnly(Side.CLIENT)
class TERendererStatue() extends TERenderer(WAOptions.weepingAngel1) {

	// create a variable for the entity instance
	var angelEntity: EntityWeepingAngel = null
	// Get an instance of the angel model
	val angelModel: ModelWeepingAngel = new ModelWeepingAngel()

	// Default Constructor
	{

	}

	// End Constructor

	// Other Constructors

	// End Constructors

	override protected def render(tileEntity: TileEntity, renderPartialTicks: Float,
			f5: Float): Unit = {
		// Check for an invalid
		if (tileEntity == null || !tileEntity.isInstanceOf[TileEntityStatue]) return

		// Cast
		val statueTE: TileEntityStatue = tileEntity.asInstanceOf[TileEntityStatue]

		// New matrix
		GL11.glPushMatrix()

		// Check the metadata
		if (statueTE.getBlockMetadata == 0) {
			// New matrix
			GL11.glPushMatrix()

			// Rotate back rightside up (due to entity rendering)
			GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F)
			// Translate to correct positioning
			GL11.glTranslatef(0.0F, -1.5F, 0.0F)

			// Rotate the statue according to the rotation stored in the statue's data
			GL11.glRotatef(statueTE.getRotation(), 0.0F, 1.0F, 0.0F)

			// Create a new entity instance to modify
			this.angelEntity = new EntityWeepingAngel(statueTE.getWorldObj)

			this.angelEntity.setAngryState(statueTE.getFacialState().asInstanceOf[Byte])
			this.angelEntity.setArmState(statueTE.getArmState().asInstanceOf[Byte])

			statueTE.getFacialState() match {
				case 0 =>
					UtilRender.bindResource(WAOptions.weepingAngel1)
				case 1 =>
					UtilRender.bindResource(WAOptions.weepingAngel2)
			}

			// Render the model
			this.angelModel
					.render(angelEntity, 0.0F, 0.0F, 0.0F, 0.0F,
			            0.0F, f5)

			// End matrix for entity rendering
			GL11.glPopMatrix()

			// New matrix for slab rendering
			GL11.glPushMatrix()

			// Bind the texture for blocks
			UtilRender.bindResource(TextureMap.locationBlocksTexture)

			// Dont light me up!
			RenderHelper.disableStandardItemLighting()

			// Blend fucntions
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
			GL11.glEnable(GL11.GL_BLEND)

			// Weird rendering stuff
			if (Minecraft.isAmbientOcclusionEnabled) {
				GL11.glShadeModel(GL11.GL_SMOOTH)
			}
			else {
				GL11.glShadeModel(GL11.GL_FLAT)
			}

			// Tessellation!
			val tess: Tessellator = Tessellator.instance
			tess.startDrawingQuads()

			// New instance for rendering blocks
			val renderer: RenderBlocks = new RenderBlocks(statueTE.getWorldObj)

			// Translate so that actualy coords can be passed
			tess.setTranslation(-(statueTE.xCoord + 0.5), -(statueTE.yCoord + 0.5),
				-(statueTE.zCoord + 0.5))

			// Render the slab
			renderer.renderBlockByRenderType(Blocks.stone_slab, statueTE.xCoord, statueTE.yCoord,
				statueTE.zCoord)

			// Actually draw it
			tess.draw()
			// Reset translation
			tess.setTranslation(0, 0, 0)

			// Reenable lighting
			RenderHelper.enableStandardItemLighting()

			// End matrix for slab render
			GL11.glPopMatrix()

		}

		// End matrix for rendering
		GL11.glPopMatrix()

	}

}
