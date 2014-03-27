package com.countrygamer.weepingangels.World.Structure;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.Facing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import com.countrygamer.weepingangels.WeepingAngelsMod;
import com.countrygamer.weepingangels.Blocks.TileEnt.TileEntityPlinth;
import com.countrygamer.weepingangels.Entity.EntityStatue;
import com.countrygamer.weepingangels.lib.Util;

public class ComponentAngelDungeon {

	private int averageGroundLevel = -1;
	private static final int HEIGHT = 50;

	public ComponentAngelDungeon() {
	}

	// this method create house
	public boolean addComponentParts(World world, Random rand,
			StructureBoundingBox boundBox) {

		// int xOffset = 5, yOffset = CG_Core.DEBUG ? 20 : 0, zOffset = 0;
		// world.setBlock(world, Blocks.cobblestone,
		// 0,
		// -1 + xOffset, +0 + yOffset, +1 + zOffset, boundBox);
		/*
		 * // place generation house code here CoreUtil.fillBlocks(world, -1 +
		 * xOffset, -10 + yOffset, 0 + zOffset, 1 + xOffset, 4 + yOffset, 2 +
		 * zOffset, 0, 0, this, boundBox); // place generation house code here
		 * CoreUtil.fillBlocks(world, -1 + xOffset, -10 + yOffset, 0 + zOffset,
		 * 1 + xOffset, 4 + yOffset, 2 + zOffset, 0, 0, this, boundBox);
		 * CoreUtil.placeBlock(world, 0 + xOffset, 1 + yOffset, 1 + zOffset,
		 * WeepingAngelsMod.plinthBlocks, 1, this, boundBox); // if
		 * (CG_Core.DEBUG) // CoreUtil.placeBlock(world, 0, 4, 0, Blocks.dirt,
		 * 0, this, // boundBox); // 0 == left, 1 == right, 2 == back, 3 ==
		 * front CoreUtil.placeBlock(world, -1 + xOffset, +0 + yOffset, +1 +
		 * zOffset, Blocks.stone_stairs, 0, this, boundBox);
		 * CoreUtil.placeBlock(world, +1 + xOffset, +0 + yOffset, +1 + zOffset,
		 * Blocks.stone_stairs, 1, this, boundBox); CoreUtil.placeBlock(world,
		 * +0 + xOffset, +0 + yOffset, +2 + zOffset, Blocks.stone_stairs, 2,
		 * this, boundBox); CoreUtil.placeBlock(world, +0 + xOffset, +0 +
		 * yOffset, +0 + zOffset, Blocks.stone_stairs, 3, this, boundBox);
		 * 
		 * CoreUtil.placeBlock(world, +1 + xOffset, +0 + yOffset, +0 + zOffset,
		 * Blocks.stoneSingleSlab, 5, this, boundBox);
		 * CoreUtil.placeBlock(world, -1 + xOffset, +0 + yOffset, +0 + zOffset,
		 * Blocks.stoneSingleSlab, 5, this, boundBox);
		 * CoreUtil.placeBlock(world, +1 + xOffset, +0 + yOffset, +2 + zOffset,
		 * Blocks.stoneSingleSlab, 5, this, boundBox);
		 * CoreUtil.placeBlock(world, -1 + xOffset, +0 + yOffset, +2 + zOffset,
		 * Blocks.stoneSingleSlab, 5, this, boundBox);
		 * 
		 * CoreUtil.fillVariedStoneBlocks(world, -1 + xOffset, -6 + yOffset, 0 +
		 * zOffset, 1 + xOffset, -1 + yOffset, 2 + zOffset, this, boundBox);
		 * CoreUtil.fillVariedStoneBlocks(world, -1 + xOffset, -10 + yOffset, 0
		 * + zOffset, 1 + xOffset, -7 + yOffset, 4 + zOffset, this, boundBox);
		 * CoreUtil.fillVariedStoneBlocks(world, -5 + xOffset, -10 + yOffset, 4
		 * + zOffset, 5 + xOffset, -6 + yOffset, 14 + zOffset, this, boundBox);
		 * 
		 * CoreUtil.fillBlocks(world, 0 + xOffset, -7 + yOffset, 1 + zOffset, 0
		 * + xOffset, -1 + yOffset, 1 + zOffset, 0, 0, this, boundBox);
		 * CoreUtil.fillBlocks(world, 0 + xOffset, -9 + yOffset, 1 + zOffset, 0
		 * + xOffset, -8 + yOffset, 5 + zOffset, 0, 0, this, boundBox);
		 * CoreUtil.fillBlocks(world, -4 + xOffset, -9 + yOffset, 5 + zOffset, 4
		 * + xOffset, -7 + yOffset, 13 + zOffset, 0, 0, this, boundBox);
		 * 
		 * int corX = 0 + xOffset, corY = -9 + yOffset, corZ = 9 + zOffset;
		 * CoreUtil.placeBlock(world, corX - 3, corY - 1, corZ - 3,
		 * Blocks.glowStone, 0, this, boundBox); CoreUtil.placeBlock(world, corX
		 * - 3, corY - 1, corZ + 3, Blocks.glowStone, 0, this, boundBox);
		 * CoreUtil.placeBlock(world, corX + 3, corY - 1, corZ - 3,
		 * Blocks.glowStone, 0, this, boundBox); CoreUtil.placeBlock(world, corX
		 * + 3, corY - 1, corZ + 3, Blocks.glowStone, 0, this, boundBox);
		 * 
		 * CoreUtil.placeBlock(world, corX - 4, corY, corZ + 1,
		 * WeepingAngelsMod.plinthBlocks, 1, this, boundBox);
		 * CoreUtil.placeBlock(world, corX - 4, corY, corZ - 1,
		 * WeepingAngelsMod.plinthBlocks, 1, this, boundBox);
		 * CoreUtil.placeBlock(world, corX + 4, corY, corZ + 1,
		 * WeepingAngelsMod.plinthBlocks, 1, this, boundBox);
		 * CoreUtil.placeBlock(world, corX + 4, corY, corZ - 1,
		 * WeepingAngelsMod.plinthBlocks, 1, this, boundBox);
		 * 
		 * CoreUtil.placeBlock(world, corX - 1, corY, corZ + 4,
		 * WeepingAngelsMod.plinthBlocks, 1, this, boundBox);
		 * CoreUtil.placeBlock(world, corX - 1, corY, corZ - 4,
		 * WeepingAngelsMod.plinthBlocks, 1, this, boundBox);
		 * CoreUtil.placeBlock(world, corX + 1, corY, corZ + 4,
		 * WeepingAngelsMod.plinthBlocks, 1, this, boundBox);
		 * CoreUtil.placeBlock(world, corX + 1, corY, corZ - 4,
		 * WeepingAngelsMod.plinthBlocks, 1, this, boundBox);
		 * 
		 * CoreUtil.placeBlock(world, corX - 3, corY, corZ + 3,
		 * WeepingAngelsMod.plinthBlocks, 1, this, boundBox);
		 * CoreUtil.placeBlock(world, corX - 3, corY, corZ - 3,
		 * WeepingAngelsMod.plinthBlocks, 1, this, boundBox);
		 * CoreUtil.placeBlock(world, corX + 3, corY, corZ + 3,
		 * WeepingAngelsMod.plinthBlocks, 1, this, boundBox);
		 * CoreUtil.placeBlock(world, corX + 3, corY, corZ - 3,
		 * WeepingAngelsMod.plinthBlocks, 1, this, boundBox);
		 * 
		 * CoreUtil.placeBlock(world, corX, corY + 0, corZ - 5,
		 * Blocks.fenceIron, 0, this, boundBox); CoreUtil.placeBlock(world,
		 * corX, corY + 1, corZ - 5, Blocks.fenceIron, 0, this, boundBox);
		 * 
		 * for (int l = -1; l > -9; l--) { CoreUtil.placeBlock(world, 0 +
		 * xOffset, l + yOffset, 1 + zOffset, Blocks.ladder, 2, this, boundBox);
		 * } CoreUtil.placeBlock(world, 0 + xOffset, 0 + yOffset, 1 + zOffset,
		 * Blocks.trapdoor, (5 + 4) | 8, this, boundBox);
		 */
		return true;
	}

