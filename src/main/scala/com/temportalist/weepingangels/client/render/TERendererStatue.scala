package com.temportalist.weepingangels.client.render

import com.temportalist.origin.library.client.utility.TessRenderer
import com.temportalist.origin.wrapper.client.render.TERenderer
import com.temportalist.weepingangels.client.render.models.ModelWeepingAngel
import com.temportalist.weepingangels.common.WAOptions
import com.temportalist.weepingangels.common.entity.EntityWeepingAngel
import com.temportalist.weepingangels.common.tile.TEStatue
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
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
	var angelEntity: EntityWeepingAngel = null
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

		GlStateManager.translate(-0.5, -0.5, -0.5)
		// Render the slab
		Minecraft.getMinecraft.getBlockRendererDispatcher.renderBlock(
			Blocks.stone_slab.getDefaultState, statueTE.getPos, statueTE.getWorld,
			TessRenderer.getTess().getWorldRenderer
		)
		GlStateManager.translate(.5, .5, .5)

	}

}
