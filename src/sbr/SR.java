package sbr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SR {

	private final boolean debug = true;
	private final static String[] RoundRobin = { "N", "E", "S", "W" };
	private static int RRIndex[];

	public Graph graph;
	private int nUnitSeg;
	private int nRegSeg;

	private ArrayList<Integer> TopologicalDistances = new ArrayList<Integer>();
	private double TDMean;

	private int subNet, maxSN;
	public ArrayList<Segment> segments;
	private ArrayList<String[]> routwFailLinks = new ArrayList<>();

	private List<Switch> visiteds;
	private List<Switch> nVisiteds;

	public SR(String fileName) {
		graph = new Graph(fileName);
		segments = new ArrayList<>();
		visiteds = new ArrayList<>();
		nVisiteds = new ArrayList<>();
		nUnitSeg = 0;
		nRegSeg = 0;
		RRIndex = new int[2];
		RRIndex[0] = -1;
		RRIndex[1] = -1;
		subNet = 0;
		maxSN = 0;
	}

	public SR(File fileName) {
		graph = new Graph(fileName);
		// this.routwFailLinks = graph.getroutwFailLinks();
		// graph.PrintFailRouters();
		segments = new ArrayList<>();
		visiteds = new ArrayList<>();
		nVisiteds = new ArrayList<>();
		nUnitSeg = 0;
		nRegSeg = 0;
		RRIndex = new int[2];
		RRIndex[0] = -1;
		RRIndex[1] = -1;
		subNet = 0;
		maxSN = 0;
	}

	public void computeSegments() {
		int Nx = (int) Math.sqrt(graph.getSwitches().size()) - 1;
		int Ny = Nx;
		String max = Nx + "" + Ny;
		for (int i = Ny - 1; i >= 0; i--) {
			String min = 0 + "" + i;
			if (debug)
				System.err.println("#Min: " + min + " #Max: " + max);
			computeSegments(min, max);
		}
	}

	private void computeSegments(String min, String max) {

		// fill not visiteds' list
		int xMin = Integer.valueOf(min.substring(0, 1));
		int yMin = Integer.valueOf(min.substring(1, 2));
		int xMax = Integer.valueOf(max.substring(0, 1));
		int yMax = Integer.valueOf(max.substring(1, 2));
		for (int x = xMin; x <= xMax; x++) {
			for (int y = yMax; y >= yMin; y--) {
				Switch sw = graph.getSwitch(x + "" + y);
				if (!sw.isVisited() && !nVisiteds.contains(sw))
					nVisiteds.add(sw);
			}
		}

		if (debug)
			System.err.println("Subnet now: " + subNet);
		Segment sg = new Segment();

		// Choose the start switch
		boolean first = (yMin + 1 == yMax);
		boolean pair = ((yMin + 1) % 2 == 0);
		Switch sw;
		Switch left = graph.getSwitch(xMin + "" + (yMin + 1));
		Switch right = graph.getSwitch(xMax + "" + (yMin + 1));
		if (first || (left.isVisited() && right.isVisited())) {
			if (pair)
				sw = left;
			else
				sw = right;
		} else if (left.isVisited()) {
			sw = left;
		} else /* if(right.isVisited()) */{
			sw = right;
		}

		if (debug)
			System.err.println("#starting: " + sw.getNome());

		Switch sw2;
		if (!sw.isVisited()) {
			if ((sw2 = nextVisited(min, max)) == null) {
				sw.setStart();
				if (debug)
					System.err.println(sw.getNome() + " is Start.");
				sw.setSubNet(subNet);
				// sw.setVisited();
				// nVisiteds.remove(sw);
				// visiteds.add(sw);
				// sg.add(sw);
				// sw.setSegment(sg);
			} else {
				sw = sw2;
				subNet = sw.getSubNet();
			}
		} else {
			subNet = sw.getSubNet();
		}
		segments.add(sg);

		do {
			this.resetRRIndex();
			if (find(sw, sg, min, max)) {
				sg = new Segment();
				segments.add(sg);
				if (debug)
					System.err.println("New Segment.");
			} else {
				if (sw.isVisited()) {
					sw.setTerminal();
					if (debug)
						System.err.println(sw.getNome() + " is Terminal.");
				} else if (min.equals("00")) {// (sw.isStart() &&
												// nVisiteds.size() == 1) {
					sw.setTerminal();
					if (debug)
						System.err.println(sw.getNome() + " is Terminal.");
					sw.setVisited();
					nVisiteds.remove(sw);
					visiteds.add(sw);
				} else {
					sw.unsetStart();
				}
			}
			if ((sw = nextVisited(min, max)) == null) {
				if ((xMin == 0 && yMin == 0)
						&& (sw = nextNotVisited(min, max)) != null) {
					subNet = ++maxSN;
					if (debug)
						System.err.println("Subnet now: " + subNet);
					sg.add(sw);
					sw.setStart();
					if (debug)
						System.err.println(sw.getNome() + " is Start.");
					// sw.setVisited();
					// nVisiteds.remove(sw);
					// visiteds.add(sw);
					sw.setSubNet(subNet);
				} else {
					// if (sg.isEmpty())
					if (sg.getLinks().isEmpty())
						segments.remove(sg);
					break;
				}
			}
		} while (sw != null);

	}

	protected boolean find(Switch sw, Segment segm, String min, String max) {

		sw.setTVisited();
		if (!sw.isVisited()) {
			segm.add(sw);
			sw.setSegment(segm);
		} else if (!sw.belongsTo(subNet) && !(sw.isStart() && sw.isTerminal()))
			return false;
		if (debug)
			System.err.println("Switch now: " + sw.getNome());
		ArrayList<Link> links = sw.suitableLinks(min, max);
		if (links == null) {
			if (debug)
				System.err.println("No Suitable Links found.");
			sw.unsetTVisited();
			sw.setSegment(null);
			segm.remove(sw);
			return false;
		}
		while (!links.isEmpty()) {
			Link ln = getNextLink(links);
			links.remove(ln);
			Link nl = ln.getDestino().getLink(ln.getOrigem());
			if (debug)
				System.err.println("Link now: " + ln.getOrigem().getNome()
						+ " <-> " + ln.getDestino().getNome());
			ln.setTVisited();
			nl.setTVisited();
			segm.add(ln);
			Switch nsw = ln.other(sw);
			if (nsw.isIn(min, max)) {
				if (((nsw.isVisited() || nsw.isStart()) && nsw
						.belongsTo(subNet)) || find(nsw, segm, min, max)) {
					ln.setVisited();
					ln.unsetTVisited();
					nl.setVisited();
					nl.unsetTVisited();
					sw.setVisited();
					sw.unsetTVisited();
					nsw.setVisited();
					nVisiteds.remove(sw);
					visiteds.add(sw);
					nVisiteds.remove(nsw);
					visiteds.add(nsw);
					if (nsw.isTerminal() && nsw.isStart()
							&& !nsw.belongsTo(subNet)
							&& (nsw.getSegment() == null)) {
						nsw.unsetTerminal();
						nsw.unsetStart();
						nsw.setSegment(segm);
						segm.add(nsw);
					}
					nsw.setSubNet(subNet);
					return true;
				}
			}
			ln.unsetTVisited();
			nl.unsetTVisited();
			segm.remove(ln);
		}
		segm.remove(sw);
		sw.setSegment(null);
		sw.unsetTVisited();

		return false;
	}

	/*
	 * search for a switch marked as visited, belonging to the current subnet,
	 * and with at least one link not marked as visited.
	 */
	protected Switch nextVisited(String min, String max) {
		// get switches from visiteds' list
		for (int i = visiteds.size() - 1; i >= 0; i--) {
			Switch sw = visiteds.get(i);
			if (!sw.isTerminal() && sw.isIn(min, max)) {
				if (debug)
					System.err.println(" - Switch " + sw.getNome());
				ArrayList<Link> lE = sw.getLinks();
				for (Link e : lE) {
					if (!e.isVisited() && e.getDestino().isIn(min, max)) {
						if (debug)
							System.err.println(" - - Link "
									+ e.getOrigem().getNome() + " -> "
									+ e.getDestino().getNome());
						if (debug)
							System.err.println("nextVisited " + sw.getNome());
						subNet = sw.getSubNet();
						return sw;
					}
				}
			}
		}
		if (debug)
			System.err.println("nextVisited not found for subnet " + subNet);
		return null;
	}

	/*
	 * look for a switch that is not marked as visited not marked as terminal,
	 * and attached to a terminal switch.
	 */
	protected Switch nextNotVisited(String min, String max) {
		for (Switch sw : visiteds) {
			if (sw.isIn(min, max) && sw.isTerminal()) {
				List<Switch> lS = sw.getNeighbors();
				for (Switch s : lS) {
					if (!s.isVisited() && !s.isTerminal() && s.isIn(min, max)) {
						if (debug)
							System.err.println("nextNotVisited " + s.getNome());
						return s;
					}
				}
			}
		}
		if (debug)
			System.err.println("nextNotVisited not found");
		return null;
	}

	/*
	 * try to make a small segment by choosing a link for close a cycle making a
	 * turn every time. RRIndex keeps track of the last turn
	 */
	protected Link getNextLink(ArrayList<Link> links) {
		Link got = null;
		int index;
		if (RRIndex[0] == -1) {
			if (RRIndex[1] == -1) { // first choice of this computation
				index = 0;
			} else { // second choice
				index = (RRIndex[1] + 1) % 4;
			}
		} else { // others choices
			index = (RRIndex[0] + 2) % 4;
			if ((index + RRIndex[1]) % 2 == 0) {
				index = (index + 1) % 4;
			}
		}
		while (true) {
			for (Link ln : links) {
				if (ln.getCor() == RoundRobin[index]) {
					got = ln;
					break;
				}
			}
			if (got != null)
				break;
			else {
				if (RRIndex[1] == ((RRIndex[0] + 1) % 4))
					index = (index + 3) % 4;
				else
					index = (index + 1) % 4;
			}
		}
		// updates the last turn
		if (index != RRIndex[1]) {
			RRIndex[0] = RRIndex[1];
			RRIndex[1] = index;
		}
		return got;
	}

	private void resetRRIndex() {
		RRIndex[0] = -1;
		RRIndex[1] = -1;
	}

	public void listSegments() {
		int i = 1;
		for (Segment seg : segments) {
			System.err.println("Segment ns" + i++ + ": " + seg);
		}
	}

	public void printRestrictions() {
		File restrictions = new File("Restriction.txt");

		try {
			FileWriter wRestrictions = new FileWriter(restrictions);
			BufferedWriter bw = new BufferedWriter(wRestrictions);

			for (Switch sw : graph.getSwitches()) {
				bw.write(sw.getRestrictions());
				bw.newLine();
			}

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void printUnitSeg() {

		try {
			FileWriter unitSeg = new FileWriter(new File("unitSeg"));
			unitSeg.write(Integer.toString(nUnitSeg));

			unitSeg.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void printRegSeg() {

		try {
			FileWriter regSeg = new FileWriter(new File("RegSeg"));
			regSeg.write(Integer.toString(nRegSeg));

			regSeg.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setrestrictions() {
		for (Segment segment : segments) {
			if (segment.getLinks().isEmpty())
				continue;

			if (segment.isUnitary()) {
				nUnitSeg++;
				// No traffic allowed at link
				Switch Starting = segment.getLinks().get(0).getOrigem();
				Switch Ending = segment.getLinks().get(0).getDestino();
				System.err.println("Start: " + Starting.getNome() + " Ending: "
						+ Ending.getNome());
				// Restricted link
				String opStarting = Starting.getLink(Ending).getCor();
				String opEnding = Ending.getLink(Starting).getCor();
				// Restrictions at Starting core
				for (Link link : Starting.getAdj())
					if (link.getCor() != opStarting)
						Starting.addRestriction(link.getCor(), opStarting);
				// Restrictions at Ending core
				for (Link link : Ending.getAdj())
					if (link.getCor() != opEnding)
						Ending.addRestriction(link.getCor(), opEnding);
				continue;
			}
			// Put it at first or second link
			if (segment.getSwitchs().size() == 1) {
				nRegSeg++;
				segment.getSwitchs()
						.get(0)
						.addRestriction(
								segment.getLinks().get(0).getInvColor(),
								segment.getLinks().get(1).getCor());
				segment.getSwitchs()
						.get(0)
						.addRestriction(segment.getLinks().get(1).getCor(),
								segment.getLinks().get(0).getInvColor());
				continue;
			}
			// At this point we have or starting or regular segment
			if (segment.isRegular()) {
				nRegSeg++;
				Switch restrict = segment.getSwitchs().get(1);
				restrict.addRestriction(
						segment.getLinks().get(1).getInvColor(), segment
								.getLinks().get(2).getCor());
				restrict.addRestriction(segment.getLinks().get(2).getCor(),
						segment.getLinks().get(1).getInvColor());
				continue;
			}
			if (segment.isStarting()) {
				Switch restrict = segment.getSwitchs().get(1);
				restrict.addRestriction(
						segment.getLinks().get(0).getInvColor(), segment
								.getLinks().get(1).getCor());
				restrict.addRestriction(segment.getLinks().get(1).getCor(),
						segment.getLinks().get(0).getInvColor());
			}
		}
	}

	public void setRestrictions() {
		for (Segment segment : segments) {

			/*
			 * Regardless the type of segment place a restriction at second
			 * turn. If just have one turn place at it.
			 */

			if (segment.isStarting()) {
				int nTurns = 0;
				Link firstLink, secondLink;
				String ip = null, op = null;
				Switch restrict = null;

				for (int nLink = 0; nLink < segment.getLinks().size() - 1; nLink++) {
					firstLink = segment.getLinks().get(nLink);
					secondLink = segment.getLinks().get(nLink + 1);

					if (firstLink.getCor() != secondLink.getCor())
						nTurns++;

					// The number of turn you want to place the restriction
					if (nTurns == 2) {
						ip = firstLink.getDestino()
								.getLink(firstLink.getOrigem()).getCor();
						// System.err.println("Input Port: " + ip);
						op = secondLink.getCor();
						// System.err.println("Output Port: " + op);
						restrict = firstLink.getDestino();
						// System.err.println("Switch: " + restrict.getNome());
						break;
					}
				}
				restrict.addRestriction(ip, op);
				restrict.addRestriction(op, ip);
			}

			if (segment.isRegular()) {
				int nTurns = 0;
				Link firstLink = null, secondLink = null;
				String ip = null, op = null;
				Switch restrict = null;

				for (int nLink = 0; nLink < segment.getLinks().size() - 1; nLink++) {
					firstLink = segment.getLinks().get(nLink);
					secondLink = segment.getLinks().get(nLink + 1);

					if (firstLink.getCor() != secondLink.getCor())
						nTurns++;

					// The number of turn you want to place the restriction
					if (nTurns == 2) {
						ip = firstLink.getDestino()
								.getLink(firstLink.getOrigem()).getCor();
						op = secondLink.getCor();
						restrict = firstLink.getDestino();
						break;
					}
				}
				if (nTurns == 1) {
					ip = firstLink.getDestino().getLink(firstLink.getOrigem())
							.getCor();
					op = secondLink.getCor();
					restrict = firstLink.getDestino();
				}

				restrict.addRestriction(ip, op);
				restrict.addRestriction(op, ip);

			}

			if (segment.isUnitary()) {
				nUnitSeg++;
				// No traffic allowed at link
				Switch Starting = segment.getLinks().get(0).getOrigem();
				Switch Ending = segment.getLinks().get(0).getDestino();
				// Restricted link
				String opStarting = Starting.getLink(Ending).getCor();
				String opEnding = Ending.getLink(Starting).getCor();

				// Restriction at local link
				// Starting.addRestriction("I", opStarting);
				// Ending.addRestriction("I", opEnding);

				for (Link link : Starting.getAdj()) {
					if (link.getCor() == Starting.getLink(Ending).getCor())
						continue;

					Starting.addRestriction(link.getCor(), opStarting);
				}

				for (Link link : Ending.getAdj()) {
					if (link.getCor() == Ending.getLink(Starting).getCor())
						continue;

					Ending.addRestriction(link.getCor(), opEnding);
				}

			}
		}

	}

	// Calc the average topological distance between faults. If 0 or 1 fault
	// return MAX_VALUE
	public double calcTopologicDistance() {

		// if one or none faults return "-1"
		if (routwFailLinks.size() == 0 && routwFailLinks.size() == 1)
			return Double.MAX_VALUE;

		double topologicDistance = 0.0;
		int r = 1; // not necessary
		for (int i = 0; i < routwFailLinks.size(); i++) {
			for (int j = i; j < routwFailLinks.size(); j++) {
				if (i == j)
					continue;

				// Get the smallest distance between failures.
				int TDaux = Integer.MAX_VALUE;
				for (int a = 0; a < 2; a++) {
					for (int b = 0; b < 2; b++) {
						int k = Math
								.abs(Integer.valueOf(routwFailLinks.get(i)[a]
										.charAt(0))
										- Integer.valueOf(routwFailLinks.get(j)[b]
												.charAt(0)))
								+ Math.abs(Integer.valueOf(routwFailLinks
										.get(i)[a].charAt(1))
										- Integer.valueOf(routwFailLinks.get(j)[b]
												.charAt(1)));

						System.err.println(routwFailLinks.get(i)[a] + "-"
								+ routwFailLinks.get(j)[b]);
						System.err.println("#k: " + k);

						TDaux = (TDaux < k) ? TDaux : k;
					}

				}
				/*
				 * int a=
				 * Math.abs(Integer.valueOf(routwFailLinks.get(i)[0].charAt
				 * (0))-Integer.valueOf(routwFailLinks.get(j)[0].charAt(0))) +
				 * Math
				 * .abs(Integer.valueOf(routwFailLinks.get(i)[0].charAt(1))-
				 * Integer .valueOf(routwFailLinks.get(j)[0].charAt(1)));
				 * //System.err.println(a); TDaux=a; int b=
				 * Math.abs(Integer.valueOf
				 * (routwFailLinks.get(i)[0].charAt(0))-Integer
				 * .valueOf(routwFailLinks.get(j)[1].charAt(0))) +
				 * Math.abs(Integer
				 * .valueOf(routwFailLinks.get(i)[0].charAt(1))-Integer
				 * .valueOf(routwFailLinks.get(j)[1].charAt(1)));
				 * //System.err.println(b); TDaux=(TDaux<b)?TDaux:b; int c=
				 * Math.
				 * abs(Integer.valueOf(routwFailLinks.get(i)[1].charAt(0))-
				 * Integer .valueOf(routwFailLinks.get(j)[0].charAt(0))) +
				 * Math.abs(Integer
				 * .valueOf(routwFailLinks.get(i)[1].charAt(1))-Integer
				 * .valueOf(routwFailLinks.get(j)[0].charAt(1)));
				 * //System.err.println(c); TDaux=(TDaux<c)?TDaux:c; int d=
				 * Math.
				 * abs(Integer.valueOf(routwFailLinks.get(i)[1].charAt(0))-
				 * Integer .valueOf(routwFailLinks.get(j)[1].charAt(0))) +
				 * Math.abs(Integer
				 * .valueOf(routwFailLinks.get(i)[1].charAt(1))-Integer
				 * .valueOf(routwFailLinks.get(j)[1].charAt(1)));
				 * //System.err.println(d); TDaux=(TDaux<d)?TDaux:d;
				 */

				System.err.println("#TD: " + TDaux);
				System.err.println("#Pares: " + r++);

				topologicDistance += TDaux;
				TopologicalDistances.add(TDaux);

				// System.err.println(topologicDistance);
			}
		}
		TDMean = 2 * topologicDistance
				/ (routwFailLinks.size() * (routwFailLinks.size() - 1));
		return TDMean;
	}

	public double topologicalDistanceStd() {

		double temp = 0.0;
		for (double td : TopologicalDistances)
			temp += (td - TDMean) * (td - TDMean);

		double variance = (temp / (double) (TopologicalDistances.size()));
		// size-1 for sample. We have population

		return Math.sqrt(variance);

	}

	public void printTopologicDistanceStd() {
		try {
			FileWriter topDistStd = new FileWriter(new File("topDist-Std"));
			topDistStd.write(Double.toString(this.topologicalDistanceStd()));

			topDistStd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printTopologicDistance(double topologicDistance) {
		try {
			FileWriter topDist = new FileWriter(new File("topDist"));
			topDist.write(Double.toString(topologicDistance));

			topDist.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
