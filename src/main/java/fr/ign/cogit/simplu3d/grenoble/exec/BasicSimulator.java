package fr.ign.cogit.simplu3d.grenoble.exec;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import fr.ign.cogit.simplu3d.io.nonStructDatabase.shp.LoaderSHP;
import fr.ign.cogit.simplu3d.io.shapefile.SaveGeneratedObjects;
import fr.ign.cogit.simplu3d.model.BasicPropertyUnit;
import fr.ign.cogit.simplu3d.model.CadastralParcel;
import fr.ign.cogit.simplu3d.model.Environnement;
import fr.ign.cogit.simplu3d.rjmcmc.cuboid.geometry.impl.Cuboid;
import fr.ign.cogit.simplu3d.rjmcmc.cuboid.optimizer.cuboid.OptimisedBuildingsCuboidFinalDirectRejection;
import fr.ign.cogit.simplu3d.rjmcmc.generic.predicate.SamplePredicate;
import fr.ign.cogit.simplu3d.util.SimpluParameters;
import fr.ign.cogit.simplu3d.util.SimpluParametersJSON;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.0
 * 
 *          Simulateur standard
 * 
 * 
 */
public class BasicSimulator {

	/**
	 * @param args
	 */

	// [buildin-g_footprint_rectangle_cli_main
	public static void main(String[] args) throws Exception {
	 
		// Loading of configuration file that contains sampling space
		// information and simulated annealing configuration
//		String folderName = BasicSimulator.class.getClassLoader().getResource("scenario/").getPath();
	  String folderName = "./scenario/";
		String fileName = "building_parameters.json";
		SimpluParameters p = new SimpluParametersJSON(new File(folderName + fileName));
		
		// Rules parameters.8
		// Distance to road
		double distReculVoirie = 0;
		// Distance to bottom of the parcel
		double distReculFond = 0;
		// Distance to lateral parcel limits
		double distReculLat = 0;
		// Distance between two buildings of a parcel
		double distanceInterBati = 0;
		// Maximal ratio built area
		double maximalCES = 0.6;
		
		
		//Code pour mettre à jour les paramètres
		Options options = configFirstParameters();
		

		CommandLineParser parser = new DefaultParser();

		CommandLine cmd = parser.parse(options, args);
		
		if (cmd.hasOption(ATT_DIST_RECUL_VOIRIE)) {
			distReculVoirie = Double.parseDouble(cmd.getOptionValue(ATT_DIST_RECUL_VOIRIE));
		}

		if (cmd.hasOption(ATT_DIST_RECUL_FOND)) {
			distReculFond = Double.parseDouble(cmd.getOptionValue(ATT_DIST_RECUL_FOND));
		}
		
		if (cmd.hasOption(ATT_DIST_RECUL_LAT)) {
			distReculLat = Double.parseDouble(cmd.getOptionValue(ATT_DIST_RECUL_LAT));
		}
		
		if (cmd.hasOption(ATT_DIST_INTER_BATI)) {
			distanceInterBati = Double.parseDouble(cmd.getOptionValue(ATT_DIST_INTER_BATI));
		}
		
		if (cmd.hasOption(ATT_MAX_CES)) {
			maximalCES = Double.parseDouble(cmd.getOptionValue(ATT_MAX_CES));
		}

		System.out.println("Les règles sont : ");
		System.out.println("Distance de recul à la voirie : " + distReculVoirie);
		System.out.println("Distance de recul au fond de parcel : " + distReculFond);
		System.out.println("Distance de recul latéral : " + distReculLat);
		System.out.println("Distance de recul entre bâtiments : " +  distanceInterBati);
		System.out.println("CES max : " +  maximalCES);

		
		
		System.out.println(p.get("result").toString());
		
		// Load default environment (data are in resource directory)
		Environnement env = LoaderSHP.loadNoDTM(new File("./data/test"));

//		System.out.println(env.getBpU().size() + " bpus");
//		System.out.println(env.getCadastralParcels().size() + " parcels");
//		System.out.println(env.getBuildings().size() + " buildings");
		// Select a parcel on which generation is proceeded
//		env.getBuildings().clear();
		for (BasicPropertyUnit b : env.getBpU()) {
		  for (CadastralParcel c : b.getCadastralParcels()) {
		    if (c.hasToBeSimulated()) {
		      simulateAndSave(b, env, p, distReculVoirie,  distReculFond,  distanceInterBati,  distReculLat, maximalCES);
		    }
		  }
		}
	



		
	}
	
	
	private final static String ATT_DIST_RECUL_VOIRIE = "distReculVoirie";
	// Distance to bottom of the parcel
	private final static String  ATT_DIST_RECUL_FOND = "distReculFond";
	// Distance to lateral parcel limits
	private final  static String  ATT_DIST_RECUL_LAT = "distReculLat";
	// Distance between two buildings of a parcel
	private final static String  ATT_DIST_INTER_BATI = "distInterBati";
	// Maximal ratio built area
	private  final static String  ATT_MAX_CES = "maximalCES";

	
	
	private static void simulateAndSave(BasicPropertyUnit bPU, Environnement env, SimpluParameters p, double distReculVoirie,  double distReculFond, double distanceInterBat, double distReculLat, double maximalCES) throws Exception {

		// Instantiation of the sampler
		OptimisedBuildingsCuboidFinalDirectRejection oCB = new OptimisedBuildingsCuboidFinalDirectRejection();

		
		// Instantiation of the rule checker
		SamplePredicate<Cuboid, GraphConfiguration<Cuboid>, BirthDeathModification<Cuboid>> pred = new SamplePredicate<>(
				bPU, distReculVoirie, distReculFond, distReculLat, distanceInterBat, maximalCES);

		// Run of the optimisation on a parcel with the predicate
		GraphConfiguration<Cuboid> cc = oCB.process(bPU, p, env, 1, pred);
		//Writting the output
		SaveGeneratedObjects.saveShapefile( p.get("result").toString() + "out_"+bPU.getCadastralParcels().get(0).getCode()+".shp", cc, bPU.getId(), 0);
		
	}

	
	private static Options configFirstParameters() {
		Options options = new Options();




		
		Option buildings = new Option(ATT_DIST_RECUL_VOIRIE, true, "Recul à la voirie");
		buildings.setRequired(false);
		buildings.setArgName(ATT_DIST_RECUL_VOIRIE);
		options.addOption(buildings);

		Option points = new Option(ATT_DIST_RECUL_FOND, true, "Recul au fond de parcelle");
		points.setRequired(false);
		points.setArgName(ATT_DIST_RECUL_FOND);
		options.addOption(points);

		Option output = new Option(ATT_DIST_RECUL_LAT, true, "Recul aux limites latérales");
		output.setRequired(false);
		output.setArgName(ATT_DIST_RECUL_LAT);
		options.addOption(output);

		Option parcels = new Option(ATT_DIST_INTER_BATI, true,
				"Recul aux bâtiments de la parcelle");
		parcels.setRequired(false);
		parcels.setArgName(ATT_DIST_INTER_BATI);
		options.addOption(parcels);

		Option extrudeBuildings = new Option(ATT_MAX_CES, true,
				"CES max");
		extrudeBuildings.setRequired(false);
		extrudeBuildings.setArgName(ATT_MAX_CES);
		options.addOption(extrudeBuildings);

	

		return options;

}
}
