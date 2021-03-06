package panda.divergentunderground.experimental;


	import com.google.common.collect.ImmutableList;
	import net.minecraft.block.state.IBlockState;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
	import net.minecraftforge.client.event.ModelBakeEvent;
	import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import panda.divergentunderground.DivergentUnderground;
import panda.divergentunderground.common.blocks.BlockHardStone;

import java.io.IOException;
	import java.nio.charset.Charset;
	import java.nio.file.Files;
	import java.nio.file.Path;
	import java.nio.file.Paths;
	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

	/**
	 * @author Cadiboo
	 */
//GNU Lesser General Public License v3.0
//
	@EventBusSubscriber(modid = DivergentUnderground.MODID)
	public final class ModelBakeEventCreator {

		@SubscribeEvent
		public static void writeAssets(final ModelBakeEvent ignored) {

			if (!(Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
				return;
			}
			DivergentUnderground.logger.info("In dev environment, will make missing jsons");

			String runDir = System.getProperty("user.dir");
			
			String dir = runDir.substring(0, runDir.length() - 3).replace("\\", "/") + "src/main/resources/assets/divergentunderground";
		    
			String blockstatesDir = dir +"/blockstates/";
			String modelsDir = dir +"/models/item/";
			String langDir = dir +"/lang/";

			final HashMap<String, String> blockstates = new HashMap<>();

			getModEntries(ForgeRegistries.BLOCKS).forEach(block -> {

				if(!(block instanceof BlockHardStone)){
					return;
				}
				final String name = block.getRegistryName().getPath();

				final ImmutableList<IBlockState> validStates = block.getBlockState().getValidStates();
				
				String texture = ((BlockHardStone)block).textureLocation;
				if (validStates.size() == 4) {
					final String blockstate = "{\r\n  \"forge_marker\": 1,\r\n  \"defaults\": {\r\n    \"textures\": {\r\n      \"all\": \""+texture+"\"\r\n    }\r\n  },\r\n    \"variants\": {\r\n      \"hardness=0\":{ \"model\": \"divergentunderground:cube_all\" },              \r\n      \"hardness=1\":{ \"model\": \"divergentunderground:cube_all\" },              \r\n      \"hardness=2\":{ \"model\": \"divergentunderground:cube_all\" },\r\n      \"hardness=3\":{ \"model\": \"divergentunderground:cube_all\" }\r\n    }\r\n}\r\n";
					blockstates.put(name, blockstate);
				} else {
					DivergentUnderground.logger.error("Something is wrong");
				}

			});

			blockstates.forEach((name, state) -> {
				final ArrayList<String> data = new ArrayList<>(Arrays.asList(state.split("\n")));
				data.removeIf(String::isEmpty);

				final Path file = Paths.get(blockstatesDir+ name.toLowerCase() + ".json");
				if(Files.notExists(file)){
					file.toFile().getParentFile().mkdirs();
					try {
						DivergentUnderground.logger.info("Writing Blockstate " + name.toLowerCase() + ".json");
						Files.write(file, data, Charset.forName("UTF-8"));
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			});

		}

		public static <T extends IForgeRegistryEntry<T>> List<T> getModEntries(final IForgeRegistry<T> registry) {
			return registry.getValues().stream()
					.filter(entry -> Objects.requireNonNull(entry.getRegistryName()).getNamespace().equals(DivergentUnderground.MODID))
					.collect(Collectors.toList());
		}


	}
