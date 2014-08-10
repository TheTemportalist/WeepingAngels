package com.countrygamer.weepingangels.morph

import morph.api.Ability
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation

/**
 *
 *
 * @author CountryGamer
 */
class AngelFreezeAbility() extends Ability() {

	override def getType: String = {
		"angelFreeze"
	}

	override def getIcon: ResourceLocation = {
		null
	}

	override def kill(): Unit = {

	}

	override def tick(): Unit = {

	}

	override def postRender(): Unit = {

	}

	override def save(tag: NBTTagCompound): Unit = {

	}

	override def load(tag: NBTTagCompound): Unit = {

	}

	/**
	 * Creates a copy of this ability for use with parents.
	 * As previously stated before the ability instance used during registration is a base so it needs to be cloned for use with parents.
	 */
	override def clone: Ability = {
		new AngelFreezeAbility()
	}

}
