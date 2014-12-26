package sbr;

import java.io.File;

import utils.AbstractProgress;
import utils.ProgressEvent;

public class Segmentation extends AbstractProgress {

	private SR segmentation;

	public Segmentation(String file) {
		// create the graph
		segmentation = new SR(new File(file));
	}

	public void load()
	{
		// print the graph
		// segmentation.printGraph();
		// run the algorithm
		reportProgress(new ProgressEvent(0, "Computing Segments"));
		segmentation.computeSegments();
		// list segments
		segmentation.listSegments();
		// insert restrictions
		reportProgress(new ProgressEvent(2, "Setting Restrictions"));
		segmentation.setrestrictions();
		// print restrictions
		segmentation.printRestrictions();
		segmentation.printUnitSeg();
	}

	public int graphSize() {
		//return segmentation.graph.size();
		return 0;
	}

	public Iterable<Link> links() {
		//return segmentation.graph.getLinks();
		return null;
	}

	public Iterable<Switch> switches() {
		return segmentation.graph.getSwitches();
	}

	public Link get(String src, String dst) {
		//return segmentation.graph.get(String src, String dst);
		return null;
	}

	public Switch get(String sw) {
		return segmentation.graph.getSwitch(sw);
	}

	//Iterable unitaries();

	//Iterable bridges();

	public Iterable<Segment> segments() {
		return segmentation.segments;
	}
}
