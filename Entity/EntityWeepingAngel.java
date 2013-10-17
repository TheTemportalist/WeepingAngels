package WeepingAngels.Entity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import WeepingAngels.WeepingAngelsMod;

public class EntityWeepingAngel extends EntityCreature {
    
    private int spawntimer;
    private int randomSoundDelay;
    private boolean canSeeSkyAndDay;
    
    private float moveSpeed;
    private float maxSpeed = 7F, minSpeed = 0.3F;
    
    
    public EntityWeepingAngel(World world) {
        super(world);
        this.experienceValue = 5;
        this.spawntimer = 5;
        this.isImmuneToFire = true;
        
        this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));
        
        
    }

    // ~~~~~~~~~~~~~~~ Entity Mob Properties ~~~~~~~~~~~~~~~~~~~~~~~~~
    public void onLivingUpdate() {
        this.updateArmSwingProgress();
        float f = this.getBrightness(1.0F);
        
        if (f > 0.5F) {
            this.entityAge += 2;
        }
        
        super.onLivingUpdate();
    }
    
    public void onUpdate() {
        this.moveStrafing = (this.moveForward = 0.0F);
        this.moveSpeed = this.minSpeed;
        
        if (!this.worldObj.isRemote &&
                this.worldObj.difficultySetting == 0) {
            this.setDead();
        }
        
        if(this.spawntimer >= 0)
            --this.spawntimer;
        
        if(this.entityToAttack == null)
            this.entityToAttack = this.findPlayerToAttack();
        
        if(this.entityToAttack != null)
            this.moveSpeed = this.maxSpeed;
        else
            this.moveSpeed = this.minSpeed;
        
        
        
        
        if(this.worldObj.isDaytime()) {
            float f = getBrightness(1.0F);
            if(f > 0.5F && this.worldObj.canBlockSeeTheSky(
                    MathHelper.floor_double(this.posX),
                    MathHelper.floor_double(this.posY),
                    MathHelper.floor_double(this.posZ)) &&
                    this.rand.nextFloat() * 30F < (f - 0.4F) * 2.0F)
                this.canSeeSkyAndDay = true;
            else
                this.canSeeSkyAndDay = false;
        }
        
        
        EntityPlayer ep = this.findPlayerToAttack();
        if(this.isInFieldOfVision(ep)) {
            if(WeepingAngelsMod.DEBUG) System.out.println("Angel can be seen");
            this.moveSpeed = 0.0F;
        }
        
        
        
        
        
        
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
                .setAttribute(this.moveSpeed);
        super.onUpdate();
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.23000000417232513D);
        this.getAttributeMap().func_111150_b(SharedMonsterAttributes.attackDamage);
    }
    
    public float getBlockPathWeight(int par1, int par2, int par3) {
        return 0.5F - this.worldObj.getLightBrightness(par1, par2, par3);
    }
    protected boolean isValidLightLevel() {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.boundingBox.minY);
        int k = MathHelper.floor_double(this.posZ);
        
        if(this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, i, j, k) >
                this.rand.nextInt(32)) {
            return false;
        }else{
            int l = this.worldObj.getBlockLightValue(i, j, k);
        
            if (this.worldObj.isThundering()) {
                int i1 = this.worldObj.skylightSubtracted;
                this.worldObj.skylightSubtracted = 10;
                l = this.worldObj.getBlockLightValue(i, j, k);
                this.worldObj.skylightSubtracted = i1;
            }
            
            return l <= this.rand.nextInt(8);
        }
    }
    public boolean getCanSpawnHere() {
        if(this.worldObj.difficultySetting > 0 &&
                this.isValidLightLevel() &&
                super.getCanSpawnHere()) {
            int i = MathHelper.floor_double(this.posX);
            int j2 = MathHelper.floor_double(this.boundingBox.minY);
            int j1 = MathHelper.floor_double(this.posY + j2);
            int k = MathHelper.floor_double(this.posZ);
            
            if(j1 < 60)
                return WeepingAngelsMod.worldSpawnAngels;
        }
        return false;
    }
    
    // ~~~~~~~~~~~~~~~ Weeping Angel Attributes ~~~~~~~~~~~~~~~~~~~~~~
    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, Byte.valueOf((byte)0)); //Angry
        this.dataWatcher.addObject(17, Byte.valueOf((byte)0)); //ArmMovement
    }
        
    public boolean getAngry() {
        return this.dataWatcher.getWatchableObjectByte(16) == 1; 
    }
    public boolean getArmMovement() {
        return this.dataWatcher.getWatchableObjectByte(17) == 1; 
    }
    
    @Override
    protected EntityPlayer findPlayerToAttack() {
        if(this.spawntimer < 0){
            EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, 64D);
            if(entityplayer != null && this.canAngelBeSeenMultiplayer()) {
                return entityplayer;
            }else{
                return null;
            }
        } 
        return null;
    }
    
    // ~~~~~ Freezing the Angel ~~~~~
    private boolean isInFieldOfVision(EntityLivingBase player) {
        if(player == null)
            return false;
        Vec3 vec3 = player.getLook(1.0F).normalize();
        Vec3 vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(
                this.posX - player.posX,
                this.boundingBox.minY +
                    (double)(this.height) -
                    (player.posY +
                            (double)player.getEyeHeight()),
                this.posZ - player.posZ);
        double d0 = vec31.lengthVector();
        vec31 = vec31.normalize();
        double d1 = vec3.dotProduct(vec31);
        return d1 > 1.0D - 0.025D / d0 ? player.canEntityBeSeen(this) : false;
    }
    
    // ~~~~~ Freezing the Angel 2~~~~~
    private int[] transparentBlocks = { 20, 8, 9 , 10, 11, 18, 27, 
            28, 30, 31, 32, 37, 38, 39, 
            40, 44, 50, 51, 52, 59, 64, 
            65, 66, 67, 69, 70, 71, 72, 75, 
            76, 77, 78, 83, 85, 90, 92, 96, 
            101, 102, 106, 107, 108, 109, 
            111, 113, 114, 114, 117};
    private boolean isBlockTransparent(int id)
    {
        for(int i = 0; i < this.transparentBlocks.length; i++)
        {
            if(id == this.transparentBlocks[i])
            {
                return true;
            }
        }
        return false;
    }
    private MovingObjectPosition rayTraceBlocks(Vec3 par1Vec3D, Vec3 par2Vec3D)
    {
        boolean par3 = false;
        boolean par4 = false;

        if (Double.isNaN(par1Vec3D.xCoord) || Double.isNaN(par1Vec3D.yCoord) || Double.isNaN(par1Vec3D.zCoord))
        {
            return null;
        }

        if (Double.isNaN(par2Vec3D.xCoord) || Double.isNaN(par2Vec3D.yCoord) || Double.isNaN(par2Vec3D.zCoord))
        {
            return null;
        }

        int i = MathHelper.floor_double(par2Vec3D.xCoord);
        int j = MathHelper.floor_double(par2Vec3D.yCoord);
        int k = MathHelper.floor_double(par2Vec3D.zCoord);
        int l = MathHelper.floor_double(par1Vec3D.xCoord);
        int i1 = MathHelper.floor_double(par1Vec3D.yCoord);
        int j1 = MathHelper.floor_double(par1Vec3D.zCoord);
        int k1 = worldObj.getBlockId(l, i1, j1);
        int i2 = worldObj.getBlockMetadata(l, i1, j1);
        Block block = Block.blocksList[k1];

        if ((!par4 || block == null || block.getCollisionBoundingBoxFromPool(worldObj, l, i1, j1) != null) && k1 > 0 && block.canCollideCheck(i2, par3))
        {
            MovingObjectPosition movingobjectposition = block.collisionRayTrace(worldObj, l, i1, j1, par1Vec3D, par2Vec3D);

            if (movingobjectposition != null)
            {
                return movingobjectposition;
            }
        }

        for (int l1 = 200; l1-- >= 0;)
        {
            if (Double.isNaN(par1Vec3D.xCoord) || Double.isNaN(par1Vec3D.yCoord) || Double.isNaN(par1Vec3D.zCoord))
            {
                return null;
            }

            if (l == i && i1 == j && j1 == k)
            {
                return null;
            }

            boolean flag = true;
            boolean flag1 = true;
            boolean flag2 = true;
            double d = 999D;
            double d1 = 999D;
            double d2 = 999D;

            if (i > l)
            {
                d = (double)l + 1.0D;
            }
            else if (i < l)
            {
                d = (double)l + 0.0D;
            }
            else
            {
                flag = false;
            }

            if (j > i1)
            {
                d1 = (double)i1 + 1.0D;
            }
            else if (j < i1)
            {
                d1 = (double)i1 + 0.0D;
            }
            else
            {
                flag1 = false;
            }

            if (k > j1)
            {
                d2 = (double)j1 + 1.0D;
            }
            else if (k < j1)
            {
                d2 = (double)j1 + 0.0D;
            }
            else
            {
                flag2 = false;
            }

            double d3 = 999D;
            double d4 = 999D;
            double d5 = 999D;
            double d6 = par2Vec3D.xCoord - par1Vec3D.xCoord;
            double d7 = par2Vec3D.yCoord - par1Vec3D.yCoord;
            double d8 = par2Vec3D.zCoord - par1Vec3D.zCoord;

            if (flag)
            {
                d3 = (d - par1Vec3D.xCoord) / d6;
            }

            if (flag1)
            {
                d4 = (d1 - par1Vec3D.yCoord) / d7;
            }

            if (flag2)
            {
                d5 = (d2 - par1Vec3D.zCoord) / d8;
            }

            byte byte0 = 0;

            if (d3 < d4 && d3 < d5)
            {
                if (i > l)
                {
                    byte0 = 4;
                }
                else
                {
                    byte0 = 5;
                }

                par1Vec3D.xCoord = d;
                par1Vec3D.yCoord += d7 * d3;
                par1Vec3D.zCoord += d8 * d3;
            }
            else if (d4 < d5)
            {
                if (j > i1)
                {
                    byte0 = 0;
                }
                else
                {
                    byte0 = 1;
                }

                par1Vec3D.xCoord += d6 * d4;
                par1Vec3D.yCoord = d1;
                par1Vec3D.zCoord += d8 * d4;
            }
            else
            {
                if (k > j1)
                {
                    byte0 = 2;
                }
                else
                {
                    byte0 = 3;
                }

                par1Vec3D.xCoord += d6 * d5;
                par1Vec3D.yCoord += d7 * d5;
                par1Vec3D.zCoord = d2;
            }

            Vec3 vec3d = Vec3.createVectorHelper(par1Vec3D.xCoord, par1Vec3D.yCoord, par1Vec3D.zCoord);
            l = (int)(vec3d.xCoord = MathHelper.floor_double(par1Vec3D.xCoord));

            if (byte0 == 5)
            {
                l--;
                vec3d.xCoord++;
            }

            i1 = (int)(vec3d.yCoord = MathHelper.floor_double(par1Vec3D.yCoord));

            if (byte0 == 1)
            {
                i1--;
                vec3d.yCoord++;
            }

            j1 = (int)(vec3d.zCoord = MathHelper.floor_double(par1Vec3D.zCoord));

            if (byte0 == 3)
            {
                j1--;
                vec3d.zCoord++;
            }

            int j2 = worldObj.getBlockId(l, i1, j1);
            int k2 = worldObj.getBlockMetadata(l, i1, j1);
            Block block1 = Block.blocksList[j2];

            if ((!par4 || block1 == null || block1.getCollisionBoundingBoxFromPool(worldObj, l, i1, j1) != null) && j2 > 0 && block1.canCollideCheck(k2, par3) && !this.isBlockTransparent(j2))
            {
                MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(worldObj, l, i1, j1, par1Vec3D, par2Vec3D);

                if (movingobjectposition1 != null)
                {
                    return movingobjectposition1;
                }
            }
        }

        return null;
    }
    private boolean LineOfSightCheck(EntityPlayer entity1) {
        if(entity1 == null)
            return false;
        
        if(this.worldObj.rayTraceBlocks_do_do(
                Vec3.createVectorHelper(
                        this.posX,
                        this.posY + (double)getEyeHeight(),
                        this.posZ),
                Vec3.createVectorHelper(
                        entity1.posX,
                        entity1.posY + (double)entity1.getEyeHeight(),
                        entity1.posZ),
                true, true) == null)
            return true;
        if(this.worldObj.rayTraceBlocks_do_do(
                Vec3.createVectorHelper(
                        this.posX,
                        this.posY + this.height,
                        this.posZ),
                Vec3.createVectorHelper(
                        entity1.posX,
                        entity1.posY + (double)entity1.getEyeHeight(),
                        entity1.posZ),
                true, true) == null)
            return true;
        if(this.worldObj.rayTraceBlocks_do_do(
                Vec3.createVectorHelper(
                        this.posX,
                        this.posY + (this.height * 0.1),
                        this.posZ),
                Vec3.createVectorHelper(
                        entity1.posX,
                        entity1.posY + (double)entity1.getEyeHeight(),
                        entity1.posZ),
                        true, true) == null)
            return true;
        if(this.worldObj.rayTraceBlocks_do_do(
                Vec3.createVectorHelper(
                        posX + 0.7,
                        posY + (double)getEyeHeight(),
                        posZ),
                Vec3.createVectorHelper(
                        entity1.posX,
                        entity1.posY + (double)entity1.getEyeHeight(),
                        entity1.posZ),
                        true, true) == null)
            return true;
        if(this.worldObj.rayTraceBlocks_do_do(
                Vec3.createVectorHelper(
                        posX - 0.7,
                        posY + (double)getEyeHeight(),
                        posZ),
                Vec3.createVectorHelper(
                        entity1.posX,
                        entity1.posY + (double)entity1.getEyeHeight(),
                        entity1.posZ),
                        true, true) == null)
            return true;
        if(this.worldObj.rayTraceBlocks_do_do(
                Vec3.createVectorHelper(
                        posX,
                        posY + (double)getEyeHeight(),
                        posZ + 0.7),
                Vec3.createVectorHelper(
                        entity1.posX,
                        entity1.posY + (double)entity1.getEyeHeight(),
                        entity1.posZ),
                        true, true) == null)
            return true;
        if(this.worldObj.rayTraceBlocks_do_do(
                Vec3.createVectorHelper(
                        posX,
                        posY + (double)getEyeHeight(),
                        posZ - 0.7),
                Vec3.createVectorHelper(
                        entity1.posX,
                        entity1.posY + (double)entity1.getEyeHeight(),
                        entity1.posZ),
                        true, true) == null)
            return true;
        if(this.worldObj.rayTraceBlocks_do_do(
                Vec3.createVectorHelper(
                        posX,
                        posY + (height * 1.2),
                        posZ - 0.7),
                Vec3.createVectorHelper(
                        entity1.posX,
                        entity1.posY + (double)entity1.getEyeHeight(),
                        entity1.posZ),
                        true, true) == null)
            return true;
        if(this.worldObj.rayTraceBlocks_do_do(
                Vec3.createVectorHelper(
                        posX,
                        posY + (height * 1.2) + 1,
                        posZ),
                Vec3.createVectorHelper(
                        entity1.posX,
                        entity1.posY + (double)entity1.getEyeHeight(),
                        entity1.posZ),
                        true, true) == null);
            return true;
    }
    public boolean GetFlag(float f, float f1, float f2, float f3, float f4)
    {
        if(f < f3)
        {
            if(f2 >= f + f4)
            {
                return true;
            }
            if(f2 <= f1)
            {
                return true;
            }
        }
        if(f1 >= f4)
        {
            if(f2 <= f1 - f4)
            {
                return true;
            }
            if(f2 >= f)
            {
                return true;
            }
        }
        if(f1 < f4 && f >= f3)
        {
            return f2 <= f1 && f2 > f;
        } else
        {
            return false;
        }
    }
    private boolean isInFieldOfVision1(Entity entityweepingangel,
            EntityPlayer entityToAttack, float f4i, float f5i)
    {
        float f = entityToAttack.rotationYaw;
        float f1 = entityToAttack.rotationPitch;
        entityToAttack.attackEntityAsMob(entityToAttack);
        float f2 = entityToAttack.rotationYaw;
        float f3 = entityToAttack.rotationPitch;
        entityToAttack.rotationYaw = f;
        entityToAttack.rotationPitch = f1;
        f = f2;
        f1 = f3;
        float f4 = f4i; // 70f
        float f5 = f5i; // 65f
        float f6 = entityToAttack.rotationYaw - f4;
        float f7 = entityToAttack.rotationYaw + f4;
        float f8 = entityToAttack.rotationPitch - f5;
        float f9 = entityToAttack.rotationPitch + f5;
        boolean flag = this.GetFlag(f6, f7, f, 0.0F, 360F);
        boolean flag1 = this.GetFlag(f8, f9, f1, -180F, 180F);
        return flag && flag1 &&
                (entityToAttack.canEntityBeSeen(entityweepingangel) ||
                        LineOfSightCheck(entityToAttack));
    }
    private boolean canAngelBeSeen(EntityPlayer entity1) {
        if(worldObj.getFullBlockLightValue(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) < 1)
        {
            this.randomSoundDelay = rand.nextInt(40);
            return false;
        }
        if(entity1.canEntityBeSeen(this) || this.LineOfSightCheck(entity1))
        {
            return this.isInFieldOfVision1(this, entity1, 70, 65);
        } else
        {
            return false;
        }
    }
    private boolean canAngelBeSeenMultiplayer() {
        if(worldObj.getFullBlockLightValue(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) < 1)
        {
            this.randomSoundDelay = rand.nextInt(40);
            return false;
        }
        int i = 0;
        List list = worldObj.getEntitiesWithinAABB(EntityPlayer.class, boundingBox.expand(64D, 20D, 64D));
        for(int j = 0; j < list.size(); j++)
        {
            EntityPlayer entity1 = (EntityPlayer)list.get(j);
            if(entity1 instanceof EntityPlayer)
            {
                if(this.canAngelBeSeen(entity1))
                {
                    i++;
                }
            }
        }
        if(i > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
}