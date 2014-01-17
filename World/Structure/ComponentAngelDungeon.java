package WeepingAngels.World.Structure;

import java.util.List;
import java.util.Random;

import WeepingAngels.WeepingAngelsMod;
import WeepingAngels.Blocks.TileEnt.TileEntityPlinth;
import WeepingAngels.Entity.EntityStatue;
import WeepingAngels.World.GenerateAngelDungeon;
import WeepingAngels.lib.Util;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.ComponentVillage;
import net.minecraft.world.gen.structure.ComponentVillageStartPiece;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class ComponentAngelDungeon extends ComponentVillage {

	private int averageGroundLevel = -1;
	private static final int HEIGHT = 4;

	public ComponentAngelDungeon(
			ComponentVillageStartPiece par1ComponentVillageStartPiece,
			int componentType, Random par3Random,
			StructureBoundingBox par4StructureBoundingBox, int par5) {
		super(par1ComponentVillageStartPiece, componentType);
		this.coordBaseMode = par5;
		this.boundingBox = par4StructureBoundingBox;
	}

	public static ComponentAngelDungeon buildComponent(
			ComponentVillageStartPiece startPiece, List par1List,
			Random random, int par3, int par4, int par5, int par6, int par7) {
		StructureBoundingBox var8 = StructureBoundingBox
				.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 5,
						HEIGHT, 5, par6);
		return canVillageGoDeeper(var8)
				&& StructureComponent.findIntersecting(par1List, var8) == null ? new ComponentAngelDungeon(
				startPiece, par7, random, var8, par6) : null;
	}

	// this method create house
	public boolean addComponentParts(World world, Random rand,
			StructureBoundingBox boundBox) {
		if (this.averageGroundLevel < 0) {
			this.averageGroundLevel = this.getAverageGroundLevel(world,
					boundBox);
			if (this.averageGroundLevel < 0) {
				return true;
			}

			this.boundingBox.offset(0, this.averageGroundLevel
					- this.boundingBox.maxY + HEIGHT - 1, 0);
		}

		// place generation house code here
		// worldObj, structBB, minX, minY, minZ, maxX, maxY, maxZ)
		this.fillWithAir(world, boundBox, -1, -10, 0, 1, 4, 2); // -1~1, 0~2
		// world, blockid, meta, x, y, z, boundBox
		this.placeBlockAtCurrentPosition(world,
				WeepingAngelsMod.plinthBlock.blockID, 1, 0, 1, 1, boundBox);
		this.placeBlockAtCurrentPosition(world, Block.dirt.blockID, 0, 0, 4, 0,
				boundBox);
		// 0 == left, 1 == right, 2 == back, 3 == front
		this.stoneStair(world, -1, +0, +1, 0, boundBox);
		this.stoneStair(world, +1, +0, +1, 1, boundBox);
		this.stoneStair(world, +0, +0, +2, 2, boundBox);
		this.stoneStair(world, +0, +0, +0, 3, boundBox);
		this.placeBlockAtCurrentPosition(world, Block.stoneSingleSlab.blockID,
				5, +1, +0, +0, boundBox);
		this.placeBlockAtCurrentPosition(world, Block.stoneSingleSlab.blockID,
				5, -1, +0, +0, boundBox);
		this.placeBlockAtCurrentPosition(world, Block.stoneSingleSlab.blockID,
				5, +1, +0, +2, boundBox);
		this.placeBlockAtCurrentPosition(world, Block.stoneSingleSlab.blockID,
				5, -1, +0, +2, boundBox);

		return true;
	}

	private void stoneBlock(World world, int x, int y, int z,
			StructureBoundingBox boundBox) {
		int meta = 0;
		if ((new Random()).nextInt(100) <= 30)
			meta = 1;
		this.placeBlockAtCurrentPosition(world, Block.stoneBrick.blockID, meta,
				x, y, z, boundBox);
	}

	private void stoneStair(World world, int x, int y, int z, int dir,
			StructureBoundingBox boundBox) {
		this.placeBlockAtCurrentPosition(
				world,
				Block.stairsStoneBrick.blockID,
				this.getMetadataWithOffset(Block.stairsCobblestone.blockID, dir),
				x, y, z, boundBox);
	}

}
