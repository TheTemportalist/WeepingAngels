package com.countrygamer.weepingangels.client.render

import com.countrygamer.cgo.common.lib.util.General
import com.countrygamer.weepingangels.client.render.models.ModelWeepingAngel
import com.countrygamer.weepingangels.common.WAOptions
import com.countrygamer.weepingangels.common.entity.EntityWeepingAngel
import com.countrygamer.weepingangels.common.extended.{AngelPlayer, AngelPlayerHandler}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import morph.api.Api
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityLiving, EntityLivingBase}
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
		this.texture
	}

	override def doRender(entity: EntityLiving, d1: Double, d2: Double, d3: Double, f1: Float,
			f2: Float): Unit = {

		entity match {
			case angel: EntityWeepingAngel =>

				if (General.isModLoaded("Morph")) {
					val player: EntityPlayer = Minecraft.getMinecraft.thePlayer
					val morphedEntity: EntityLivingBase = Api
							.getMorphEntity(player.getCommandSenderName, true)
					if (morphedEntity != null && morphedEntity.equals(entity)) {
						val angelPlayer: AngelPlayer = AngelPlayerHandler.get(player)
						angel.setAngryState(angelPlayer.getAngryState())
						angel.setArmState(angelPlayer.getArmState())
					}
				}

				if (angel.getAngryState > 0) {
					this.texture = WAOptions.weepingAngel2
				}
				else {
					this.texture = WAOptions.weepingAngel1
				}

			case _ =>
		}

		super.doRender(entity, d1, d2, d3, f1, f2)
	}

}
