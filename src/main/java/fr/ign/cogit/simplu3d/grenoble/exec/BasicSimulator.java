package fr.ign.cogit.simplu3d.grenoble.exec;

import java.io.File;

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

		System.out.println(p.get("result").toString());
		
		// Load default environment (data are in resource directory)
		Environnement env = LoaderSHP.loadNoDTM(new File("./data/test"));

		System.out.println(env.getBpU().size() + " bpus");
		System.out.println(env.getCadastralParcels().size() + " parcels");
		System.out.println(env.getBuildings().size() + " buildings");
		// Select a parcel on which generation is proceeded
		
		for (BasicPropertyUnit b : env.getBpU()) {
		  for (CadastralParcel c : b.getCadastralParcels()) {
		    if (c.hasToBeSimulated()) {
		      simulateAndSave(b, env, p, distReculVoirie,  distReculFond,  distanceInterBati,  distReculLat, maximalCES);
		    }
		  }
		}
	



		
	}
	
	private static void simulateAndSave(BasicPropertyUnit bPU, Environnement env, SimpluParameters p, double distReculVoirie,  double distReculFond, double distanceInterBat, double distReculLat, double maximalCES) throws Exception {

		System.out.println(bPU.getPol2D());
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

}
