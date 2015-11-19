package com.temportalist.weepingangels.common

import java.util.Map.Entry

import com.google.gson.{JsonObject, JsonArray, JsonElement}
import com.temportalist.origin.api.common.lib.ConfigJson
import com.temportalist.origin.api.common.utility.Scala
import com.temportalist.origin.foundation.common.register.OptionRegister
import net.minecraft.util.ResourceLocation

import scala.collection.mutable

/**
 *
 *
 * @author TheTemportalist
 */
object WAOptions extends OptionRegister {

	override def getExtension: String = "json"

	val maxDecrepitation_amount: Int = 6000
	val ticksPerDecrepitation: Int = 1200
	val decrepitationAge_max: Int = this.maxDecrepitation_amount * this.ticksPerDecrepitation

	val statueGui: Int = 0
	val timeManipGui: Int = 1

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

	val spawnHeightRanges = mutable.Map[Int, (Int, Int)]()

	var spawnProbability: Int = 80
	var maxLightLevelForSpawn: Int = 8
	var angelOverridesPlayerArmor: Boolean = false
	var angelsCanBeFed: Boolean = true

	var angelsLookForTorches: Boolean = false
	var angelMaxSpeed: Float = 0.4F

	var morphedAngryTicks: Int = 1200
	var morphedChaseTicks: Int = 600

	var angelThrowsVoice: Boolean = true
	var mobSounds: Array[String] = null
	val throwVoiceDelay_Min: Int = 20 * 15
	val throwVoiceDelay_Max: Int = 20 * 60 * 3

