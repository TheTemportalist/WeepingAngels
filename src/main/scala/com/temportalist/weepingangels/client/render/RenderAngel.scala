package com.temportalist.weepingangels.client.render

import com.temportalist.weepingangels.client.render.models.ModelWeepingAngel
import com.temportalist.weepingangels.common.WAOptions
import com.temportalist.weepingangels.common.entity.EntityAngel
import net.minecraft.client.renderer.entity.{RenderLiving, RenderManager}
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation

/**
 *
 *
 * @author TheTemportalist 1/26/15
 */
class RenderAngel(manager: RenderManager) extends RenderLiving(
	manager, new ModelWeepingAngel(), 0.5F
) {

	override def getEntityTexture(entity: Entity): ResourceLocation = {
		entity match {
			case angel: EntityAngel =>
				if (angel.getAngryState > 0) {
					WAOptions.weepingAngel2
				}
				else {
					WAOptions.weepingAngel1
				}
			case _ =>
				WAOptions.weepingAngel1
		}
	}

	override def doRender(entity: Entity, x: Double, y: Double, z: Double, p_76986_8_ : Float,
			partialTicks: Float): Unit = {
		entity match {
			case angel: EntityAngel =>
				// morph things
				/*
				if (Loader.isModLoaded("Morph")) {
					val player: EntityPlayer = Minecraft.getMinecraft.thePlayer
					val morphedEntity: EntityLivingBase = Api
							.getMorphEntity(player.getName, true)
					if (morphedEntity != null && morphedEntity.equals(entity)) {
						val angelPlayer: AngelPlayer = AngelPlayerHandler.get(player)
						angel.setAngryState(angelPlayer.getAngryState())
						angel.setArmState(angelPlayer.getArmState())
					}
				}
				*/
			case _ =>
		}
		super.doRender(entity, x, y, z, p_76986_8_, partialTicks)
	}

}
