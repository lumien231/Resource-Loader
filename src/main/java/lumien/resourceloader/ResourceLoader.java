package lumien.resourceloader;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lumien.resourceloader.loader.NormalResourceLoader;
import lumien.resourceloader.loader.OverridingResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ResourceLoader.MOD_ID, name = ResourceLoader.MOD_NAME, version = ResourceLoader.MOD_VERSION, clientSideOnly = true, acceptedMinecraftVersions = "[1.12,1.13)", certificateFingerprint = "@FINGERPRINT@")
public class ResourceLoader
{
	public static final String MOD_ID = "resourceloader";
	public static final String MOD_NAME = "Resource Loader";
	public static final String MOD_VERSION = "@VERSION@";

	@Instance(MOD_ID)
	public static ResourceLoader INSTANCE;

	public static Logger logger = LogManager.getLogger("ResourceLoader");

	static NormalResourceLoader normalResourceLoader;
	static OverridingResourceLoader overridingResourceLoader;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{

	}

	public static void insertNormalPack(List resourceList)
	{
		if (normalResourceLoader == null)
		{
			normalResourceLoader = new NormalResourceLoader();
		}

		resourceList.add(normalResourceLoader);
	}

	public static void insertForcedPack(List resourcePackList)
	{
		if (overridingResourceLoader == null)
		{
			overridingResourceLoader = new OverridingResourceLoader();
		}

		resourcePackList.add(overridingResourceLoader);
	}
}
