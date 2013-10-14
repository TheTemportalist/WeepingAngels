package WeepingAngels.Client.Render;

import WeepingAngels.Client.Model.ModelWeepingAngel;
import WeepingAngels.Entity.EntityWeepingAngel;
import WeepingAngels.lib.Reference;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderWeepingAngel extends RenderLiving
{
	protected ModelWeepingAngel weepingAngelModel;
	
    public RenderWeepingAngel(float f)
    {
        super(new ModelWeepingAngel(), f);
        this.weepingAngelModel = (ModelWeepingAngel)this.mainModel;
    }
    
    public void renderWeepingAngel(EntityWeepingAngel entityweepingangel, double d, double d1, double d2, 
            float f, float f1)
    {
        super.doRenderLiving(entityweepingangel, d, d1, d2, f, f1);
    }

    public void doRenderLiving(EntityLiving entityliving, double d, double d1, double d2, 
            float f, float f1)
    {
        renderWeepingAngel((EntityWeepingAngel)entityliving, d, d1, d2, f, f1);
    }

    public void doRender(Entity entity, double d, double d1, double d2, 
            float f, float f1)
    {
        renderWeepingAngel((EntityWeepingAngel)entity, d, d1, d2, f, f1);
    }

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return Reference.weepingAngelTex;
	}

	
	
}