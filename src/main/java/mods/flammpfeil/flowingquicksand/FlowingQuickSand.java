package mods.flammpfeil.flowingquicksand;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.init.*;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod(modid = FlowingQuickSand.MODID, name=FlowingQuickSand.MODNAME ,version = FlowingQuickSand.VERSION)
public class FlowingQuickSand
{
    public static final String MODID = "flowingquicksand";
    public static final String MODNAME = "FlowingQuickSand";
    public static final String VERSION = "1.0";

    static Fluid fluidQuickSand;
    static Block blockQuickSand;
    static PotionType potionTypeQuicksand;

    static public final Material QuickSand = new MaterialLiquid(MapColor.SAND);

    @EventHandler
    public void constructing(FMLConstructionEvent event){
        FluidRegistry.enableUniversalBucket();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){

        {
            UniversalBucket ub = ForgeModContainer.getInstance().universalBucket;
            if (ub != null && ub.getContainerItem() == null)
                ub.setContainerItem(Items.BUCKET);
        }

        fluidQuickSand = new Fluid("quicksand", new ResourceLocation("blocks/sand"), new ResourceLocation(MODID , "blocks/sand_flow"))
            .setFillSound(SoundEvents.BLOCK_SAND_BREAK)
            .setEmptySound(SoundEvents.BLOCK_SAND_PLACE)
            .setDensity(3000).setViscosity(6000);
        FluidRegistry.registerFluid(fluidQuickSand);

        FluidRegistry.addBucketForFluid(fluidQuickSand);

        blockQuickSand = new BlockFluidClassic(fluidQuickSand, QuickSand){
            {
                setSoundType(SoundType.SAND);
            }

            @Override
            protected void flowIntoBlock(World world, BlockPos pos, int meta) {
                if (meta < 0) return;
                if (displaceIfPossible(world, pos))
                {
                    IBlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();
                    if(state.getMaterial() instanceof MaterialLiquid) {
                        world.setBlockState(pos, Blocks.SANDSTONE.getDefaultState(), 3);
                    }else{
                        super.flowIntoBlock(world, pos, meta);
                    }
                }
            }

            @Override
            public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState iblockstate, Entity entity, double yToTest, Material materialIn, boolean testingHead) {
                if(materialIn == Material.WATER)
                    return Boolean.TRUE;
                return super.isEntityInsideMaterial(world, blockpos, iblockstate, entity, yToTest, materialIn, testingHead);
            }
        }.setQuantaPerBlock(3).setRegistryName(MODID,"quicksand").setUnlocalizedName("flowingquicksand").setCreativeTab(CreativeTabs.DECORATIONS);
        GameRegistry.register(blockQuickSand);
        //GameRegistry.register(new ItemBlock(blockQuickSand).setRegistryName(blockQuickSand.getRegistryName()));

        potionTypeQuicksand = new PotionType(new PotionEffect[] {new PotionEffect(new DummyPotion(true, MapColor.SAND.colorValue), 0)}){
            {
                registerPotionType("quicksand", this);
            }
        };

        CoreProxy.proxy.initRenderer();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        {
            FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("quicksand", 333)
                , PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), potionTypeQuicksand)
                , new ItemStack(Items.GLASS_BOTTLE));

            BrewingRecipeRegistry.addRecipe(
                    PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER),
                    new ItemStack(Blocks.SAND),
                    PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), potionTypeQuicksand));
        }

        {
            FluidStack fs = new FluidStack(fluidQuickSand, Fluid.BUCKET_VOLUME);
            UniversalBucket ub = ForgeModContainer.getInstance().universalBucket;
            ItemStack stack = new ItemStack(ub);
            if (ub.fill(stack, fs, true) == fs.amount)
            {
                FluidContainerRegistry.registerFluidContainer(fluidQuickSand,stack, new ItemStack(Items.BUCKET));
            }

            BrewingRecipeRegistry.addRecipe(
                    new ItemStack(Items.WATER_BUCKET),
                    new ItemStack(Blocks.SANDSTONE),
                    stack.copy());

            GameRegistry.addRecipe(new ShapelessOreRecipe(Blocks.SAND, stack.copy()){
                public boolean matches(InventoryCrafting var1, World world)
                {
                    ArrayList<Object> required = new ArrayList<Object>(input);

                    for (int x = 0; x < var1.getSizeInventory(); x++) {
                        ItemStack slot = var1.getStackInSlot(x);

                        if (slot == null) continue;

                        boolean inRecipe = false;
                        Iterator<Object> req = required.iterator();

                        while (req.hasNext()) {
                            boolean match = false;

                            Object next = req.next();

                            if (next instanceof ItemStack) {
                                match = ItemStack.areItemStacksEqual((ItemStack) next, slot);
                            }

                            if (match) {
                                inRecipe = true;
                                required.remove(next);
                                break;
                            }
                        }

                        if (!inRecipe) {
                            return false;
                        }
                    }

                    return required.isEmpty();
                }
            });
        }

    }

    static public class DummyPotion extends Potion{
        public DummyPotion(boolean isBadEffectIn, int liquidColorIn) {
            super(isBadEffectIn, liquidColorIn);
        }
    }
}
