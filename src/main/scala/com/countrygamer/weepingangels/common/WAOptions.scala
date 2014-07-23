package com.countrygamer.weepingangels.common

import com.countrygamer.cgo.common.lib.util.Config
import com.countrygamer.cgo.wrapper.common.registries.OptionRegister
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation

/**
 *
 *
 * @author CountryGamer
 */
object WAOptions extends OptionRegister {

	val statueGui: Int = 0

	var angelsCanConvertPlayers: Boolean = true
	var conversionChance: Int = 10
	var maxAngelHealth: Int = 20
	var totalConversionTime: Int = 20

	var angelsCanTeleportPlayers: Boolean = true
	var teleportationChance: Int = 35
	var teleportationMinRange: Int = 5
	var teleportationMaxRange: Int = 200

	var angelsTakePlayerName: Boolean = true
	var angelsStealPlayerInventory: Boolean = false

	var angelsOnlyHurtWithPickaxe: Boolean = true
	var maximumSpawnHeight: Int = 40
	var spawnProbability: Int = 80
	var maxLightLevelForSpawn: Int = 8

	var angelsLookForTorches: Boolean = false
	var angelMaxSpeed: Float = 0.4F

	override def register(): Unit = {
		val stats: String = "Statistics"

		this.angelsCanConvertPlayers = Config.getAndComment(this.config, stats,
			"Angels can infect players",
			"When an Angel attacks a player," +
					" there is a chance that the Angel with start" +
					" converting the player into an angel",
			true
		)

		this.conversionChance = Config.getAndComment(this.config, stats,
			"Infection Percentage",
			"The percentage chance that an Angel will start converting a player." +
					" Disabled if 'Angels can infect players' is false.",
			10
		)

		this.maxAngelHealth = Config.getAndComment(this.config, stats,
			"Maximum Angel Health",
			"The maximum health of an Angel." +
					" Also is the maximum health of an angel when a player is infected.",
			20
		)

		this.totalConversionTime = Config.getAndComment(this.config, stats,
			"Maximum Ticks For Infection",
			"The maximum time in ticks (20 ticks = 1 second) that it takes for" +
					" an Angel (inside an infected player) to get to full health." +
					" Default is 6000 (5 minutes)" +
					" (20 ticks per second * 60 seconds per minute * 5 minutes)",
			6000
		)

		this.angelsCanTeleportPlayers = Config.getAndComment(this.config, stats,
			"Angels can teleport players",
			"When an Angel attacks a player, if the Angel does not infect the player," +
					" the Angel has a chance to teleport the player." +
					" If this is false, Angels will not be able to teleport players.",
			true
		)

		this.teleportationChance = Config.getAndComment(this.config, stats,
			"Teleportation Percentage",
			"The percentage change that an Angel will teleport a player," +
					" if player does not get infected. Default 35% chance of teleportation.",
			35
		)

		this.teleportationMinRange = Config.getAndComment(this.config, stats,
			"Minimum Teleportation Range",
			"The minimum distance that an Angel will teleport a player. Default 5.",
			5
		)

		this.teleportationMaxRange = Config.getAndComment(this.config, stats,
			"Maximum Teleportation Range",
			"The maximum distance that an Angel will teleport a player. Default 200.",
			200
		)

		this.angelsTakePlayerName = Config.getAndComment(this.config, stats,
			"Angels take a player's name",
			"If you die from an Angel's infection, and an Angel is spawned, it will ahve your name.",
			true
		)

		this.angelsStealPlayerInventory = Config.getAndComment(this.config, stats,
			"Angels steal a player's inventory",
			"If you die from an Angel's infection, and an Angel is spawned," +
					" then it will steal your inventory. You will have to kill that angel to" +
					" get your inventory back.",
			false
		)

		this.angelsOnlyHurtWithPickaxe = Config.getAndComment(this.config, stats,
			"Angels only hurt with pickaxe",
			"Angels can only be damaged with a pickaxe",
			true
		)

		this.maximumSpawnHeight = Config.getAndComment(this.config, stats,
			"Max spawn height",
			"The maximum spawn height for Angels." +
					" Angels will only be able to spawn between levels 0 and (this), in darkness." +
					" Default 40.",
			40
		)

		this.spawnProbability = Config.getAndComment(this.config, stats,
			"Weighted Spawn Probability",
			"The weighted probability that an Angel can spawn. The larger this number is, the" +
					" more often Angels will spawn. Default 3",
			80
		)

		this.maxLightLevelForSpawn = Config.getAndComment(this.config, stats,
			"Max Light Level",
			"The maximum light level for an Angel to spawn",
			8
		)

		this.angelsLookForTorches = Config.getAndComment(this.config, stats,
			"Angels can knock down light",
			"If enabled, then Angels will be able to look for light sources and knock them down." +
					" WARNING! The more Angels there are in the world, the more lag this will cause." +
					" This feature is still UNSTABLE!",
			false
		)

		this.angelMaxSpeed = Config.getAndComment(this.config, stats,
			"Angel Max Speed",
			"The maximum movement speed of an Angel",
			0.4D
		).asInstanceOf[Float]

	}

	@SideOnly(Side.CLIENT)
	override def getGuiConfigClass(): Class[_ <: GuiScreen] = {
		return classOf[com.countrygamer.weepingangels.client.gui.configFactory.GuiConfig]
	}

	final val weepingAngel1: ResourceLocation = new ResourceLocation(WeepingAngels.pluginID,
		"textures/model/entity/weepingangel.png")
	final val weepingAngel2: ResourceLocation = new ResourceLocation(WeepingAngels.pluginID,
		"textures/model/entity/weepingangel-angry.png")

}
