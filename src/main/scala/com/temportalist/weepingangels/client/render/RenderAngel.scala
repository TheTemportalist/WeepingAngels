package com.temportalist.weepingangels.client.render

import com.temportalist.weepingangels.client.render.models.ModelWeepingAngel
import com.temportalist.weepingangels.common.WAOptions
import net.minecraft.client.renderer.entity.{RenderLiving, RenderManager}
import net.minecraft.entity.{Entity, EntityLiving}
import net.minecraft.util.ResourceLocation

/**
 *
 *
 * @author TheTemportalist 1/26/15
 */
class RenderAngel(manager: RenderManager) extends RenderLiving(
	manager, new ModelWeepingAngel(), 0.5F
) {

	override def doRender(entity: EntityLiving, x: Double, y: Double, z: Double, p_76986_8_ : Float,
			partialTicks: Float): Unit = {
		println ("render")
		super.doRender(entity, x, y, z, p_76986_8_, partialTicks)
	}

	override def getEntityTexture(entity: Entity): ResourceLocation = WAOptions.weepingAngel1

}
