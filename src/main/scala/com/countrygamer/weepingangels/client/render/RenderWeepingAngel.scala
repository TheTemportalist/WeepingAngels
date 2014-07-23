package com.countrygamer.weepingangels.client.render

import com.countrygamer.weepingangels.client.render.models.ModelWeepingAngel
import com.countrygamer.weepingangels.common.WAOptions
import com.countrygamer.weepingangels.common.entity.EntityWeepingAngel
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.entity.{EntityLiving, Entity}
import net.minecraft.util.ResourceLocation

/**
 *
 *
 * @author CountryGamer
 */
@SideOnly(Side.CLIENT)
class RenderWeepingAngel() extends RenderLiving(new ModelWeepingAngel(), 0.5F) {

	var texture: ResourceLocation = WAOptions.weepingAngel1

	// Default Constructor
	{

	}

	// End Constructor

	override def getEntityTexture(p_110775_1_ : Entity): ResourceLocation = {
		return this.texture
	}

	override def doRender(entity: EntityLiving, d1: Double, d2: Double, d3: Double, f1: Float,
			f2: Float): Unit = {

		if (entity.isInstanceOf[EntityWeepingAngel]) {
			if (entity.asInstanceOf[EntityWeepingAngel].getAngryState() > 0) {
				this.texture = WAOptions.weepingAngel2
			}
			else {
				this.texture = WAOptions.weepingAngel1
			}
		}

		super.doRender(entity, d1, d2, d3, f1, f2)
	}

}
