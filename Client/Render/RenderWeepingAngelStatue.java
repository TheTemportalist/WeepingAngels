package WeepingAngels.Client.Render;


import WeepingAngels.Client.Model.ModelWeepingAngel;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderWeepingAngelStatue extends RenderLiving{

	public RenderWeepingAngelStatue()
	{
		super(new ModelWeepingAngel(), 0.5f);
	}

	private static final ResourceLocation Texture = new ResourceLocation("weepingangels:textures/entity/weepingangel.png");
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {

	return Texture;

	}
}
