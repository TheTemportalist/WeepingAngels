package com.temportalist.weepingangels.client.render

import com.temportalist.origin.wrapper.client.render.TERenderer
import com.temportalist.weepingangels.client.render.models.ModelWeepingAngel
import com.temportalist.weepingangels.common.WAOptions
import com.temportalist.weepingangels.common.entity.EntityAngel
import com.temportalist.weepingangels.common.tile.TEStatue
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
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

		val pos: BlockPos = statueTE.getPos
		//GlStateManager.translate(-pos.getX, -pos.getY, -pos.getZ)
		// Render the slab
		/*
		Minecraft.getMinecraft.getBlockRendererDispatcher.renderBlock(
			Blocks.stone_slab.getDefaultState, statueTE.getPos, statueTE.getWorld,
			TessRenderer.getTess().getWorldRenderer
		)
		*/
		this.bindTexture(TextureMap.locationBlocksTexture)
		Minecraft.getMinecraft.getBlockRendererDispatcher.renderBlockBrightness(
			Blocks.stone_slab.getDefaultState, 1.0F
		)
		//GlStateManager.translate(.5, .5, .5)

	}

}
