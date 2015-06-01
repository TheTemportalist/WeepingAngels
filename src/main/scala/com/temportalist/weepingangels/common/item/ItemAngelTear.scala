package com.temportalist.weepingangels.common.item

import java.util

import com.temportalist.origin.api.common.item.ItemBase
import com.temportalist.origin.api.common.utility.{Generic, Teleport}
import com.temportalist.weepingangels.common.{WAOptions, WeepingAngels}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist
 */
class ItemAngelTear() extends ItemBase(WeepingAngels.MODID, "angelTear") {

	override def onItemRightClick(itemStack: ItemStack, world: World,
			player: EntityPlayer): ItemStack = {

		if (itemStack.hasTagCompound) {
			val tagCom: NBTTagCompound = itemStack.getTagCompound
			if (tagCom.getString("type").equals("Teleportation")) {
				Teleport.toCursorPosition(player, 500.0D)
			}
			else if (tagCom.getString("type").equals("Time Manipulation")) {
				player.openGui(
					WeepingAngels, WAOptions.timeManipGui, world,
					player.posX.toInt, player.posY.toInt, player.posZ.toInt
				)
			}
			if (tagCom.getInteger("uses") > 0) {
				tagCom.setInteger("uses", tagCom.getInteger("uses") - 1)
				if (tagCom.getInteger("uses") <= 0) {
					itemStack.stackSize -= 1
				}
			}
		}
		itemStack
	}

	@SideOnly(Side.CLIENT)
	override def addInformation(itemStack: ItemStack, player: EntityPlayer, list: util.List[_],
			isAdvanced: Boolean): Unit = {
		if (itemStack.hasTagCompound) {
			Generic.addToList(list, itemStack.getTagCompound.getString("type"))
			val uses: Int = itemStack.getTagCompound.getInteger("uses")
			Generic.addToList(list, "Uses: " + (if (uses < 0) "Creative" else uses))
		}
	}

	override def getSubItems(item: Item, tab: CreativeTabs, list: util.List[_]): Unit = {
		val itemStack: ItemStack = new ItemStack(item, 1, 0)

		itemStack.setTagCompound(new NBTTagCompound)
		itemStack.getTagCompound.setString("type", "Teleportation")
		itemStack.getTagCompound.setInteger("uses", -1)
		Generic.addToList(list, itemStack.copy())
		itemStack.setTagCompound(null)

		itemStack.setTagCompound(new NBTTagCompound)
		itemStack.getTagCompound.setString("type", "Time Manipulation")
		itemStack.getTagCompound.setInteger("uses", -1)
		Generic.addToList(list, itemStack.copy())
		itemStack.setTagCompound(null)

	}

	@SideOnly(Side.CLIENT)
	override def registerIcons(reg: IIconRegister): Unit = {
	this.itemIcon = Items.ghast_tear.getIconFromDamage(0)
	}

}
