package WeepingAngels.Items;

import java.util.ArrayList;

import WeepingAngels.WeepingAngelsMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import CountryGamer_Core.Items.ItemBase;

public class ItemSonic extends ItemBase {

	public ItemSonic(int id, String modid, String name) {
		super(id, modid, name);
	}

	public boolean onItemUse(ItemStack itemStack, EntityPlayer player,
			World world, int x, int y, int z, int side, float par8, float par9,
			float par10) {

		int blockID = world.getBlockId(x, y, z);
		if (blockID == Block.doorIron.blockID) {
			if (world.getBlockId(x, y - 1, z) == Block.doorIron.blockID) {
				y -= 1;
			}
			// 0 & 4, 1 & 5, 2 & 6, 3 & 7
			ArrayList<Integer> metadata = new ArrayList();
			for (int i = 0; i <= 3; i++) {
				metadata.add(i + 4);
			}
			if (WeepingAngelsMod.DEBUG) {
				WeepingAngelsMod.log.info("0:" + metadata.get(0) + "|" + "1:"
						+ metadata.get(1) + "|" + "2:" + metadata.get(2) + "|"
						+ "3:" + metadata.get(3) + "|");
			}

			BlockDoor door = (BlockDoor) Block.doorIron;
			int meta = door.getFullMetadata(world, x, y, z);
			if (meta < 0 || meta > 7)
				meta = 0;
			int newMeta = 0;
			if (meta <= 3) {
				newMeta = metadata.get(meta);
			} else {
				newMeta = metadata.indexOf(meta);
			}
			world.setBlockMetadataWithNotify(x, y, z, newMeta, 2);
			world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
			return true;
		}

		return false;
	}

}