	private int getMeta() {
		int meta = 0;
		int chance = (new Random()).nextInt(100);
		if (chance <= 45) {
			meta = 1;
			if (chance <= 10)
				meta = 2;
		}
		return meta;
	}

	private void stoneBlock(World world, int x, int y, int z,
			StructureBoundingBox boundBox) {
		int meta = this.getMeta();
		world.setBlock(x, y, z, Blocks.stonebrick, meta, 3);
	}

	private void stoneStair(World world, int x, int y, int z, int dir,
			StructureBoundingBox boundBox) {
		world.setBlock(x, y, z, Blocks.stone_brick_stairs,
				this.getMetadataWithOffset(Blocks.stone_brick_stairs, dir), 3);
	}

	private void fillWithStone(World world, StructureBoundingBox boundBox,
			int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		for (int k2 = minY; k2 <= maxY; ++k2) {
			for (int l2 = minX; l2 <= maxX; ++l2) {
				for (int i3 = minZ; i3 <= maxZ; ++i3) {
					if (!false) {
						int meta = this.getMeta();
						if (k2 != minY && k2 != maxY && l2 != minX
								&& l2 != maxX && i3 != minZ && i3 != maxZ) {
							world.setBlock(l2, k2, i3, Blocks.stonebrick, meta,
									3);
						} else {
							world.setBlock(l2, k2, i3, Blocks.stonebrick, meta,
									3);
						}
					}
				}
			}
		}

	}