	override def register(): Unit = {
		val stats: String = "Statistics"

		this.angelsCanConvertPlayers = this.getAndComment(stats,
			"Angels can infect players",
			"When an Angel attacks a player," +
					" there is a chance that the Angel with start" +
					" converting the player into an angel",
			value = true
		)

		this.conversionChance = this.getAndComment(stats,
			"Infection Percentage",
			"The percentage chance that an Angel will start converting a player." +
					" Disabled if 'Angels can infect players' is false.",
			10
		)

		this.maxAngelHealth = this.getAndComment(stats,
			"Angel Health",
			"The spawn health of an Angel." +
					" Also is the maximum health of an angel when a player is infected.",
			20
		)

		this.totalConversionTime = this.getAndComment(stats,
			"Maximum Ticks For Infection",
			"The maximum time in ticks (20 ticks = 1 second) that it takes for" +
					" an Angel (inside an infected player) to get to full health." +
					" Default is 6000 (5 minutes)" +
					" (20 ticks per second * 60 seconds per minute * 5 minutes)",
			6000
		)

		this.angelsCanTeleportPlayers = this.getAndComment(stats,
			"Angels can teleport players",
			"When an Angel attacks a player, if the Angel does not infect the player," +
					" the Angel has a chance to teleport the player." +
					" If this is false, Angels will not be able to teleport players.",
			value = true
		)

		this.teleportationChance = this.getAndComment(stats,
			"Teleportation Percentage",
			"The percentage change that an Angel will teleport a player," +
					" if player does not get infected. Default 35% chance of teleportation.",
			35
		)

		this.teleportationMinRange = this.getAndComment(stats,
			"Minimum Teleportation Range",
			"The minimum distance that an Angel will teleport a player. Default 5.",
			5
		)

		this.teleportationMaxRange = this.getAndComment(stats,
			"Maximum Teleportation Range",
			"The maximum distance that an Angel will teleport a player. Default 200.",
			200
		)

		this.angelsTakePlayerName = this.getAndComment(stats,
			"Angels take a player's name",
			"If you die from an Angel's infection, and an Angel is spawned, it will have your name.",
			value = true
		)

		this.angelsStealPlayerInventory = this.getAndComment(stats,
			"Angels steal a player's inventory",
			"If you die from an Angel's infection, and an Angel is spawned," +
					" then it will steal your inventory. You will have to kill that angel to" +
					" get your inventory back.",
			value = false
		)

		this.angelsOnlyHurtWithPickaxe = this.getAndComment(stats,
			"Angels only hurt with pickaxe",
			"Angels can only be damaged with a pickaxe",
			true
		)

		this.spawnProbability = this.getAndComment(stats,
			"Weighted Spawn Probability",
			"The weighted probability that an Angel can spawn. The larger this number is, the" +
					" more often Angels will spawn. Default 3",
			80
		)

		this.maxLightLevelForSpawn = this.getAndComment(stats,
			"Max Light Level",
			"The maximum light level for an Angel to spawn",
			8
		)

		this.angelsLookForTorches = this.getAndComment(stats,
			"Angels can knock down light",
			"If enabled, then Angels will be able to look for light sources and knock them down." +
					" WARNING! The more Angels there are in the world, the more lag this will cause." +
					" This feature is still UNSTABLE!",
			value = false
		)

		this.angelMaxSpeed = this.getAndComment(stats,
			"Angel Max Speed",
			"The maximum movement speed of an Angel",
			0.4D
		).asInstanceOf[Float]

		this.angelOverridesPlayerArmor = this.getAndComment(stats,
			"Angel ignores armor",
			"Angels ignore a player's armor when they hurt a player",
			value = false
		)

		this.morphedAngryTicks = this.getAndComment(stats,
			"Morphed Angry Length",
			"When morphed as a lonely assassin," +
					" you will stay angry at anyone who attacks for your this many TICKS",
			1200
		)

		this.morphedChaseTicks = this.getAndComment(stats,
			"Morphed Chase Length",
			"When morphed as a lonely assassin," +
					" you will chase a target (after attacking them) for this many TICKS",
			600
		)

		this.angelThrowsVoice = this.getAndComment(stats,
			"Angels throw sounds", "Angels are able to distract you with sounds", true
		)

		this.mobSounds = this.getAndComment("other",
			"mob sounds", "Any sounds you want an angel to be able to say.", Array[String](
				"mob.endermen.idle",
				"mob.silverfish.say",
				"mob.skeleton.say",
				"mob.spider.say",
				"mob.villager.idle",
				"mob.wither.idle",
				"mob.zombie.say",
				"creeper.primed"
			)
		)

		// get the json object which is how the config object is populated
		val json = this.config.asInstanceOf[ConfigJson].configJson

		// check if the json has a spawn variable
		if (!json.has("spawn")) {
			val spawn = new JsonObject

			// create the overworld default config values
			val overworld = new JsonObject
			overworld.addProperty("dimensionID", 0)
			overworld.addProperty("minSpawnHeight", 0)
			overworld.addProperty("maxSpawnHeight", 40)
			spawn.add("overworld", overworld)

			// add the spawn object to the json config
			json.add("spawn", spawn)
		}
		try {
			// try to load the spawn object
			val spawn = json.get("spawn").getAsJsonObject
			Scala.iterateCol(spawn.entrySet(), (entry: Entry[String, JsonElement]) => {
				// the name of the entry is inconsequential. All we care about is the dimID
				val dimJson = entry.getValue.getAsJsonObject
				// add it to the spawn ranges. Used in EntityAngel.getCanSpawnHere
				this.spawnHeightRanges(dimJson.get("dimensionID").getAsInt) =
						(dimJson.get("minSpawnHeight").getAsInt,
								dimJson.get("maxSpawnHeight").getAsInt)
			})
		}
		catch {
			case e: Exception => e.printStackTrace()
		}

	}

	final val weepingAngel1: ResourceLocation = new ResourceLocation(WeepingAngels.MODID,
		"textures/model/entity/weepingangel.png")
	final val weepingAngel2: ResourceLocation = new ResourceLocation(WeepingAngels.MODID,
		"textures/model/entity/weepingangel-angry.png")

}
