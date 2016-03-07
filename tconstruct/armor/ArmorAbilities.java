package tconstruct.armor;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import java.util.*;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.TConstruct;
import tconstruct.armor.items.TravelGear;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.library.modifier.IModifyable;
import tconstruct.util.network.HealthUpdatePacket;

public class ArmorAbilities
{
    public static List<String> stepBoostedPlayers = new ArrayList();
    //ItemStack prevFeet;
    double prevMotionY;

    @SubscribeEvent
    public void playerTick (TickEvent.PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        TPlayerStats stats = TPlayerStats.get(player);

        // Wall climb
        if (stats.climbWalls)
        {
            double motionX = player.posX - player.lastTickPosX;
            double motionZ = player.posZ - player.lastTickPosZ;
            double motionY = player.posY - player.lastTickPosY - 0.762;
            if (motionY > 0.0D && (motionX == 0D || motionZ == 0D))
            {
                player.fallDistance = 0.0F;
            }
        }

        //Feet changes
        ItemStack feet = player.getCurrentArmor(0);
        if (feet != null)
        {
            if (feet.getItem() instanceof IModifyable && !player.isSneaking())
            {
                NBTTagCompound tag = feet.getTagCompound().getCompoundTag(((IModifyable) feet.getItem()).getBaseTagName());
                int sole = tag.getInteger("Slimy Soles");
                if (sole > 0)
                {
                    if (!player.isSneaking() && player.onGround && prevMotionY < -0.4)
                        player.motionY = -prevMotionY * (Math.min(0.99, sole * 0.2));
                }
            }
            prevMotionY = player.motionY;
        }
        /* Former step height boost handling 
        if (feet != prevFeet)
        {
            if (prevFeet != null && prevFeet.getItem() instanceof TravelGear)
                player.stepHeight -= 0.6f;
            if (feet != null && feet.getItem() instanceof TravelGear)
                player.stepHeight += 0.6f;
            prevFeet = feet;
        }*/
        boolean stepBoosted = stepBoostedPlayers.contains(player.getGameProfile().getName());
        if (stepBoosted)
            player.stepHeight = 1.1f;
        if (!stepBoosted && feet != null && feet.getItem() instanceof TravelGear)
        {
            stepBoostedPlayers.add(player.getGameProfile().getName());
        }
        else if (stepBoosted && (feet == null || !(feet.getItem() instanceof TravelGear)))
        {
            stepBoostedPlayers.remove(player.getGameProfile().getName());
            player.stepHeight -= 0.6f;
        }
        //TODO: Proper minimap support
        /*ItemStack stack = player.inventory.getStackInSlot(8);
        if (stack != null && stack.getItem() instanceof ItemMap)
        {
            stack.getItem().onUpdate(stack, player.worldObj, player, 8, true);
        }*/
    }


    @SubscribeEvent
    public void dimensionChanged(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        if(event.player == null || !(event.player instanceof EntityPlayerMP))
            return;

        // this callback is only called serverside
        float oldHealth = event.player.getHealth();
        // tell the client to update its hp
        TConstruct.packetPipeline.sendTo(new HealthUpdatePacket(oldHealth), (net.minecraft.entity.player.EntityPlayerMP) event.player);
    }
}
