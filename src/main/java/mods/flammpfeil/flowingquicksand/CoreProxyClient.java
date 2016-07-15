package mods.flammpfeil.flowingquicksand;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
public class CoreProxyClient extends CoreProxy {

	public static ModelResourceLocation fluidModelLocation = new ModelResourceLocation(new ResourceLocation(FlowingQuickSand.MODID,"quicksand"),"quicksand");

	@Override
	public void initRenderer() {

		/*
		ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(FlowingQuickSand.blockQuickSand), new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				return fluidModelLocation;
			}
		});
		*/
		ModelLoader.setCustomStateMapper(FlowingQuickSand.blockQuickSand, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return fluidModelLocation;
			}
		});

    }
}
