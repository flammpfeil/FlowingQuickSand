package mods.flammpfeil.flowingquicksand;

import net.minecraftforge.fml.common.SidedProxy;

public class CoreProxy {
	@SidedProxy(clientSide = "mods.flammpfeil.flowingquicksand.CoreProxyClient", serverSide = "mods.flammpfeil.flowingquicksand.CoreProxy")
	public static CoreProxy proxy;


	public void initRenderer(){}
}
