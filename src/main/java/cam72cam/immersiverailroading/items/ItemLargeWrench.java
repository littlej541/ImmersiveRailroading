package cam72cam.immersiverailroading.items;

import cam72cam.immersiverailroading.ImmersiveRailroading;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
//TODO buildcraft.api.tools.IToolWrench
import net.minecraft.util.ResourceLocation;

public class ItemLargeWrench extends Item {
	public static final String NAME = "item_large_wrench";
	
	public ItemLargeWrench() {
		super();
		setUnlocalizedName(ImmersiveRailroading.MODID + ":" + NAME);
		setRegistryName(new ResourceLocation(ImmersiveRailroading.MODID, NAME));
        this.setCreativeTab(CreativeTabs.TOOLS);
	}
}