	private void statue(World world, StructureBoundingBox boundBox, int x,
			int y, int z, int rotation) {
		world.setBlock(x, y, z, WeepingAngelsMod.plinthBlock, 1, 3);
		/**
		 * 0 through 14 evens only
		 */
		int statueYaw = 6;

		TileEntityPlinth statue = new TileEntityPlinth();
		EntityStatue ent = Util.getEntityStatue(world, EntityStatue.class);
		if (ent != null) {
			// statue.setRotation(statueYaw);
			// statue.statueEntity = ent;
		}
		// world.setBlockTileEntity(x, y, z, statue);

	}

	public void fillWithAir(World world,
			StructureBoundingBox par2StructureBoundingBox, int x, int y, int z,
			int par6, int par7, int par8) {
		for (int k1 = y; k1 <= par7; ++k1) {
			for (int l1 = x; l1 <= par6; ++l1) {
				for (int i2 = z; i2 <= par8; ++i2) {
					world.setBlock(l1, k1, i2, null);
				}
			}
		}
	}

	int coordBaseMode = 0;

	public int getMetadataWithOffset(Block block, int par2) {
		if (block == Blocks.rail) {
			if (this.coordBaseMode == 1 || this.coordBaseMode == 3) {
				if (par2 == 1) {
					return 0;
				}

				return 1;
			}
		} else if (block != Blocks.wooden_door && block != Blocks.iron_door) {
			if (block != Blocks.stone_stairs && block != Blocks.oak_stairs
					&& block != Blocks.nether_brick_stairs
					&& block != Blocks.stone_brick_stairs
					&& block != Blocks.sandstone_stairs) {
				if (block == Blocks.ladder) {
					if (this.coordBaseMode == 0) {
						if (par2 == 2) {
							return 3;
						}

						if (par2 == 3) {
							return 2;
						}
					} else if (this.coordBaseMode == 1) {
						if (par2 == 2) {
							return 4;
						}

						if (par2 == 3) {
							return 5;
						}

						if (par2 == 4) {
							return 2;
						}

						if (par2 == 5) {
							return 3;
						}
					} else if (this.coordBaseMode == 3) {
						if (par2 == 2) {
							return 5;
						}

						if (par2 == 3) {
							return 4;
						}

						if (par2 == 4) {
							return 2;
						}

						if (par2 == 5) {
							return 3;
						}
					}
				} else if (block == Blocks.stone_button) {
					if (this.coordBaseMode == 0) {
						if (par2 == 3) {
							return 4;
						}

						if (par2 == 4) {
							return 3;
						}
					} else if (this.coordBaseMode == 1) {
						if (par2 == 3) {
							return 1;
						}

						if (par2 == 4) {
							return 2;
						}

						if (par2 == 2) {
							return 3;
						}

						if (par2 == 1) {
							return 4;
						}
					} else if (this.coordBaseMode == 3) {
						if (par2 == 3) {
							return 2;
						}

						if (par2 == 4) {
							return 1;
						}

						if (par2 == 2) {
							return 3;
						}

						if (par2 == 1) {
							return 4;
						}
					}
				} else if (block != Blocks.tripwire
						&& (block == null || !(block instanceof BlockDirectional))) {
					if (block == Blocks.piston || block == Blocks.sticky_piston
							|| block == Blocks.lever
							|| block == Blocks.dispenser) {
						if (this.coordBaseMode == 0) {
							if (par2 == 2 || par2 == 3) {
								return Facing.oppositeSide[par2];
							}
						} else if (this.coordBaseMode == 1) {
							if (par2 == 2) {
								return 4;
							}

							if (par2 == 3) {
								return 5;
							}

							if (par2 == 4) {
								return 2;
							}

							if (par2 == 5) {
								return 3;
							}
						} else if (this.coordBaseMode == 3) {
							if (par2 == 2) {
								return 5;
							}

							if (par2 == 3) {
								return 4;
							}

							if (par2 == 4) {
								return 2;
							}

							if (par2 == 5) {
								return 3;
							}
						}
					}
				} else if (this.coordBaseMode == 0) {
					if (par2 == 0 || par2 == 2) {
						return Direction.rotateOpposite[par2];
					}
				} else if (this.coordBaseMode == 1) {
					if (par2 == 2) {
						return 1;
					}

					if (par2 == 0) {
						return 3;
					}

					if (par2 == 1) {
						return 2;
					}

					if (par2 == 3) {
						return 0;
					}
				} else if (this.coordBaseMode == 3) {
					if (par2 == 2) {
						return 3;
					}

					if (par2 == 0) {
						return 1;
					}

					if (par2 == 1) {
						return 2;
					}

					if (par2 == 3) {
						return 0;
					}
				}
			} else if (this.coordBaseMode == 0) {
				if (par2 == 2) {
					return 3;
				}

				if (par2 == 3) {
					return 2;
				}
			} else if (this.coordBaseMode == 1) {
				if (par2 == 0) {
					return 2;
				}

				if (par2 == 1) {
					return 3;
				}

				if (par2 == 2) {
					return 0;
				}

				if (par2 == 3) {
					return 1;
				}
			} else if (this.coordBaseMode == 3) {
				if (par2 == 0) {
					return 2;
				}

				if (par2 == 1) {
					return 3;
				}

				if (par2 == 2) {
					return 1;
				}

				if (par2 == 3) {
					return 0;
				}
			}
		} else if (this.coordBaseMode == 0) {
			if (par2 == 0) {
				return 2;
			}

			if (par2 == 2) {
				return 0;
			}
		} else {
			if (this.coordBaseMode == 1) {
				return par2 + 1 & 3;
			}

			if (this.coordBaseMode == 3) {
				return par2 + 3 & 3;
			}
		}

		return par2;
	}

}
