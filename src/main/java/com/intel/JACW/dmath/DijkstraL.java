package com.intel.JACW.dmath;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;
import com.intel.mttest.util.GR;
import com.intel.mttest.util.MTTestResourceManager;


/**
 * <p> Classic [long] implementation of Dijkstra algorithm.</p>
 * <p> Input graph comes from the file. </p>
 * <p> It assumes that start vertex is 0 and all d[i][j] >= 0 && d[i][j] <= Integer.MAX_VALUE / countOfVerticies </p>
 * <p> Test format: </p>
 * <ul>
 * <li>countOfVerticies</li>
 * <li>d[0][0]    d[0][1]    d[0][2]    ...    d[0][n-1]  </li>
 * <li>d[1][0]    d[1][1]    d[1][2]    ...    d[1][n-1]  </li>
 * <li>...                                                </li>
 * <li>d[n-1][0]  d[n-1][1]  d[n-1][2]  ...    d[n-1][n-1]</li> 
 * </ul>
 */
public class DijkstraL extends AbstractTestCase {

	@XMLParameter(defaultValue = "dijkstra_200.txt")
	public String goldenFileName;
	
	public int cntVertices;

	private final long distUnreachable = Long.MAX_VALUE >> 1;
	
	private long[] distance;
	private boolean used[];
	private long[][] weights;
  	private int startVertex;

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);
		File file = new File(GR.getGoldenDir(), goldenFileName);
		BufferedReader in = null; 
		try {
			startVertex = 0;
			InputStream inStream = MTTestResourceManager.openFileAsInputStream(file);
			in = new BufferedReader(new InputStreamReader(inStream));
			String s = in.readLine();
			cntVertices = Integer.parseInt(s);
			int maxEdgeCost = Integer.MAX_VALUE / cntVertices; // Any path consists at most of (argCntVertices - 1) edges. edge +1 on the last update
			weights = new long[cntVertices][cntVertices];
			for (int row = 0; row < cntVertices; row++) {
				s = in.readLine();
				StringTokenizer st = new StringTokenizer(s);
				for (int col = 0; col < cntVertices; col++) {
					weights[row][col] = Long.parseLong(st.nextToken());
					if(weights[row][col] > maxEdgeCost || weights[row][col] < 0) {
						in.close();
						throw new InvalidTestFormatException("edge weight is incorrect", this.getClass());
					}
				}
			}
			in.close();
			distance = new long[cntVertices];
			used = new boolean[cntVertices];
		} catch (IOException e) {
			try {
				in.close();
			} catch (Throwable ee) {
			}
			throw new InvalidTestFormatException("Malformed " + file, this.getClass());
		} 
	}

	@Override
	public long iteration() {
		long counter = 0;
		for (int i = 0; i < repeats; i++) {
			findRoutes();
			counter += 1;
		}
		return counter;
	}

	private void findRoutes() {
		Arrays.fill(distance, distUnreachable);
		distance[startVertex] = 0;
		Arrays.fill(used, false);
		while(true) {
		    int nextV = -1;
		    for (int v = 0; v < cntVertices; v++)
		        if (!used[v] && (nextV == -1 || distance[v] < distance[nextV]))
		            nextV = v;
		    if(nextV == -1) {
			break;
		    }
		    used[nextV] = true;
		    for (int v = 0; v < cntVertices; v++) {
		    	distance[v] = Math.min(distance[v], distance[nextV] + weights[nextV][v]);
		    }
	    }
	}	
}