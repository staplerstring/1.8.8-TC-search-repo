package tconstruct.library.weaponry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.StatCollector;
import tconstruct.library.TConstructRegistry;
import tconstruct.weaponry.TinkerWeaponry;
import tconstruct.util.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.tools.ToolCore;

import java.util.List;

public abstract class AmmoItem extends ToolCore implements IAmmo {
    public AmmoItem(int baseDamage, String name) {
        super(baseDamage);
        this.setCreativeTab(TConstructRegistry.weaponryTab);
    }

    @Override
    public int getAmmoCount(ItemStack stack) {
        if(!stack.hasTagCompound()) return 0;
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        return tags.getInteger("Ammo");
    }

    @Override
    public int getMaxAmmo(ItemStack stack) {
        if(!stack.hasTagCompound()) return 0;
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        return getMaxAmmo(tags);
    }

    @Override
    public int getMaxAmmo(NBTTagCompound tags) {
        float dur = tags.getInteger("TotalDurability");
        return Math.max(1, (int) Math.ceil(dur * getAmmoModifier()));
    }

    @Override
    public int addAmmo(int toAdd, ItemStack stack) {
        if(!stack.hasTagCompound()) return toAdd;
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        int oldCount = tags.getInteger("Ammo");
        int newCount = Math.min(oldCount + toAdd, getMaxAmmo(stack));
        tags.setInteger("Ammo", newCount);
        return toAdd - (newCount - oldCount);
    }

    @Override
    public int consumeAmmo(int toUse, ItemStack stack) {
        if(!stack.hasTagCompound()) return toUse;
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        int oldCount = tags.getInteger("Ammo");
        int newCount = Math.max(oldCount - toUse, 0);
        tags.setInteger("Ammo", newCount);
        return toUse - (oldCount - newCount);
    }

    @Override
    public void setAmmo(int count, ItemStack stack) {
        if(!stack.hasTagCompound()) return;

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        tags.setInteger("Ammo", count);
    }

    public float getAmmoModifier() { return 0.1f; }

    public boolean pickupAmmo(ItemStack stack, ItemStack candidate, EntityPlayer player)
    {
        if(stack.getItem() == null || !stack.hasTagCompound() || !(stack.getItem() instanceof IAmmo))
            return false;

        // check if our candidate fits
        if(candidate != null)
        {
            // same item
            if(testIfAmmoMatches(stack, candidate)) {
                IAmmo pickedup = ((IAmmo) stack.getItem());
                IAmmo ininventory = ((IAmmo) candidate.getItem());
                // we can be sure that it's ammo, since stack is ammo and they're equal
                int count = pickedup.getAmmoCount(stack);
                if(count != ininventory.addAmmo(count, candidate))
                    return true;
            }
        }

        // search the players inventory
        for(ItemStack invItem : player.inventory.mainInventory) {
            if (testIfAmmoMatches(stack, invItem)) {
                IAmmo pickedup = ((IAmmo) stack.getItem());
                IAmmo ininventory = ((IAmmo) invItem.getItem());
                // we can be sure that it's ammo, since stack is ammo and they're equal
                int count = pickedup.getAmmoCount(stack);
                if(count != ininventory.addAmmo(count, invItem))
                    return true;
            }
        }

        // couldn't find a matching thing.
        return false;
    }

    private boolean testIfAmmoMatches(ItemStack reference, ItemStack candidate)
    {
        if(candidate == null)
            return false;
        if(!candidate.hasTagCompound() || !candidate.getTagCompound().hasKey("InfiTool"))
            return false;

        if(reference.getItem() != candidate.getItem())
            return false;

        NBTTagCompound referenceTags = getComparisonTags(reference);
        NBTTagCompound testTags = getComparisonTags(candidate);

        return referenceTags.equals(testTags);
    }

    private NBTTagCompound getComparisonTags(ItemStack stack) {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        NBTTagCompound out = new NBTTagCompound();

        copyTag(out, tags, "Head");
        copyTag(out, tags, "Handle");
        copyTag(out, tags, "Accessory");
        copyTag(out, tags, "Extra");
        copyTag(out, tags, "RenderHead");
        copyTag(out, tags, "RenderHandle");
        copyTag(out, tags, "RenderAccessory");
        copyTag(out, tags, "RenderExtra");
        copyTag(out, tags, "TotalDurability");
        copyTag(out, tags, "Attack");
        copyTag(out, tags, "MiningSpeed");
        copyTag(out, tags, "HarvestLevel");
        copyTag(out, tags, "Modifiers");

        return out;
    }

    private void copyTag(NBTTagCompound out, NBTTagCompound in, String tag) {
        if(in.hasKey(tag))
            out.setInteger(tag, in.getInteger(tag));
    }


    @Override
    public boolean onLeftClickEntity (ItemStack stack, EntityPlayer player, Entity entity)
    {
        // ammo doesn't hurt on smacking stuff with it
        return false;
    }
}
