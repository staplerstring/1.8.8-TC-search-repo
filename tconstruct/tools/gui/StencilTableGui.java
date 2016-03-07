package tconstruct.tools.gui;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import cpw.mods.fml.common.Optional;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import tconstruct.TConstruct;
import tconstruct.library.client.*;
import tconstruct.library.crafting.StencilBuilder;
import tconstruct.tools.inventory.PatternShaperContainer;
import tconstruct.tools.logic.StencilTableLogic;
import tconstruct.util.network.PatternTablePacket;

import java.util.Collections;
import java.util.List;

@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public class StencilTableGui extends GuiContainer implements INEIGuiHandler
{
    StencilTableLogic logic;
    int activeButton;

    public StencilTableGui(InventoryPlayer inventoryplayer, StencilTableLogic shaper, World world, int x, int y, int z)
    {
        super(new PatternShaperContainer(inventoryplayer, shaper));
        logic = shaper;
        activeButton = 0;
    }

    @Override
    public void onGuiClosed ()
    {
        super.onGuiClosed();
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        fontRendererObj.drawString(StatCollector.translateToLocal("crafters.PatternShaper"), 50, 6, 0x404040);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/patternshaper.png");

    @Override
    protected void drawGuiContainerBackgroundLayer (float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        int cornerX = (this.width - this.xSize) / 2;
        int cornerY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(cornerX, cornerY, 0, 0, this.xSize, this.ySize);
        if (!logic.isStackInSlot(0))
        {
            this.drawTexturedModalRect(cornerX + 47, cornerY + 34, 176, 0, 18, 18);
        }
    }

    @Override
    public void initGui ()
    {
        super.initGui();

        int bpr = 4; // buttons per row!
        int cornerX = this.guiLeft - 22 * bpr;
        int cornerY = this.guiTop + 2;

        this.buttonList.clear();

        int id = 0;
        for (int iter = 0; iter < TConstructClientRegistry.stencilButtons.size(); iter++)
        {
            StencilGuiElement element = TConstructClientRegistry.stencilButtons.get(iter);
            if (element.stencilIndex == -1)
                continue;
            GuiButtonStencil button = new GuiButtonStencil(id++, cornerX + 22 * (iter % bpr), cornerY + 22 * (iter / bpr), element.buttonIconX, element.buttonIconY, element.domain, element.texture, element);
            this.buttonList.add(button);
        }

        // secondary buttons, yay!
        // these are to use for other mods :I
        cornerX = this.guiLeft + this.xSize + 4;
        for (int iter = 0; iter < TConstructClientRegistry.stencilButtons2.size(); iter++)
        {
            StencilGuiElement element = TConstructClientRegistry.stencilButtons2.get(iter);
            if (element.stencilIndex == -1)
                continue;
            GuiButtonStencil button = new GuiButtonStencil(id++, cornerX + 22 * (iter % bpr), cornerY + 22 * (iter / bpr), element.buttonIconX, element.buttonIconY, element.domain, element.texture, element);
            this.buttonList.add(button);
        }

        // get the correct setting :I
        ItemStack stack;
        if (logic.getStackInSlot(1) != null)
        {
            activeButton = StencilBuilder.getId(logic.getStackInSlot(1));
            setActiveButton(activeButton);
            stack = StencilBuilder.getStencil(((GuiButtonStencil) this.buttonList.get(activeButton)).element.stencilIndex);
        }
        else
            stack = null;

        logic.setSelectedPattern(stack);
        updateServer(stack);
    }

    @Override
    protected void actionPerformed (GuiButton button)
    {
        ItemStack pattern = logic.getStackInSlot(0);
        if (pattern != null && StencilBuilder.isBlank(pattern))
        {
            int id = ((GuiButtonStencil) button).element.stencilIndex;
            ItemStack stack = StencilBuilder.getStencil(id);
            if (stack != null)
            {
                logic.setSelectedPattern(stack);
                updateServer(stack);
            }
        }

        setActiveButton(button.id);
    }

    private void setActiveButton (int id)
    {
        // deactivate old button
        ((GuiButton) this.buttonList.get(activeButton)).enabled = true;
        // update active button
        activeButton = id;
        // activate the button
        ((GuiButton) this.buttonList.get(activeButton)).enabled = false;
    }

    void updateServer (ItemStack stack)
    {
        TConstruct.packetPipeline.sendToServer(new PatternTablePacket(logic.xCoord, logic.yCoord, logic.zCoord, stack));
    }

    @Override
    public VisiblityData modifyVisiblity(GuiContainer guiContainer, VisiblityData visiblityData) {
        return visiblityData;
    }

    @Override
    public Iterable<Integer> getItemSpawnSlots(GuiContainer guiContainer, ItemStack itemStack) {
        return null;
    }

    @Override
    public List<TaggedInventoryArea> getInventoryAreas(GuiContainer guiContainer) {
        return Collections.emptyList();
    }

    @Override
    public boolean handleDragNDrop(GuiContainer guiContainer, int i, int i2, ItemStack itemStack, int i3) {
        return false;
    }

    @Override
    public boolean hideItemPanelSlot(GuiContainer guiContainer, int x, int y, int w, int h) {
        // is it in the horizontal column of the right buttons?
        if(x > this.guiLeft + this.xSize + 4 && x < this.guiLeft + this.xSize + 4 + 22*3 + 16)
            if(y > this.guiTop - 10 && y < this.guiTop + 2 + 22*(TConstructClientRegistry.stencilButtons2.size()-1)/4 + 22*(TConstructClientRegistry.stencilButtons2.size()%4 > 0 ? 1 : 0))
                return true;

        return false;
    }
}