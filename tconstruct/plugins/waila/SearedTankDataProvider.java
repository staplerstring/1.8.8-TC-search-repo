package tconstruct.plugins.waila;

import java.util.List;
import mcp.mobius.waila.api.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.smeltery.logic.LavaTankLogic;

public class SearedTankDataProvider implements IWailaDataProvider
{

    @Override
    public ItemStack getWailaStack (IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    @Override
    public List<String> getWailaHead (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getTileEntity() instanceof LavaTankLogic && config.getConfig("tcon.searedtank", true))
        {
            LavaTankLogic te = (LavaTankLogic) accessor.getTileEntity();
            if (te.containsFluid())
            {
                FluidStack fs = te.tank.getFluid();
                currenttip.add(StatCollector.translateToLocal("tconstruct.waila.liquidtag") + WailaRegistrar.fluidNameHelper(fs));
                currenttip.add(StatCollector.translateToLocal("tconstruct.waila.amounttag") + fs.amount + "/" + te.tank.getCapacity());
            }
            else
            {
                currenttip.add(SpecialChars.ITALIC + StatCollector.translateToLocal("tconstruct.waila.empty"));
            }
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaTail (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

}
