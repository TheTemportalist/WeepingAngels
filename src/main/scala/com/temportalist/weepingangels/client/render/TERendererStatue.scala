package com.temportalist.weepingangels.client.render

import com.temportalist.origin.library.client.utility.Rendering
import com.temportalist.origin.wrapper.client.render.TERenderer
import com.temportalist.weepingangels.client.render.models.ModelWeepingAngel
import com.temportalist.weepingangels.common.WAOptions
import com.temportalist.weepingangels.common.entity.EntityAngel
import com.temportalist.weepingangels.common.tile.TEStatue
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

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

	override protected def render(tileEntity: TileEntity, renderPartialTicks: Float,
			f5: Float): Unit = {
		// todo fix GL things, see Parker
		// Check for an invalid
		if (tileEntity == null || !tileEntity.isInstanceOf[TEStatue]) return
		if (tileEntity.getBlockMetadata != 0) return

		// Cast
		val statueTE: TEStatue = tileEntity.asInstanceOf[TEStatue]

		GlStateManager.translate(-0.5, -0.5, 0.5)
		this.bindTexture(TextureMap.locationBlocksTexture)
		Minecraft.getMinecraft.getBlockRendererDispatcher.renderBlockBrightness(
			Blocks.stone_slab.getDefaultState, 1.0F
		)
		GlStateManager.translate(0.5, 0.5, 0.5)

		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F)
		GlStateManager.rotate(-90, 0, 1, 0)
		GlStateManager.translate(0.0F, -1.5F, 0.0F)
		// Rotate the statue according to the rotation stored in the statue's data
		GlStateManager.rotate(statueTE.getRotation, 0.0F, 1.0F, 0.0F)

		this.angelEntity = new EntityAngel(statueTE.getWorld)
		this.angelModel.isChild = false
		this.angelEntity.setAngryState(statueTE.getFacialState.asInstanceOf[Byte])
		this.angelEntity.setArmState(statueTE.getArmState.asInstanceOf[Byte])
		// todo corruption
		/*
		setGrowingAge correllated with corruption
		*/
		statueTE.getFacialState match {
			case 0 => Rendering.bindResource(WAOptions.weepingAngel1)
			case 1 => Rendering.bindResource(WAOptions.weepingAngel2)
			case _ =>
		}
		// Render the model
		this.angelModel.render(angelEntity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, f5)

	}

}
