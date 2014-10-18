package com.countrygamer.weepingangels.common

import com.countrygamer.cgo.library.common.helpers.RegisterHelper
import com.countrygamer.cgo.wrapper.common.PluginWrapper
import com.countrygamer.cgo.wrapper.common.extended.ExtendedEntityHandler
import com.countrygamer.weepingangels.common.entity.{EntityAngelArrow, EntityWeepingAngel}
import com.countrygamer.weepingangels.common.extended.{AngelPlayer, AngelPlayerHandler}
import com.countrygamer.weepingangels.common.generation.VaultGenerator
import com.countrygamer.weepingangels.common.init.{WABlocks, WAEntity, WAItems}
import com.countrygamer.weepingangels.common.network.{PacketModifyStatue, PacketSetTime}
import com.countrygamer.weepingangels.morph.AbilityQuantumLock
import cpw.mods.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.common.{Mod, SidedProxy}
import morph.api.Ability
import net.minecraft.enchantment.{Enchantment, EnchantmentHelper}
import net.minecraft.entity.boss.EntityDragon
import net.minecraft.entity.monster.EntityEnderman
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.world.World
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.player.{ArrowLooseEvent, ArrowNockEvent}

/**
 *
 *
 * @author CountryGamer
 */
@Mod(modid = WeepingAngels.pluginID, name = WeepingAngels.pluginName, version = "@PLUGIN_VERSION@",
	modLanguage = "scala",
	guiFactory = WeepingAngels.clientProxy,
	dependencies = "required-after:Forge@[10.13,);required-after:cgo@[3.2,);after:Morph@[0,);"
)
object WeepingAngels extends PluginWrapper {

	final val pluginID = "weepingangels"
	final val pluginName = "Weeping Angels"
	final val clientProxy = "com.countrygamer.weepingangels.client.ClientProxy"
	final val serverProxy = "om.countrygamer.weepingangels.server.ServerProxy"

	@SidedProxy(
		clientSide = this.clientProxy,
		serverSide = this.serverProxy
	)
	var proxy: CommonProxy = null

	@Mod.EventHandler
	def preInit(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(this.pluginID, this.pluginName, event, this.proxy, WAOptions, WABlocks,
			WAItems, WAEntity)

		RegisterHelper.registerExtendedPlayer("Extended Angel Player", classOf[AngelPlayer],
			deathPersistance = false)

		RegisterHelper.registerHandler(AngelPlayerHandler, null)
		RegisterHelper.registerHandler(this, null)

		RegisterHelper.registerPacketHandler(this.pluginID, classOf[PacketModifyStatue],
			classOf[PacketSetTime])

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event)

		GameRegistry.registerWorldGenerator(VaultGenerator, 0)

	}

	@Mod.EventHandler
	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)

		Ability.registerAbility("timelock", classOf[AbilityQuantumLock])
		Ability.mapAbilities(classOf[EntityWeepingAngel],
			new AbilityQuantumLock(),
			Ability.createNewAbilityByType("hostile", new Array[String](0)),
			Ability.createNewAbilityByType("step", Array[String]("3F")),
			Ability.createNewAbilityByType("fireImmunity", new Array[String](0))
		)

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
		var angel: EntityWeepingAngel = null
		for (i <- 0 until list.size()) {
			list.get(i) match {
				case em: EntityEnderman =>
					angel = new EntityWeepingAngel(dragon.worldObj)
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
