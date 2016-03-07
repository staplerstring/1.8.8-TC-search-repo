package tconstruct.smeltery.logic;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.CastingRecipe;
import tconstruct.library.event.*;

public class CastingBasinLogic extends CastingBlockLogic
{
    public CastingBasinLogic()
    {
        super(TConstructRegistry.getBasinCasting());
    }

    @Override
    public SmelteryCastEvent getCastingEvent (CastingRecipe recipe, FluidStack metal)
    {
        return new SmelteryCastEvent.CastingBasin(recipe, metal);
    }

    @Override
    public SmelteryCastedEvent getCastedEvent (CastingRecipe recipe, ItemStack result)
    {
        return new SmelteryCastedEvent.CastingBasin(recipe, result);
    }
}
