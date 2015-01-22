
package rbr;

import java.io.File;
import java.util.ArrayList;

import utils.AbstractProgress;
import utils.ProgressEvent;

public class RBR extends AbstractProgress
{ 
	private Graph graph;
	private double[] LinkWeightStats;
	private double ard;
	private String topologyFile;
	private String restrictionFile;
	private String inputDir;
	RBRTools tools = new RBRTools();
	int Nct;
	double globalAr;
	

	public RBR(String topologyFile, String restrictionFile, int Nct, String inputDir)
	{
		this.topologyFile = topologyFile;
		this.restrictionFile = restrictionFile;
		this.inputDir = inputDir;
		this.Nct = Nct;
	}
	
	public void load()
	{
		String merge = "merge";
		double reachability = 1.0;
		
		//reportProgress(new ProgressEvent(8, "File name: " + topologyFile));

		System.err.println("File name: "+topologyFile);

		//Make File by Filename and generate graph
		File topology = new File(topologyFile);
		
		//Make graph
		graph = new Graph(topology);
		ArrayList<ArrayList<Router>> paths;
		ArrayList<ArrayList<Router>> simplePaths;

		tools.setRestricitions(restrictionFile);

		//Compute paths and make the routing options
		System.out.println("Paths Computation");
		reportProgress(new ProgressEvent(12, "Paths Computation"));
		paths = tools.pathsComputation(graph);

		System.out.println("Regions Computation");
		reportProgress(new ProgressEvent(25, "Regions Computation"));
		tools.regionsComput(graph);

		//Adjust regions to avoid overlap
		System.out.println("Regions Adjustment");
		reportProgress(new ProgressEvent(30, "Regions Adjustment"));
		tools.adjustsRegions(graph);

		//Do the merge if asked
		System.out.println("Regions Merge");
		reportProgress(new ProgressEvent(40, "Regions Merge"));

		if(merge.equals("merge"))
		{
			System.out.println("Merge");
			for(Router vertice : graph.getVertices())
			{
				tools.Merge(graph, vertice, reachability);
			}
		}

		//Make the routing tables
		System.out.println("Making Tables");
		reportProgress(new ProgressEvent(62, "Making Tables"));
		float[] regionStats = tools.getRegionsStats(graph);
		tools.doRoutingTable(regionStats, graph);         

		System.out.println("Doing Average Routing Distance and Link Weight...");
		reportProgress(new ProgressEvent(75, "Doing Average Routing Distance and Link Weight..."));
		simplePaths = tools.getSimplePaths(paths, graph);
		
		if(!(this.inputDir.equals("nonNCT") && (this.Nct < 0)))
		{
			globalAr = tools.getAr(this.inputDir, simplePaths, this.Nct);
		}

		//Make statistic file with hop count, regions, routing distance and link weight values 
		LinkWeightStats = tools.linkWeightStats(simplePaths, graph);
		
		/*Check if this is necessary*/
		//tools.makeStats(tools.getHopCountStats(paths), /*tools.getRegionsStats(graph)*/regionStats, tools.getRoutingDistance(simplePaths, graph),/*tools.linkWeightStats(simplePaths, graph)*/LinkWeightStats);
		this.ard = tools.getRoutingDistance(simplePaths, graph);
		
		System.out.println("RBR to "+topologyFile+" done!");
		reportProgress(new ProgressEvent(100, "RBR to " + topologyFile + " done!"));
	}
	
	public double getArMax()
	{
		return tools.getArMax();
	}
	
	public double getArMin()
	{
		return tools.getArMin();
	}
	
	public double getArStd()
	{
		return tools.getArStd();
	}
	
	public double getAr()
	{
		return this.globalAr;
	}
	
	public ArrayList<String> getPairAr (String src)
	{
		return tools.getPairAr(src);
	}

	public double getArd()
	{
		return this.ard;
	}

	public int graphSize()
	{
		return this.graph.getVertices().size();
	}

	public Iterable<Link> links() 
	{
		/*Changed type of links at graph*/
		return this.graph.getLinks();
	}

	public Iterable<Router> switches()
	{
		/*Changed type of links at graph*/
		return this.graph.getVertices();
	}

	public Link get(String src, String dst)
	{
		return graph.getVertice(src).getLink(graph.getVertice(dst));
	}

	public Router get(String sw)
	{
		return graph.getVertice(sw);
	}

	public double LinkWeightMean()
	{
		return LinkWeightStats[0];
	}

	public double LingWeightStdDev()
	{
		return LinkWeightStats[1];
	}




}


