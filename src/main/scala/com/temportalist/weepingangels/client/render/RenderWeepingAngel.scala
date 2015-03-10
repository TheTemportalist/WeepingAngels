package com.temportalist.weepingangels.client.render

import com.temportalist.weepingangels.client.render.models.ModelWeepingAngel
import com.temportalist.weepingangels.common.WAOptions
import net.minecraft.client.renderer.entity.{RenderLiving, RenderManager}
import net.minecraft.entity.{Entity, EntityLiving}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class RenderWeepingAngel(manager: RenderManager)
		extends RenderLiving(manager, new ModelWeepingAngel(), 0.5F) {

	var texture: ResourceLocation = WAOptions.weepingAngel1

	override def getEntityTexture(entity: Entity): ResourceLocation = {
		//println ("get texture")
		this.texture
	}

	override def doRender(entity: Entity, x: Double, y: Double, z: Double, p_76986_8_ : Float,
			partialTicks: Float): Unit = {
		//println ("render e")
		super.doRender(entity, x, y, z, p_76986_8_, partialTicks)
	}

	override def doRender(entity: EntityLiving, d1: Double, d2: Double, d3: Double, f1: Float,
			f2: Float): Unit = {


		//println()
		this.bindEntityTexture(entity)

		super.doRender(entity, d1, d2, d3, f1, f2)
	}

	/*
	override def bindEntityTexture(entity: Entity): Boolean = {
		entity match {
			case angel: EntityWeepingAngel =>
				GlStateManager.bindTexture(angel.getTextureID())
				true
			case _ =>
				false
		}
	}
	*/

}
