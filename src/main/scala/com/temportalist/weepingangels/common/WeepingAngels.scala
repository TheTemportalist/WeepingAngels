package com.temportalist.weepingangels.common

import com.temportalist.origin.library.common.handlers.RegisterHelper
import com.temportalist.origin.wrapper.common.ModWrapper
import com.temportalist.origin.wrapper.common.extended.ExtendedEntityHandler
import com.temportalist.weepingangels.client.gui.GuiDynamic
import com.temportalist.weepingangels.common.entity.{EntityAngel, EntityAngelArrow}
import com.temportalist.weepingangels.common.extended.{AngelPlayer, AngelPlayerHandler}
import com.temportalist.weepingangels.common.generation.VaultGenerator
import com.temportalist.weepingangels.common.init.{WABlocks, WAEntity, WAItems}
import com.temportalist.weepingangels.common.network.{PacketModifyStatue, PacketSetTime}
import net.minecraft.enchantment.{Enchantment, EnchantmentHelper}
import net.minecraft.entity.boss.EntityDragon
import net.minecraft.entity.monster.EntityEnderman
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.world.World
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.player.{ArrowLooseEvent, ArrowNockEvent}
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.common.{Mod, SidedProxy}

/**
 *
 *
 * @author TheTemportalist
 */
@Mod(modid = WeepingAngels.MODID, name = WeepingAngels.MODNAME, version = "@PLUGIN_VERSION@",
	modLanguage = "scala",
	guiFactory = WeepingAngels.clientProxy,
	dependencies = "required-after:origin@[4,);after:Morph@[0,);"
)
object WeepingAngels extends ModWrapper {

	final val MODID = "weepingangels"
	final val MODNAME = "Weeping Angels"
	final val clientProxy = "com.temportalist.weepingangels.client.ProxyClient"
	final val serverProxy = "com.temportalist.weepingangels.server.ProxyServer"

	@SidedProxy(clientSide = this.clientProxy, serverSide = this.serverProxy)
	var proxy: ProxyCommon = null

	@Mod.EventHandler
	def preInit(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(this.MODID, this.MODNAME, event, this.proxy, WAOptions, WABlocks,
			WAItems, WAEntity)

		RegisterHelper.registerExtendedPlayer("Extended Angel Player", classOf[AngelPlayer],
			deathPersistance = false)

		RegisterHelper.registerHandler(AngelPlayerHandler, GuiDynamic, EntityAngel)

		RegisterHelper.registerPacketHandler(this.MODID, classOf[PacketModifyStatue],
			classOf[PacketSetTime])

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event, this.proxy)

		GameRegistry.registerWorldGenerator(VaultGenerator, 0)

	}

	@Mod.EventHandler
	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)

	}

	@SubscribeEvent
	def arrowNock(event: ArrowNockEvent): Unit = {
		if (event.entityPlayer.inventory.hasItem(WAItems.angelArrow)) {
			event.entityPlayer.setItemInUse(
				event.result,
				Items.bow.getMaxItemUseDuration(event.result)
			)
			event.setCanceled(true)
		}
	}

	@SubscribeEvent
	def arrowLoose(event: ArrowLooseEvent): Unit = {
		if (event.entityPlayer.inventory.hasItem(WAItems.angelArrow)) {
			event.setCanceled(true)
			val world: World = event.entityPlayer.worldObj

			val j: Int = event.charge
			val flag: Boolean = event.entityPlayer.capabilities.isCreativeMode ||
					EnchantmentHelper
							.getEnchantmentLevel(Enchantment.infinity.effectId, event.bow) > 0

			var charge1: Float = j.asInstanceOf[Float] / 20.0F
			charge1 = (charge1 * charge1 + charge1 * 2.0F) / 3.0F
			if (charge1.asInstanceOf[Double] < 0.1D) {
				return
			}
			if (charge1 > 1.0F) {
				charge1 = 1.0F
			}

			val entityarrow: EntityAngelArrow = new EntityAngelArrow(
				world, event.entityPlayer, charge1 * 2.0F
			)
			if (charge1 == 1.0F) {
				entityarrow.setIsCritical(true)
			}

			val powerLevel: Int = EnchantmentHelper.getEnchantmentLevel(
				Enchantment.power.effectId, event.bow
			)
			if (powerLevel > 0) {
				entityarrow.setDamage(
					entityarrow.getDamage + powerLevel.asInstanceOf[Double] * 0.5D + 0.5D)
			}

			val punchLevel: Int = EnchantmentHelper.getEnchantmentLevel(
				Enchantment.punch.effectId, event.bow
			)
			if (punchLevel > 0) {
				entityarrow.setKnockbackStrength(punchLevel)
			}

			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, event.bow) > 0) {
				entityarrow.setFire(100)
			}

			event.bow.damageItem(1, event.entityPlayer)

			world.playSoundAtEntity(
				event.entityPlayer, "random.bow", 1.0F,
				1.0F / (world.rand.nextFloat * 0.4F + 1.2F) + charge1 * 0.5F
			)

			if (flag) {
				entityarrow.canBePickedUp = 2
			}
			else {
				event.entityPlayer.inventory.consumeInventoryItem(WAItems.angelArrow)
			}

			if (!world.isRemote) {
				world.spawnEntityInWorld(entityarrow)
			}

		}
	}

	@SubscribeEvent
	def onHitEntity(event: LivingAttackEvent): Unit = {
		if (event.source.isProjectile && event.source.getSourceOfDamage != null &&
				event.source.getSourceOfDamage.isInstanceOf[EntityAngelArrow]) {
			event.entityLiving match {
				case player: EntityPlayer =>
					val angelPlayer: AngelPlayer = ExtendedEntityHandler
							.getExtended(player, classOf[AngelPlayer]).asInstanceOf[AngelPlayer]
					if (!angelPlayer.converting()) {
						angelPlayer.startConversion()
						angelPlayer.setAngelHealth(0.0F)
						angelPlayer.clearRegenTicks()
						event.setCanceled(true)
					}
				//case dragonPart: EntityDragonPart =>
				//	this.hitDragon(dragonPart.entityDragonObj.asInstanceOf[EntityDragon])
				case dragon: EntityDragon =>
					this.hitDragon(dragon)
					//dragon.setDead()
					event.setCanceled(true)
				case _ =>
			}
		}
	}

	private def hitDragon(dragon: EntityDragon): Unit = {
		if (dragon.worldObj.isRemote) {
			return
		}
		val list: java.util.List[_] = dragon.worldObj.loadedEntityList
		var angel: EntityAngel = null
		for (i <- 0 until list.size()) {
			list.get(i) match {
				case em: EntityEnderman =>
					angel = new EntityAngel(dragon.worldObj)
					angel.setPositionAndRotation(
						em.posX,
						em.posY,
						em.posZ,
						em.rotationYaw, em.rotationPitch
					)
					em.setDead()
					dragon.worldObj.spawnEntityInWorld(angel)
				case _ =>
			}
		}
	}

}
