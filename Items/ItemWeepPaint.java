package WeepingAngels.Items;

import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import WeepingAngels.WeepingAngelsMod;
import WeepingAngels.Entity.EntityWAPainting;

public class ItemWeepPaint extends Item {

	public ItemWeepPaint(int id, Class paintClass) {
		super(id);
		//this.hangingEntityClass = paintClass;
		this.hangingEntityClass = EntityPainting.class;
	}
	
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player,
			World world, int x, int y, int z, int side,
			float par8, float par9, float par10) {
		
		if (side == 0)
        {
            return false;
        }
        else if (side == 1)
        {
            return false;
        }
        else
        {
            int i1 = Direction.facingToDirection[side];
            EntityHanging entityhanging = this.createHangingEntity(
            		world, x, y, z, i1);

            if (!player.canPlayerEdit(x, y, z, side, itemStack))
            {
                return false;
            }
            else
            {
                if (entityhanging != null && entityhanging.onValidSurface())
                {
                    if (!world.isRemote)
                    {
                        world.spawnEntityInWorld(entityhanging);
                    }

                    --itemStack.stackSize;
                }

                return true;
            }
        }
	}
	private final Class hangingEntityClass;
	private EntityHanging createHangingEntity(
			World world, int x, int y, int z, int side) {
		EntityHanging ent = null;
		/*
		if(this.hangingEntityClass == EntityWAPainting.class)
			ent = (EntityHanging)(new EntityWAPainting(
					world, x, y, z, side, WeepingAngelsMod.wapNAME));
		else
		*/
		if(this.hangingEntityClass == EntityPainting.class)
			ent = (EntityHanging)(new EntityPainting(
					world, x, y, z, side, WeepingAngelsMod.wapNAME));
		else
			ent = (EntityHanging)(new EntityItemFrame(world, x, y, z, side));
		
		return ent;
	}
	
}
